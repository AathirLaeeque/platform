package com.leucine.streem.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leucine.commons.id.IdGenerator;
import com.leucine.streem.collections.partial.PartialEntityObject;
import com.leucine.streem.constant.State;
import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.JobDto;
import com.leucine.streem.dto.ParameterDto;
import com.leucine.streem.dto.RuleHideShowDto;
import com.leucine.streem.dto.mapper.IJobMapper;
import com.leucine.streem.dto.mapper.IUserMapper;
import com.leucine.streem.dto.request.CreateJobRequest;
import com.leucine.streem.dto.request.ParameterExecuteRequest;
import com.leucine.streem.dto.request.TaskExecutionAssignmentRequest;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.exception.ExceptionType;
import com.leucine.streem.exception.MultiStatusException;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.*;
import com.leucine.streem.model.helper.BaseEntity;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.repository.*;
import com.leucine.streem.service.*;
import com.leucine.streem.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.model.Recur;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateJobService implements ICreateJobService {
  private final ICodeService codeService;
  private final IChecklistDefaultUsersRepository checklistDefaultUsersRepository;
  private final IChecklistRelationService checklistRelationService;
  private final IChecklistRepository checklistRepository;
  private final IFacilityRepository facilityRepository;
  private final IJobAuditService jobAuditService;
  private final IJobAssignmentService jobAssignmentService;
  private final JobLogService jobLogService;
  private final IJobMapper jobMapper;
  private final IJobRepository jobRepository;
  private final IParameterExecutionService parameterExecutionService;
  private final IParameterRepository parameterRepository;
  private final IParameterValueRepository parameterValueRepository;
  private final ISchedulerRepository schedulerRepository;
  private final IStageReportService stageReportService;
  private final IUserMapper userMapper;
  private final IUserRepository userRepository;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public JobDto createJob(CreateJobRequest createJobRequest, PrincipalUser principalUser, Facility facility, boolean isScheduled,
                          Scheduler scheduler, Long nextExpectedStartDate) throws ResourceNotFoundException, StreemException, IOException, MultiStatusException {
    Checklist checklist = checklistRepository
      .readById(createJobRequest.getChecklistId())
      .orElseThrow(() -> new ResourceNotFoundException(createJobRequest.getChecklistId(), ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    return createJob(checklist, createJobRequest, principalUser, facility, isScheduled, scheduler, nextExpectedStartDate);
  }

  @Override
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
  public void createScheduledJob(Long schedulerId, Long dateTime) {
    log.info("[createScheduledJob] request to create a scheduled job, schedulerId: {}, dateTime: {}", schedulerId, dateTime);
    try {
      Scheduler scheduler = schedulerRepository.findById(schedulerId)
        .orElseThrow(() -> new ResourceNotFoundException(schedulerId, ErrorCode.SCHEDULER_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

      if (!State.Scheduler.DEPRECATED.equals(scheduler.getState()) && !scheduler.isArchived()) {
        Map<Long, ParameterExecuteRequest> parameterValues = JsonUtils.convertValue(scheduler.getData().get(Scheduler.PARAMETER_VALUES), new TypeReference<>() {});

        Facility facility = facilityRepository.findById(scheduler.getFacilityId()).get();
        User user = userRepository.findById(User.SYSTEM_USER_ID).get();
        PrincipalUser principalUser = userMapper.toPrincipalUser(user);

        CreateJobRequest createJobRequest = new CreateJobRequest();
        createJobRequest.setChecklistId(scheduler.getChecklistId());
        createJobRequest.setParameterValues(parameterValues);

        Long expectedStartDateTime;
        Recur recurrence = RecurrenceRuleUtils.parseRecurrenceRuleExpression(scheduler.getRecurrenceRule());
        LocalDateTime localDateTime = DateTimeUtils.getLocalDateTime(dateTime);
        ZoneId zoneId = ZoneId.systemDefault();
        Date date = Date.from(localDateTime.atZone(zoneId).toInstant());
        Date nextDate = RecurrenceRuleUtils.getNextEventAfter(recurrence, scheduler.getRecurrenceRule(), date);
        if (null != nextDate) {
          expectedStartDateTime = DateTimeUtils.getEpochTime(nextDate);
        } else {
          expectedStartDateTime = null;
        }

        if (null != expectedStartDateTime) {
          var jobAlreadyCreated = jobRepository.isJobExistsBySchedulerIdAndDateGreaterThanOrEqualToExpectedStartDate(scheduler.getId(), expectedStartDateTime);

          if (jobAlreadyCreated) {
            log.info("[createScheduledJob] skipping job creation since job was already created for this date, scheduler: {}", scheduler);
          } else {
            Checklist checklist = checklistRepository
              .readById(createJobRequest.getChecklistId())
              .orElseThrow(() -> new ResourceNotFoundException(createJobRequest.getChecklistId(), ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
            createJob(checklist, createJobRequest, principalUser, facility, true, scheduler, expectedStartDateTime);
          }
        }
      }
    } catch (Exception ex) {
      log.error("[createScheduledJob] error creating a scheduled job", ex);
    }
  }

  private JobDto createJob(Checklist checklist, CreateJobRequest createJobRequest, PrincipalUser principalUser, Facility facility, boolean isScheduled,
                           Scheduler scheduler, Long nextExpectedStartDate) throws ResourceNotFoundException, StreemException, IOException, MultiStatusException {

    User principalUserEntity = userRepository.findById(principalUser.getId()).get();

    if (!State.Checklist.PUBLISHED.equals(checklist.getState())) {
      ValidationUtils.invalidate(createJobRequest.getChecklistId(), ErrorCode.PROCESS_NOT_PUBLISHED);
    }

    Job job = new Job();
    job.setId(IdGenerator.getInstance().nextId());
    job.setState(State.Job.UNASSIGNED);
    for (Stage stage : checklist.getStages()) {
      createTaskExecutions(job, stage, principalUserEntity);
    }

    if (isScheduled) {
      job.setScheduler(scheduler);
      job.setScheduled(true);
      Integer interval = scheduler.getDueDateInterval();
      job.setExpectedStartDate(nextExpectedStartDate);
      LocalDateTime expectedEndDate = DateTimeUtils.getLocalDateTime(nextExpectedStartDate).plusSeconds(interval);
      job.setExpectedEndDate(DateTimeUtils.getEpochTime(expectedEndDate));
    }

    job.setChecklist(checklist);
    job.setFacility(facility);
    job.setOrganisation(checklist.getOrganisation());
    job.setUseCase(checklist.getUseCase());
    job.setUseCaseId(checklist.getUseCase().getId());
    job.setCode(codeService.getCode(Type.EntityType.JOB, facility.getOrganisation().getId()));
    job.setState(State.Job.UNASSIGNED);
    job.setCreatedBy(principalUserEntity);
    job.setModifiedBy(principalUserEntity);

    Map<Long, List<PartialEntityObject>> relationsRequestMap = createJobRequest.getRelations();

    job = jobRepository.save(job);

    checklistRelationService.checklistRelationService(relationsRequestMap, checklist, job, principalUserEntity);

    // Execute rules temporarily, create job parameter values and validate if all mandatory parameters are provided
    Map<Long, ParameterExecuteRequest> parameterValues = createJobRequest.getParameterValues();
    List<Parameter> parameters = parameterRepository.getParametersByChecklistIdAndTargetEntityType(checklist.getId(), Type.ParameterTargetEntityType.PROCESS);
    List<ParameterDto> parameterDtos = new ArrayList<>();

    RuleHideShowDto tempRuleHideShow = parameterExecutionService.tempExecuteRules(createJobRequest.getParameterValues(), checklist.getId());
    Set<String> hideParameterSet = tempRuleHideShow.getHide();
    List<ParameterValue> jobParameterValueList = new ArrayList<>();

    for (Parameter parameter : parameters) {
      ParameterValue jobParameterValue = createParameterValue(job, parameter, principalUserEntity);
      jobParameterValueList.add(jobParameterValue);

      if (!parameterValues.containsKey(parameter.getId()) && parameter.isMandatory() && !hideParameterSet.contains(parameter.getId().toString())) {
        ValidationUtils.invalidate(parameter.getId(), ErrorCode.MANDATORY_PARAMETER_VALUES_NOT_PROVIDED);
      }

      parameterValueRepository.saveAll(jobParameterValueList);
    }

    // TODO refactor
    jobLogService.createJobLog(job.getIdAsString(), job.getCode(), job.getState(), job.getCreatedAt(), userMapper.toUserAuditDto(principalUser), checklist.getIdAsString(),
      checklist.getName(), checklist.getCode(), facility.getIdAsString(), principalUser);
    for (Parameter parameter : parameters) {
      if (parameterValues.containsKey(parameter.getId())) {
        ParameterExecuteRequest parameterExecuteRequest = parameterValues.get(parameter.getId());
        parameterExecuteRequest.setJobId(job.getId());
        ParameterDto parameterDto = parameterExecutionService.executeParameter(job.getId(), parameterExecuteRequest, false, Type.JobLogTriggerType.PROCESS_PARAMETER_VALUE, principalUser);
        parameterDtos.add(parameterDto);
      }
    }

    // TODO temporary workaround, getting a lazy initialization exception here
    JobDto jobDto = new JobDto();
    if (!isScheduled) {
      jobDto = jobMapper.toDto(job);
      jobDto.setParameterValues(parameterDtos);
    }

    stageReportService.registerStagesForJob(checklist.getId(), job.getId());
    jobAuditService.createJob(job.getIdAsString(), principalUser);

    // we are updating job log state inside this method, hence this must be called after job audits and job log creation
    assignDefaultUsersToJob(checklist, job, facility, principalUser);
    jobLogService.updateJobState(job.getIdAsString(), principalUser, job.getState());

    return jobDto;
  }

  private void createTaskExecutions(Job job, Stage stage, User principalUserEntity) {
    for (Task task : stage.getTasks()) {
      job.addTaskExecution(createTaskExecution(job, task, principalUserEntity));
      createParameterValues(job, task, principalUserEntity);
    }
  }

  private void createParameterValues(Job job, Task task, User principalUserEntity) {
    for (Parameter parameter : task.getParameters()) {
      job.addParameterValue(createParameterValue(job, parameter, principalUserEntity));
    }
  }

  private TaskExecution createTaskExecution(Job job, Task task, User principalUserEntity) {
    TaskExecution taskExecution = new TaskExecution();
    taskExecution.setId(IdGenerator.getInstance().nextId());
    taskExecution.setJob(job);
    taskExecution.setTask(task);
    taskExecution.setCreatedBy(principalUserEntity);
    taskExecution.setModifiedBy(principalUserEntity);
    taskExecution.setState(State.TaskExecution.NOT_STARTED);
    return taskExecution;
  }

  private ParameterValue createParameterValue(Job job, Parameter parameter, User principalUserEntity) {
    ParameterValue parameterValue = new ParameterValue();
    parameterValue.setJob(job);
    parameterValue.setParameter(parameter);
    parameterValue.setHidden(parameter.isHidden());
    parameterValue.setState(State.ParameterExecution.NOT_STARTED);
    parameterValue.setCreatedBy(principalUserEntity);
    return parameterValue;
  }

  private void assignDefaultUsersToJob(Checklist checklist, Job job, Facility facility, PrincipalUser principalUser) throws StreemException, MultiStatusException, ResourceNotFoundException {
    var defaultUsers = checklistDefaultUsersRepository.findUserIdsByChecklistIdAndFacilityId(checklist.getId(), facility.getId());
    if (!Utility.isEmpty(defaultUsers)) {
      var jobId = job.getId();
      for (var userId : defaultUsers) {
        var taskIds = checklistDefaultUsersRepository.findTaskIdsByChecklistIdAndUserIdAndFacilityId(checklist.getId(), userId, facility.getId());
        var assignedUser = new HashSet<Long>();
        assignedUser.add(userId);
        var bulkAssignmentRequest = new TaskExecutionAssignmentRequest(getDefaultUserTaskExecutionId(taskIds, job), assignedUser, new HashSet<>());
        jobAssignmentService.assignUsers(jobId, bulkAssignmentRequest, false, principalUser);
      }
    }
  }

  private Set<Long> getDefaultUserTaskExecutionId(Set<String> taskIds, Job job) {
    return job.getTaskExecutions().stream()
      .filter(taskExecution -> taskIds.contains(taskExecution.getTask().getId().toString()))
      .map(BaseEntity::getId)
      .collect(Collectors.toSet());
  }
}
