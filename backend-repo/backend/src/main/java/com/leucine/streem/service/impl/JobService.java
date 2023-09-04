package com.leucine.streem.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leucine.commons.id.IdGenerator;
import com.leucine.streem.collections.JobLog;
import com.leucine.streem.constant.*;
import com.leucine.streem.dto.*;
import com.leucine.streem.dto.mapper.*;
import com.leucine.streem.dto.projection.*;
import com.leucine.streem.dto.request.CreateJobRequest;
import com.leucine.streem.dto.request.JobCweDetailRequest;
import com.leucine.streem.dto.request.TaskExecutionAssignmentRequest;
import com.leucine.streem.dto.request.UpdateJobRequest;
import com.leucine.streem.dto.response.Error;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.exception.ExceptionType;
import com.leucine.streem.exception.MultiStatusException;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.*;
import com.leucine.streem.model.helper.BaseEntity;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.model.helper.SpecificationBuilder;
import com.leucine.streem.model.helper.parameter.ShouldBeParameter;
import com.leucine.streem.model.helper.search.SearchCriteria;
import com.leucine.streem.repository.*;
import com.leucine.streem.service.*;
import com.leucine.streem.util.DateTimeUtils;
import com.leucine.streem.util.JsonUtils;
import com.leucine.streem.util.Utility;
import com.leucine.streem.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService implements IJobService {
  private static final String JOB_ASSIGNEE_PATH = "taskExecutions.assignees.user.id";

  private final IParameterValueRepository parameterValueRepository;
  private final IJobRepository jobRepository;
  private final IJobMapper jobMapper;
  private final ITaskExecutionRepository taskExecutionRepository;
  private final IUserRepository userRepository;
  private final IJobCweService jobCweService;
  private final ITaskExecutionAssigneeRepository taskExecutionAssigneeRepository;
  private final IParameterRepository parameterRepository;
  private final ITempParameterValueRepository tempParameterValueRepository;
  private final IJobAuditService jobAuditService;
  private final IStageReportService stageReportService;
  private final IStageRepository stageRepository;
  private final IStageMapper stageMapper;
  private final ITaskMapper taskMapper;
  private final ITaskExecutionService taskExecutionService;
  private final IUserMapper userMapper;
  private final JobLogService jobLogService;
  private final IFacilityRepository facilityRepository;
  private final IParameterMapper parameterMapper;
  private final IParameterVerificationRepository parameterVerificationRepository;
  private final ITaskExecutionTimerService taskExecutionTimerService;
  private final ICreateJobService createJobService;
  private final IJobAssignmentService jobAssignmentService;


  //TODO add stats object if required, applies everywhere in job execution
  @Override
  public JobDto getJobById(Long jobId) throws ResourceNotFoundException {
    log.info("[getJobById] Request to get job, jobId: {}", jobId);
    Job job = jobRepository.readById(jobId).orElseThrow(() -> new ResourceNotFoundException(jobId, ErrorCode.JOB_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    List<ParameterValue> parameterValues = parameterValueRepository.readByJobId(jobId);
    List<TaskExecution> taskExecutionList = taskExecutionRepository.readByJobId(jobId);
    Map<Long, List<TaskPauseReasonOrComment>> pauseCommentsOrReason = taskExecutionTimerService.calculateDurationAndReturnReasonsOrComments(taskExecutionList);
    Map<Long, ParameterValue> taskParameterValuesMap = new HashMap<>();
    Map<Long, ParameterValue> jobParameterValuesMap = new HashMap<>();
    Set<Parameter> jobParameters = new HashSet<>();
    for (ParameterValue av : parameterValues) {
      var parameter = av.getParameter();
      if (Type.ParameterTargetEntityType.TASK.equals(av.getParameter().getTargetEntityType())) {
        taskParameterValuesMap.put(parameter.getId(), av);
      } else {
        jobParameters.add(parameter);
        jobParameterValuesMap.put(parameter.getId(), av);
      }
    }
    Map<Long, TaskExecution> taskExecutionMap = taskExecutionList.stream().collect(Collectors.toMap(te -> te.getTask().getId(), Function.identity()));
    List<TempParameterValue> tempParameterValues = tempParameterValueRepository.readByJobId(jobId);

    Map<Long, TempParameterValue> tempParameterValueMap = tempParameterValues.stream().collect(Collectors.toMap(av -> av.getParameter().getId(), Function.identity()));
    Map<Long, List<ParameterVerification>> parameterVerificationMap = attachParameterVerificationsData(jobId);
    JobDto jobDto = jobMapper.toDto(job, taskParameterValuesMap, taskExecutionMap, tempParameterValueMap, pauseCommentsOrReason, parameterVerificationMap);
    List<ParameterDto> parameterDtos = parameterMapper.toDto(jobParameters, jobParameterValuesMap, null, null, new HashMap<>(), new HashMap<>());
    jobDto.setParameterValues(parameterDtos);
    return jobDto;
  }

  @Override
  public Page<JobPartialDto> getAllJobs(String objectId, String filters, Pageable pageable) {
    log.info("[getAllJobs] Request to get all jobs, filters: {}, pageable: {}", filters, pageable);

    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    SearchCriteria organisationSearchCriteria = (new SearchCriteria()).setField(Job.ORGANISATION_ID).setOp(Operator.Search.EQ.toString()).setValues(Collections.singletonList(principalUser.getOrganisationId()));
    SearchCriteria facilitySearchCriteria = null;
    Long currentFacilityId = principalUser.getCurrentFacilityId();
    if (currentFacilityId != null && !currentFacilityId.equals(Misc.ALL_FACILITY_ID)) {
      facilitySearchCriteria =
        (new SearchCriteria()).setField(Job.FACILITY_ID).setOp(Operator.Search.EQ.toString()).setValues(Collections.singletonList(currentFacilityId));
    }

    /*--Fetch JobsIds wrt Specification--*/
    Specification<Job> specification;

    if (!Utility.isEmpty(objectId)) {
      Set<Long> jobIds = getJobIdsHavingObjectInChoices(objectId);
      SearchCriteria jobIdsCriteria = (new SearchCriteria()).setField(Job.ID).setOp(Operator.Search.ANY.toString()).setValues(new ArrayList<>(jobIds));
      if (Utility.isEmpty(jobIds)) {
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
      }
      specification = SpecificationBuilder.createSpecification(filters, Arrays.asList(organisationSearchCriteria, facilitySearchCriteria, jobIdsCriteria));
    } else {
      specification = SpecificationBuilder.createSpecification(filters, Arrays.asList(organisationSearchCriteria, facilitySearchCriteria));
    }
    Page<Job> jobPage = jobRepository.findAll(specification, pageable);

    /*--Fetch Jobs Data wrt JobIds--*/
    Set<Long> ids = jobPage.getContent()
      .stream().map(BaseEntity::getId).collect(Collectors.toSet());
    List<Job> jobs = jobRepository.readAllByIdIn(ids, pageable.getSort());

    List<JobPartialDto> jobDtoList = jobMapper.jobToJobPartialDto(jobs, getJobAssignees(ids), getTasksCount(ids));

    return new PageImpl<>(jobDtoList, pageable, jobPage.getTotalElements());
  }

  @Override
  public Page<JobPartialDto> getAllJobsCount(String objectId, String filters, Pageable pageable) {
    log.info("[getAllJobs] Request to get all jobs count, filters: {}, pageable: {}", filters, pageable);

    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    SearchCriteria organisationSearchCriteria = (new SearchCriteria()).setField(Job.ORGANISATION_ID).setOp(Operator.Search.EQ.toString()).setValues(Collections.singletonList(principalUser.getOrganisationId()));
    SearchCriteria facilitySearchCriteria = null;
    Long currentFacilityId = principalUser.getCurrentFacilityId();
    if (currentFacilityId != null && !currentFacilityId.equals(Misc.ALL_FACILITY_ID)) {
      facilitySearchCriteria =
        (new SearchCriteria()).setField(Job.FACILITY_ID).setOp(Operator.Search.EQ.toString()).setValues(Collections.singletonList(currentFacilityId));
    }

    /*--Fetch JobsIds wrt Specification--*/
    Specification<Job> specification;

    if (!Utility.isEmpty(objectId)) {
      Set<Long> jobIds = getJobIdsHavingObjectInChoices(objectId);
      SearchCriteria jobIdsCriteria = (new SearchCriteria()).setField(Job.ID).setOp(Operator.Search.ANY.toString()).setValues(new ArrayList<>(jobIds));
      if (Utility.isEmpty(jobIds)) {
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
      }
      specification = SpecificationBuilder.createSpecification(filters, Arrays.asList(organisationSearchCriteria, facilitySearchCriteria, jobIdsCriteria));
    } else {
      specification = SpecificationBuilder.createSpecification(filters, Arrays.asList(organisationSearchCriteria, facilitySearchCriteria));
    }

    Page<Job> jobPage = jobRepository.findAll(specification, pageable);

    return new PageImpl<>(Collections.emptyList(), pageable, jobPage.getTotalElements());
  }

  @Override
  public Page<JobPartialDto> getJobsAssignedToMe(String objectId, String filters, Pageable pageable) {
    log.info("[getJobsAssignedToMe] Request to get jobs assigned to logged in user, filters: {}, pageable: {}", filters, pageable);

    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    SearchCriteria organisationSearchCriteria = (new SearchCriteria()).setField(Job.ORGANISATION_ID).setOp(Operator.Search.EQ.toString()).setValues(Collections.singletonList(principalUser.getOrganisationId()));
    SearchCriteria facilitySearchCriteria = null;
    Long currentFacilityId = principalUser.getCurrentFacilityId();
    if (currentFacilityId != null && !currentFacilityId.equals(Misc.ALL_FACILITY_ID)) {
      facilitySearchCriteria =
        (new SearchCriteria()).setField(Job.FACILITY_ID).setOp(Operator.Search.EQ.toString()).setValues(Collections.singletonList(currentFacilityId));
    }

    SearchCriteria mandatorySearchCriteria = new SearchCriteria()
      .setField(JOB_ASSIGNEE_PATH)
      .setOp(Operator.Search.ANY.toString())
      .setValues(Collections.singletonList(principalUser.getId()));


    /*--Fetch JobsIds wrt Specification--*/
    Specification<Job> specification;

    if (!Utility.isEmpty(objectId)) {
      Set<Long> jobIds = getJobIdsHavingObjectInChoices(objectId);
      SearchCriteria jobIdsCriteria = (new SearchCriteria()).setField(Job.ID).setOp(Operator.Search.ANY.toString()).setValues(new ArrayList<>(jobIds));
      if (Utility.isEmpty(jobIds)) {
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
      }
      specification = SpecificationBuilder.createSpecification(filters, Arrays.asList(mandatorySearchCriteria, organisationSearchCriteria, facilitySearchCriteria, jobIdsCriteria));
    } else {
      specification = SpecificationBuilder.createSpecification(filters, Arrays.asList(mandatorySearchCriteria, organisationSearchCriteria, facilitySearchCriteria));
    }

    Page<Job> jobPage = jobRepository.findAll(specification, pageable);

    /*--Fetch Jobs Data wrt JobIds--*/
    Set<Long> ids = jobPage.getContent()
      .stream().map(BaseEntity::getId).collect(Collectors.toSet());
    List<Job> jobs = jobRepository.readAllByIdIn(ids, pageable.getSort());

    List<JobPartialDto> jobDtoList = jobMapper.jobToJobPartialDto(jobs, getJobAssignees(ids), getTasksCount(ids));

    return new PageImpl<>(jobDtoList, pageable, jobPage.getTotalElements());
  }

  @Override
  public CountDto getJobsAssignedToMeCount(String objectId, String filters) {
    log.info("[getJobsAssignedToMeCount] Request to get jobs count assigned to logged in user, filters: {}", filters);

    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    SearchCriteria organisationSearchCriteria = (new SearchCriteria()).setField(Job.ORGANISATION_ID).setOp(Operator.Search.EQ.toString()).setValues(Collections.singletonList(principalUser.getOrganisationId()));
    SearchCriteria facilitySearchCriteria = null;
    Long currentFacilityId = principalUser.getCurrentFacilityId();
    if (!Misc.ALL_FACILITY_ID.equals(currentFacilityId)) {
      facilitySearchCriteria =
        (new SearchCriteria()).setField(Job.FACILITY_ID).setOp(Operator.Search.EQ.toString()).setValues(Collections.singletonList(currentFacilityId));
    }

    SearchCriteria mandatorySearchCriteria = new SearchCriteria()
      .setField(JOB_ASSIGNEE_PATH)
      .setOp(Operator.Search.ANY.toString())
      .setValues(Collections.singletonList(principalUser.getId()));

    Specification<Job> specification;

    if (!Utility.isEmpty(objectId)) {
      Set<Long> jobIds = getJobIdsHavingObjectInChoices(objectId);
      SearchCriteria jobIdsCriteria = (new SearchCriteria()).setField(Job.ID).setOp(Operator.Search.ANY.toString()).setValues(new ArrayList<>(jobIds));
      if (Utility.isEmpty(jobIds)) {
        CountDto countDto = new CountDto();
        countDto.setCount(String.valueOf(0));
        return countDto;
      }
      specification = SpecificationBuilder.createSpecification(filters, Arrays.asList(mandatorySearchCriteria, organisationSearchCriteria, facilitySearchCriteria, jobIdsCriteria));
    } else {
      specification = SpecificationBuilder.createSpecification(filters, Arrays.asList(mandatorySearchCriteria, organisationSearchCriteria, facilitySearchCriteria));
    }

    long jobsCount = jobRepository.count(specification);

    CountDto countDto = new CountDto();
    countDto.setCount(String.valueOf(jobsCount));
    return countDto;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public JobDto createJob(CreateJobRequest createJobRequest) throws ResourceNotFoundException, StreemException, MultiStatusException, IOException {
    log.info("[createJob] Request to create a job, createJobRequest: {}", createJobRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long currentFacilityId = principalUser.getCurrentFacilityId();
    Facility facility = facilityRepository.getOne(currentFacilityId);
    if (Objects.equals(principalUser.getCurrentFacilityId(), null)) {
      ValidationUtils.invalidate(principalUser.getId(), ErrorCode.JOB_CANNOT_BE_CREATED_FROM_ALL_FACILITY);
    }
    return createJobService.createJob(createJobRequest, principalUser, facility, false, null, null);
  }

  @Override
  public BasicDto updateJob(Long jobId, UpdateJobRequest updateJobRequest) throws ResourceNotFoundException, StreemException {
    log.info("[updateJob] Request to update job, jobId: {}, updateJobRequest: {}", jobId, updateJobRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getReferenceById(principalUser.getId());

    Job job = jobRepository.findById(jobId).orElseThrow(() -> new ResourceNotFoundException(jobId, ErrorCode.JOB_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    validateJobState(jobId, Action.Job.UPDATE, job.getState());

    if (!Utility.isNull(updateJobRequest.getExpectedStartDate())) {
      job.setExpectedStartDate(updateJobRequest.getExpectedStartDate());
    }

    if (!Utility.isNull(updateJobRequest.getExpectedEndDate())) {
      job.setExpectedEndDate(updateJobRequest.getExpectedEndDate());
    }

    if (DateTimeUtils.isDateAfter(job.getExpectedStartDate(), job.getExpectedEndDate())) {
      ValidationUtils.invalidate(jobId, ErrorCode.JOB_EXPECTED_START_DATE_CANNOT_BE_AFTER_EXPECTED_END_DATE);
    }

    if (DateTimeUtils.isDateInPast(job.getExpectedStartDate())) {
      ValidationUtils.invalidate(jobId, ErrorCode.JOB_EXPECTED_START_DATE_CANNOT_BE_A_PAST_DATE);
    }

    if (DateTimeUtils.isDateInPast(job.getExpectedEndDate())) {
      ValidationUtils.invalidate(jobId, ErrorCode.JOB_EXPECTED_END_DATE_CANNOT_BE_A_PAST_DATE);
    }

    job.setModifiedAt(DateTimeUtils.now());
    job.setModifiedBy(principalUserEntity);
    jobRepository.save(job);

    jobLogService.updateJobState(job.getIdAsString(), principalUser, job.getState());
    BasicDto basicDto = new BasicDto();
    basicDto.setMessage("success");
    return basicDto;
  }

  @Override
  @Transactional
  public JobInfoDto startJob(Long jobId) throws ResourceNotFoundException, StreemException {
    log.info("[startJob] Request to start job, jobId: {}", jobId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Job job = jobRepository.findById(jobId).orElseThrow(() -> new ResourceNotFoundException(jobId, ErrorCode.JOB_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Checklist checklist = job.getChecklist();
    validateIfUserIsAssignedToExecuteJob(jobId, principalUser.getId());
    validateJobState(jobId, Action.Job.START, job.getState());

    job.setStartedAt(DateTimeUtils.now());
    job.setStartedBy(principalUserEntity);
    job.setState(State.Job.IN_PROGRESS);
    job.setModifiedBy(principalUserEntity);
    JobInfoDto jobDto = jobMapper.toJobInfoDto(jobRepository.save(job), principalUser);

    jobAuditService.startJob(jobDto, principalUser);
    UserAuditDto userAuditDto = userMapper.toUserAuditDto(principalUserEntity);
    jobLogService.recordJobLogTrigger(job.getIdAsString(), JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.JOB_STARTED_BY, JobLogMisc.JOB, null,
      Utility.getFullNameFromPrincipalUser(principalUser), Utility.getFullNameFromPrincipalUser(principalUser), userAuditDto);
    jobLogService.recordJobLogTrigger(job.getIdAsString(), JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.JOB_START_TIME, JobLogMisc.JOB, null,
      String.valueOf(job.getStartedAt()), String.valueOf(job.getStartedAt()), userAuditDto);
    jobLogService.updateJobState(String.valueOf(jobId), principalUser, job.getState());
    return jobDto;
  }

  @Override
  public JobInfoDto completeJob(Long jobId) throws ResourceNotFoundException, StreemException {
    log.info("[completeJob] Request to complete job, jobId: {}", jobId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Job job = jobRepository.findById(jobId).orElseThrow(() -> new ResourceNotFoundException(jobId, ErrorCode.JOB_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    JobInfoDto jobDto = completeJob(job, principalUser, principalUserEntity);

    if (job.isScheduled()) {
      try {
        createJobService.createScheduledJob(job.getSchedulerId(), job.getExpectedStartDate());
      } catch (Exception ex) {
        log.error("[completeJobWithException] error creating a scheduled job", ex);
      }
    }
    return jobDto;
  }

  @Transactional(rollbackFor = Exception.class)
  public JobInfoDto completeJob(Job job, PrincipalUser principalUser, User principalUserEntity) throws StreemException {
    validateJobState(job.getId(), Action.Job.COMPLETE, job.getState());
    validateMandatoryParametersIncomplete(job.getId());
    validateIfTasksAreCompleted(job.getId());
    job.setEndedAt(DateTimeUtils.now());
    job.setEndedBy(principalUserEntity);
    job.setModifiedBy(principalUserEntity);
    job.setState(State.Job.COMPLETED);
    JobInfoDto jobDto = jobMapper.toJobInfoDto(jobRepository.save(job), principalUser);

    stageReportService.unregisterStagesForJob(job.getId());
    jobAuditService.completeJob(jobDto, principalUser);
    jobLogService.recordJobLogTrigger(job.getIdAsString(), JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.JOB_ENDED_BY, JobLogMisc.JOB, null,
      Utility.getFullNameFromPrincipalUser(principalUser), Utility.getFullNameFromPrincipalUser(principalUser), userMapper.toUserAuditDto(principalUserEntity));
    jobLogService.recordJobLogTrigger(job.getIdAsString(), JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.JOB_END_TIME, JobLogMisc.JOB, null,
      String.valueOf(job.getEndedAt()), String.valueOf(job.getEndedAt()), userMapper.toUserAuditDto(principalUserEntity));
    jobLogService.updateJobState(String.valueOf(job.getId()), principalUser, job.getState());

    return jobDto;
  }

  private void validateIfTasksAreCompleted(Long jobId) throws StreemException {
    final var taskIds = taskExecutionRepository.findNonCompletedTasksByJobId(jobId);
    final List<Error> errorList = new ArrayList<>();
    taskIds
      .forEach(taskId -> ValidationUtils.addError(taskId, errorList, ErrorCode.TASK_INCOMPLETE));
    if (!Utility.isEmpty(errorList)) {
      ValidationUtils.invalidate(ErrorMessage.TASKS_INCOMPLETE, errorList);
    }
  }

  @Override
  public JobInfoDto completeJobWithException(Long jobId, JobCweDetailRequest jobCweDetailRequest) throws ResourceNotFoundException, StreemException {
    log.info("[completeJobWithException] Request to complete job with exception, jobId: {}, jobCweDetailRequest: {}", jobId, jobCweDetailRequest);
    if (Utility.isEmpty(jobCweDetailRequest.getComment())) {
      ValidationUtils.invalidate(jobId, ErrorCode.COMMENT_TO_COMPLETE_JOB_WITH_EXCEPTION_CANNOT_BE_EMPTY);
    }

    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Job job = jobRepository.findById(jobId).orElseThrow(() -> new ResourceNotFoundException(jobId, ErrorCode.JOB_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    JobInfoDto jobInfoDto =  completeJobWithException(job, principalUserEntity, jobCweDetailRequest, principalUser);

    if (job.isScheduled()) {
      try {
        createJobService.createScheduledJob(job.getSchedulerId(), job.getExpectedStartDate());
      } catch (Exception ex) {
        log.error("[completeJobWithException] error creating a scheduled job", ex);
      }
    }

    return jobInfoDto;
  }

  // TODO this is a workaround, some strange behaviour transactional instance isn't getting passed to createScheduledJob
  // so we are creating a new transactional instance here and created a separate method
  @Transactional(rollbackFor = Exception.class)
  public JobInfoDto completeJobWithException(Job job, User principalUserEntity, JobCweDetailRequest jobCweDetailRequest, PrincipalUser principalUser) throws StreemException {
    validateJobState(job.getId(), Action.Job.COMPLETE_WITH_EXCEPTION, job.getState());
    validateIfTasksBelongToCompletedStates(job.getId());

    jobCweService.createJobCweDetail(jobCweDetailRequest, job, principalUserEntity);

    job.setState(State.Job.COMPLETED_WITH_EXCEPTION);
    job.setEndedAt(DateTimeUtils.now());
    job.setEndedBy(principalUserEntity);
    job.setModifiedBy(principalUserEntity);

    JobInfoDto jobInfoDto = jobMapper.toJobInfoDto(jobRepository.save(job), principalUser);

    stageReportService.unregisterStagesForJob(job.getId());
    jobAuditService.completeJobWithException(job.getId(), jobCweDetailRequest, principalUser);

    UserAuditDto userAuditDto = userMapper.toUserAuditDto(principalUserEntity);
    jobLogService.recordJobLogTrigger(job.getIdAsString(), JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.JOB_ENDED_BY, JobLogMisc.JOB, null,
      Utility.getFullNameFromPrincipalUser(principalUser), Utility.getFullNameFromPrincipalUser(principalUser), userAuditDto);
    jobLogService.recordJobLogTrigger(job.getIdAsString(), JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.JOB_END_TIME, JobLogMisc.JOB, null,
      String.valueOf(job.getEndedAt()), String.valueOf(job.getEndedAt()), userAuditDto);
    jobLogService.updateJobState(String.valueOf(job.getId()), principalUser, job.getState());

    return jobInfoDto;
  }

  @Override
  @Transactional
  public BasicDto bulkAssign(Long jobId, TaskExecutionAssignmentRequest taskExecutionAssignmentRequest, boolean notify) throws ResourceNotFoundException, StreemException, MultiStatusException {
    log.info("[bulkAssign] Request to bulk assign tasks, jobId: {}, taskExecutionAssignmentRequest: {}, notify: {}", jobId, taskExecutionAssignmentRequest, notify);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    jobAssignmentService.assignUsers(jobId, taskExecutionAssignmentRequest, notify, principalUser);
    BasicDto basicDto = new BasicDto();
    basicDto.setMessage("success");
    return basicDto;
  }

  private TaskExecutionAssignmentRequest assignments(List<Error> errorList, TaskExecutionAssignmentRequest taskExecutionAssignmentRequest, PrincipalUser principalUser) {
    User principalUserEntity = userRepository.getById(principalUser.getId());

    Set<Long> assignedIds = new HashSet<>();
    Set<Long> unassignedIds = new HashSet<>();

    var taskExecutionIds = taskExecutionAssignmentRequest.getTaskExecutionIds();

    List<TaskExecution> taskExecutions = taskExecutionRepository.findAllById(taskExecutionIds);
    Map<Long, TaskExecution> taskExecutionMap = taskExecutions.stream()
      .collect(Collectors.toMap(BaseEntity::getId, Function.identity()));

    List<TaskExecutionAssigneeBasicView> taskExecutionUserMappings;
    Map<Long, Set<Long>> userIdTaskExecutionIdsMapping = null;

    if (!Utility.isEmpty(taskExecutionAssignmentRequest.getAssignedUserIds())) {
      /*
        find users from `taskExecutionAssignmentRequest.getAssignedUserIds()`
        and the taskExecutions they are assigned to. This will be used to exclude
        them from adding into `assignees` list (i.e. they are already assigned to the task execution
      */
      taskExecutionUserMappings = taskExecutionAssigneeRepository
        .findByTaskExecutionIdInAndUserIdIn(taskExecutionIds, taskExecutionAssignmentRequest.getAssignedUserIds());
      userIdTaskExecutionIdsMapping = taskExecutionUserMappings.stream()
        .collect(Collectors.groupingBy(te -> Long.valueOf(te.getUserId()), Collectors.mapping(te -> Long.valueOf(te.getTaskExecutionId()), Collectors.toSet())));
    }

    List<TaskExecutionUserMapping> assignees = new ArrayList<>();
    for (Long taskExecutionId : taskExecutionIds) {
      TaskExecution taskExecution = taskExecutionMap.get(taskExecutionId);

      if (!State.TASK_COMPLETED_STATES.contains(taskExecution.getState())) {
        if (!Utility.isEmpty(taskExecutionAssignmentRequest.getAssignedUserIds())) {
          for (Long userId : taskExecutionAssignmentRequest.getAssignedUserIds()) {
            if (!(userIdTaskExecutionIdsMapping.containsKey(userId)
              && userIdTaskExecutionIdsMapping.get(userId).contains(taskExecutionId))) {
              assignedIds.add(userId);
              assignees.add(new TaskExecutionUserMapping(taskExecutionRepository.getById(taskExecutionId), userRepository.getById(userId), principalUserEntity));
            }
          }
        }

        if (!Utility.isEmpty(taskExecutionAssignmentRequest.getUnassignedUserIds())) {
          taskExecutionUserMappings = taskExecutionAssigneeRepository
            .findByTaskExecutionIdAndUserIdIn(taskExecutionId, taskExecutionAssignmentRequest.getUnassignedUserIds());

          for (TaskExecutionAssigneeBasicView taskExecutionAssigneeBasicView : taskExecutionUserMappings) {
            if (taskExecutionAssigneeBasicView.getIsActionPerformed()) {
              ValidationUtils.addError(taskExecutionId, taskExecutionAssigneeBasicView.getUserId(), errorList, ErrorCode.FAILED_TO_UNASSIGN_SINCE_USER_PERFORMED_ACTIONS_ON_TASK);
            } else if (State.TaskExecutionAssignee.SIGNED_OFF.equals(taskExecutionAssigneeBasicView.getAssigneeState())) {
              ValidationUtils.addError(taskExecutionId, taskExecutionAssigneeBasicView.getUserId(), errorList, ErrorCode.FAILED_TO_UNASSIGN_SINCE_USER_SIGNED_OFF_TASK);
            } else {
              unassignedIds.add(Long.valueOf(taskExecutionAssigneeBasicView.getUserId()));
            }
          }

          taskExecutionAssigneeRepository.unassignUsersByTaskExecutions(Collections.singleton(taskExecutionId), unassignedIds);
        }
      } else {
        //TODO: I don't think user id is required here, this was requested from UI
        for (Long userId : taskExecutionAssignmentRequest.getAssignedUserIds()) {
          ValidationUtils.addError(taskExecutionId, String.valueOf(userId), errorList, ErrorCode.TASK_COMPLETED_ASSIGNMENT_FAILED);
        }
        for (Long userId : taskExecutionAssignmentRequest.getUnassignedUserIds()) {
          ValidationUtils.addError(taskExecutionId, String.valueOf(userId), errorList, ErrorCode.TASK_COMPLETED_ASSIGNMENT_FAILED);
        }
      }
    }

    //Bulk save all the assignees
    if (!assignees.isEmpty()) {
      taskExecutionAssigneeRepository.saveAll(assignees);
    }
    return new TaskExecutionAssignmentRequest(taskExecutionIds, assignedIds, unassignedIds);
  }

  @Override
  public List<TaskExecutionAssigneeDetailsView> getAssignees(Long jobId) {
    return taskExecutionAssigneeRepository.findByJobId(jobId, taskExecutionRepository.getTaskExecutionCountByJobId(jobId));
  }

  @Override
  public JobReportDto getJobReport(Long jobId) throws ResourceNotFoundException, JsonProcessingException {
    Job job = jobRepository.findById(jobId).orElseThrow(() -> new ResourceNotFoundException(jobId, ErrorCode.JOB_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    JobReportDto jobReportDto = jobMapper.toJobReportDto(job);

    if (State.Job.COMPLETED_WITH_EXCEPTION.equals(job.getState())) {
      jobReportDto.setCweDetails(jobCweService.getJobCweDetail(jobId));
    }

    List<Stage> stages = stageRepository.findStagesByJobIdAndAllTaskExecutionStateIn(jobId, State.TASK_COMPLETED_STATES);
    List<StageReportDto> stageReportDtos = new ArrayList<>();

    Map<Long, StageReportDto> stageReportDtoMap = new HashMap<>();
    for (Stage stage : stages) {
      StageReportDto stageReportDto = stageMapper.toStageReportDto(stage);
      stageReportDtoMap.put(stage.getId(), stageReportDto);
      stageReportDtos.add(stageReportDto);
    }

    Set<Long> stageIds = stages.stream().map(BaseEntity::getId).collect(Collectors.toSet());

    List<TaskExecution> taskExecutions = taskExecutionRepository.findByJobIdAndStageIdIn(jobId, stageIds);
    Map<Long, TaskReportDto> taskReportDtoMap = new HashMap<>();

    List<Long> taskIds = taskExecutions.stream().map(te -> te.getTask().getId()).collect(Collectors.toList());
    List<ParameterValue> shouldBeParameterExecutions = parameterValueRepository.findByJobIdAndTaskIdParameterTypeIn(jobId, taskIds, Collections.singletonList(Type.Parameter.SHOULD_BE));
    Map<Long, List<ParameterValue>> taskIdShouldBeParameterValueMap = shouldBeParameterExecutions.stream()
      .collect(Collectors.groupingBy(av -> av.getParameter().getTask().getId(), Collectors.mapping(av -> av, Collectors.toList())));

    List<ParameterValue> yesNoParameterExecutions = parameterValueRepository.findByJobIdAndTaskIdParameterTypeIn(jobId, taskIds, Collections.singletonList(Type.Parameter.YES_NO));
    List<ParameterValue> noParameterExecutions = yesNoParameterExecutions.stream()
      .filter(parameterValue -> !Utility.isEmpty(parameterValue.getReason())).toList();
    Map<Long, List<ParameterValue>> taskIdNoParameterValueMap = noParameterExecutions.stream()
      .collect(Collectors.groupingBy(av -> av.getParameter().getTask().getId(), Collectors.mapping(av -> av, Collectors.toList())));

    long totalDuration = 0;
    int totalTasks = 0;
    //calculated throughout all the tasks
    int totalExceptionsInJob = 0;

    long minStartedAtForStage = Long.MAX_VALUE;
    long maxEndedAtForStage = Long.MIN_VALUE;

    Long totalStageDuration = null;
    Long currentStageId = null;
    Set<AssigneeSignOffDto> assignees = new HashSet<>();
    Map<String, Long> recentSignOffDetails = new HashMap<>();
    for (TaskExecution taskExecution : taskExecutions) {
      var taskExecutionUserMappings = taskExecution.getAssignees();
      for (var taskExecutionUserMapping : taskExecutionUserMappings) {
        setAssignees(assignees, recentSignOffDetails, taskExecutionUserMapping);
      }

      Task task = taskExecution.getTask();
      Stage stage = task.getStage();
      TaskReportDto taskReportDto = null;

      if (currentStageId == null) {
        currentStageId = stage.getId();
      } else if (!stage.getId().equals(currentStageId)) {
        if (totalStageDuration == null) {
          totalStageDuration = maxEndedAtForStage - minStartedAtForStage;
        } else {
          totalStageDuration += (maxEndedAtForStage - minStartedAtForStage);
        }
        stageReportDtoMap.get(currentStageId).setTotalDuration(maxEndedAtForStage - minStartedAtForStage);
        stageReportDtoMap.get(currentStageId).setAverageTaskCompletionDuration(totalDuration / totalTasks);
        currentStageId = stage.getId();
        totalDuration = 0;
        totalTasks = 0;
        minStartedAtForStage = Long.MAX_VALUE;
        maxEndedAtForStage = Long.MIN_VALUE;
      }
      totalTasks++;
      totalDuration += (taskExecution.getEndedAt() - taskExecution.getStartedAt());

      maxEndedAtForStage = maxEndedAtForStage < taskExecution.getEndedAt() ? taskExecution.getEndedAt() : maxEndedAtForStage;
      minStartedAtForStage = minStartedAtForStage > taskExecution.getStartedAt() ? taskExecution.getStartedAt() : minStartedAtForStage;

      UserAuditDto userAuditDto = userMapper.toUserAuditDto(taskExecution.getModifiedBy());
      UserAuditDto correctedByUserAuditDto = userMapper.toUserAuditDto(taskExecution.getCorrectedBy());

      if (State.TASK_EXECUTION_EXCEPTION_STATE.contains(taskExecution.getState()) || taskExecutionService.isInvalidTimedTaskCompletedState(task, taskExecution.getStartedAt(),
        taskExecution.getEndedAt()) || taskExecution.getCorrectionReason() != null) {
        if (taskReportDtoMap.containsKey(task.getId())) {
          taskReportDto = taskReportDtoMap.get(task.getId());
        } else {
          taskReportDto = taskMapper.toTaskReportDto(task);
          taskReportDtoMap.put(task.getId(), taskReportDto);
          stageReportDtoMap.get(stage.getId()).getTasks().add(taskReportDto);
        }
        totalExceptionsInJob++;
        stageReportDtoMap.get(currentStageId).setTotalTaskExceptions(stageReportDtoMap.get(stage.getId()).getTotalTaskExceptions() + 1);
      }

      if (State.TaskExecution.COMPLETED_WITH_EXCEPTION.equals(taskExecution.getState())) {
        taskReportDto.getExceptions().add(createTaskExceptionDtoForTask(taskExecution, userAuditDto, Type.TaskException.COMPLETED_WITH_EXCEPTION, null));
      } else if (State.TaskExecution.SKIPPED.equals(taskExecution.getState())) {
        taskReportDto.getExceptions().add(createTaskExceptionDtoForTask(taskExecution, userAuditDto, Type.TaskException.SKIPPED, null));
      } else if (taskExecution.getCorrectionReason() != null) {
        taskReportDto.getExceptions().add(createTaskExceptionDtoForTask(taskExecution, correctedByUserAuditDto, Type.TaskException.ERROR_CORRECTION, null));
      } else if (taskExecutionService.isInvalidTimedTaskCompletedState(task, taskExecution.getStartedAt(), taskExecution.getEndedAt())) {
        userAuditDto = userMapper.toUserAuditDto(taskExecution.getStartedBy());
        TaskTimerDto taskTimerDto = new TaskTimerDto()
          .setEndedAt(taskExecution.getEndedAt())
          .setStartedAt(taskExecution.getStartedAt())
          .setTimerOperator(task.getTimerOperator())
          .setMinPeriod(task.getMinPeriod())
          .setMaxPeriod(task.getMaxPeriod());

        taskReportDto.getExceptions().add(createTaskExceptionDtoForTask(taskExecution, userAuditDto, Type.TaskException.DURATION_EXCEPTION, taskTimerDto));
      }

      if (taskIdShouldBeParameterValueMap.containsKey(task.getId())) {
        for (ParameterValue parameterValue : taskIdShouldBeParameterValueMap.get(task.getId())) {
          boolean parameterDeviationFound = false;
          TaskExceptionDto taskExceptionDto = null;

          userAuditDto = userMapper.toUserAuditDto(parameterValue.getModifiedBy());

          if (!Utility.isEmpty(parameterValue.getValue())) {
            ShouldBeParameter shouldBeParameter = JsonUtils.readValue(parameterValue.getParameter().getData().toString(), ShouldBeParameter.class);
            Operator.Parameter operator = Operator.Parameter.valueOf(shouldBeParameter.getOperator());

            double lowerValue = Utility.isEmpty(shouldBeParameter.getLowerValue()) ? 0 : Double.parseDouble(shouldBeParameter.getLowerValue());
            double upperValue = Utility.isEmpty(shouldBeParameter.getUpperValue()) ? 0 : Double.parseDouble(shouldBeParameter.getUpperValue());
            double value = Utility.isEmpty(shouldBeParameter.getValue()) ? 0 : Double.parseDouble(shouldBeParameter.getValue());
            double userInput = Double.parseDouble(parameterValue.getValue());

            switch (operator) {
              case BETWEEN:
                if (userInput < lowerValue || userInput > upperValue) {
                  parameterDeviationFound = true;
                  taskExceptionDto = createTaskExceptionDtoForParameterDeviation(parameterValue, userAuditDto, userInput);
                }
                break;
              case EQUAL_TO:
                if (userInput != value) {
                  parameterDeviationFound = true;
                  taskExceptionDto = createTaskExceptionDtoForParameterDeviation(parameterValue, userAuditDto, userInput);
                }
                break;
              case LESS_THAN:
                if (userInput >= value) {
                  parameterDeviationFound = true;
                  taskExceptionDto = createTaskExceptionDtoForParameterDeviation(parameterValue, userAuditDto, userInput);
                }
                break;
              case LESS_THAN_EQUAL_TO:
                if (userInput > value) {
                  parameterDeviationFound = true;
                  taskExceptionDto = createTaskExceptionDtoForParameterDeviation(parameterValue, userAuditDto, userInput);
                }
                break;
              case MORE_THAN:
                if (userInput <= value) {
                  parameterDeviationFound = true;
                  taskExceptionDto = createTaskExceptionDtoForParameterDeviation(parameterValue, userAuditDto, userInput);
                }
                break;
              case MORE_THAN_EQUAL_TO:
                if (userInput < value) {
                  parameterDeviationFound = true;
                  taskExceptionDto = createTaskExceptionDtoForParameterDeviation(parameterValue, userAuditDto, userInput);
                }
                break;
            }
          }

          if (parameterDeviationFound) {
            if (taskReportDtoMap.containsKey(task.getId())) {
              taskReportDto = taskReportDtoMap.get(task.getId());
            } else {
              taskReportDto = taskMapper.toTaskReportDto(task);
              taskReportDtoMap.put(task.getId(), taskReportDto);
              stageReportDtoMap.get(stage.getId()).getTasks().add(taskReportDto);
            }
            totalExceptionsInJob++;
            stageReportDtoMap.get(stage.getId()).setTotalTaskExceptions(stageReportDtoMap.get(stage.getId()).getTotalTaskExceptions() + 1);
            taskReportDto.getExceptions().add(taskExceptionDto);
          }
        }
      }
      if (taskIdNoParameterValueMap.containsKey(task.getId())) {
        for (ParameterValue parameterValue : taskIdNoParameterValueMap.get(task.getId())) {
          userAuditDto = userMapper.toUserAuditDto(parameterValue.getModifiedBy());
          TaskExceptionDto taskExceptionDto = createTaskExceptionDtoForYesNoType(parameterValue, userAuditDto);
          if (taskReportDtoMap.containsKey(task.getId())) {
            taskReportDto = taskReportDtoMap.get(task.getId());
          } else {
            taskReportDto = taskMapper.toTaskReportDto(task);
            taskReportDtoMap.put(task.getId(), taskReportDto);
            stageReportDtoMap.get(stage.getId()).getTasks().add(taskReportDto);
          }
          totalExceptionsInJob++;
          stageReportDtoMap.get(stage.getId()).setTotalTaskExceptions(stageReportDtoMap.get(stage.getId()).getTotalTaskExceptions() + 1);
          taskReportDto.getExceptions().add(taskExceptionDto);
        }
      }
    }

    var assigneeSignOffDtos = getAssigneeSignOffDtos(assignees, recentSignOffDetails);
    jobReportDto.setAssignees(assigneeSignOffDtos);

    long totalStages = 0L;
    long taskCount = 0L;
    for (var stage : job.getChecklist().getStages()) {
      totalStages++;
      taskCount = taskCount + stage.getTasks().size();
    }
    jobReportDto.setTotalStages(totalStages);
    jobReportDto.setTotalTask(taskCount);

    if (currentStageId != null) {
      if (totalStageDuration == null) {
        totalStageDuration = maxEndedAtForStage - minStartedAtForStage;
      } else {
        totalStageDuration += (maxEndedAtForStage - minStartedAtForStage);
      }
      stageReportDtoMap.get(currentStageId).setTotalDuration(maxEndedAtForStage - minStartedAtForStage);
      stageReportDtoMap.get(currentStageId).setAverageTaskCompletionDuration(totalDuration / totalTasks);
    }

    if (State.JOB_COMPLETED_STATES.contains(job.getState())) {
      UserAuditDto userAuditDto = userMapper.toUserAuditDto(job.getModifiedBy());
      jobReportDto.setCompletedBy(userAuditDto);
      jobReportDto.setEndedBy(userAuditDto);

      if (job.getStartedAt() != null && job.getEndedAt() != null) {
        jobReportDto.setTotalDuration(job.getEndedAt() - job.getStartedAt());
      }
    }

    List<ParameterValue> jobParameterValues = parameterValueRepository.findByJobIdAndParameterTargetEntityTypeIn(jobId, Collections.singletonList(Type.ParameterTargetEntityType.PROCESS));
    Map<Long, ParameterValue> jobParameterValuesMap = new HashMap<>();
    Set<Parameter> jobParameters = new HashSet<>();
    for (ParameterValue av : jobParameterValues) {
      var parameter = av.getParameter();
      jobParameters.add(parameter);
      jobParameterValuesMap.put(parameter.getId(), av);
    }
    List<ParameterDto> parameterDtos = parameterMapper.toDto(jobParameters, jobParameterValuesMap, null, null, new HashMap<>(), null);
    jobReportDto.setParameterValues(parameterDtos);

    UserAuditDto userAuditDto = userMapper.toUserAuditDto(job.getCreatedBy());
    jobReportDto.setCreatedBy(userAuditDto);

    jobReportDto.setTotalStageDuration(totalStageDuration);
    jobReportDto.setTotalAssignees(taskExecutionAssigneeRepository.getJobAssigneesCount(jobId));
    jobReportDto.setTotalTaskExceptions(totalExceptionsInJob);
    jobReportDto.setStages(stageReportDtos);
    return jobReportDto;
  }

  @Override
  public JobInformationDto getJobInformation(Long jobId) throws ResourceNotFoundException {
    Job job = jobRepository.findById(jobId).orElseThrow(() -> new ResourceNotFoundException(jobId, ErrorCode.JOB_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    return jobMapper.toJobInformationDto(job);
  }

  private List<AssigneeSignOffDto> getAssigneeSignOffDtos(Set<AssigneeSignOffDto> assignees, Map<String, Long> recentSignOffDetails) {
    return assignees.stream().sorted(Comparator.comparing(u -> u.getFirstName() + u.getLastName()))
      .map(a -> {
        Long value = recentSignOffDetails.get(a.getId());
        a.setRecentSignOffAt(value);
        return a;
      }).toList();
  }

  private void setAssignees(Set<AssigneeSignOffDto> assignees, Map<String, Long> recentSignOffDetails, TaskExecutionUserMapping taskExecutionUserMapping) {
    var assigneeSignOffDto = userMapper.toAssigneeSignOffDto(taskExecutionUserMapping.getUser());
    assignees.add(assigneeSignOffDto);
    if (taskExecutionUserMapping.getState().equals(State.TaskExecutionAssignee.SIGNED_OFF)) {
      Long value = recentSignOffDetails.get(assigneeSignOffDto.getId());
      if (value == null || value < taskExecutionUserMapping.getModifiedAt()) {
        assigneeSignOffDto.setRecentSignOffAt(taskExecutionUserMapping.getModifiedAt());
        recentSignOffDetails.put(assigneeSignOffDto.getId(), taskExecutionUserMapping.getModifiedAt());
      }
    } else {
      if (!recentSignOffDetails.containsKey(assigneeSignOffDto.getId())) {
        recentSignOffDetails.put(assigneeSignOffDto.getId(), null);
      }
    }
  }

  @Override
  public JobReportDto printJobReport(Long jobId) throws ResourceNotFoundException, JsonProcessingException {
    var principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var jobReportDto = getJobReport(jobId);
    jobAuditService.printJobReport(jobReportDto, principalUser);
    return jobReportDto;
  }

  private TaskExceptionDto createTaskExceptionDtoForTask(TaskExecution taskExecution, UserAuditDto userAuditDto,
                                                         Type.TaskException taskExceptionType, TaskTimerDto timer) {
    TaskExceptionDto taskExceptionDto = new TaskExceptionDto();
    if (taskExceptionType.equals(Type.TaskException.ERROR_CORRECTION)) {
      taskExceptionDto.setRemark(taskExecution.getCorrectionReason());
    } else {
      taskExceptionDto.setRemark(taskExecution.getReason());

    }
    taskExceptionDto.setType(taskExceptionType.name());
    taskExceptionDto.setInitiator(userAuditDto);
    taskExceptionDto.setTimer(timer);
    return taskExceptionDto;
  }

  @Override
  public JobPrintDto printJob(Long jobId) throws ResourceNotFoundException {
    log.info("[printJob] Request to print job, jobId: {}", jobId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Job job = jobRepository.readById(jobId).orElseThrow(() -> new ResourceNotFoundException(jobId, ErrorCode.JOB_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    List<ParameterValue> parameterValues = parameterValueRepository.readByJobId(jobId);
    List<TaskExecution> taskExecutions = taskExecutionRepository.readByJobId(jobId);
    Map<Long, TaskExecution> taskExecutionMap = taskExecutions.stream().collect(Collectors.toMap(te -> te.getTask().getId(), Function.identity()));
    Map<Long, ParameterValue> taskParameterValuesMap = new HashMap<>();
    Map<Long, ParameterValue> jobParameterValuesMap = new HashMap<>();
    Set<Parameter> jobParameters = new HashSet<>();
    for (ParameterValue av : parameterValues) {
      var parameter = av.getParameter();
      if (Type.ParameterTargetEntityType.TASK.equals(av.getParameter().getTargetEntityType())) {
        taskParameterValuesMap.put(parameter.getId(), av);
      } else {
        jobParameters.add(parameter);
        jobParameterValuesMap.put(parameter.getId(), av);
      }
    }

    List<TempParameterValue> tempParameterValues = tempParameterValueRepository.readByJobId(jobId);
    Map<Long, TempParameterValue> tempParameterValueMap = tempParameterValues.stream().collect(Collectors.toMap(av -> av.getParameter().getId(), Function.identity()));
    Map<Long, List<ParameterVerification>> parameterVerificationMap = attachParameterVerificationsData(jobId);
    JobPrintDto jobPrintDto = jobMapper.toJobPrintDto(job, taskParameterValuesMap, taskExecutionMap, tempParameterValueMap, new HashMap<>(), parameterVerificationMap);

    Set<AssigneeSignOffDto> assignees = new HashSet<>();
    Map<String, Long> recentSignOffDetails = new HashMap<>();
    for (TaskExecution taskExecution : taskExecutions) {
      Set<TaskExecutionUserMapping> taskExecutionUserMappings = taskExecution.getAssignees();
      for (TaskExecutionUserMapping taskExecutionUserMapping : taskExecutionUserMappings) {
        setAssignees(assignees, recentSignOffDetails, taskExecutionUserMapping);
      }
    }
    var assigneeSignOffDtos = getAssigneeSignOffDtos(assignees, recentSignOffDetails);
    jobPrintDto.setAssignees(assigneeSignOffDtos);

    if (State.Job.COMPLETED_WITH_EXCEPTION.equals(job.getState())) {
      jobPrintDto.setCweDetails(jobCweService.getJobCweDetail(jobId));
    }

    if (State.JOB_COMPLETED_STATES.contains(job.getState()) && job.getStartedAt() != null && job.getEndedAt() != null) {
      jobPrintDto.setTotalDuration(job.getEndedAt() - job.getStartedAt());
    }
    long totalStages = 0L;
    long totalTasks = 0L;
    for (Stage stage : job.getChecklist().getStages()) {
      totalStages++;
      totalTasks = totalTasks + stage.getTasks().size();
    }
    jobPrintDto.setTotalStages(totalStages);
    jobPrintDto.setTotalTask(totalTasks);

    List<ParameterDto> parameterDtos = parameterMapper.toDto(jobParameters, jobParameterValuesMap, null, null, new HashMap<>(), new HashMap<>());
    jobPrintDto.setParameterValues(parameterDtos);


    jobAuditService.printJob(jobPrintDto, principalUser);
    return jobPrintDto;
  }

  private TaskExceptionDto createTaskExceptionDtoForParameterDeviation(ParameterValue parameterValue, UserAuditDto userAuditDto, double userInput) {
    TaskExceptionDto taskExceptionDto = new TaskExceptionDto();
    taskExceptionDto.setRemark(parameterValue.getReason());
    taskExceptionDto.setInitiator(userAuditDto);
    taskExceptionDto.setType(Type.TaskException.PARAMETER_DEVIATION.name());

    ParameterDeviationDto parameterDeviationDto = new ParameterDeviationDto();
    parameterDeviationDto.setParameter(parameterValue.getParameter().getData());
    parameterDeviationDto.setUserInput(userInput);

    taskExceptionDto.setParameterDeviation(parameterDeviationDto);

    return taskExceptionDto;
  }

  private TaskExceptionDto createTaskExceptionDtoForYesNoType(ParameterValue parameterValue, UserAuditDto userAuditDto) {
    TaskExceptionDto taskExceptionDto = new TaskExceptionDto();
    taskExceptionDto.setRemark(parameterValue.getReason());
    taskExceptionDto.setInitiator(userAuditDto);
    taskExceptionDto.setType(Type.TaskException.YES_NO.name());
    return taskExceptionDto;
  }

  //TODO State Management ?
  private void validateJobState(Long jobId, Action.Job action, State.Job state) throws StreemException {
    switch (action) {
      case START:
        if (State.Job.BLOCKED.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_IS_BLOCKED);
        }
        if (State.Job.UNASSIGNED.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.UNASSIGNED_JOB_CANNOT_BE_STARTED);
        }
        if (State.Job.IN_PROGRESS.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_ALREADY_STARTED);
        }
        if (State.Job.COMPLETED_WITH_EXCEPTION.equals(state) || State.Job.COMPLETED.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_ALREADY_COMPLETED);
        }
        break;
      case COMPLETE:
        if (State.Job.BLOCKED.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_IS_BLOCKED);
        }
        if (State.Job.COMPLETED.equals(state) || State.Job.COMPLETED_WITH_EXCEPTION.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_ALREADY_COMPLETED);
        }
        if (!State.Job.IN_PROGRESS.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_IS_NOT_IN_PROGRESS);
        }
        break;
      case COMPLETE_WITH_EXCEPTION:
        if (State.Job.COMPLETED.equals(state) || State.Job.COMPLETED_WITH_EXCEPTION.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_ALREADY_COMPLETED);
        }
        break;
      case BLOCKED:
        if (State.Job.COMPLETED.equals(state) || State.Job.COMPLETED_WITH_EXCEPTION.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_ALREADY_COMPLETED);
        }

        if (!State.Job.IN_PROGRESS.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_IS_NOT_IN_PROGRESS);
        }

        break;
      case IN_PROGRESS:
        if (State.Job.IN_PROGRESS.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_ALREADY_STARTED);
        }
        if (State.Job.COMPLETED.equals(state) || State.Job.COMPLETED_WITH_EXCEPTION.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_ALREADY_COMPLETED);
        }
        if (!State.Job.BLOCKED.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_IS_NOT_IN_BLOCKED);
        }
        break;
      case ASSIGN, UPDATE:
        if (State.JOB_COMPLETED_STATES.contains(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_ALREADY_COMPLETED);
        }
        break;
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

  /**
   * function checks if user is assigned to any of the tasks in the job
   */
  private void validateIfUserIsAssignedToExecuteJob(Long jobId, Long userId) throws StreemException {
    if (!taskExecutionAssigneeRepository.isUserAssignedToAnyTask(jobId, userId)) {
      ValidationUtils.invalidate(jobId, ErrorCode.USER_NOT_ASSIGNED_TO_EXECUTE_JOB);
    }
  }

  /**
   * function checks if all the mandatory parameters are completed.
   * Exclusion - Skipped and Completed Tasks
   *
   * @param jobId
   * @throws StreemException
   */
  private void validateMandatoryParametersIncomplete(Long jobId) throws StreemException {
    List<Error> errorList = new ArrayList<>();
    List<ParameterView> incompleteParameters = parameterRepository.findIncompleteParametersByJobId(jobId);
    if (!Utility.isEmpty(incompleteParameters)) {
      setIncompleteParameters(incompleteParameters, errorList);
      ValidationUtils.invalidate(ErrorMessage.MANDATORY_PARAMETERS_NOT_COMPLETED, errorList);
    }
  }

  private void setIncompleteParameters(List<ParameterView> parameterViews, List<Error> errors) {
    Set<Long> taskIds = new HashSet<>();
    Set<Long> parameterIds = new HashSet<>();

    for (ParameterView parameterView : parameterViews) {
      if (!taskIds.contains(parameterView.getTaskId())) {
        taskIds.add(parameterView.getTaskId());
        ValidationUtils.addError(parameterView.getTaskId(), errors, ErrorCode.TASK_INCOMPLETE);
      }
      if (!parameterIds.contains(parameterView.getParameterId())) {
        parameterIds.add(parameterView.getParameterId());
        ValidationUtils.addError(parameterView.getParameterId(), errors, ErrorCode.PARAMETER_INCOMPLETE);
      }
    }
  }

  private void validateIfTasksBelongToCompletedStates(Long jobId) throws StreemException {
    List<Long> taskIds = taskExecutionRepository.findNonCompletedTaskIdsByJobId(jobId);
    if (!Utility.isEmpty(taskIds)) {
      ValidationUtils.invalidate(jobId, ErrorCode.TASK_IN_PROGRESS);
    }
    taskIds = taskExecutionRepository.findEnabledForCorrectionTaskIdsByJobId(jobId);
    if (!Utility.isEmpty(taskIds)) {
      ValidationUtils.invalidate(jobId, ErrorCode.TASK_ENABLED_FOR_CORRECTION);
    }
  }

  @Override
  public JobStateDto getJobState(Long jobId) throws ResourceNotFoundException {
    log.info("[getJobState] Request to get job state, jobId: {}", jobId);
    Job job = jobRepository.findById(jobId).orElseThrow(() -> new ResourceNotFoundException(jobId, ErrorCode.JOB_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    return jobMapper.toJobStateDto(job);
  }

  @Override
  public Page<JobPartialDto> getAllByResource(String objectId, String filters, Pageable pageable) {
    log.info("[getAllByResource] Request to find all jobs by resource, objectTypeId: {}, filters: {}, pageable: {}", objectId, filters, pageable);

    // TODO URGENT this needs to be optimized
    Set<Long> jobIds = getJobIdsHavingObjectInChoices(objectId);

    if (!Utility.isEmpty(jobIds)) {
      PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      SearchCriteria organisationSearchCriteria = (new SearchCriteria()).setField(Job.ORGANISATION_ID).setOp(Operator.Search.EQ.toString()).setValues(Collections.singletonList(principalUser.getOrganisationId()));
      SearchCriteria jobIdsCriteria = (new SearchCriteria()).setField(Job.ID).setOp(Operator.Search.ANY.toString()).setValues(new ArrayList<>(jobIds));

      SearchCriteria facilitySearchCriteria = null;
      Long currentFacilityId = principalUser.getCurrentFacilityId();
      if (currentFacilityId != null && !currentFacilityId.equals(Misc.ALL_FACILITY_ID)) {
        facilitySearchCriteria =
          (new SearchCriteria()).setField(Job.FACILITY_ID).setOp(Operator.Search.EQ.toString()).setValues(Collections.singletonList(currentFacilityId));
      }

      /*--Fetch JobsIds wrt Specification--*/
      Specification<Job> specification = SpecificationBuilder.createSpecification(filters, Arrays.asList(organisationSearchCriteria, facilitySearchCriteria, jobIdsCriteria));
      Page<Job> jobPage = jobRepository.findAll(specification, pageable);

      Set<Long> ids = jobPage.getContent()
        .stream().map(BaseEntity::getId).collect(Collectors.toSet());
      List<Job> jobs = jobRepository.readAllByIdIn(ids, pageable.getSort());

      List<JobPartialDto> jobDtoList = jobMapper.jobToJobPartialDto(jobs, getJobAssignees(ids), getTasksCount(ids));

      return new PageImpl<>(jobDtoList, pageable, jobPage.getTotalElements());
    } else {
      return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }
  }

  private Map<String, List<JobAssigneeView>> getJobAssignees(Set<Long> jobIds) {
    List<JobAssigneeView> jobAssignees = taskExecutionAssigneeRepository.getJobAssignees(jobIds);
    return jobAssignees.stream().collect(Collectors.groupingBy(JobAssigneeView::getJobId));
  }

  private Map<String, TaskExecutionCountView> getTasksCount(Set<Long> jobIds) {
    List<TaskExecutionCountView> taskExecutionCountViewList = taskExecutionRepository.findCompletedAndTotalTaskExecutionCountByJobIds(jobIds);
    return taskExecutionCountViewList.stream().collect(Collectors.toMap(TaskExecutionCountView::getJobId, t -> t));
  }

  @Override
  public StageDetailsDto getStageData(Long jobId, Long stageId) throws ResourceNotFoundException {
    log.info("[getStageData] Request to get stage data, jobId: {}, stageId:{}", jobId, stageId);
    Job job = jobRepository.findById(jobId).orElseThrow(() -> new ResourceNotFoundException(jobId, ErrorCode.JOB_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Stage stage = stageRepository.readById(stageId).orElseThrow(() -> new ResourceNotFoundException(stageId, ErrorCode.STAGE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    StageDetailsDto stageDetailsDto = new StageDetailsDto();
    stageDetailsDto.setJobId(job.getIdAsString());
    stageDetailsDto.setJobState(job.getState());

    List<ParameterValue> parameterValues = parameterValueRepository.readByJobIdAndStageId(jobId, stageId);
    List<TaskExecution> taskExecutions = taskExecutionRepository.readByJobIdAndStageId(jobId, stageId);
    Map<Long, List<TaskPauseReasonOrComment>> pauseCommentsOrReason = taskExecutionTimerService.calculateDurationAndReturnReasonsOrComments(taskExecutions);


    Map<Long, ParameterValue> parameterValueMap = parameterValues.stream()
      .collect(Collectors.toMap(av -> av.getParameter().getId(), Function.identity()));
    Map<Long, TaskExecution> taskExecutionMap = taskExecutions.stream()
      .collect(Collectors.toMap(te -> te.getTask().getId(), Function.identity()));
    List<TempParameterValue> tempParameterValues = tempParameterValueRepository.readByJobIdAndStageId(jobId, stageId);
    Map<Long, TempParameterValue> tempParameterValueMap = tempParameterValues.stream()
      .collect(Collectors.toMap(av -> av.getParameter().getId(), Function.identity()));
    Map<Long, List<ParameterVerification>> parameterVerificationPeerAndSelf = attachParameterVerificationsData(jobId);
    stageDetailsDto.setStage(stageMapper.toDto(stage, parameterValueMap, taskExecutionMap, tempParameterValueMap, pauseCommentsOrReason, parameterVerificationPeerAndSelf));
    stageDetailsDto.setStageReports(stageReportService.getStageExecutionInfo(jobId));
    return stageDetailsDto;
  }

  @Override
  public boolean isJobExistsBySchedulerIdAndDateGreaterThanOrEqualToExpectedStartDate(Long schedulerId, Long epochDateTime) {
    return jobRepository.isJobExistsBySchedulerIdAndDateGreaterThanOrEqualToExpectedStartDate(schedulerId, epochDateTime);
  }

  @Override
  public Page<ShouldBeParameterStatusView> getShouldBeParameterStatus(String processName, String parameterName, Pageable pageable) {
    log.info("[getShouldBeParameterStatus] Request to get all should be parameters pending for approval, processName: {}, parameterName: {}", processName, parameterName);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    long facilityId = principalUser.getCurrentFacilityId();
    return jobRepository.getAllShouldBeParameterStatus(facilityId, "%" + parameterName + "%", "%" + processName + "%", pageable);

  }

  private void recordJobLogForRelations(JobDto jobDto, UserAuditDto userBasicInfoDto) {
    for (RelationValueDto relationValueDto : jobDto.getRelations()) {
      if (!Utility.isEmpty(relationValueDto.getTargets())) {
        var targets = relationValueDto.getTargets();
        StringBuilder value = new StringBuilder(targets.get(0).getExternalId());
        for (int i = 1; i < targets.size(); i++) {
          value.append(", ").append(targets.get(i).getExternalId());
        }
        jobLogService.recordJobLogTrigger(jobDto.getId(), relationValueDto.getId(), Type.JobLogTriggerType.RELATION_VALUE, relationValueDto.getDisplayName(), null, value.toString(), value.toString(), userBasicInfoDto);
      }
    }
  }

  /**
   * here we have created map of parameter value id and list of verifications with latest status of self and peer and through this map we have created map of parameterId and list of verifications
   */

  private Map<Long, List<ParameterVerification>> attachParameterVerificationsData(Long jobId) {
    List<ParameterVerification> parameterVerifications = parameterVerificationRepository.findByJobId(jobId);
    Map<Long, List<ParameterVerification>> parameterVerificationMap = new HashMap<>();
    Map<Long, Map<Type.VerificationType, ParameterVerification>> tempMap = new HashMap<>();

    if (Utility.isEmpty(parameterVerifications)) {
      return parameterVerificationMap;
    }

    for (ParameterVerification parameterVerification : parameterVerifications) {
      Long parameterValueId = parameterVerification.getParameterValueId();
      Type.VerificationType verificationType = parameterVerification.getVerificationType();

      Map<Type.VerificationType, ParameterVerification> typeMap = tempMap.computeIfAbsent(parameterValueId, k -> new HashMap<>());
      if (!typeMap.containsKey(verificationType) || parameterVerification.getModifiedAt() > typeMap.get(verificationType).getModifiedAt()) {
        typeMap.put(verificationType, parameterVerification);
      }
    }

    for (Map.Entry<Long, Map<Type.VerificationType, ParameterVerification>> entry : tempMap.entrySet()) {
      parameterVerificationMap.put(entry.getKey(), new ArrayList<>(entry.getValue().values()));
    }
    return parameterVerificationMap;
  }

  private Set<Long> getJobIdsHavingObjectInChoices(String objectId) {
    String jsonChoices = String.format("""
      [
          {
              "objectId": "%s"
          }
      ]
      """, objectId);
    return parameterValueRepository.getJobIdsByTargetEntityTypeAndObjectInChoices(Type.ParameterTargetEntityType.PROCESS.name(), jsonChoices);
  }
}
