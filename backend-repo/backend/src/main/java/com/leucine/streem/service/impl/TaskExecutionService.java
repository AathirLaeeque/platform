package com.leucine.streem.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.constant.*;
import com.leucine.streem.dto.*;
import com.leucine.streem.dto.mapper.ITaskExecutionMapper;
import com.leucine.streem.dto.mapper.ITaskMapper;
import com.leucine.streem.dto.mapper.IUserMapper;
import com.leucine.streem.dto.projection.TaskExecutionAssigneeView;
import com.leucine.streem.dto.request.TaskCompletionRequest;
import com.leucine.streem.dto.request.TaskExecutionRequest;
import com.leucine.streem.dto.request.TaskPauseOrResumeRequest;
import com.leucine.streem.dto.request.TaskSignOffRequest;
import com.leucine.streem.dto.response.Error;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.exception.ExceptionType;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.*;
import com.leucine.streem.model.helper.BaseEntity;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.model.helper.parameter.ChoiceParameterBase;
import com.leucine.streem.model.helper.parameter.ValueParameterBase;
import com.leucine.streem.repository.*;
import com.leucine.streem.service.*;
import com.leucine.streem.util.DateTimeUtils;
import com.leucine.streem.util.JsonUtils;
import com.leucine.streem.util.Utility;
import com.leucine.streem.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
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
public class TaskExecutionService implements ITaskExecutionService {
  private final ITaskExecutionRepository taskExecutionRepository;
  private final ITaskExecutionMapper taskExecutionMapper;
  private final IParameterValueRepository parameterValueRepository;
  private final IUserRepository userRepository;
  private final ITempParameterValueRepository tempParameterValueRepository;
  private final ITaskExecutionAssigneeRepository taskExecutionAssigneeRepository;
  private final IJobAuditService jobAuditService;
  private final IStageReportService stageReportService;
  private final ITaskRepository taskRepository;
  private final ITaskMapper taskMapper;
  private final IJobLogService jobLogService;
  private final ITaskAutomationService taskAutomationService;
  private final IUserMapper userMapper;
  private final ITaskExecutionTimerRepository taskExecutionTimerRepository;
  private final ITaskExecutionTimerService taskExecutionTimerService;

  @Override
  public TaskDto getTask(Long taskId, Long jobId) throws ResourceNotFoundException {
    log.info("[getTask] Request to fetch task with id: {} for job: {}", taskId, jobId);
    Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    TaskExecution taskExecution = taskExecutionRepository.readByJobIdAndTaskId(jobId, taskId);

    Map<Long, TaskExecution> taskExecutionMap = new HashMap<>();
    taskExecutionMap.put(taskId, taskExecution);

    List<Long> parameterIds = task.getParameters().stream().map(BaseEntity::getId).collect(Collectors.toList());

    List<ParameterValue> parameterValues = parameterValueRepository.readByJobIdAndParameterIdIn(jobId, parameterIds);
    Map<Long, ParameterValue> parameterValueMap = parameterValues.stream()
      .collect(Collectors.toMap(av -> av.getParameter().getId(), Function.identity()));

    List<TempParameterValue> tempParameterValues = tempParameterValueRepository.readByJobIdAndParameterIdIn(jobId, parameterIds);

    Map<Long, TempParameterValue> tempParameterValueMap = tempParameterValues.stream()
      .collect(Collectors.toMap(av -> av.getParameter().getId(), Function.identity()));

    return taskMapper.toDto(task, parameterValueMap, taskExecutionMap, tempParameterValueMap, null, null);
  }

  //TODO add stats object if required, applies everywhere in task execution
  @Override
  @Transactional
  public TaskExecutionDto startTask(Long taskId, TaskExecutionRequest taskExecutionRequest) throws ResourceNotFoundException, StreemException {
    log.info("[startTask] Request to start task, taskId: {}, taskExecutionRequest: {} ", taskId, taskExecutionRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    TaskExecution taskExecution = taskExecutionRepository.readByTaskIdAndJobId(taskId, taskExecutionRequest.getJobId())
      .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    User principalUserEntity = userRepository.getReferenceById(principalUser.getId());
    String jobId = taskExecutionRequest.getJobId().toString();
    Task task = taskExecution.getTask();

    validateJobState(taskExecution.getJob().getId(), Action.Task.START, taskExecution.getJob().getState());
    validateTaskState(taskExecution.getTask().getId(), Action.Task.START, taskExecution.getState());
    TaskExecutionUserMapping taskExecutionUserMapping = validateAndGetAssignedUser(taskId, taskExecution, principalUserEntity);

    taskExecution.setStartedAt(DateTimeUtils.now());
    taskExecution.setModifiedAt(DateTimeUtils.now());
    taskExecution.setStartedBy(principalUserEntity);
    taskExecution.setModifiedBy(principalUserEntity);
    taskExecution.setState(State.TaskExecution.IN_PROGRESS);

    TaskExecutionDto taskExecutionDto = taskExecutionMapper.toDto(taskExecutionRepository.save(taskExecution), principalUser);
    updateUserAction(taskExecutionUserMapping);
    jobAuditService.startTask(taskId, taskExecutionRequest, principalUser);
    stageReportService.setStageToInProgress(taskExecutionRequest.getJobId(), taskId);
    UserAuditDto userAuditDto = userMapper.toUserAuditDto(principalUserEntity);
    jobLogService.recordJobLogTrigger(jobId, task.getIdAsString(), Type.JobLogTriggerType.TSK_STARTED_BY, task.getName(), null,
      Utility.getFullNameFromPrincipalUser(principalUser), Utility.getFullNameFromPrincipalUser(principalUser), userAuditDto);
    jobLogService.recordJobLogTrigger(jobId, task.getIdAsString(), Type.JobLogTriggerType.TSK_START_TIME, task.getName(), null, String.valueOf(
      taskExecution.getStartedAt()), String.valueOf(
      taskExecution.getStartedAt()), userAuditDto);
    return taskExecutionDto;
  }

  @Override
  public BasicDto validateTask(Long taskId, Long jobId) throws StreemException, ResourceNotFoundException {
    log.info("[validateTask] Request to validate task execution, taskId: {}, jobId: {}", taskId, jobId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    TaskExecution taskExecution = taskExecutionRepository.readByTaskIdAndJobId(taskId, jobId)
      .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Task task = taskExecution.getTask();

    validateJobState(taskExecution.getJob().getId(), Action.Task.COMPLETE, taskExecution.getJob().getState());
    validateTaskState(taskExecution.getTask().getId(), Action.Task.COMPLETE, taskExecution.getState());
    validateAndGetAssignedUser(taskId, taskExecution, principalUserEntity);
    validateIncompleteParameters(jobId, task.getId());

    BasicDto basicDto = new BasicDto();
    basicDto.setMessage("success");
    return basicDto;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public TaskExecutionDto completeTask(Long taskId, TaskCompletionRequest taskCompletionRequest) throws ResourceNotFoundException, StreemException, IOException {
    log.info("[completeTask] Request to complete task, taskId: {}, taskCompletionRequest: {}", taskId, taskCompletionRequest.getJobId());
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getReferenceById(principalUser.getId());
    TaskExecution taskExecution = taskExecutionRepository.readByTaskIdAndJobId(taskId, taskCompletionRequest.getJobId())
      .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Task task = taskExecution.getTask();
    validateJobState(taskExecution.getJob().getId(), Action.Task.COMPLETE, taskExecution.getJob().getState());
    validateTaskState(taskExecution.getTask().getId(), Action.Task.COMPLETE, taskExecution.getState());
    TaskExecutionUserMapping taskExecutionUserMapping = validateAndGetAssignedUser(taskId, taskExecution, principalUserEntity);
    validateIncompleteVerificationParameters(taskCompletionRequest.getJobId(), task.getId());
    validateIncompleteParameters(taskCompletionRequest.getJobId(), task.getId());
    verifyDataIntegrity(taskCompletionRequest);

    long endedAt = DateTimeUtils.now();
    if (isInvalidTimedTaskCompletedState(task, taskExecution.getStartedAt(), endedAt)) {
      ValidationUtils.validateNotEmpty(taskCompletionRequest.getReason(), taskId, ErrorCode.TIMED_TASK_REASON_CANNOT_BE_EMPTY);
      taskExecution.setReason(taskCompletionRequest.getReason());
    }

    taskExecution.setEndedAt(endedAt);
    taskExecution.setEndedBy(principalUserEntity);
    taskExecution.setModifiedBy(principalUserEntity);
    taskExecution.setModifiedAt(DateTimeUtils.now());
    taskExecution.setState(State.TaskExecution.COMPLETED);
    TaskExecutionDto taskExecutionDto = taskExecutionMapper.toDto(taskExecutionRepository.save(taskExecution), principalUser);

    updateUserAction(taskExecutionUserMapping);

    stageReportService.incrementTaskCompleteCount(taskCompletionRequest.getJobId(), taskId);
    jobAuditService.completeTask(taskId, taskCompletionRequest, principalUser);
    Job job = taskExecution.getJob();
    UserAuditDto userAuditDto = userMapper.toUserAuditDto(principalUserEntity);
    jobLogService.recordJobLogTrigger(job.getIdAsString(), task.getIdAsString(), Type.JobLogTriggerType.TSK_ENDED_BY, task.getName(), null,
      Utility.getFullNameFromPrincipalUser(principalUser), Utility.getFullNameFromPrincipalUser(principalUser), userAuditDto);
    jobLogService.recordJobLogTrigger(job.getIdAsString(), task.getIdAsString(), Type.JobLogTriggerType.TSK_END_TIME, task.getName(), null, String.valueOf(
      taskExecution.getEndedAt()), String.valueOf(
      taskExecution.getEndedAt()), userAuditDto);

    taskAutomationService.completeTaskAutomations(taskId, taskCompletionRequest.getJobId(), taskCompletionRequest.getAutomationReason(), principalUser);
    return taskExecutionDto;
  }

  private void verifyDataIntegrity(TaskCompletionRequest taskCompletionRequest) throws ResourceNotFoundException, StreemException, IOException {
    var parameterRequestList = taskCompletionRequest.getParameters();
    List<Error> errorList = new ArrayList<>();
    for (var parameterRequest : parameterRequestList) {
      //TODO: Move this logic outside for loop
      var parameterValue = parameterValueRepository.readByParameterIdAndJobId(parameterRequest.getId(), taskCompletionRequest.getJobId())
        .orElseThrow(() -> new ResourceNotFoundException(parameterRequest.getId(), ErrorCode.PARAMETER_VALUE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
      var parameter = parameterValue.getParameter();
      String requestData = parameterRequest.getData().toString();
      verifyDataIntegrityOfParameter(parameter, parameterValue, requestData, errorList);
      var parameterId = parameterValue.getParameter().getId();
      verifyDataIntegrityForReason(parameterId, parameterRequest.getReason(), parameterValue.getReason(), errorList);
    }
    if (!Utility.isEmpty(errorList)) {
      ValidationUtils.invalidate(ErrorMessage.DATA_INCONSISTENCY_ERROR, errorList);
    }
  }

  private void verifyDataIntegrityForReason(Long parameterId, String requestReason, String responseReason, List<Error> errorList) {
    if (!Utility.nullSafeEquals(requestReason, responseReason)) {
      ValidationUtils.addError(parameterId, errorList, ErrorCode.PARAMETER_DATA_INCONSISTENCY);
    }
  }

  private void verifyDataIntegrityOfParameter(Parameter parameter, ParameterValue parameterValue, String requestData, List<Error> errorList) throws IOException {
    var parameterType = parameter.getType();
    switch (parameterType) {
      case YES_NO, MULTISELECT, SINGLE_SELECT, CHECKLIST ->
        verifyDataIntegrityOfChoiceParameter(parameterValue, requestData, errorList);
      case MULTI_LINE, SHOULD_BE -> verifyDataIntegrityOfValueParameter(parameterValue, requestData, errorList);
    }
  }

  private <T extends ChoiceParameterBase> void verifyDataIntegrityOfChoiceParameter(ParameterValue parameterValue, String requestData, List<Error> errorList) throws IOException {
    List<T> choiceParameterList = JsonUtils.jsonToCollectionType(requestData, List.class, ChoiceParameterBase.class);
    var choices = parameterValue.getChoices();
    if (choices == null) {
      return;
    }
    Map<String, String> responseData = JsonUtils.convertValue(choices, new TypeReference<>() {
    });

    for (var choiceParameter : choiceParameterList) {
      String state = responseData.get(choiceParameter.getId());
      if (!choiceParameter.getState().equals(state)) {
        ValidationUtils.addError(parameterValue.getParameter().getId(), errorList, ErrorCode.PARAMETER_DATA_INCONSISTENCY);
      }
    }
  }

  private <T extends ValueParameterBase> void verifyDataIntegrityOfValueParameter(ParameterValue parameterValue, String requestData, List<Error> errorList) throws JsonProcessingException {
    var parameterRequest = JsonUtils.readValue(requestData, ValueParameterBase.class);
    var requestValue = parameterRequest.getInput();
    var responseValue = parameterValue.getValue();
    if (!Utility.nullSafeEquals(requestValue, responseValue)) {
      ValidationUtils.addError(parameterValue.getParameter().getId(), errorList, ErrorCode.PARAMETER_DATA_INCONSISTENCY);
    }
  }

  @Override
  @Transactional
  public TaskExecutionDto skipTask(Long taskId, TaskExecutionRequest taskExecutionRequest) throws ResourceNotFoundException, StreemException {
    log.info("[skipTask] Request to skip task, taskId: {}, taskExecutionRequest: {}", taskId, taskExecutionRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    TaskExecution taskExecution = taskExecutionRepository.readByTaskIdAndJobId(taskId, taskExecutionRequest.getJobId())
      .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    ValidationUtils.validateNotEmpty(taskExecutionRequest.getReason(), taskId, ErrorCode.PROVIDE_REASON_TO_SKIP_TASK);
    validateJobState(taskExecution.getJob().getId(), Action.Task.SKIP, taskExecution.getJob().getState());
    validateTaskState(taskExecution.getJob().getId(), Action.Task.SKIP, taskExecution.getState());
    TaskExecutionUserMapping taskExecutionUserMapping = validateAndGetAssignedUser(taskId, taskExecution, principalUserEntity);

    taskExecution.setReason(taskExecutionRequest.getReason());
    taskExecution.setEndedAt(DateTimeUtils.now());
    taskExecution.setEndedBy(principalUserEntity);
    taskExecution.setModifiedBy(principalUserEntity);
    taskExecution.setModifiedAt(DateTimeUtils.now());
    taskExecution.setState(State.TaskExecution.SKIPPED);
    TaskExecutionDto taskExecutionDto = taskExecutionMapper.toDto(taskExecutionRepository.save(taskExecution), principalUser);

    updateUserAction(taskExecutionUserMapping);

    stageReportService.incrementTaskCompleteCount(taskExecutionRequest.getJobId(), taskId);
    jobAuditService.skipTask(taskId, taskExecutionRequest, principalUser);
    return taskExecutionDto;
  }

  @Override
  public TaskExecutionDto completeWithException(Long taskId, TaskCompletionRequest taskCompletionRequest) throws ResourceNotFoundException, StreemException, IOException {
    log.info("[completeWithException] Request to complete task with Exception, taskId: {}, taskCompletionRequest: {}", taskId, taskCompletionRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    TaskExecution taskExecution = taskExecutionRepository.readByTaskIdAndJobId(taskId, taskCompletionRequest.getJobId())
      .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    ValidationUtils.validateNotEmpty(taskCompletionRequest.getReason(), taskId, ErrorCode.PROVIDE_REASON_TO_FORCE_CLOSE_TASK);
    validateJobState(taskExecution.getJob().getId(), Action.Task.COMPLETE_WITH_EXCEPTION, taskExecution.getJob().getState());
    validateTaskState(taskExecution.getTask().getId(), Action.Task.COMPLETE_WITH_EXCEPTION, taskExecution.getState());
    verifyDataIntegrity(taskCompletionRequest);
    TaskExecutionUserMapping taskExecutionUserMapping = validateAndGetAssignedUser(taskId, taskExecution, principalUserEntity);

    taskExecution.setReason(taskCompletionRequest.getReason());
    taskExecution.setEndedAt(DateTimeUtils.now());
    taskExecution.setEndedBy(principalUserEntity);
    taskExecution.setModifiedAt(DateTimeUtils.now());
    taskExecution.setModifiedBy(principalUserEntity);
    taskExecution.setState(State.TaskExecution.COMPLETED_WITH_EXCEPTION);
    TaskExecutionDto taskExecutionDto = taskExecutionMapper.toDto(taskExecutionRepository.save(taskExecution), principalUser);

    updateUserAction(taskExecutionUserMapping);

    stageReportService.incrementTaskCompleteCount(taskCompletionRequest.getJobId(), taskId);
    jobAuditService.completeTaskWithException(taskId, taskCompletionRequest, principalUser);
    return taskExecutionDto;
  }

  @Override
  public BasicDto signOff(TaskSignOffRequest taskSignOffRequest) throws StreemException {
    log.info("[signOff] Request to sign off tasks,  taskSignOffRequest: {}", taskSignOffRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    List<Long> nonSignedOffTaskIds = taskExecutionRepository.findNonSignedOffTaskIdsByJobIdAndUserId(taskSignOffRequest.getJobId(), principalUser.getId());
    List<TaskExecution> taskExecutions = taskExecutionRepository.readByJobIdAndTaskIdIn(taskSignOffRequest.getJobId(), nonSignedOffTaskIds);

    if (!Utility.isEmpty(taskExecutions)) {
      validateTasksAndSignOff(taskExecutions);
    }

    jobAuditService.signedOffTasks(taskSignOffRequest, principalUser);
    BasicDto basicDto = new BasicDto();
    basicDto.setMessage("success");
    return basicDto;
  }

  @Override
  @Transactional
  public TaskExecutionDto enableCorrection(Long taskId, TaskExecutionRequest taskExecutionRequest) throws ResourceNotFoundException, StreemException {
    log.info("[enableCorrection] Request to enable correction for task, taskId: {}, taskExecutionRequest: {}", taskId, taskExecutionRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());

    if (!Utility.trimAndCheckIfEmpty(taskExecutionRequest.getReason())) {
      ValidationUtils.invalidate(taskId, ErrorCode.REASON_CANNOT_BE_EMPTY);
    }

    TaskExecution taskExecution = taskExecutionRepository.readByTaskIdAndJobId(taskId, taskExecutionRequest.getJobId())
      .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    if (taskExecution.isCorrectionEnabled()) {
      ValidationUtils.invalidate(taskExecution.getId(), ErrorCode.TASK_ALREADY_ENABLED_FOR_CORRECTION);
    }

    Job job = taskExecution.getJob();
    if (State.JOB_COMPLETED_STATES.contains(job.getState())) {
      ValidationUtils.invalidate(job.getId(), ErrorCode.JOB_ALREADY_COMPLETED);
    }
    List<Long> parameterIds = parameterValueRepository.findExecutableParameterIdsByTaskId(taskId);
    List<ParameterValue> parameterValues = parameterValueRepository.readByJobIdAndParameterIdIn(taskExecutionRequest.getJobId(), parameterIds);

    //select all parameter values for this particular task
    List<TempParameterValue> tempParameterValuesBeforeMapping = tempParameterValueRepository.readByJobIdAndParameterIdIn(taskExecutionRequest.getJobId(), parameterIds);

    if ((null != parameterValues && tempParameterValuesBeforeMapping != null) && parameterValues.size() != tempParameterValuesBeforeMapping.size()) {
      List<TempParameterValue> tempParameterValues = new ArrayList<>();
      for (ParameterValue parameterValue : parameterValues) {
        TempParameterValue tempParameterValue = new TempParameterValue();
        tempParameterValue.setJob(job);
        tempParameterValue.setChoices(parameterValue.getChoices());
        tempParameterValue.setValue(parameterValue.getValue());
        tempParameterValue.setParameter(parameterValue.getParameter());
        tempParameterValue.setState(parameterValue.getState());
        tempParameterValue.setCreatedBy(principalUserEntity);
        tempParameterValue.setCreatedAt(parameterValue.getCreatedAt());
        tempParameterValue.setModifiedBy(parameterValue.getModifiedBy());
        tempParameterValue.setModifiedAt(parameterValue.getModifiedAt());
        tempParameterValues.add(tempParameterValue);
        tempParameterValue = tempParameterValueRepository.save(tempParameterValue);
        if (null != parameterValue.getMedias()) {
          for (ParameterValueMediaMapping media : parameterValue.getMedias()) {
            tempParameterValue.addMedia(media.getMedia(), principalUserEntity);
          }
          tempParameterValueRepository.save(tempParameterValue);
        }
      }
    }

    TaskExecutionDto taskExecutionDto = taskExecutionMapper.toDto(taskExecution);

    parameterValueRepository.updateStateForParameters(taskExecutionRequest.getJobId(), State.ParameterExecution.ENABLED_FOR_CORRECTION.name(), parameterIds);

    taskExecutionRepository.enableCorrection(taskExecutionRequest.getCorrectionReason(), taskExecution.getId());
    taskExecutionDto.setCorrectionEnabled(true);

    taskExecutionDto.setCorrectionReason(taskExecutionRequest.getCorrectionReason());

    jobAuditService.enableTaskForCorrection(taskId, taskExecutionRequest, principalUser);
    return taskExecutionDto;
  }

  @Override
  @Transactional
  public TaskExecutionDto completeCorrection(Long taskId, TaskExecutionRequest taskExecutionRequest) throws ResourceNotFoundException, StreemException {
    log.info("[completeCorrection] Request to complete correction, taskId: {}, taskExecutionRequest: {}", taskId, taskExecutionRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    TaskExecution taskExecution = taskExecutionRepository.readByTaskIdAndJobId(taskId, taskExecutionRequest.getJobId())
      .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    if (!taskExecution.isCorrectionEnabled()) {
      ValidationUtils.invalidate(taskExecution.getId(), ErrorCode.TASK_NOT_ENABLED_FOR_CORRECTION);
    }
    Job job = taskExecution.getJob();
    if (State.JOB_COMPLETED_STATES.contains(job.getState())) {
      ValidationUtils.invalidate(job.getId(), ErrorCode.JOB_ALREADY_COMPLETED);
    }
    List<Long> parameterIds = parameterValueRepository.findExecutableParameterIdsByTaskId(taskId);
    List<TempParameterValue> tempParameterValues = tempParameterValueRepository.readByJobIdAndParameterIdIn(taskExecutionRequest.getJobId(), parameterIds);

    List<ParameterValue> parameterValues = parameterValueRepository.readByJobIdAndParameterIdIn(taskExecutionRequest.getJobId(), parameterIds);

    Map<Long, ParameterValue> parameterValueMap = new HashMap<>();
    for (ParameterValue parameterValue : parameterValues) {
      parameterValueMap.put(parameterValue.getParameter().getId(), parameterValue);
    }
    Set<Long> hide = new HashSet<>();
    Set<Long> show = new HashSet<>();
    for (TempParameterValue tempParameterValue : tempParameterValues) {
      if (Type.PARAMETER_MEDIA_TYPES.contains(tempParameterValue.getParameter().getType())) {
        ParameterValue parameterValue = parameterValueMap.get(tempParameterValue.getParameter().getId());
        updateParameterMediasOnErrorCorrection(parameterValue, parameterValue.getMedias(), tempParameterValue, tempParameterValue.getMedias(), principalUserEntity);
      } else {
        String choices = tempParameterValue.getChoices() == null ? null : tempParameterValue.getChoices().toString();
        var modifiedBy = tempParameterValue.getModifiedBy();
        parameterValueRepository.updateParameterValues(taskExecutionRequest.getJobId(), tempParameterValue.getParameter().getId(), tempParameterValue.getState().name(),
          tempParameterValue.getValue(), choices, tempParameterValue.getReason(), modifiedBy == null ? null : modifiedBy.getId(), tempParameterValue.getModifiedAt());
        ParameterValue parameterValue = parameterValueRepository.readByParameterIdAndJobId(tempParameterValue.getParameter().getId(), job.getId())
          .orElseThrow(() -> new ResourceNotFoundException(tempParameterValue.getParameter().getId(), ErrorCode.PARAMETER_VALUE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
        JsonNode oldChoices = parameterValue.getChoices();
        // TODO Move to new service
//        RuleHideShowDto ruleHideShowDto = parameterExecutionService.updateRules(job.getId(), tempParameterValue.getParameter(), oldChoices);
//        hide.addAll(ruleHideShowDto.getHide());
//        show.addAll(ruleHideShowDto.getShow());
      }
    }

    taskExecution.setCorrectedBy(principalUserEntity);
    taskExecution.setCorrectedAt(DateTimeUtils.now());
    taskExecutionRepository.save(taskExecution);
    TaskExecutionDto taskExecutionDto = taskExecutionMapper.toDto(taskExecution);
    taskExecutionDto.setShow(show);
    taskExecutionDto.setHide(hide);
    taskExecutionRepository.completeCorrection(taskExecution.getId());
    taskExecutionDto.setCorrectionEnabled(false);

    jobAuditService.completeCorrection(taskId, taskExecutionRequest, principalUser);
    return taskExecutionDto;
  }

  @Override
  @Transactional
  public TaskExecutionDto cancelCorrection(Long taskId, TaskExecutionRequest taskExecutionRequest) throws ResourceNotFoundException, StreemException {
    log.info("[cancelCorrection] Request to cancel correction, taskId: {}, taskExecutionRequest: {}", taskId, taskExecutionRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    TaskExecution taskExecution = taskExecutionRepository.readByTaskIdAndJobId(taskId, taskExecutionRequest.getJobId())
      .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    List<Long> parameterIds = parameterValueRepository.findExecutableParameterIdsByTaskId(taskId);

    if (!taskExecution.isCorrectionEnabled()) {
      ValidationUtils.invalidate(taskExecution.getId(), ErrorCode.TASK_NOT_ENABLED_FOR_CORRECTION);
    }
    Job job = taskExecution.getJob();
    if (State.JOB_COMPLETED_STATES.contains(job.getState())) {
      ValidationUtils.invalidate(job.getId(), ErrorCode.JOB_ALREADY_COMPLETED);
    }
    parameterValueRepository.updateStateForParameters(taskExecutionRequest.getJobId(), State.ParameterExecution.EXECUTED.name(), parameterIds);

    TaskExecutionDto taskExecutionDto = taskExecutionMapper.toDto(taskExecution);
    taskExecutionDto.setCorrectionReason(null);
    taskExecutionRepository.cancelCorrection(taskExecution.getId());
    taskExecutionDto.setCorrectionEnabled(false);

    jobAuditService.cancelCorrection(taskId, taskExecutionRequest, principalUser);
    return taskExecutionDto;
  }

  @Override
  public List<TaskExecutionAssigneeView> getTaskExecutionAssignees(Set<Long> taskExecutionIds) {
    log.info("[getTaskExecutionAssignees] Request to fetch task execution assignees, taskExecutionIds: {}", taskExecutionIds);
    return taskExecutionAssigneeRepository.findByTaskExecutionIdIn(taskExecutionIds, taskExecutionIds.size());
  }

  @Override
  public TaskExecution getTaskExecutionByJobAndTaskId(Long taskId, Long jobId) {
    log.info("[getTaskExecutionByJobAndTaskId] Request to fetch task execution, taskId: {}, jobId: {}", taskId, jobId);
    return taskExecutionRepository.readByTaskIdAndJobId(taskId, jobId).get();
  }

  @Override
  public TaskExecutionUserMapping validateAndGetAssignedUser(Long taskId, TaskExecution taskExecution, User user) throws ResourceNotFoundException {
    log.info("[validateAndGetAssignedUser] Request to validate and getting assigned user,  taskId: {}, taskExecution: {}, user: {}", taskId, taskExecution, user);
    return taskExecutionAssigneeRepository.findByTaskExecutionAndUser(taskExecution, user)
      .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.USER_NOT_ASSIGNED_TO_EXECUTE_TASK, ExceptionType.ENTITY_NOT_FOUND));
  }

  @Override
  public void updateUserAction(TaskExecutionUserMapping taskExecutionUserMapping) {
    log.info("[updateUserAction] Request to update user action for taskExecutionUserMapping: {}", taskExecutionUserMapping);
    if (!taskExecutionUserMapping.isActionPerformed()) {
      taskExecutionUserMapping.setActionPerformed(true);
      taskExecutionAssigneeRepository.save(taskExecutionUserMapping);
    }
  }

  @Override
  public boolean isInvalidTimedTaskCompletedState(Task task, Long startedAt, Long endedAt) {
    log.info("[isInvalidTimedTaskCompletedState] Request to check if task is invalid timed task completed state, task: {}, startedAt: {}, endedAt: {}", task, startedAt, endedAt);
    if (startedAt == null || endedAt == null || !task.isTimed()) {
      return false;
    }
    long totalTime = endedAt - startedAt;
    Operator.Timer timerOperator = Operator.Timer.valueOf(task.getTimerOperator());

    return (Operator.Timer.NOT_LESS_THAN.equals(timerOperator) && (task.getMinPeriod() > totalTime || task.getMaxPeriod() < totalTime))
      || Operator.Timer.LESS_THAN.equals(timerOperator) && task.getMaxPeriod() < totalTime;
  }

  private void validateTasksAndSignOff(List<TaskExecution> taskExecutions) throws StreemException {
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Set<Long> taskExecutionIds = new HashSet<>();
    Job job = taskExecutions.get(0).getJob();
    validateJobState(job.getId(), Action.Task.SIGN_OFF, job.getState());

    List<Error> errorList = new ArrayList<>();
    for (TaskExecution taskExecution : taskExecutions) {
      if (!State.TASK_COMPLETED_STATES.contains(taskExecution.getState()) || taskExecution.isCorrectionEnabled()) {
        ValidationUtils.addError(taskExecution.getTask().getId(), errorList, ErrorCode.TASK_INCOMPLETE);
      }
      taskExecutionIds.add(taskExecution.getId());
    }

    if (!Utility.isEmpty(errorList)) {
      ValidationUtils.invalidate(ErrorMessage.TASKS_INCOMPLETE, errorList);
    }

    if (!taskExecutionIds.isEmpty()) {
      taskExecutionAssigneeRepository.updateAssigneeState(State.TaskExecutionAssignee.SIGNED_OFF.name(), principalUser.getId()
        , taskExecutionIds, principalUser.getId(), DateTimeUtils.now());
    }
  }

  private void updateParameterMediasOnErrorCorrection(ParameterValue parameterValue, List<ParameterValueMediaMapping> parameterValueMediaMappings, TempParameterValue tempParameterValue,
                                                      List<TempParameterValueMediaMapping> tempParameterValueMediaMappings, User principalUserEntity) {
    Map<Long, Pair<ParameterValueMediaMapping, Media>> mediaMap = new HashMap<>();
    for (ParameterValueMediaMapping parameterValueMediaMapping : parameterValueMediaMappings) {
      Pair<ParameterValueMediaMapping, Media> mediaPair = Pair.of(parameterValueMediaMapping, parameterValueMediaMapping.getMedia());
      mediaMap.put(parameterValueMediaMapping.getMedia().getId(), mediaPair);
    }

    for (TempParameterValueMediaMapping parameterValueMedia : tempParameterValueMediaMappings) {
      if (mediaMap.containsKey(parameterValueMedia.getMedia().getId())) {
        Pair<ParameterValueMediaMapping, Media> mediaPair = mediaMap.get(parameterValueMedia.getMedia().getId());
        mediaPair.getFirst().setArchived(parameterValueMedia.isArchived());
      } else {
        parameterValue.addMedia(parameterValueMedia.getMedia(), principalUserEntity);
        parameterValue.setState(tempParameterValue.getState());
        parameterValue.setModifiedBy(principalUserEntity);
        parameterValueRepository.save(parameterValue);
      }
    }
  }

  private List<Error> createIncompleteParameterErrorList(List<Long> parameterIds) {
    List<Error> errorList = new ArrayList<>();
    for (Long id : parameterIds) {
      ValidationUtils.addError(id, errorList, ErrorCode.PARAMETER_INCOMPLETE);
    }
    return errorList;
  }

  private List<Error> createIncompleteVerificationParameterErrorList(List<Long> parameterIds) {
    List<Error> errorList = new ArrayList<>();
    for (Long id : parameterIds) {
      ValidationUtils.addError(id, errorList, ErrorCode.PARAMETER_VERIFICATION_INCOMPLETE);
    }
    return errorList;
  }

  private void validateIncompleteParameters(Long jobId, Long taskId) throws StreemException {
    List<Long> incompleteMandatoryParameterIds = parameterValueRepository.findIncompleteMandatoryParameterIdsByJobIdAndTaskId(jobId, taskId);

    if (!Utility.isEmpty(incompleteMandatoryParameterIds)) {
      throw new StreemException(ErrorMessage.MANDATORY_PARAMETERS_NOT_COMPLETED, createIncompleteParameterErrorList(incompleteMandatoryParameterIds));
    }
  }

  private void validateIncompleteVerificationParameters(Long jobId, Long taskId) throws StreemException {
    List<Long> incompleteVerificationParameterIds = parameterValueRepository.findVerificationIncompleteParameterIdsByJobIdAndTaskId(jobId, taskId);

    if (!Utility.isEmpty(incompleteVerificationParameterIds)) {
      throw new StreemException(ErrorMessage.PENDING_VERIFICATION_PARAMETERS, createIncompleteVerificationParameterErrorList(incompleteVerificationParameterIds));
    }
  }

  private void validateTimer(Long taskId, Operator.Timer timerOperator, Long minPeriod, Long maxPeriod, Long totalPeriod, String reason) throws StreemException {
    if ((timerOperator.equals(Operator.Timer.NOT_LESS_THAN) && (minPeriod > totalPeriod || maxPeriod < totalPeriod))
      || (timerOperator.equals(Operator.Timer.LESS_THAN) && maxPeriod < totalPeriod)) {
      ValidationUtils.validateNotEmpty(reason, taskId, ErrorCode.TIMED_TASK_REASON_CANNOT_BE_EMPTY);
    }
  }

  private void validateJobState(Long jobId, Action.Task taskAction, State.Job jobState) throws StreemException {

    if (State.Job.BLOCKED.equals(jobState)) {
      ValidationUtils.invalidate(jobId, ErrorCode.JOB_IS_BLOCKED);
    }

    switch (taskAction) {
      case START:
        if (!State.Job.IN_PROGRESS.equals(jobState)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_IS_NOT_IN_PROGRESS);
        }
        break;
      case ASSIGN:
      case COMPLETE_WITH_EXCEPTION:
      case COMPLETE:
      case SIGN_OFF:
      case SKIP:
        if (State.JOB_COMPLETED_STATES.contains(jobState)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_ALREADY_COMPLETED);
        }
        break;
    }
  }

  //TODO state management ?
  private void validateTaskState(Long id, Action.Task taskAction, State.TaskExecution state) throws StreemException {
    switch (taskAction) {
      case ASSIGN, COMPLETE_WITH_EXCEPTION, SKIP:
        if (State.TASK_COMPLETED_STATES.contains(state)) {
          ValidationUtils.invalidate(id, ErrorCode.TASK_ALREADY_COMPLETED);
        }
        break;
      case START:
        if (State.TASK_COMPLETED_STATES.contains(state)) {
          ValidationUtils.invalidate(id, ErrorCode.TASK_ALREADY_COMPLETED);
        }
        if (state == State.TaskExecution.IN_PROGRESS)
          ValidationUtils.invalidate(id, ErrorCode.TASK_ALREADY_IN_PROGRESS);
        break;
      case COMPLETE:
        if (!State.TaskExecution.IN_PROGRESS.equals(state)) {
          ValidationUtils.invalidate(id, ErrorCode.TASK_NOT_IN_PROGRESS);
        }
        break;
    }
  }

  /**
   * @param taskId                   id of the task to pause
   * @param taskPauseOrResumeRequest contains jobId, reason and comment for the pause request
   * @return
   * @throws StreemException
   */
  @Override
  public TaskExecutionDto pauseTask(Long taskId, TaskPauseOrResumeRequest taskPauseOrResumeRequest) throws StreemException {
    log.info("[pauseTask] Request to pause task: {} and TaskPauseOrResumeRequest :{} ", taskId, taskPauseOrResumeRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    TaskExecution taskExecution = taskExecutionRepository.readByJobIdAndTaskId(taskPauseOrResumeRequest.jobId(), taskId);
    if (taskExecution == null) {
      ValidationUtils.invalidate(taskId, ErrorCode.TASK_NOT_FOUND);
    }
    User principalUserEntity = userRepository.getReferenceById(principalUser.getId());
    List<TaskPauseReasonOrComment> pausedReason = new ArrayList<>();

    if (taskExecution.getState() == State.TaskExecution.IN_PROGRESS) {

      taskExecution.setState(State.TaskExecution.PAUSED);
      taskExecutionTimerService.saveTaskPauseTimer(taskPauseOrResumeRequest, taskExecution, principalUserEntity);
      pausedReason = taskExecutionTimerService.calculateDurationAndReturnReasonsOrComments(List.of(taskExecution)).get(taskExecution.getId());
      taskExecutionRepository.save(taskExecution);

    } else {
      ValidationUtils.invalidate(taskId, ErrorCode.TASK_IS_IN_NON_RESUMABLE_STATE);
    }
    jobAuditService.pauseTask(taskId, taskPauseOrResumeRequest, principalUser);
    return taskExecutionMapper.toDto(taskExecution, pausedReason);
  }

  /**
   * @param taskId                   id of the task to pause
   * @param taskPauseOrResumeRequest contains jobId, reason and comment for the pause request
   * @return
   * @throws StreemException
   */
  @Override
  public TaskExecutionDto resumeTask(Long taskId, TaskPauseOrResumeRequest taskPauseOrResumeRequest) throws StreemException {
    log.info("[resumeTask] Request to resume task: {} and TaskPauseOrResumeRequest :{} ", taskId, taskPauseOrResumeRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getReferenceById(principalUser.getId());
    TaskExecution taskExecution = taskExecutionRepository.readByJobIdAndTaskId(taskPauseOrResumeRequest.jobId(), taskId);
    List<TaskPauseReasonOrComment> pausedReason = new ArrayList<>();

    long now = DateTimeUtils.now();
    if (taskExecution.getState() == State.TaskExecution.PAUSED) {

      TaskExecutionTimer toBeResumedTimer = taskExecutionTimerRepository.findPausedTimerByTaskExecutionIdAndJobId(taskExecution.getId());
      toBeResumedTimer.setResumedAt(now);
      toBeResumedTimer.setModifiedBy(principalUserEntity);
      toBeResumedTimer.setModifiedAt(now);
      taskExecutionTimerRepository.save(toBeResumedTimer);

      pausedReason = taskExecutionTimerService.calculateDurationAndReturnReasonsOrComments(List.of(taskExecution)).get(taskExecution.getId());
      taskExecution.setState(State.TaskExecution.IN_PROGRESS);
      taskExecutionRepository.save(taskExecution);

    } else {
      ValidationUtils.invalidate(taskId, ErrorCode.TASK_IS_IN_NON_PAUSED_STATE);
    }
    jobAuditService.resumeTask(taskId, taskPauseOrResumeRequest, principalUser);
    return taskExecutionMapper.toDto(taskExecution, pausedReason);
  }

}
