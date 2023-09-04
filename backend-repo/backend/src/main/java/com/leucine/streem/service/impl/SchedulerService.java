package com.leucine.streem.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.commons.id.IdGenerator;
import com.leucine.streem.constant.*;
import com.leucine.streem.dto.*;
import com.leucine.streem.dto.mapper.ISchedulerMapper;
import com.leucine.streem.dto.mapper.IUserMapper;
import com.leucine.streem.dto.request.CreateProcessSchedulerRequest;
import com.leucine.streem.dto.request.ParameterExecuteRequest;
import com.leucine.streem.dto.request.QuartzSchedulerRequest;
import com.leucine.streem.dto.request.UpdateSchedulerRequest;
import com.leucine.streem.dto.response.Error;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.exception.*;
import com.leucine.streem.model.*;
import com.leucine.streem.model.helper.IdCodeHolder;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.model.helper.SpecificationBuilder;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService implements ISchedulerService {

  private final IQuartzService quartzService;
  private final ISchedulerRepository schedulerRepository;
  private final IFacilityRepository facilityRepository;
  private final IUserRepository userRepository;
  private final IChecklistRepository checklistRepository;
  private final ICodeService codeService;
  private final IVersionService versionService;
  private final ISchedulerMapper schedulerMapper;
  private final IUserMapper userMapper;
  private final ICreateJobService createJobService;
  // TODO not right, update this
  private final TransactionTemplate transactionTemplate;

  /**
   * Create a scheduler and schedule a job for the scheduler created.
   * @param createProcessSchedulerRequest CreateProcessSchedulerRequest object containing the scheduler details to be created and job to be scheduled for the scheduler created.
   * @return SchedulerPartialDto object containing the scheduler details created.
   * @throws ResourceNotFoundException
   */
  @Override
  public SchedulerPartialDto createScheduler(CreateProcessSchedulerRequest createProcessSchedulerRequest) throws ResourceNotFoundException, StreemException, MultiStatusException, IOException {
    log.info("[createScheduler] Request to create a scheduler, createProcessSchedulerRequest: {}", createProcessSchedulerRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    List<Error> errorList = new ArrayList<>();
    Scheduler scheduler = transactionTemplate.execute(status -> {
      try {
        Long currentFacilityId = principalUser.getCurrentFacilityId();
        Facility facility = facilityRepository.getReferenceById(currentFacilityId);
        User principalUserEntity = userRepository.getReferenceById(principalUser.getId());

        Checklist checklist = checklistRepository
          .findById(createProcessSchedulerRequest.getChecklistId())
          .orElseThrow(() -> new ResourceNotFoundException(createProcessSchedulerRequest.getChecklistId(), ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

        return storeSchedulerAndScheduleJob(createProcessSchedulerRequest, null, checklist, facility, checklist.getUseCase(), errorList, principalUserEntity);
      } catch (Exception ex) {
        log.error("[createScheduler] error creating a scheduler", ex);
      }
      return null;
    });

    if (!Utility.isEmpty(errorList)) {
      ValidationUtils.invalidate("error creating a scheduler", errorList);
    }

    if (scheduler == null) {
      ValidationUtils.invalidate(ErrorCode.ERROR_CREATING_A_SCHEDULER.getDescription(), ErrorCode.ERROR_CREATING_A_SCHEDULER);
    }

    if (DateTimeUtils.isDateWithinNext24Hours(createProcessSchedulerRequest.getExpectedStartDate())) {
      LocalDateTime oneDayBefore = DateTimeUtils.getLocalDateTime(DateTimeUtils.now()).minusDays(1);
      long oneDayBeforeInEpoch = DateTimeUtils.getEpochTime(oneDayBefore);
      log.info("[createScheduler] creating a job right away since the expected start date of scheduler is within the next 24 hours");
      try {
        createJobService.createScheduledJob(scheduler.getId(), oneDayBeforeInEpoch);
      } catch (Exception ex) {
        log.error("[createScheduler] error creating a scheduled job", ex);
      }
    }
    return schedulerMapper.toPartialDto(scheduler);
  }

  /**
   * Get all schedulers based on the filters and pageable.
   * @param filters
   * @param pageable
   * @return
   */
  @Override
  public Page<SchedulerPartialDto> getAllScheduler(String filters, Pageable pageable) {
    log.info("[getAllScheduler] Request to get all schedulers, filters: {}, pageable: {}", filters, pageable);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    SearchCriteria facilitySearchCriteria = null;
    Long currentFacilityId = principalUser.getCurrentFacilityId();

    if (currentFacilityId != null && !currentFacilityId.equals(Misc.ALL_FACILITY_ID)) {
      facilitySearchCriteria =
        (new SearchCriteria()).setField(CollectionKey.FACILITY_ID).setOp(Operator.Search.EQ.toString()).setValues(Collections.singletonList(currentFacilityId));
    }

    Specification<Scheduler> specification = SpecificationBuilder.createSpecification(filters, Collections.singletonList(facilitySearchCriteria));
    Page<Scheduler> schedulerPage = schedulerRepository.findAll(specification, pageable);

    return new PageImpl<>(schedulerMapper.toPartialDto(schedulerPage.getContent()), pageable, schedulerPage.getTotalElements());
  }

  /**
   * Get a scheduler based on the schedulerId.
   * @param schedulerId
   * @return
   * @throws ResourceNotFoundException
   */
  @Override
  public SchedulerDto getScheduler(Long schedulerId) throws ResourceNotFoundException {
    log.info("[getScheduler] Request to fetch a scheduler, schedulerId: {}", schedulerId);

    Scheduler scheduler = schedulerRepository.findById(schedulerId)
      .orElseThrow(() -> new ResourceNotFoundException(schedulerId, ErrorCode.SCHEDULER_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    return schedulerMapper.toDto(scheduler);
  }

  /**
   * Update a scheduler and reschedule a job for the scheduler updated. If the scheduler is already running, the scheduler will be stopped and rescheduled with the new details.
   * @param schedulerId Id of the scheduler to be deleted.
   * @Return SchedulerDto object containing the scheduler details deleted.
   * @throws ResourceNotFoundException
   */
  @Override
  public SchedulerDto updateScheduler(Long schedulerId, UpdateSchedulerRequest updateSchedulerRequest) throws ResourceNotFoundException, StreemException {
    log.info("[updateScheduler] Request to update a scheduler, schedulerId: {}, updateProcessSchedulerRequest: {}", schedulerId, updateSchedulerRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Facility facility = facilityRepository.getReferenceById(principalUser.getCurrentFacilityId());
    User principalUserEntity = userRepository.getReferenceById(principalUser.getId());

    Scheduler existingScheduler = schedulerRepository.findById(schedulerId)
      .orElseThrow(() -> new ResourceNotFoundException(schedulerId, ErrorCode.SCHEDULER_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    Checklist checklist = checklistRepository
      .findById(existingScheduler.getChecklistId())
      .orElseThrow(() -> new ResourceNotFoundException(existingScheduler.getChecklistId(), ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    // We do not allow editing of checklist Id and job parameter values, we take that from the existing scheduler and pass the same
    // to the revised scheduler
    CreateProcessSchedulerRequest createProcessSchedulerRequest = schedulerMapper.toCreateRequest(updateSchedulerRequest);
    Map<Long, ParameterExecuteRequest> parameterValues = JsonUtils.convertValue(existingScheduler.getData().get(Scheduler.PARAMETER_VALUES), new TypeReference<>() {});
    createProcessSchedulerRequest.setParameterValues(parameterValues);
    createProcessSchedulerRequest.setChecklistId(existingScheduler.getChecklistId());

    List<Error> errorList = new ArrayList<>();

    Scheduler newScheduler = transactionTemplate.execute(status -> {
        try {
          Scheduler scheduler = storeSchedulerAndScheduleJob(createProcessSchedulerRequest, existingScheduler, checklist, facility, checklist.getUseCase(), errorList, principalUserEntity);

          if (!Utility.isEmpty(errorList)) {
            ValidationUtils.invalidate("error creating a scheduler", errorList);
          }
          // Update the state of the parent scheduler to deprecated and stop the scheduler.
          existingScheduler.setState(State.Scheduler.DEPRECATED);
          existingScheduler.setDeprecatedAt(DateTimeUtils.now());
          existingScheduler.setModifiedBy(principalUserEntity);
          schedulerRepository.save(existingScheduler);
          quartzService.stopScheduler(existingScheduler.getIdAsString(), Type.ScheduledJobGroup.JOBS);
          return scheduler;
        } catch (Exception ex) {
          log.error("[createScheduler] error creating a scheduler", ex);
        }
        return null;
      }
    );

    if (!Utility.isEmpty(errorList)) {
      ValidationUtils.invalidate("error creating a scheduler", errorList);
    }

    if (newScheduler == null) {
      ValidationUtils.invalidate(ErrorCode.ERROR_CREATING_A_SCHEDULER.getDescription(), ErrorCode.ERROR_CREATING_A_SCHEDULER);
    }

    if (DateTimeUtils.isDateWithinNext24Hours(createProcessSchedulerRequest.getExpectedStartDate())) {
      LocalDateTime oneDayBefore = DateTimeUtils.getLocalDateTime(DateTimeUtils.now()).minusDays(1);
      long oneDayBeforeInEpoch = DateTimeUtils.getEpochTime(oneDayBefore);
      log.info("[createScheduler] creating a job right away since the expected start date of scheduler is within the next 24 hours");
      try {
        createJobService.createScheduledJob(newScheduler.getId(), oneDayBeforeInEpoch);
      } catch (Exception ex) {
        log.error("[createScheduler] error creating a scheduled job", ex);
      }
    }

    return schedulerMapper.toDto(newScheduler);

  }

  /**
   * Get the scheduler info for a scheduler. The scheduler info contains the scheduler details and the version history of the scheduler.
   * @param schedulerId Id of the scheduler to be deleted.
   * @throws ResourceNotFoundException
   */
  @Override
  public SchedulerInfoDto getSchedulerInfo(Long schedulerId) throws ResourceNotFoundException {
    log.info("[getSchedulerInfo] Request to fetch info for a scheduler, schedulerId: {}", schedulerId);

    Scheduler scheduler = schedulerRepository.findById(schedulerId)
      .orElseThrow(() -> new ResourceNotFoundException(schedulerId, ErrorCode.SCHEDULER_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    List<VersionDto> versionHistory = null;
    Long ancestor = Utility.isNull(scheduler.getVersion()) ? null : scheduler.getVersion().getAncestor();
    if (Utility.isNotNull(ancestor)) {
      List<Version> versions = versionService.findAllByAncestor(ancestor);
      if (!Utility.isEmpty(versions)) {
        List<Scheduler> previousSchedulers = schedulerRepository.findAllById(versions.stream().map(Version::getSelf).collect(Collectors.toSet()));
        Map<Long, IdCodeHolder> map = previousSchedulers.stream().filter(c -> ((c.getState() == State.Scheduler.PUBLISHED) || c.getState() == State.Scheduler.DEPRECATED)).collect(Collectors.toMap(Scheduler::getId, c -> new IdCodeHolder(c.getId(), c.getCode(), c.getName())));
        versionHistory = versions.stream().filter(v -> map.get(v.getSelf()) != null).map(v -> {
          IdCodeHolder idCodeHolder = map.get(v.getSelf());
          return ((new VersionDto())
            .setId(String.valueOf(v.getSelf()))
            .setCode(idCodeHolder.getCode())
            .setName(idCodeHolder.getName())
            .setVersionNumber(v.getVersion())
            .setDeprecatedAt(v.getDeprecatedAt())
            .setAudit((new AuditDto()).setCreatedAt(v.getCreatedAt()).setCreatedBy(userMapper.toUserAuditDto(v.getCreatedBy()))
              .setModifiedAt(v.getModifiedAt()).setModifiedBy(userMapper.toUserAuditDto(v.getModifiedBy()))));
        }).collect(Collectors.toList());
      }
    }

    SchedulerInfoDto schedulerInfoDto = schedulerMapper.toInfoDto(scheduler);
    schedulerInfoDto.setVersions(versionHistory);
    return schedulerInfoDto;
  }

  /**
   * Archive a scheduler
   * @param schedulerId Id of the scheduler to be archived.
   * @return BasicDto
   * @throws ResourceNotFoundException
   * @throws StreemException
   */
  @Transactional
  @Override
  public BasicDto archiveScheduler(Long schedulerId) throws ResourceNotFoundException, StreemException {
    log.info("[archiveScheduler] Request to archive a scheduler, schedulerId: {}", schedulerId);
    Scheduler scheduler = schedulerRepository.findById(schedulerId)
      .orElseThrow(() -> new ResourceNotFoundException(schedulerId, ErrorCode.SCHEDULER_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    scheduler.setArchived(true);
    schedulerRepository.save(scheduler);

    quartzService.stopScheduler(String.valueOf(schedulerId), Type.ScheduledJobGroup.JOBS);

    BasicDto basicDto = new BasicDto();
    basicDto.setMessage("success");
    return basicDto;
  }

  @Override
  public void findAndDeprecateSchedulersForChecklist(Long checklistId, User user) throws StreemException {
    log.info("[findAndDeprecateSchedulersForChecklist] Request to find all schedulers and deprecate for a given checklist, checklistId: {}", checklistId);
    List<Scheduler> schedulers = schedulerRepository.findByChecklistId(checklistId);
    if (!Utility.isEmpty(schedulers)) {
      for (Scheduler scheduler : schedulers) {
        Long time = DateTimeUtils.now();
        scheduler.setState(State.Scheduler.DEPRECATED);
        scheduler.setDeprecatedAt(time);
        scheduler.setModifiedAt(time);
        scheduler.setModifiedBy(user);
        quartzService.stopScheduler(scheduler.getIdAsString(), Type.ScheduledJobGroup.JOBS);
      }
    }
    schedulerRepository.saveAll(schedulers);
  }

  private Scheduler storeSchedulerAndScheduleJob(CreateProcessSchedulerRequest createProcessSchedulerRequest, Scheduler parentScheduler,
                                                 Checklist checklist, Facility facility, UseCase useCase, List<Error> errorList, User principalUserEntity) throws StreemException {

    Map<String, Object> jobData = new HashMap<>();
    jobData.put(Scheduler.PARAMETER_VALUES, createProcessSchedulerRequest.getParameterValues());
    JsonNode data = JsonUtils.valueToNode(jobData);

    if (0 == createProcessSchedulerRequest.getDueDateInterval()) {
      ValidationUtils.addError(errorList, ErrorCode.INVALID_SCHEDULER_DUE_AFTER_DATE);
    }

    boolean isDateInPast = DateTimeUtils.isDateInPast(createProcessSchedulerRequest.getExpectedStartDate());
    if (isDateInPast) {
      ValidationUtils.addError(errorList, ErrorCode.INVALID_SCHEDULER_START_DATE);
    }

    if (Utility.isEmpty(errorList)) {
      Scheduler scheduler = createSchedulerEntityObject(checklist, facility, useCase, createProcessSchedulerRequest, data, principalUserEntity);

      if (null == parentScheduler) {
        // Create new version with number 1
        Version version = versionService.createNewVersion(scheduler.getId(), Type.EntityType.SCHEDULER, principalUserEntity);
        version = versionService.publishVersion(version);
        scheduler.setVersion(version);
        schedulerRepository.save(scheduler);
      } else {
        // If we have a parent scheduler, create a new version for the parent scheduler
        Version version = versionService.createNewVersionFromParent(scheduler.getId(), Type.EntityType.SCHEDULER, parentScheduler.getVersion(), parentScheduler.getId());
        version = versionService.publishVersion(version);
        scheduler.setVersion(version);
        schedulerRepository.save(scheduler);
      }


      // Only if we have a recurrence send a request to create quartz job
      if (!Utility.isEmpty(createProcessSchedulerRequest.getRecurrence())) {
        QuartzSchedulerRequest quartzSchedulerRequest = createSchedulerRequest(createProcessSchedulerRequest, scheduler.getIdAsString());
        quartzService.scheduleJob(quartzSchedulerRequest, scheduler);
      }
      return scheduler;
    }

    return null;
  }

  private QuartzSchedulerRequest createSchedulerRequest(CreateProcessSchedulerRequest createProcessSchedulerRequest, String identity) {
    QuartzSchedulerRequest quartzSchedulerRequest = new QuartzSchedulerRequest();

    quartzSchedulerRequest.setExpectedStartDate(createProcessSchedulerRequest.getExpectedStartDate());
    quartzSchedulerRequest.setIdentity(identity);
    quartzSchedulerRequest.setJobGroup(Type.ScheduledJobGroup.JOBS);
    quartzSchedulerRequest.setScheduledJobType(Type.ScheduledJobType.CREATE_JOB);
    quartzSchedulerRequest.setRecurrence(createProcessSchedulerRequest.getRecurrence());

    return quartzSchedulerRequest;
  }

  private Scheduler createSchedulerEntityObject(Checklist checklist, Facility facility, UseCase useCase,
                                                CreateProcessSchedulerRequest createProcessSchedulerRequest, JsonNode data,
                                                User principalUserEntity) {
    Scheduler scheduler = new Scheduler();

    scheduler.setName(createProcessSchedulerRequest.getName());
    scheduler.setDescription(createProcessSchedulerRequest.getDescription());
    scheduler.setCode(codeService.getCode(Type.EntityType.SCHEDULER, principalUserEntity.getOrganisationId()));
    scheduler.setId(IdGenerator.getInstance().nextId());
    scheduler.setChecklist(checklist);
    scheduler.setChecklistId(checklist.getId());
    scheduler.setState(State.Scheduler.PUBLISHED);
    scheduler.setChecklistName(checklist.getName());
    scheduler.setFacility(facility);
    scheduler.setFacilityId(facility.getId());
    scheduler.setUseCase(useCase);

    scheduler.setExpectedStartDate(createProcessSchedulerRequest.getExpectedStartDate());

//    LocalDateTime oneDayAfter = DateTimeUtils.getLocalDateTime(createProcessSchedulerRequest.getExpectedStartDate()).minusDays(1);
//    long oneDayAfterInEpoch = DateTimeUtils.getEpochTime(oneDayAfter);
//    scheduler.setSchedulerTriggerDate(oneDayAfterInEpoch);

    scheduler.setDueDateInterval(createProcessSchedulerRequest.getDueDateInterval());
    scheduler.setDueDateDuration(createProcessSchedulerRequest.getDueDateDuration());
    scheduler.setRepeated(createProcessSchedulerRequest.isRepeated());
    scheduler.setRecurrenceRule(createProcessSchedulerRequest.getRecurrence());
    scheduler.setCustomRecurrence(createProcessSchedulerRequest.isCustomRecurrence());

    scheduler.setCreatedBy(principalUserEntity);
    scheduler.setModifiedBy(principalUserEntity);
    scheduler.setCreatedAt(DateTimeUtils.now());
    scheduler.setModifiedAt(DateTimeUtils.now());

    scheduler.setData(data);

    scheduler.setEnabled(true);

    return scheduler;
  }
}
