package com.leucine.streem.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leucine.streem.collections.EntityObject;
import com.leucine.streem.collections.JobLogMediaData;
import com.leucine.streem.collections.MappedRelation;
import com.leucine.streem.collections.PropertyValue;
import com.leucine.streem.config.MediaConfig;
import com.leucine.streem.constant.*;
import com.leucine.streem.dto.*;
import com.leucine.streem.dto.mapper.*;
import com.leucine.streem.dto.request.ExecuteMediaPrameterRequest;
import com.leucine.streem.dto.request.ParameterExecuteRequest;
import com.leucine.streem.dto.request.ParameterRequest;
import com.leucine.streem.dto.request.ParameterStateChangeRequest;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.exception.ExceptionType;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.*;
import com.leucine.streem.model.helper.BaseEntity;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.model.helper.parameter.*;
import com.leucine.streem.repository.*;
import com.leucine.streem.service.*;
import com.leucine.streem.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
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
public class ParameterExecutionService implements IParameterExecutionService {
  private final IParameterValueRepository parameterValueRepository;
  private final IParameterMapper parameterMapper;
  private final IParameterValueMapper parameterValueMapper;
  private final IUserRepository userRepository;
  private final IMediaRepository mediaRepository;
  private final ITaskExecutionService taskExecutionService;
  private final ITempParameterValueRepository tempParameterValueRepository;
  private final IParameterValueApprovalRepository iParameterValueApprovalRepository;
  private final ITempParameterValueMapper tempParameterValueMapper;
  private final IJobRepository jobRepository;
  private final IUserMapper userMapper;
  private final IJobAuditService jobAuditService;
  private final ITempParameterValueMediaMapper tempParameterValueMediaMapper;
  private final IJobLogService jobLogService;
  private final MediaConfig mediaConfig;
  private final ObjectMapper objectMapper;
  private final IChecklistRelationService checklistRelationService;
  private final IParameterRepository parameterRepository;
  private final INotificationService notificationService;
  private final IEntityObjectService entityObjectService;
  private final IAutoInitializedParameterRepository autoInitializedParameterRepository;
  private final IParameterVerificationRepository parameterVerificationRepository;
  private final IParameterVerificationMapper parameterVerificationMapper;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ParameterDto executeParameter(Long jobId, ParameterExecuteRequest parameterExecuteRequest, boolean isAutoInitialized) throws IOException, StreemException, ResourceNotFoundException {
    log.info("[executeParameter] Request to execute task parameter, parameterExecuteRequest: {}", parameterExecuteRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return executeParameter(jobId, parameterExecuteRequest, isAutoInitialized, Type.JobLogTriggerType.PARAMETER_VALUE, principalUser);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ParameterDto executeParameter(Long jobId, ParameterExecuteRequest parameterExecuteRequest, boolean isAutoInitialized, Type.JobLogTriggerType jobLogTriggerType, PrincipalUser principalUser) throws IOException, StreemException, ResourceNotFoundException {
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    ParameterRequest parameterRequest = parameterExecuteRequest.getParameter();
    ParameterValue parameterValue = parameterValueRepository.readByParameterIdAndJobId(parameterRequest.getId(), jobId)
      .orElseThrow(() -> new ResourceNotFoundException(parameterExecuteRequest.getParameter().getId(), ErrorCode.PARAMETER_VALUE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    ParameterValueDto currentValueDto = parameterValueMapper.toDto(parameterValue);
    Parameter parameter = parameterValue.getParameter();
    if (parameterValue.getState().equals(State.ParameterExecution.APPROVAL_PENDING)) {
      ValidationUtils.invalidate(parameterValue.getParameterId(), ErrorCode.CANNOT_EXECUTE_VERIFICATION_PENDING_PARAMETER);
    }

    List<MediaDto> oldMedias = parameterMapper.getMedias(parameterValue.getMedias());

    // For auto initialized parameters, we do not need task validation
    if (!isAutoInitialized && Type.ParameterTargetEntityType.TASK.equals(parameter.getTargetEntityType())) {
      TaskExecution taskExecution = taskExecutionService.getTaskExecutionByJobAndTaskId(parameter.getTaskId(), jobId);
      validateJobState(parameterValue.getJob().getId(), Action.Parameter.EXECUTE, parameterValue.getJob().getState());

      validateTaskState(taskExecution.getState(), Action.Parameter.EXECUTE, parameter.getId());
      TaskExecutionUserMapping taskExecutionUserMapping = taskExecutionService.validateAndGetAssignedUser(parameter.getTask().getId(), taskExecution, principalUserEntity);
      taskExecutionService.updateUserAction(taskExecutionUserMapping);
    }

    //TODO accepting a response due to strange behaviour observed in
    // single select parameter not updating 'state' inspite of which
    // when debugging state seems to be updated
    parameterValue = setParameterResponse(parameter, parameterValue, parameterRequest.getData().toString(), parameterExecuteRequest.getReason(),
      jobId, false, null,
      parameterValue.getMedias(), null, principalUserEntity);

    parameterValue.setModifiedBy(principalUserEntity);
    parameterValue.setModifiedAt(DateTimeUtils.now());

    /* we are handling this here because for some parameters like checklist if all the items are not selected we set their state to BEING_EXECUTED.
    This state belongs to partially executed parameters as well as parameters that have verifications and are not executed*/

    if (!parameter.getVerificationType().equals(Type.VerificationType.NONE) && parameterValue.getState().equals(State.ParameterExecution.EXECUTED)) {
      parameterValue.setState(State.ParameterExecution.BEING_EXECUTED);
    }

    ParameterDto parameterDto = parameterMapper.toDto(parameter);
    ParameterValueDto parameterValueDto = parameterValueMapper.toDto(parameterValueRepository.save(parameterValue));
    if (!isAutoInitialized) {
      attachParameterVerifications(parameter, jobId, parameterValue, parameterValueDto);
    }
    parameterDto.setResponse(parameterValueDto);
    RuleHideShowDto ruleHideShowDto = updateRules(jobId, parameter);

    // TODO Update log parameter logic to read from parameter value, but also remember executions are done fast ? make use of queues ? the case is same with job logs
    UserAuditDto userBasicInfoDto = userMapper.toUserAuditDto(principalUserEntity);
    updateJobLog(jobId, parameterExecuteRequest.getParameter().getId(), parameter.getType(), parameterExecuteRequest.getReason(), parameter.getLabel(), jobLogTriggerType, userBasicInfoDto);

    findAndAutoInitializeAllTheParameterReferencedBy(parameterValue.getJob().getId(), parameter.getId());

    if (Type.ParameterTargetEntityType.TASK.equals(parameter.getTargetEntityType())) {
      jobAuditService.executedParameter(jobId, parameter.getId(), currentValueDto, oldMedias, parameter.getType(), false, parameterExecuteRequest.getReason(), principalUser);
    }

    parameterDto.setShow(ruleHideShowDto.getShow());
    parameterDto.setHide(ruleHideShowDto.getHide());
    return parameterDto;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public TempParameterDto executeParameterForError(ParameterExecuteRequest parameterExecuteRequest) throws IOException, StreemException, ResourceNotFoundException {
    log.info("[executeParameterForError] Request to fix error in parameter, parameterExecuteRequest: {}", parameterExecuteRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    ParameterRequest parameterRequest = parameterExecuteRequest.getParameter();
    ParameterValue parameterValue = parameterValueRepository.readByParameterIdAndJobId(parameterRequest.getId(), parameterExecuteRequest.getJobId())
      .orElseThrow(() -> new ResourceNotFoundException(parameterRequest.getId(), ErrorCode.PARAMETER_VALUE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Parameter parameter = parameterValue.getParameter();
    //TODO temp fix, find the right approach
    UserAuditDto modifiedBy = userMapper.toUserAuditDto(parameterValue.getModifiedBy());

    // TODO: this complete logic till throwing error is a workaround for task execution not having correction enabled on the task where auto initialize parameter is present
    TaskExecution taskExecution = taskExecutionService.getTaskExecutionByJobAndTaskId(parameter.getTaskId(), parameterExecuteRequest.getJobId());
    if (!taskExecution.isCorrectionEnabled()) {
      ValidationUtils.invalidate(parameter.getTaskId(), ErrorCode.DEPENDENT_TASK_NOT_ENABLED_FOR_CORRECTION);
    }

    TempParameterValue tempParameterValue = tempParameterValueRepository.findByParameterIdAndJobId(parameter.getId(), parameterExecuteRequest.getJobId())
      .orElseThrow(() -> new ResourceNotFoundException(parameterExecuteRequest.getParameter().getId(), ErrorCode.TEMP_PARAMETER_VALUE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    TempParameterValueDto oldValueDto = tempParameterValueMapper.toDto(tempParameterValue);

    List<MediaDto> oldMedias = parameterMapper.getMedias(parameterValue.getMedias());

    validateJobState(parameterValue.getJob().getId(), Action.Parameter.EXECUTE, parameterValue.getJob().getState());
    TempParameterDto tempParameterDto = parameterMapper.toTempParameterDto(parameter);
    TempParameterValueDto tempParameterValueDto = new TempParameterValueDto();
    //set responses

    tempParameterValue = setParameterResponse(parameter, tempParameterValue, parameterRequest.getData().toString(), parameterExecuteRequest.getReason(),
      parameterExecuteRequest.getJobId(), true, tempParameterValueDto, null, tempParameterValue.getMedias(), principalUserEntity);

    //This updates medias
    if (Type.PARAMETER_MEDIA_TYPES.contains(parameter.getType())) {
      tempParameterValueDto.setMedias(tempParameterValueMediaMapper.toDto(tempParameterValue.getMedias()));
    }

    PartialAuditDto partialAuditDto = new PartialAuditDto();
    partialAuditDto.setModifiedBy(modifiedBy);
    partialAuditDto.setModifiedAt(parameterValue.getModifiedAt());
    tempParameterValueDto.setAudit(partialAuditDto);
    tempParameterDto.setResponse(tempParameterValueDto);

    findAndAutoInitializeAllTheParameterReferencedByForErrorCorrection(parameterValue.getJob().getId(), parameter.getId());

    jobAuditService.executedParameter(parameterExecuteRequest.getJobId(), parameter.getId(), oldValueDto, oldMedias, parameter.getType(), true, parameterExecuteRequest.getReason(), principalUser);
    return tempParameterDto;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ParameterDto rejectParameter(ParameterStateChangeRequest parameterStateChangeRequest) throws ResourceNotFoundException, StreemException {
    log.info("[rejectParameter] Request to reject parameter, parameterStateChangeRequest: {}", parameterStateChangeRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Job job = jobRepository.findById(parameterStateChangeRequest.getJobId()).orElseThrow(() -> new ResourceNotFoundException(parameterStateChangeRequest.getJobId(), ErrorCode.JOB_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    ParameterValue parameterValue = parameterValueRepository.readByParameterIdAndJobId(parameterStateChangeRequest.getParameterId(), parameterStateChangeRequest.getJobId())
      .orElseThrow(() -> new ResourceNotFoundException(parameterStateChangeRequest.getParameterId(), ErrorCode.PARAMETER_VALUE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    validateReviewerRoleForParameterApproval(parameterStateChangeRequest.getParameterId());

    /*--Change the Job state First--*/
    job.setModifiedBy(principalUserEntity);
    job.setState(State.Job.IN_PROGRESS);
    jobRepository.save(job);

    Parameter parameter = parameterValue.getParameter();
    TaskExecution taskExecution = taskExecutionService.getTaskExecutionByJobAndTaskId(parameter.getTask().getId(), parameterStateChangeRequest.getJobId());

    validateJobState(parameterValue.getJob().getId(), Action.Parameter.REJECTED, parameterValue.getJob().getState());
    validateTaskState(taskExecution.getState(), Action.Parameter.REJECTED, parameter.getId());

    ParameterDto parameterDto = updateParameterState(parameterValue, State.ParameterExecution.BEING_EXECUTED_AFTER_REJECTED, principalUserEntity, State.ParameterValue.REJECTED);

    jobAuditService.rejectParameter(parameterStateChangeRequest.getJobId(), parameterDto, parameterStateChangeRequest.getParameterId(), principalUser);
    jobLogService.updateJobState(job.getIdAsString(), principalUser, job.getState());
    return parameterDto;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ParameterDto approveParameter(ParameterStateChangeRequest parameterStateChangeRequest) throws ResourceNotFoundException, StreemException {
    log.info("[approveParameter] Request to approve parameter, parameterStateChangeRequest: {}", parameterStateChangeRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Job job = jobRepository.findById(parameterStateChangeRequest.getJobId()).orElseThrow(() -> new ResourceNotFoundException(parameterStateChangeRequest.getJobId(), ErrorCode.JOB_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    ParameterValue parameterValue = parameterValueRepository.readByParameterIdAndJobId(parameterStateChangeRequest.getParameterId(), parameterStateChangeRequest.getJobId())
      .orElseThrow(() -> new ResourceNotFoundException(parameterStateChangeRequest.getParameterId(), ErrorCode.PARAMETER_VALUE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    validateReviewerRoleForParameterApproval(parameterStateChangeRequest.getParameterId());

    /*--Change the Job State First--*/
    job.setModifiedBy(principalUserEntity);
    job.setState(State.Job.IN_PROGRESS);
    jobRepository.save(job);

    Parameter parameter = parameterValue.getParameter();
    TaskExecution taskExecution = taskExecutionService.getTaskExecutionByJobAndTaskId(parameter.getTask().getId(), parameterStateChangeRequest.getJobId());

    validateJobState(parameterValue.getJob().getId(), Action.Parameter.APPROVED, parameterValue.getJob().getState());
    validateTaskState(taskExecution.getState(), Action.Parameter.APPROVED, parameter.getId());

    State.ParameterExecution executionState = State.ParameterExecution.EXECUTED;
    if (!Type.VerificationType.NONE.equals(parameter.getVerificationType())) {
      executionState = State.ParameterExecution.BEING_EXECUTED;
    }

    ParameterDto parameterDto = updateParameterState(parameterValue, executionState, principalUserEntity, State.ParameterValue.APPROVED);

    jobAuditService.approveParameter(parameterStateChangeRequest.getJobId(), parameterDto, parameterStateChangeRequest.getParameterId(), principalUser);
    jobLogService.updateJobState(job.getIdAsString(), principalUser, job.getState());
    return parameterDto;
  }

  //TODO validations to state management ?
  private void validateJobState(Long id, Action.Parameter action, State.Job state) throws StreemException {
    if (Action.Parameter.EXECUTE.equals(action) && State.JOB_COMPLETED_STATES.contains(state)) {
      ValidationUtils.invalidate(id, ErrorCode.JOB_ALREADY_COMPLETED);
    }
    if (!Action.PARAMETER_APROVAL_ACTIONS.contains(action) && State.Job.BLOCKED.equals(state)) {
      ValidationUtils.invalidate(id, ErrorCode.JOB_IS_BLOCKED);
    }
  }

  private void validateTaskState(State.TaskExecution taskExecutionState, Action.Parameter action, Long id) throws StreemException {
    if (Action.Parameter.EXECUTE.equals(action) && (State.TASK_COMPLETED_STATES.contains(taskExecutionState))) {
      ValidationUtils.invalidate(id, ErrorCode.CANNOT_EXECUTE_PARAMETER_ON_A_COMPLETED_TASK);
    }
    if (Action.Parameter.EXECUTE.equals(action) && State.TaskExecution.NOT_STARTED.equals(taskExecutionState)) {
      ValidationUtils.invalidate(id, ErrorCode.CANNOT_EXECUTE_PARAMETER_ON_A_NONSTARTED_TASK);
    }
  }

  private <T extends ParameterValueBase> T setParameterResponse(Parameter parameter, T parameterValue, String data,
                                                                String reason, Long jobId, boolean isExecutedForCorrection, TempParameterValueDto tempParameterValueDto,
                                                                List<ParameterValueMediaMapping> parameterValueMediaMappings, List<TempParameterValueMediaMapping> tempParameterValueMediaMappings, User principalUserEntity) throws IOException, StreemException, ResourceNotFoundException {
    Type.Parameter parameterType = parameter.getType();
    return switch (parameterType) {
      case CALCULATION ->
        executeCalculationParameter(parameterValue, parameter, jobId, data, isExecutedForCorrection, tempParameterValueDto, principalUserEntity);
      case CHECKLIST ->
        executeChecklistParameter(parameterValue, parameter, jobId, data, reason, isExecutedForCorrection, tempParameterValueDto, principalUserEntity);
      case DATE, DATE_TIME ->
        executeDateParameter(parameterValue, data, tempParameterValueDto, isExecutedForCorrection, parameter, jobId, principalUserEntity);
      case MEDIA, FILE_UPLOAD ->
        executeMediaParameter(parameterValue, parameter, data, parameterValueMediaMappings, tempParameterValueMediaMappings, principalUserEntity, isExecutedForCorrection);
      case MULTISELECT ->
        executeMultiSelectParameter(parameterValue, parameter, jobId, data, reason, isExecutedForCorrection, tempParameterValueDto, principalUserEntity);
      case NUMBER ->
        executeNumberParameter(parameterValue, data, tempParameterValueDto, isExecutedForCorrection, parameter, jobId, principalUserEntity);
      case SHOULD_BE ->
        executeShouldBeParameter(parameterValue, parameter, jobId, data, reason, isExecutedForCorrection, tempParameterValueDto, principalUserEntity);
      case RESOURCE, MULTI_RESOURCE ->
        executeResourceParameter(parameterValue, parameter, jobId, data, isExecutedForCorrection, tempParameterValueDto, principalUserEntity);
      case SINGLE_SELECT ->
        executeSingleSelectParameter(parameterValue, parameter, jobId, data, reason, isExecutedForCorrection, tempParameterValueDto, principalUserEntity);
      case SIGNATURE ->
        executeSignatureParameter(parameterValue, parameter, data, isExecutedForCorrection, parameterValueMediaMappings, tempParameterValueMediaMappings, principalUserEntity);
      case MULTI_LINE, SINGLE_LINE ->
        executeTextParameter(parameterValue, data, tempParameterValueDto, isExecutedForCorrection, parameter, jobId, principalUserEntity);
      case YES_NO ->
        executeYesNoParameter(parameterValue, parameter, jobId, data, reason, isExecutedForCorrection, tempParameterValueDto, principalUserEntity);
      default -> parameterValue;
    };
  }

  private <T extends ParameterValueBase> T executeMediaParameter(T parameterValue, Parameter parameter, String data,
                                                                 List<ParameterValueMediaMapping> parameterValueMediaMappings,
                                                                 List<TempParameterValueMediaMapping> tempParameterValueMediaMappings,
                                                                 User principalUserEntity, boolean isExecutedForCorrection) throws JsonProcessingException, StreemException {
    MediaParameterBase mediaParameterBase = (MediaParameterBase) JsonUtils.readValue(data, ParameterUtils.getClassForParameter(Type.Parameter.MEDIA));
    List<ExecuteMediaPrameterRequest> mediaRequestList = mediaParameterBase.getMedias();

    if (Utility.isEmpty(mediaRequestList)) {
      ValidationUtils.invalidate(parameter.getId(), ErrorCode.PARAMETER_EXECUTION_MEDIA_CANNOT_BE_EMPTY);
    }

    Map<Long, ExecuteMediaPrameterRequest> mediaRequestMap = mediaRequestList.stream().collect(Collectors.toMap(m -> Long.valueOf(m.getMediaId()), Function.identity()));
    Set<Long> mediaIds = mediaRequestList.stream().map(m -> Long.valueOf(m.getMediaId())).collect(Collectors.toSet());

    List<Media> mediaList = mediaRepository.findAll(mediaIds);

    for (Media media : mediaList) {
      if (Utility.trimAndCheckIfEmpty(mediaRequestMap.get(media.getId()).getName())) {
        ValidationUtils.invalidate(parameter.getId(), ErrorCode.PARAMETER_EXECUTION_MEDIA_NAME_CANNOT_BE_EMPTY);
      }
      media.setName(mediaRequestMap.get(media.getId()).getName());
      media.setDescription(mediaRequestMap.get(media.getId()).getDescription());
    }

    mediaRepository.saveAll(mediaList);
    Set<Long> existingMediaIds;
    if (isExecutedForCorrection) {
      existingMediaIds = tempParameterValueMediaMappings.stream().map(pvm -> pvm.getParameterValueMediaId().getMediaId()).collect(Collectors.toSet());
    } else {
      existingMediaIds = parameterValueMediaMappings.stream().map(pvm -> pvm.getParameterValueMediaId().getMediaId()).collect(Collectors.toSet());
    }

    List<Media> archivedMedias = new ArrayList<>();

    for (Media media : mediaList) {
      if (existingMediaIds.contains(media.getId())) {
        ExecuteMediaPrameterRequest mediaParameterRequest = mediaRequestMap.get(media.getId());
        if (mediaParameterRequest.getArchived()) {
          if (Utility.isEmpty(mediaParameterRequest.getReason())) {
            ValidationUtils.invalidate(parameter.getId(), ErrorCode.PARAMETER_EXECUTION_MEDIA_ARCHIVED_REASON_CANNOT_BE_EMPTY);
          }
          archivedMedias.add(media);
        }
      } else {
        parameterValue.addMedia(media, principalUserEntity);
      }
    }

    parameterValue.setState(State.ParameterExecution.EXECUTED);

    for (Media media : archivedMedias) {
      parameterValue.archiveMedia(media, principalUserEntity);
    }

    if (!isExecutedForCorrection) {
      boolean isNonArchivedMediaParameterExist = ((ParameterValue) parameterValue).getMedias().stream().anyMatch(m -> !m.isArchived());
      if (!isNonArchivedMediaParameterExist) {
        parameterValue.setState(State.ParameterExecution.BEING_EXECUTED);
      }
    } else {
      boolean isNonArchivedMediaParameterExist = ((TempParameterValue) parameterValue).getMedias().stream().anyMatch(m -> !m.isArchived());
      if (!isNonArchivedMediaParameterExist) {
        parameterValue.setState(State.ParameterExecution.BEING_EXECUTED);
      }
    }

    return parameterValue;
  }

  private <T extends ParameterValueBase> T executeSignatureParameter(T parameterValue, Parameter parameter, String data, boolean isExecutedForCorrection,
                                                                     List<ParameterValueMediaMapping> parameterValueMediaMappings,
                                                                     List<TempParameterValueMediaMapping> tempParameterValueMediaMappings,
                                                                     User principalUserEntity) throws JsonProcessingException, StreemException {
    MediaParameter mediaParameter = JsonUtils.readValue(data, MediaParameter.class);
    List<ExecuteMediaPrameterRequest> mediaDtoList = mediaParameter.getMedias();

    if (Utility.isEmpty(mediaDtoList)) {
      ValidationUtils.invalidate(parameter.getId(), ErrorCode.PARAMETER_EXECUTION_MEDIA_CANNOT_BE_EMPTY);
    }

    Media media = mediaRepository.findById(Long.valueOf(mediaDtoList.get(0).getMediaId())).get();
    parameterValue.setState(State.ParameterExecution.EXECUTED);

    if (!isExecutedForCorrection) {
      if (null != parameterValueMediaMappings) {
        for (ParameterValueMediaMapping parameterValueMediaMapping : parameterValueMediaMappings) {
          parameterValue.archiveMedia(parameterValueMediaMapping.getMedia(), principalUserEntity);
        }
      }
    } else {
      if (null != tempParameterValueMediaMappings) {
        for (TempParameterValueMediaMapping tempParameterValueMediaMapping : tempParameterValueMediaMappings) {
          parameterValue.archiveMedia(tempParameterValueMediaMapping.getMedia(), principalUserEntity);
        }
      }
    }

    parameterValue.addMedia(media, principalUserEntity);
    return parameterValue;
  }

  private <T extends ParameterValueBase> T executeTextParameter(T parameterValue, String data, TempParameterValueDto tempParameterValueDto, boolean isExecutedForCorrection, Parameter parameter, Long jobId, User principalUserEntity) throws JsonProcessingException {
    TextParameter multiLineParameter = JsonUtils.readValue(data, TextParameter.class);
    State.ParameterExecution parameterExecutionState = (!Utility.isEmpty(multiLineParameter.getInput())) ? State.ParameterExecution.EXECUTED : State.ParameterExecution.BEING_EXECUTED;

    if (isExecutedForCorrection) {
      tempParameterValueRepository.updateParameterValuesAndState(jobId, parameter.getId(), multiLineParameter.getInput(), parameterExecutionState.name(), principalUserEntity.getId(), DateTimeUtils.now());
      tempParameterValueDto.setValue(multiLineParameter.getInput());
    } else {
      parameterValue.setValue(multiLineParameter.getInput());
      parameterValue.setState(parameterExecutionState);
    }

    return parameterValue;
  }

  private <T extends ParameterValueBase> T executeYesNoParameter(T parameterValue, Parameter parameter, Long jobId, String data, String reason,
                                                                 boolean isExecutedForCorrection, TempParameterValueDto tempParameterValueDto, User principalUserEntity) throws IOException, StreemException {
    Map<String, String> parameterChoices = new HashMap<>();
    List<YesNoParameter> yesNoParameters = JsonUtils.jsonToCollectionType(data, List.class, YesNoParameter.class);
    for (YesNoParameter baseParameter : yesNoParameters) {
      String id = baseParameter.getId();
      String state = baseParameter.getState();
      if (State.Selection.SELECTED.equals(State.Selection.valueOf(state))) {
        parameterChoices.put(id, State.Selection.SELECTED.name());
        //TODO remove the types - the checklist data already has lower case
        if (baseParameter.getType().equals("no")) {
          ValidationUtils.validateNotEmpty(reason, parameter.getId(), ErrorCode.PROVIDE_REASON_FOR_YES_NO_PARAMETER);
        } else {
          reason = "";
        }
      } else {
        parameterChoices.put(id, State.Selection.NOT_SELECTED.name());
      }
    }

    if (isExecutedForCorrection) {
      JsonNode jsonNode = JsonUtils.valueToNode(parameterChoices);
      tempParameterValueRepository.updateParameterChoicesAndReasonAndState(jobId, parameter.getId(), jsonNode.toString(), reason, State.ParameterExecution.EXECUTED.name(), principalUserEntity.getId(), DateTimeUtils.now());
      tempParameterValueDto.setChoices(jsonNode);
    } else {
      parameterValue.setChoices(JsonUtils.valueToNode(parameterChoices));
      parameterValue.setState(State.ParameterExecution.EXECUTED);
      parameterValue.setReason(reason);
    }

    return parameterValue;
  }

  private <T extends ParameterValueBase> T executeChecklistParameter(T parameterValue, Parameter parameter, Long jobId, String data, String reason,
                                                                     boolean isExecutedForCorrection, TempParameterValueDto tempParameterValueDto, User principalUserEntity) throws IOException, StreemException {
    Map<String, String> parameterChoices = new HashMap<>();
    List<ChecklistParameter> parameters = JsonUtils.jsonToCollectionType(data, List.class, ChecklistParameter.class);
    // set update id and choice selections
    boolean isAllSelected = true;
    for (ChecklistParameter checklistParameter : parameters) {
      String id = checklistParameter.getId();
      String state = checklistParameter.getState();
      if (State.Selection.SELECTED.equals(State.Selection.valueOf(state))) {
        parameterChoices.put(id, State.Selection.SELECTED.name());
      } else {
        isAllSelected = false;
        parameterChoices.put(id, State.Selection.NOT_SELECTED.name());
      }
    }
    JsonNode jsonNode = JsonUtils.valueToNode(parameterChoices);
    State.ParameterExecution parameterExecutionState = (parameter.getData().size() == parameterChoices.size() && isAllSelected) ? State.ParameterExecution.EXECUTED : State.ParameterExecution.BEING_EXECUTED;

    if (!isExecutedForCorrection) {
      parameterValue.setChoices(jsonNode);
      parameterValue.setState(parameterExecutionState);
    } else {
      tempParameterValueRepository.updateParameterChoicesAndState(jobId, parameter.getId(), jsonNode.toString(), parameterExecutionState.name(), principalUserEntity.getId(), DateTimeUtils.now());
      tempParameterValueDto.setChoices(jsonNode);
      tempParameterValueDto.setState(parameterExecutionState);
    }

    return parameterValue;
  }

  private <T extends ParameterValueBase> T executeMultiSelectParameter(T parameterValue, Parameter parameter, Long jobId, String data, String reason,
                                                                       boolean isExecutedForCorrection, TempParameterValueDto tempParameterValueDto, User principalUserEntity) throws IOException, StreemException {
    Map<String, String> parameterChoices = new HashMap<>();
    List<MultiSelectParameter> parameters = JsonUtils.jsonToCollectionType(data, List.class, MultiSelectParameter.class);
    // set update id and choice selections
    boolean isNoneSelected = true;
    for (MultiSelectParameter multiSelectParameter : parameters) {
      String id = multiSelectParameter.getId();
      String state = multiSelectParameter.getState();
      if (State.Selection.SELECTED.equals(State.Selection.valueOf(state))) {
        isNoneSelected = false;
        parameterChoices.put(id, State.Selection.SELECTED.name());
      } else {
        parameterChoices.put(id, State.Selection.NOT_SELECTED.name());
      }
    }
    JsonNode jsonNode = JsonUtils.valueToNode(parameterChoices);
    State.ParameterExecution parameterExecutionState = (!Utility.isEmpty(parameterChoices) && !isNoneSelected) ? State.ParameterExecution.EXECUTED : State.ParameterExecution.BEING_EXECUTED;

    if (!isExecutedForCorrection) {
      parameterValue.setChoices(jsonNode);
      parameterValue.setState(parameterExecutionState);
    } else {
      tempParameterValueRepository.updateParameterChoicesAndState(jobId, parameter.getId(), jsonNode.toString(), parameterExecutionState.name(), principalUserEntity.getId(), DateTimeUtils.now());
      tempParameterValueDto.setChoices(jsonNode);
      tempParameterValueDto.setState(parameterExecutionState);
    }

    return parameterValue;
  }

  private <T extends ParameterValueBase> T executeSingleSelectParameter(T parameterValue, Parameter parameter, Long jobId, String data, String reason, boolean isExecutedForCorrection, TempParameterValueDto tempParameterValueDto, User principalUserEntity) throws IOException {
    Map<String, String> parameterChoices = new HashMap<>();
    List<SingleSelectParameter> parameters = JsonUtils.jsonToCollectionType(data, List.class, SingleSelectParameter.class);
    // set update id and choice selections
    boolean isNoneSelected = updateParameterChoicesAndReturnSelectionState(parameterChoices, parameters);
    JsonNode jsonNode = JsonUtils.valueToNode(parameterChoices);
    State.ParameterExecution parameterExecutionState = (!Utility.isEmpty(parameterChoices) && !isNoneSelected) ? State.ParameterExecution.EXECUTED : State.ParameterExecution.BEING_EXECUTED;

    if (!isExecutedForCorrection) {
      parameterValue.setChoices(jsonNode);
      parameterValue.setState(parameterExecutionState);
    } else {
      tempParameterValueRepository.updateParameterChoicesAndState(jobId, parameter.getId(), jsonNode.toString(), parameterExecutionState.name(), principalUserEntity.getId(), DateTimeUtils.now());
      tempParameterValueDto.setChoices(jsonNode);
      tempParameterValueDto.setState(parameterExecutionState);
    }

    return parameterValue;
  }

  private <T extends ParameterValueBase> T executeShouldBeParameter(T parameterValue, Parameter parameter, Long jobId, String data, String reason,
                                                                    boolean isExecutedForCorrection, TempParameterValueDto tempParameterValueDto, User principalUserEntity) throws IOException, StreemException, ResourceNotFoundException {
    ShouldBeParameter shouldBeParameter = JsonUtils.readValue(data, ShouldBeParameter.class);
    if (Utility.isEmpty(shouldBeParameter.getInput()) || !Utility.isNumeric(shouldBeParameter.getInput())) {
      ValidationUtils.invalidate(parameter.getId(), ErrorCode.SHOULD_BE_PARAMETER_VALUE_INVALID);
    }
    double input = Double.parseDouble(shouldBeParameter.getInput());
    String offLimitsReason = "";
    boolean isInvalidState = false;
    Operator.Parameter operator = Operator.Parameter.valueOf(shouldBeParameter.getOperator());

    switch (operator) {
      case BETWEEN:
        double lowerValue = Double.parseDouble(shouldBeParameter.getLowerValue());
        double upperValue = Double.parseDouble(shouldBeParameter.getUpperValue());
        if (input < lowerValue || input > upperValue) {
          ValidationUtils.validateNotEmpty(reason, parameter.getId(), ErrorCode.PROVIDE_REASON_FOR_PARAMETER_PARAMETER_OFF_LIMITS);
          offLimitsReason = reason;
          isInvalidState = true;
        }
        break;
      case EQUAL_TO:
        double value = Double.parseDouble(shouldBeParameter.getValue());
        if (input != value) {
          ValidationUtils.validateNotEmpty(reason, parameter.getId(), ErrorCode.PROVIDE_REASON_FOR_PARAMETER_PARAMETER_OFF_LIMITS);
          offLimitsReason = reason;
          isInvalidState = true;
        }
        break;
      case LESS_THAN:
        value = Double.parseDouble(shouldBeParameter.getValue());
        if (input >= value) {
          ValidationUtils.validateNotEmpty(reason, parameter.getId(), ErrorCode.PROVIDE_REASON_FOR_PARAMETER_PARAMETER_OFF_LIMITS);
          offLimitsReason = reason;
          isInvalidState = true;
        }
        break;
      case LESS_THAN_EQUAL_TO:
        value = Double.parseDouble(shouldBeParameter.getValue());
        if (input > value) {
          ValidationUtils.validateNotEmpty(reason, parameter.getId(), ErrorCode.PROVIDE_REASON_FOR_PARAMETER_PARAMETER_OFF_LIMITS);
          offLimitsReason = reason;
          isInvalidState = true;
        }
        break;
      case MORE_THAN:
        value = Double.parseDouble(shouldBeParameter.getValue());
        if (input <= value) {
          ValidationUtils.validateNotEmpty(reason, parameter.getId(), ErrorCode.PROVIDE_REASON_FOR_PARAMETER_PARAMETER_OFF_LIMITS);
          offLimitsReason = reason;
          isInvalidState = true;
        }
        break;
      case MORE_THAN_EQUAL_TO:
        value = Double.parseDouble(shouldBeParameter.getValue());
        if (input < value) {
          ValidationUtils.validateNotEmpty(reason, parameter.getId(), ErrorCode.PROVIDE_REASON_FOR_PARAMETER_PARAMETER_OFF_LIMITS);
          offLimitsReason = reason;
          isInvalidState = true;
        }
        break;
    }


    if (isExecutedForCorrection) {
      tempParameterValueRepository.updateParameterValuesWithReason(jobId, parameter.getId(), shouldBeParameter.getInput(), offLimitsReason,
        principalUserEntity.getId(), DateTimeUtils.now());
      tempParameterValueDto.setReason(offLimitsReason);
    } else if (isInvalidState && !validatePrincipleUserRole(Misc.SHOULD_BE_PARAMETER_REVIEWER)) {
      // For deviated parameters send for approval if roles doesn't belong to the above set
      /*--Check for Invalid State--*/
      parameterValue.setReason(offLimitsReason);
      parameterValue.setValue(shouldBeParameter.getInput());
      handleParameterApprovalRequest(parameterValue);
    } else {
      /*--removed approver if present--*/
      if (parameterValue.getParameterValueApproval() != null) {
        iParameterValueApprovalRepository.delete(parameterValue.getParameterValueApproval());
        parameterValue.setParameterValueApproval(null);
      }
      parameterValue.setReason(offLimitsReason);
      parameterValue.setValue(shouldBeParameter.getInput());
      parameterValue.setState(State.ParameterExecution.EXECUTED);
    }

    return parameterValue;
  }

  private <T extends ParameterValueBase> T executeCalculationParameter(T parameterValue, Parameter parameter, Long jobId, String data,
                                                                       boolean isExecutedForCorrection, TempParameterValueDto tempParameterValueDto, User principalUserEntity) throws IOException, StreemException {
    CalculationParameter calculationParameter = JsonUtils.readValue(data, CalculationParameter.class);

    List<CalculationParameterVariableChoices> variableChoicesList = new ArrayList<>();

    String expression = calculationParameter.getExpression();
    var variablesMap = calculationParameter.getVariables();

    List<Long> parameterIds = calculationParameter.getVariables().values().stream()
      .map(cav -> Long.valueOf(cav.getParameterId())).collect(Collectors.toList());

    Map<Long, ParameterValueBase> parameterValuesMap;
    if (isExecutedForCorrection) {
      List<TempParameterValue> parameterValues = tempParameterValueRepository.findByJobIdAndParameterIdsIn(jobId, parameterIds);
      parameterValuesMap = parameterValues.stream().collect(Collectors.toMap(pv -> pv.getParameter().getId(), act -> act));
    } else {
      List<ParameterValue> parameterValues = parameterValueRepository.findByJobIdAndParameterIdsIn(jobId, parameterIds);
      parameterValuesMap = parameterValues.stream().collect(Collectors.toMap(pv -> pv.getParameter().getId(), act -> act));
    }

    var variables = new HashSet<String>();
    Map<String, Double> variableValueMap = new HashMap<>();

    if (!Utility.isEmpty(calculationParameter.getVariables())) {
      for (Map.Entry<String, CalculationParameterVariable> entry : variablesMap.entrySet()) {
        String expressionVariable = entry.getKey();
        CalculationParameterVariable variable = entry.getValue();

        ParameterValueBase value = parameterValuesMap.get(Long.valueOf(variable.getParameterId()));

        if (Utility.isEmpty(value.getValue())) {
          ValidationUtils.invalidate(variable.getParameterId(), ErrorCode.CALCULATION_PARAMETER_DEPENDENT_PARAMETER_VALUES_NOT_SET);
        }

        Double inputValueForVariable = Double.valueOf(value.getValue());
        variables.add(expressionVariable);
        variableValueMap.put(expressionVariable, inputValueForVariable);

        CalculationParameterVariableChoices choice = new CalculationParameterVariableChoices();
        choice.setParameterId(variable.getParameterId());
        choice.setValue(value.getValue());
        variableChoicesList.add(choice);
      }
    }

    Expression e = new ExpressionBuilder(expression)
      .variables(variables)
      .build()
      .setVariables(variableValueMap);

    double result = Utility.roundUpDecimalPlaces(e.evaluate());

    JsonNode jsonNode = JsonUtils.valueToNode(variableChoicesList);

    if (!isExecutedForCorrection) {
      parameterValue.setChoices(jsonNode);
      parameterValue.setValue(String.valueOf(result));
      parameterValue.setState(State.ParameterExecution.EXECUTED);
    } else {
      tempParameterValueRepository.updateParameterChoicesAndState(jobId, parameter.getId(), jsonNode.toString(),
        State.ParameterExecution.EXECUTED.name(), principalUserEntity.getId(), DateTimeUtils.now());
      tempParameterValueRepository.updateParameterValuesAndState(jobId, parameter.getId(), String.valueOf(result),
        State.ParameterExecution.EXECUTED.name(), principalUserEntity.getId(), DateTimeUtils.now());
      tempParameterValueDto.setValue(String.valueOf(result));
      tempParameterValueDto.setChoices(jsonNode);
      tempParameterValueDto.setState(State.ParameterExecution.EXECUTED);
    }

    return parameterValue;
  }

  private <T extends ParameterValueBase> T executeNumberParameter(T parameterValue, String data, TempParameterValueDto tempParameterValueDto, boolean isExecutedForCorrection, Parameter parameter, Long jobId, User principalUserEntity) throws IOException, StreemException, ResourceNotFoundException {
    NumberParameter numberParameter = JsonUtils.readValue(data, NumberParameter.class);

    if (Utility.isNull(numberParameter.getInput()) || !Utility.isNumeric(numberParameter.getInput())) {
      ValidationUtils.invalidate(parameterValue.getId(), ErrorCode.NUMBER_PARAMETER_INVALID_VALUE);
    }

    checklistRelationService.validateParameterRelation(jobId, parameter, numberParameter.getInput());

    if (isExecutedForCorrection) {
      tempParameterValueDto.setValue(numberParameter.getInput());
      tempParameterValueRepository.updateParameterValuesAndState(jobId, parameter.getId(), tempParameterValueDto.getValue(), State.ParameterExecution.EXECUTED.name(), principalUserEntity.getId(), DateTimeUtils.now());
    } else {
      parameterValue.setValue(numberParameter.getInput());
      parameterValue.setState(State.ParameterExecution.EXECUTED);
    }

    return parameterValue;
  }

  private <T extends ParameterValueBase> T executeResourceParameter(T parameterValue, Parameter parameter, Long jobId, String data,
                                                                    boolean isExecutedForCorrection, TempParameterValueDto tempParameterValueDto, User principalUserEntity) throws IOException, StreemException, ResourceNotFoundException {
    ResourceParameter resourceParameter = JsonUtils.readValue(data, ResourceParameter.class);

    for (ResourceParameterChoiceDto resourceParameterChoiceDto : resourceParameter.getChoices()) {
      checklistRelationService.validateParameterValueChoice(resourceParameter.getObjectTypeExternalId(), resourceParameterChoiceDto.getObjectId(), resourceParameter.getPropertyValidations());
    }
    JsonNode jsonNode = JsonUtils.valueToNode(resourceParameter.getChoices());

    if (!isExecutedForCorrection) {
      parameterValue.setChoices(jsonNode);
      parameterValue.setState(State.ParameterExecution.EXECUTED);
    } else {
      tempParameterValueRepository.updateParameterChoicesAndState(jobId, parameter.getId(), jsonNode.toString(),
        State.ParameterExecution.EXECUTED.name(), principalUserEntity.getId(), DateTimeUtils.now());
      tempParameterValueDto.setChoices(jsonNode);
      tempParameterValueDto.setState(State.ParameterExecution.EXECUTED);
    }

    return parameterValue;
  }

  private <T extends ParameterValueBase> T executeDateParameter(T parameterValue, String data, TempParameterValueDto tempParameterValueDto, boolean isExecutedForCorrection, Parameter parameter, Long jobId, User principalUserEntity) throws IOException, StreemException, ResourceNotFoundException {
    DateParameter dateParameter = JsonUtils.readValue(data, DateParameter.class);

    if (Utility.isNull(dateParameter.getInput())) {
      ValidationUtils.invalidate(parameterValue.getId(), ErrorCode.DATE_PARAMETER_INVALID_VALUE);
    }

    if (isExecutedForCorrection) {
      tempParameterValueDto.setValue(dateParameter.getInput());
      tempParameterValueRepository.updateParameterValuesAndState(jobId, parameter.getId(), tempParameterValueDto.getValue(), State.ParameterExecution.EXECUTED.name(), principalUserEntity.getId(), DateTimeUtils.now());
    } else {
      parameterValue.setValue(dateParameter.getInput());
      parameterValue.setState(State.ParameterExecution.EXECUTED);
    }

    return parameterValue;
  }

  private boolean hasRole(List<RoleDto> roles, Set<String> role) {
    for (RoleDto roleData : roles) {
      if (role.contains(roleData.getName())) {
        return true;
      }
    }
    return false;
  }

  private boolean validatePrincipleUserRole(Set<String> roles) {
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return hasRole(principalUser.getRoles(), roles);
  }

  private void validateReviewerRoleForParameterApproval(Long parameterId) throws StreemException {
    if (!validatePrincipleUserRole(Misc.SHOULD_BE_PARAMETER_REVIEWER)) {
      ValidationUtils.invalidate(parameterId, ErrorCode.ONLY_SUPERVISOR_CAN_APPROVE_OR_REJECT_PARAMETER);
    }
  }

  private <T extends ParameterValueBase> void handleParameterApprovalRequest(T parameterValue) throws ResourceNotFoundException, StreemException, IOException {
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());

    /*--Change the Job State First--*/
    Job job = parameterValue.getJob();
    job.setModifiedBy(principalUserEntity);
    job.setState(State.Job.BLOCKED);
    jobRepository.save(job);

    Parameter parameter = parameterValue.getParameter();
    TaskExecution taskExecution = taskExecutionService.getTaskExecutionByJobAndTaskId(parameter.getTask().getId(), parameterValue.getJob().getId());
    taskExecutionService.validateAndGetAssignedUser(parameter.getTask().getId(), taskExecution, principalUserEntity);

    validateJobState(parameterValue.getJob().getId(), Action.Parameter.PENDING_FOR_APPROVAL, parameterValue.getJob().getState());
    validateTaskState(taskExecution.getState(), Action.Parameter.PENDING_FOR_APPROVAL, parameter.getId());

    /*Notify the Approvers*/
    notificationService.notifyAllShouldBeParameterReviewersForApproval(parameterValue.getJob().getId(), principalUser.getOrganisationId());

    parameterValue.setState(State.ParameterExecution.PENDING_FOR_APPROVAL);

    jobLogService.updateJobState(job.getIdAsString(), principalUser, job.getState());
  }

  private ParameterDto updateParameterState(ParameterValue parameterValue, State.ParameterExecution state, User approver, State.ParameterValue parameterApprovalState) {
    ParameterDto parameterDto = parameterMapper.toDto(parameterValue.getParameter());
    parameterValue.setState(state);
    if (parameterValue.getParameterValueApproval() == null) {
      parameterValue.setParameterValueApproval(new ParameterValueApproval());
    }
    parameterValue.getParameterValueApproval().setUser(approver);
    parameterValue.getParameterValueApproval().setCreatedAt(DateTimeUtils.now());
    parameterValue.getParameterValueApproval().setState(parameterApprovalState);
    parameterDto.setResponse(parameterValueMapper.toDto(parameterValueRepository.save(parameterValue)));
    return parameterDto;
  }

  @Override
  public void updateJobLog(Long jobId, Long parameterId, Type.Parameter parameterType, String parameterValueReason,
                           String label, Type.JobLogTriggerType triggerType, UserAuditDto userAuditDto) {
    try {
      Parameter parameter = parameterRepository.findById(parameterId).get();
      ParameterValue parameterValue = parameterValueRepository.findByJobIdAndParameterId(jobId, parameter.getId());
      switch (parameterType) {
        case MULTI_LINE, SINGLE_LINE, SHOULD_BE, NUMBER -> {
          jobLogService.recordJobLogTrigger(String.valueOf(jobId), String.valueOf(parameter.getId()), triggerType, label, null, parameterValue.getValue(), parameterValue.getValue(), userAuditDto);
        }
        case DATE, DATE_TIME -> {
          String formattedDate = DateTimeUtils.getFormattedDate(Long.parseLong(parameterValue.getValue()));
          jobLogService.recordJobLogTrigger(String.valueOf(jobId), String.valueOf(parameter.getId()), triggerType, label, null, formattedDate, formattedDate, userAuditDto);
        }
        case CALCULATION -> {
          CalculationParameter calculationParameter = JsonUtils.readValue(parameter.getData().toString(), CalculationParameter.class);
          String valueWithUOM = parameterValue.getValue() + Utility.SPACE + calculationParameter.getUom();
          jobLogService.recordJobLogTrigger(String.valueOf(jobId), String.valueOf(parameter.getId()), triggerType, label, null, valueWithUOM, valueWithUOM, userAuditDto);
        }
        case CHECKLIST, MULTISELECT, SINGLE_SELECT -> {
          List<ChoiceParameterBase> parameters = JsonUtils.jsonToCollectionType(parameter.getData().toString(), List.class, ChoiceParameterBase.class);
          Map<String, String> optionsNameMap = parameters.stream().collect(
            Collectors.toMap(ChoiceParameterBase::getId, ChoiceParameterBase::getName));
          JsonNode oldChoices = parameterValue.getChoices();
          Map<String, String> result = objectMapper.convertValue(oldChoices, new TypeReference<>() {
          });
          List<String> selectedItems = new ArrayList<>();
          List<String> selectedIdentifierItems = new ArrayList<>();

          for (ChoiceParameterBase choiceParameter : parameters) {
            String state = result.get(choiceParameter.getId());
            if (State.Selection.SELECTED.name().equals(state)) {
              selectedItems.add(optionsNameMap.get(choiceParameter.getId()));
              selectedIdentifierItems.add(choiceParameter.getId());
            }
          }
          String value = String.join(",", selectedItems);
          String identifierValue = String.join(",", selectedIdentifierItems);

          jobLogService.recordJobLogTrigger(String.valueOf(jobId), String.valueOf(parameter.getId()), triggerType, label, null, value, identifierValue, userAuditDto);
        }
        case YES_NO -> {
          List<YesNoParameter> parameters = JsonUtils.jsonToCollectionType(parameter.getData().toString(), List.class, YesNoParameter.class);
          Map<String, String> optionsNameMap = parameters.stream().collect(
            Collectors.toMap(ChoiceParameterBase::getId, ChoiceParameterBase::getName));
          ParameterValue activityChoiceValue = parameterValueRepository.findByJobIdAndParameterId(jobId, parameter.getId());
          JsonNode oldChoices = activityChoiceValue.getChoices();
          Map<String, String> result = objectMapper.convertValue(oldChoices, new TypeReference<>() {
          });
          List<String> selectedItems = new ArrayList<>();
          List<String> selectedIdentifierItems = new ArrayList<>();

          String reason = "";
          for (YesNoParameter yesNoParameter : parameters) {
            String state = result.get(yesNoParameter.getId());
            if (State.Selection.SELECTED.name().equals(state)) {
              selectedItems.add(optionsNameMap.get(yesNoParameter.getId()));
              selectedIdentifierItems.add(yesNoParameter.getId());

              if (yesNoParameter.getType().equals("no")) {
                reason = parameterValueReason;
              } else {
                reason = "";
              }
            }
          }
          String value = String.join(",", selectedItems);
          String identifierValue = String.join(",", selectedIdentifierItems);

          if (!Utility.isEmpty(reason)) {
            value = String.join(",", value, reason);
          }
          jobLogService.recordJobLogTrigger(String.valueOf(jobId), String.valueOf(parameter.getId()), triggerType, label, null, value, identifierValue, userAuditDto);
        }
        case MEDIA, FILE_UPLOAD, SIGNATURE -> {
          List<ParameterValueMediaMapping> medias = parameterValue.getMedias();
          List<JobLogMediaData> jobLogMedias = new ArrayList<>();
          for (ParameterValueMediaMapping parameterValueMediaMapping : medias) {
            Media parameterValueMedia = parameterValueMediaMapping.getMedia();
            JobLogMediaData jobLogMediaData = new JobLogMediaData();

            jobLogMediaData.setName(Type.Parameter.SIGNATURE.equals(parameterType) ? "Signature" : parameterValueMedia.getName());
            jobLogMediaData.setType(parameterValueMedia.getType());
            jobLogMediaData.setDescription(parameterValueMedia.getDescription());
            String link = mediaConfig.getCdn() + java.io.File.separator + parameterValueMedia.getRelativePath() + java.io.File.separator + parameterValueMedia.getFilename();
            jobLogMediaData.setLink(link);
            jobLogMedias.add(jobLogMediaData);
          }

          jobLogService.recordJobLogTrigger(String.valueOf(jobId), String.valueOf(parameter.getId()), triggerType, label, jobLogMedias, null, null, userAuditDto);
        }
        case RESOURCE, MULTI_RESOURCE -> {
          ResourceParameter resourceParameter = JsonUtils.readValue(parameter.getData().toString(), ResourceParameter.class);
          List<ResourceParameterChoiceDto> choices = JsonUtils.jsonToCollectionType(parameterValue.getChoices(), List.class, ResourceParameterChoiceDto.class);
          List<String> selectedItems = new ArrayList<>();
          List<String> selectedIdentifierItems = new ArrayList<>();
          for (ResourceParameterChoiceDto choice : choices) {
            selectedIdentifierItems.add(choice.getObjectId());
            selectedItems.add(choice.getObjectExternalId());
          }
          String value = String.join(",", selectedItems);
          String identifierValue = String.join(",", selectedIdentifierItems);
          jobLogService.recordJobLogTrigger(String.valueOf(jobId), String.valueOf(parameter.getId()), Type.JobLogTriggerType.RESOURCE_PARAMETER,
            label, null, value, identifierValue, userAuditDto);
          jobLogService.recordJobLogResource(String.valueOf(jobId), String.valueOf(parameter.getId()), parameter.getLabel(),
            resourceParameter.getObjectTypeId().toString(), resourceParameter.getObjectTypeDisplayName(), choices);
        }
      }
    } catch (Exception ex) {
      log.error("[updateJobLog] error updating job log for parameter value", ex);
    }

  }

  /**
   * @param parameterExecuteRequestMap List of all CJF parameters whose rules are to be evaluated
   * @param checklistId
   * @return
   * @throws IOException
   */
  @Override
  public RuleHideShowDto tempExecuteRules(Map<Long, ParameterExecuteRequest> parameterExecuteRequestMap, Long checklistId) throws IOException {
    log.info("[tempExecuteRules] parameterExecuteRequestMap: {}, checklistId: {}", parameterExecuteRequestMap, checklistId);
    List<ParameterRequest> parameterRequestList = new ArrayList<>();
    for (Map.Entry<Long, ParameterExecuteRequest> entry : parameterExecuteRequestMap.entrySet()) {
      parameterRequestList.add(entry.getValue().getParameter());
    }
    return executeRulesTemporarily(parameterRequestList, checklistId);

  }

  /**
   * @param jobId     To apply rules on a job with id
   * @param parameter To apply rules present on parameter
   * @return returns the list of rules to be hidden and shown
   * @throws IOException
   */


  private RuleHideShowDto updateRules(Long jobId, Parameter parameter) throws IOException {
    Set<Long> show = new HashSet<>();
    Set<Long> hide = new HashSet<>();
    Map<Long, Set<RuleImpactedByDto>> rulesImpactedByMap = new HashMap<>();
    if (!Utility.isEmpty(parameter.getRules())) {
      getCurrentHiddenState(parameter, jobId, show, hide, rulesImpactedByMap);
    }
    return evaluateRules(jobId, parameter, show, hide, rulesImpactedByMap);
  }

  /**
   * This function is used to get current hidden state of a parameter and calculate impactedBy
   *
   * @param parameter          To apply rules present on parameter
   * @param jobId              To apply rules on a job with id
   * @param show               Set of rules to be shown
   * @param hide               Set of rules to be hidden
   * @param rulesImpactedByMap Map of rules impacted by a parameter where key is parameterId of impacted parameter and value is set of rules ids and impacting parameter ids
   * @throws IOException
   */
  private void getCurrentHiddenState(Parameter parameter, Long jobId, Set<Long> show, Set<Long> hide, Map<Long, Set<RuleImpactedByDto>> rulesImpactedByMap) throws IOException {
    List<RuleDto> parameterRules = JsonUtils.jsonToCollectionType(parameter.getRules(), List.class, RuleDto.class);

    // To get all the impacted parameters from the rules
    Set<Long> parameterAffectedByRule = new HashSet<>();
    parameterRules.stream()
      .filter(ruleDto -> !Utility.isEmpty(ruleDto.getHide()))
      .flatMap(ruleDto -> ruleDto.getHide().getParameters().stream().map(Long::valueOf))
      .forEach(parameterAffectedByRule::add);

    parameterRules.stream()
      .filter(ruleDto -> !Utility.isEmpty(ruleDto.getShow()))
      .flatMap(ruleDto -> ruleDto.getShow().getParameters().stream().map(Long::valueOf))
      .forEach(parameterAffectedByRule::add);


    var parameterValues = parameterValueRepository.findByJobIdAndParameterIdsIn(jobId, parameterAffectedByRule);
    for (ParameterValue parameterValue : parameterValues) {
      //TODO: Find why parameterValue.getParameterId() is null but not below
      if (parameterValue.isHidden()) {
        hide.add(parameterValue.getParameter().getId());
      } else {
        show.add(parameterValue.getParameter().getId());
      }
      Set<RuleImpactedByDto> ruleImpactedByDtoList = JsonUtils.jsonToCollectionType(parameterValue.getImpactedBy(), Set.class, RuleImpactedByDto.class);
      if (Utility.isEmpty(ruleImpactedByDtoList)) {
        ruleImpactedByDtoList = new HashSet<>();
      }
      rulesImpactedByMap.put(parameterValue.getParameter().getId(), ruleImpactedByDtoList);
    }
  }

  /**
   * This function is used to evaluate rules for a parameter based on current hidden state of parameter and parameter type (Resource or Single Select)
   *
   * @param jobId              To apply rules on a job with id
   * @param parameter          To apply rules present on parameter
   * @param show               Set of rules to be shown
   * @param hide               Set of rules to be hidden
   * @param rulesImpactedByMap Map of rules impacted by a parameter where key is parameterId of impacted parameter and value is set of rules ids and impacting parameter ids
   * @return
   * @throws IOException
   */
  private RuleHideShowDto evaluateRules(Long jobId, Parameter parameter, Set<Long> show, Set<Long> hide, Map<Long, Set<RuleImpactedByDto>> rulesImpactedByMap) throws IOException {
    if (!Utility.isEmpty(parameter.getRules())) {
      List<RuleDto> parameterRules = JsonUtils.jsonToCollectionType(parameter.getRules(), List.class, RuleDto.class);
      Map<String, List<RuleDto>> rulesMap = parameterRules.stream().collect(Collectors.groupingBy(r -> r.getInput()[0], Collectors.mapping(Function.identity(), Collectors.toList())));

      ParameterValue parameterValue = parameterValueRepository.findByJobIdAndParameterId(jobId, parameter.getId());

      // TODO update timestamps
      if (!Utility.isEmpty(parameterValue.getChoices())) {
        resetImpactedByForAGivenParameter(parameter, show, hide, parameterRules, rulesImpactedByMap);
        if (parameter.getType() == Type.Parameter.SINGLE_SELECT) {
          Map<String, String> selectedChoice = objectMapper.readValue(parameterValue.getChoices().toString(), new TypeReference<>() {
          });
          applyRulesOfSingleSelectParameter(rulesMap, selectedChoice, show, hide, parameter.getId(), rulesImpactedByMap);
        } else if (parameter.getType() == Type.Parameter.RESOURCE) {
          applyRulesOfResourceParameter(rulesMap, parameterValue.getChoices(), show, hide, parameter.getId(), rulesImpactedByMap);
        }
        if (!Utility.isEmpty(rulesImpactedByMap)) {
          Map<Long, ParameterValue> parameterAndParameterValueMap = parameterValueRepository.findByJobIdAndParameterIdsIn(jobId, rulesImpactedByMap.keySet()).stream().collect(Collectors.toMap(pv -> pv.getParameter().getId(), Function.identity()));
          rulesImpactedByMap.forEach((p, impactedBy) -> {
            ParameterValue pv = parameterAndParameterValueMap.get(p);
            if (!Utility.isEmpty(pv)) {
              pv.setImpactedBy(JsonUtils.valueToNode(impactedBy));
            }
          });
          parameterValueRepository.saveAll(parameterAndParameterValueMap.values());
        }

      }

    }

    if (!Utility.isEmpty(show)) {
      parameterValueRepository.updateParameterValueVisibility(show, false, jobId);
    }
    if (!Utility.isEmpty(hide)) {
      parameterValueRepository.updateParameterValueVisibility(hide, true, jobId);
    }


    RuleHideShowDto ruleHideShowDto = new RuleHideShowDto();
    ruleHideShowDto.setHide(hide.stream().map(String::valueOf).collect(Collectors.toSet()));
    ruleHideShowDto.setShow(show.stream().map(String::valueOf).collect(Collectors.toSet()));
    return ruleHideShowDto;
  }

  /**
   * Reset impacted by for a given parameter
   *
   * @param parameter          Parameter whose impacted by would be reset
   * @param show               Set of rules to be shown
   * @param hide               Set of rules to be hidden
   * @param parameterRules     List of rules of the parameter
   * @param rulesImpactedByMap Map of rules impacted by a parameter where key is parameterId of impacted parameter and value is set of rules ids and impacting parameter ids
   */
  private void resetImpactedByForAGivenParameter(Parameter parameter, Set<Long> show, Set<Long> hide, List<RuleDto> parameterRules, Map<Long, Set<RuleImpactedByDto>> rulesImpactedByMap) {
    for (RuleDto ruleDto : parameterRules) {
      if (!Utility.isEmpty(ruleDto.getShow())) {
        processRules(ruleDto.getShow().getParameters(), parameter, show, hide, rulesImpactedByMap, ruleDto);
      }
      if (!Utility.isEmpty(ruleDto.getHide())) {
        processRules(ruleDto.getHide().getParameters(), parameter, hide, show, rulesImpactedByMap, ruleDto);
      }
    }
  }

  /**
   * This function is used to manipulate parameter Id type from String to Long and apply reset rules on impacted parameters
   *
   * @param parameterList      List of parameter ids
   * @param parameter          Parameter whose impacted by would be reset
   * @param show               Set of rules to be shown
   * @param hide               Set of rules to be hidden
   * @param rulesImpactedByMap Map of rules impacted by a parameter where key is parameterId of impacted parameter and value is set of rules ids and impacting parameter ids
   * @param ruleDto
   */
  private void processRules(List<String> parameterList, Parameter parameter, Set<Long> show, Set<Long> hide, Map<Long, Set<RuleImpactedByDto>> rulesImpactedByMap, RuleDto ruleDto) {
    parameterList.stream()
      .map(Long::valueOf)
      .forEach(p -> resetRules(parameter, show, hide, rulesImpactedByMap, ruleDto, p));
  }

  /**
   * reset parameters impactedBy set for all the parameters which will be affected by currenty parmeter rules
   *
   * @param parameter          Parameter whose impacted by would be reset
   * @param show               Set of rules to be shown
   * @param hide               Set of rules to be hidden
   * @param rulesImpactedByMap Map of rules impacted by a parameter where key is parameterId of impacted parameter and value is set of rules ids and impacting parameter ids
   * @param ruleDto            Rule to be removed from impacted by
   * @param toShowOrHide       Parameter id of impacted parameter
   */
  private static void resetRules(Parameter parameter, Set<Long> show, Set<Long> hide, Map<Long, Set<RuleImpactedByDto>> rulesImpactedByMap, RuleDto ruleDto, Long toShowOrHide) {
    Set<RuleImpactedByDto> impactedByOfToShowParameter = rulesImpactedByMap.get(toShowOrHide);
    if (Utility.isEmpty(impactedByOfToShowParameter)) {
      impactedByOfToShowParameter = Collections.emptySet();
    }
    impactedByOfToShowParameter.removeIf(impactedBy -> Objects.equals(impactedBy.getRuleId(), ruleDto.getId()) && Objects.equals(impactedBy.getParameterId(), parameter.getId()));
    if (Utility.isEmpty(impactedByOfToShowParameter)) {
      show.remove(toShowOrHide);
      hide.add(toShowOrHide);
    }
  }

  /**
   * This function is used to apply rules on a Resource parameter
   *
   * @param rulesMap           Map of rules where key is rule id and value is list of rules
   * @param selectedResources  List of selected resources of a resource parameter
   * @param show               Set of rules to be shown
   * @param hide               Set of rules to be hidden
   * @param parameterId        Parameter id of resource parameter
   * @param rulesImpactedByMap Map of rules impacted by a parameter where key is parameterId of impacted parameter and value is set of rules ids and impacting parameter ids
   * @throws IOException
   */
  private static void applyRulesOfResourceParameter(Map<String, List<RuleDto>> rulesMap, JsonNode selectedResources, Set<Long> show, Set<Long> hide, Long parameterId, Map<Long, Set<RuleImpactedByDto>> rulesImpactedByMap) throws IOException {
    if (Utility.isEmpty(selectedResources)) {
      return;
    }
    List<ResourceParameterChoiceDto> selectedResourceList = JsonUtils.jsonToCollectionType(selectedResources, List.class, ResourceParameterChoiceDto.class);
    for (ResourceParameterChoiceDto choice : selectedResourceList) {

      if (rulesMap.containsKey(choice.getObjectId())) {
        List<RuleDto> rules = rulesMap.get(choice.getObjectId());
        applyRules(show, hide, parameterId, rulesImpactedByMap, rules);
      }

    }
  }

  /**
   * This function is used to apply rules on a Single Select parameter
   *
   * @param rulesMap           Map of rules where key is rule id and value is list of rules
   * @param selectedChoice     Map of selected choices where key is choice id and value is selected or not
   * @param show               Set of rules to be shown
   * @param hide               Set of rules to be hidden
   * @param parameterId        Parameter id of single select parameter
   * @param rulesImpactedByMap Map of rules impacted by a parameter where key is parameterId of impacted parameter and value is set of rules ids and impacting parameter ids
   * @throws JsonProcessingException
   */
  private static void applyRulesOfSingleSelectParameter(Map<String, List<RuleDto>> rulesMap, Map<String, String> selectedChoice, Set<Long> show, Set<Long> hide, Long parameterId, Map<Long, Set<RuleImpactedByDto>> rulesImpactedByMap) throws JsonProcessingException {
    for (Map.Entry<String, String> entry : selectedChoice.entrySet()) {
      var key = entry.getKey();
      var value = entry.getValue();

      if (State.Selection.SELECTED.name().equals(value) && rulesMap.containsKey(key)) {
        List<RuleDto> rules = rulesMap.get(key);
        applyRules(show, hide, parameterId, rulesImpactedByMap, rules);
      }

    }
  }

  /**
   * This function is a common logic for applying rules on a parameter
   *
   * @param show               Set of rules to be shown
   * @param hide               Set of rules to be hidden
   * @param parameterId        Parameter id of parameter
   * @param rulesImpactedByMap Map of rules impacted by a parameter where key is parameterId of impacted parameter and value is set of rules ids and impacting parameter ids
   * @param rules              List of rules to be applied
   */
  private static void applyRules(Set<Long> show, Set<Long> hide, Long parameterId, Map<Long, Set<RuleImpactedByDto>> rulesImpactedByMap, List<RuleDto> rules) {
    for (RuleDto rule : rules) {

      if (null != rule.getHide()) {
        rule.getHide().getParameters().stream()
          .map(Long::valueOf).toList()
          .forEach(toHide -> evaluateHiddenImpactedBy(show, hide, parameterId, rulesImpactedByMap, rule, toHide));
      }

      if (null != rule.getShow()) {
        rule.getShow().getParameters().stream()
          .map(Long::valueOf).toList()
          .forEach(toShow -> evaluateShowImpactedBy(hide, show, parameterId, rulesImpactedByMap, rule, toShow));
      }

    }
  }

  /**
   * This function is used to evaluate impacted by of parameters to be hidden
   *
   * @param show               Set of rules to be shown
   * @param hide               Set of rules to be hidden
   * @param parameterId        Parameter id of parameter
   * @param rulesImpactedByMap Map of rules impacted by a parameter where key is parameterId of impacted parameter and value is set of rules ids and impacting parameter ids
   * @param rule               Rule to be applied
   * @param toHide             Parameter id of parameter to be hidden
   */
  private static void evaluateHiddenImpactedBy(Set<Long> show, Set<Long> hide, Long parameterId, Map<Long, Set<RuleImpactedByDto>> rulesImpactedByMap, RuleDto rule, Long toHide) {
    Set<RuleImpactedByDto> impactedByOfToHideParameter = rulesImpactedByMap.get(toHide);

    if (Utility.isEmpty(impactedByOfToHideParameter)) {
      show.remove(toHide);
      hide.add(toHide);
      impactedByOfToHideParameter = new HashSet<>();
    }

    impactedByOfToHideParameter.add(new RuleImpactedByDto(rule.getId(), parameterId));
    rulesImpactedByMap.put(toHide, impactedByOfToHideParameter);
  }

  /**
   * This function is used to evaluate impacted by of parameters to be shown
   *
   * @param hide               Set of rules to be hidden
   * @param show               Set of rules to be shown
   * @param parameterId        Parameter id of parameter
   * @param rulesImpactedByMap Map of rules impacted by a parameter where key is parameterId of impacted parameter and value is set of rules ids and impacting parameter ids
   * @param rule               Rule to be applied
   * @param toShow             Parameter id of parameter to be shown
   */
  private static void evaluateShowImpactedBy(Set<Long> hide, Set<Long> show, Long parameterId, Map<Long, Set<RuleImpactedByDto>> rulesImpactedByMap, RuleDto rule, Long toShow) {
    Set<RuleImpactedByDto> impactedByOfToHideParameter = rulesImpactedByMap.get(toShow);

    if (Utility.isEmpty(impactedByOfToHideParameter)) {
      hide.remove(toShow);
      show.add(toShow);
      impactedByOfToHideParameter = new HashSet<>();
    }

    impactedByOfToHideParameter.add(new RuleImpactedByDto(rule.getId(), parameterId));
    rulesImpactedByMap.put(toShow, impactedByOfToHideParameter);
  }

  /**
   * This function is used to get rules impacted by a parameter
   *
   * @param parameterRequestList List of CJF parameters to be evaluated
   * @param checklistId
   * @return
   * @throws IOException
   */
  private RuleHideShowDto executeRulesTemporarily(List<ParameterRequest> parameterRequestList, Long checklistId) throws IOException {
    Set<Long> parameterSet = parameterRequestList.stream().map(ParameterRequest::getId).collect(Collectors.toSet());
    Map<Long, Parameter> parameterMap = parameterRepository.findAllById(parameterSet).stream()
      .collect(Collectors.toMap(BaseEntity::getId, Function.identity()));

    Map<Long, ParameterRequest> parameterRequestMap = parameterRequestList.stream()
      .collect(Collectors.toMap(ParameterRequest::getId, Function.identity()));

    List<Parameter> processParameterList = parameterRepository.getParametersByChecklistIdAndTargetEntityType(checklistId, Type.ParameterTargetEntityType.PROCESS);

    Set<Long> show = new HashSet<>();
    Set<Long> hide = new HashSet<>();

    Map<Long, Set<RuleImpactedByDto>> impactedByMap = getDefaultHiddenStateAndImpactedByForCJFParameters(processParameterList, show, hide);

    for (Map.Entry<Long, ParameterRequest> parameterExecuteRequestEntry : parameterRequestMap.entrySet()) {
      Parameter parameter = parameterMap.get(parameterExecuteRequestEntry.getKey());
      JsonNode rules = parameterMap.get(parameterExecuteRequestEntry.getKey()).getRules();

      if (!Utility.isEmpty(rules)) {

        List<RuleDto> parameterRules = JsonUtils.jsonToCollectionType(rules, List.class, RuleDto.class);
        // key is the value of the rule on selection of which the rules should be applied, values is all the rules for that value
        Map<String, List<RuleDto>> rulesMap = parameterRules.stream().collect(Collectors.groupingBy(r -> r.getInput()[0], Collectors.mapping(Function.identity(), Collectors.toList())));
        JsonNode data = parameterExecuteRequestEntry.getValue().getData();

        if (parameter.getType() == Type.Parameter.SINGLE_SELECT) {

          Map<String, String> selectedChoices = new HashMap<>();
          List<SingleSelectParameter> parameters = JsonUtils.jsonToCollectionType(data, List.class, SingleSelectParameter.class);
          updateParameterChoicesAndReturnSelectionState(selectedChoices, parameters);
          applyRulesOfSingleSelectParameter(rulesMap, selectedChoices, show, hide, parameter.getId(), impactedByMap);

        } else if (parameter.getType() == Type.Parameter.RESOURCE) {
          // TODO Bring consistencies in temporary and actual execution API calls
          applyRulesOfResourceParameter(rulesMap, data.get("choices"), show, hide, parameter.getId(), impactedByMap);
        }

      }
    }

    RuleHideShowDto ruleHideShowDto = new RuleHideShowDto();
    ruleHideShowDto.setHide(hide.stream().map(String::valueOf).collect(Collectors.toSet()));
    ruleHideShowDto.setShow(show.stream().map(String::valueOf).collect(Collectors.toSet()));
    return ruleHideShowDto;
  }

  /**
   * This function calculated default hidden state of CJF parameters and calculates impacted by of CJF parameters
   *
   * @param parameterList List of CJF parameters
   * @param show          Set of parameters to be shown
   * @param hide          Set of parameters to be hidden
   * @return
   */
  private static Map<Long, Set<RuleImpactedByDto>> getDefaultHiddenStateAndImpactedByForCJFParameters(List<Parameter> parameterList, Set<Long> show, Set<Long> hide) {
    Map<Long, Set<RuleImpactedByDto>> impactedByMap = new HashMap<>();
    parameterList.forEach(parameter -> {
      if (parameter.isHidden()) {
        hide.add(parameter.getId());
      } else {
        show.add(parameter.getId());
      }
      impactedByMap.put(parameter.getId(), new HashSet<>());
    });
    return impactedByMap;
  }

  /**
   * This function stores selected choices of single select parameter in a map and returns selection state of parameter
   *
   * @param parameterChoices Map of parameter id and selected choice
   * @param parameters       List of single select parameters
   * @return
   */
  private boolean updateParameterChoicesAndReturnSelectionState(Map<String, String> parameterChoices, List<SingleSelectParameter> parameters) {
    boolean isNoneSelected = false;
    for (SingleSelectParameter singleSelectParameter : parameters) {
      String id = singleSelectParameter.getId();
      String state = singleSelectParameter.getState();
      if (state == null) {
        isNoneSelected = true;
      } else {
        if (State.Selection.SELECTED.equals(State.Selection.valueOf(state))) {
          isNoneSelected = false;
          parameterChoices.put(id, State.Selection.SELECTED.name());
        } else {
          parameterChoices.put(id, State.Selection.NOT_SELECTED.name());
        }
      }
    }
    return isNoneSelected;
  }

  void findAndAutoInitializeAllTheParameterReferencedBy(Long jobId, Long referencedParameterId) {
    List<AutoInitializedParameter> autoInitializedParameters = autoInitializedParameterRepository.findByReferencedParameterId(referencedParameterId);

    for (AutoInitializedParameter autoInitializedParameter : autoInitializedParameters) {
      Parameter parameter = parameterRepository.findById(autoInitializedParameter.getAutoInitializedParameterId()).get();
      try {
        if (Type.Parameter.CALCULATION.equals(parameter.getType())) {
          ParameterExecuteRequest parameterExecuteRequest = new ParameterExecuteRequest();
          parameterExecuteRequest.setJobId(jobId);
          ParameterRequest parameterRequest = new ParameterRequest();
          parameterRequest.setData(parameter.getData());
          parameterRequest.setId(parameter.getId());
          parameterRequest.setLabel(parameter.getLabel());
          parameterExecuteRequest.setParameter(parameterRequest);

          executeParameter(jobId, parameterExecuteRequest, true);
        } else {
          JsonNode valueData = null;
          AutoInitializeDto autoInitializeDto = JsonUtils.readValue(parameter.getAutoInitialize().toString(), AutoInitializeDto.class);
          // This is the executed parameter from which we will get the value
          Long executedParameterId = Long.valueOf(autoInitializeDto.getParameterId());

          ParameterValueBase executedResourceParameter;

          executedResourceParameter = parameterValueRepository.readByParameterIdAndJobId(executedParameterId, jobId)
            .orElseThrow(() -> new ResourceNotFoundException(parameter.getId(), ErrorCode.PARAMETER_VALUE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

          List<ResourceParameterChoiceDto> parameterChoices = JsonUtils.jsonToCollectionType(executedResourceParameter.getChoices(), List.class, ResourceParameterChoiceDto.class);
          // The parameter that is auto initialized for the following switch case parameter types
          // will always refer to a resource parameter, this resource parameter is currently only single select, hence we are fetching 0th element
          // we will pick the value from that resource parameter and set it in the auto initialized parameter
          // now this value can be either value of a property of that selected resource for the resource parameter
          // or it can be a relation of that selected resource for the resource parameter
          ResourceParameterChoiceDto resourceParameterChoice = parameterChoices.get(0);
          String objectId = resourceParameterChoice.getObjectId();
          String collection = resourceParameterChoice.getCollection();
          EntityObject entityObject = entityObjectService.findById(collection, objectId);

          switch (parameter.getType()) {
            case RESOURCE -> {
              MappedRelation mappedRelation = new MappedRelation();

              for (MappedRelation mr : entityObject.getRelations()) {
                if (mr.getExternalId().equals(autoInitializeDto.getRelation().getExternalId())) {
                  mappedRelation = mr;
                }
              }

              ResourceParameter resourceParameter = new ResourceParameter();
              List<ResourceParameterChoiceDto> resourceParameterChoiceDtos = new ArrayList<>();
              ResourceParameterChoiceDto rpcd = new ResourceParameterChoiceDto();
              if (!Utility.isEmpty(mappedRelation.getTargets())) {
                rpcd.setObjectId(mappedRelation.getTargets().get(0).getId().toString());
                rpcd.setObjectExternalId(mappedRelation.getTargets().get(0).getExternalId());
                rpcd.setObjectDisplayName(mappedRelation.getTargets().get(0).getDisplayName());
                rpcd.setCollection(mappedRelation.getTargets().get(0).getCollection());
              }

              resourceParameterChoiceDtos.add(rpcd);
              resourceParameter.setChoices(resourceParameterChoiceDtos);
              valueData = JsonUtils.valueToNode(resourceParameter);
            }
            case NUMBER, SINGLE_LINE, MULTI_LINE, DATE, DATE_TIME -> {
              PropertyValue propertyValue = null;

              for (PropertyValue pv : entityObject.getProperties()) {
                if (pv.getId().toString().equals(autoInitializeDto.getProperty().getId())) {
                  propertyValue = pv;
                }
              }

              ValueParameterBase valueParameterBase = new ValueParameterBase();
              valueParameterBase.setInput(propertyValue.getValue());
              valueData = JsonUtils.valueToNode(valueParameterBase);
            }
          }

          // TODO change this here and in update log method
          ParameterRequest parameterRequest = new ParameterRequest();
          parameterRequest.setId(parameter.getId());
          parameterRequest.setLabel(parameter.getLabel());
          parameterRequest.setData(valueData);
          ParameterExecuteRequest parameterExecuteRequest = new ParameterExecuteRequest();
          parameterExecuteRequest.setParameter(parameterRequest);
          parameterExecuteRequest.setJobId(jobId);
          parameterExecuteRequest.setReason("");

          executeParameter(jobId, parameterExecuteRequest, true);
        }
      } catch (Exception ex) {
        // Exceptions can occur due to data issues, but we don't want to stop the execution of other parameters
        log.error("[findAndAutoInitializeAllTheParameterReferencedBy] Error auto initializing parameters", ex);
      }
    }
  }

  // TODO some of the code is repeated in findAndAutoInitializeAllTheParameterReferencedBy can we refactor it
  private void findAndAutoInitializeAllTheParameterReferencedByForErrorCorrection(Long jobId, Long referencedParameterId) throws StreemException, IOException, ResourceNotFoundException {
    List<AutoInitializedParameter> autoInitializedParameters = autoInitializedParameterRepository.findByReferencedParameterId(referencedParameterId);

    for (AutoInitializedParameter autoInitializedParameter : autoInitializedParameters) {
      Parameter parameter = parameterRepository.findById(autoInitializedParameter.getAutoInitializedParameterId()).get();
      if (Type.Parameter.CALCULATION.equals(parameter.getType())) {
        ParameterExecuteRequest parameterExecuteRequest = new ParameterExecuteRequest();
        parameterExecuteRequest.setJobId(jobId);
        ParameterRequest parameterRequest = new ParameterRequest();
        parameterRequest.setData(parameter.getData());
        parameterRequest.setId(parameter.getId());
        parameterRequest.setLabel(parameter.getLabel());
        parameterExecuteRequest.setParameter(parameterRequest);

        executeParameterForError(parameterExecuteRequest);
      } else {
        JsonNode valueData = null;
        AutoInitializeDto autoInitializeDto = JsonUtils.readValue(parameter.getAutoInitialize().toString(), AutoInitializeDto.class);
        // This is the executed parameter from which we will get the value
        Long executedParameterId = Long.valueOf(autoInitializeDto.getParameterId());

        ParameterValueBase executedResourceParameter;

        executedResourceParameter = tempParameterValueRepository.readByParameterIdAndJobId(executedParameterId, jobId)
          .orElseThrow(() -> new ResourceNotFoundException(parameter.getId(), ErrorCode.PARAMETER_VALUE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

        List<ResourceParameterChoiceDto> parameterChoices = JsonUtils.jsonToCollectionType(executedResourceParameter.getChoices(), List.class, ResourceParameterChoiceDto.class);
        // The parameter that is auto initialized for the following switch case parameter types
        // will always refer to a resource parameter, this resource parameter is currently only single select, hence we are fetching 0th element
        // we will pick the value from that resource parameter and set it in the auto initialized parameter
        // now this value can be either value of a property of that selected resource for the resource parameter
        // or it can be a relation of that selected resource for the resource parameter
        ResourceParameterChoiceDto resourceParameterChoice = parameterChoices.get(0);
        String objectId = resourceParameterChoice.getObjectId();
        String collection = resourceParameterChoice.getCollection();
        EntityObject entityObject = entityObjectService.findById(collection, objectId);

        switch (parameter.getType()) {
          case RESOURCE -> {
            MappedRelation mappedRelation = new MappedRelation();

            for (MappedRelation mr : entityObject.getRelations()) {
              if (mr.getExternalId().equals(autoInitializeDto.getRelation().getExternalId())) {
                mappedRelation = mr;
              }
            }

            ResourceParameter resourceParameter = new ResourceParameter();
            List<ResourceParameterChoiceDto> resourceParameterChoiceDtos = new ArrayList<>();
            ResourceParameterChoiceDto rpcd = new ResourceParameterChoiceDto();
            if (!Utility.isEmpty(mappedRelation.getTargets())) {
              rpcd.setObjectId(mappedRelation.getTargets().get(0).getId().toString());
              rpcd.setObjectExternalId(mappedRelation.getTargets().get(0).getExternalId());
              rpcd.setObjectDisplayName(mappedRelation.getTargets().get(0).getDisplayName());
              rpcd.setCollection(mappedRelation.getTargets().get(0).getCollection());
            }

            resourceParameterChoiceDtos.add(rpcd);
            resourceParameter.setChoices(resourceParameterChoiceDtos);
            valueData = JsonUtils.valueToNode(resourceParameter);
          }
          case NUMBER, SINGLE_LINE, MULTI_LINE, DATE, DATE_TIME -> {
            PropertyValue propertyValue = null;

            for (PropertyValue pv : entityObject.getProperties()) {
              if (pv.getId().toString().equals(autoInitializeDto.getProperty().getId())) {
                propertyValue = pv;
              }
            }

            ValueParameterBase valueParameterBase = new ValueParameterBase();
            valueParameterBase.setInput(propertyValue.getValue());
            valueData = JsonUtils.valueToNode(valueParameterBase);
          }
        }

        // TODO change this here and in update log method
        ParameterRequest parameterRequest = new ParameterRequest();
        parameterRequest.setId(parameter.getId());
        parameterRequest.setLabel(parameter.getLabel());
        parameterRequest.setData(valueData);
        ParameterExecuteRequest parameterExecuteRequest = new ParameterExecuteRequest();
        parameterExecuteRequest.setParameter(parameterRequest);
        parameterExecuteRequest.setJobId(jobId);
        parameterExecuteRequest.setReason("");


        executeParameterForError(parameterExecuteRequest);
      }
    }
  }

  //TODO: optimise this
  private void attachParameterVerifications(Parameter parameter, Long jobId, ParameterValue parameterValue, ParameterValueDto parameterValueDto) {
    List<ParameterVerification> parameterVerifications = new ArrayList<>();
    if (!parameter.getVerificationType().equals(Type.VerificationType.NONE)) {
      ParameterVerification parameterVerificationSelf = parameterVerificationRepository.findByJobIdAndParameterIdAndVerificationType(jobId, parameter.getId(), String.valueOf(Type.VerificationType.SELF));
      ParameterVerification parameterVerificationPeer = parameterVerificationRepository.findByJobIdAndParameterIdAndVerificationType(jobId, parameter.getId(), String.valueOf(Type.VerificationType.PEER));
      if (!Utility.isEmpty(parameterVerificationSelf)) {
        parameterVerifications.add(parameterVerificationSelf);
      }
      if (!Utility.isEmpty(parameterVerificationPeer)) {
        parameterVerifications.add(parameterVerificationPeer);
      }
    }
    if (!Utility.isEmpty(parameterVerifications)) {
      List<ParameterVerificationDto> parameterVerificationDtos = parameterVerificationMapper.toDto(parameterVerifications);
      for (ParameterVerificationDto parameterVerificationDto : parameterVerificationDtos) {
        parameterVerificationDto.setEvaluationState(parameterValue.getState());
      }
      parameterValueDto.setParameterVerifications(parameterVerificationDtos);
    }
  }
}
