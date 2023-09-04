package com.leucine.streem.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.constant.Misc;
import com.leucine.streem.constant.Operator;
import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.*;
import com.leucine.streem.dto.mapper.IParameterMapper;
import com.leucine.streem.dto.request.*;
import com.leucine.streem.dto.response.Error;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.exception.ExceptionType;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.*;
import com.leucine.streem.model.helper.ParameterSpecificationBuilder;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.model.helper.parameter.CalculationParameter;
import com.leucine.streem.model.helper.parameter.MaterialParameter;
import com.leucine.streem.model.helper.parameter.ResourceParameter;
import com.leucine.streem.model.helper.rules.RuleCheckHelper;
import com.leucine.streem.model.helper.search.SearchCriteria;
import com.leucine.streem.repository.*;
import com.leucine.streem.service.IChecklistService;
import com.leucine.streem.service.IParameterService;
import com.leucine.streem.util.DateTimeUtils;
import com.leucine.streem.util.JsonUtils;
import com.leucine.streem.util.Utility;
import com.leucine.streem.util.ValidationUtils;
import com.leucine.streem.validator.ConstraintValidator;
import com.leucine.streem.validator.RegexValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParameterService implements IParameterService {
  private final IParameterMapper parameterMapper;
  private final IParameterRepository parameterRepository;
  private final IChecklistService checklistService;
  private final IUserRepository userRepository;
  private final ITaskRepository taskRepository;
  private final IStageRepository stageRepository;
  private final IMediaRepository mediaRepository;

  @Override
  public ParameterDto addParameterToTask(Long checklistId, Long stageId, Long taskId, ParameterCreateRequest parameterCreateRequest) throws ResourceNotFoundException, StreemException, IOException {
    log.info("[addParameterToTask] Request to create add new parameter to task, checklistId: {}, stageId: {}, taskId: {}, parameterCreateRequest: {}",
      checklistId, stageId, taskId, parameterCreateRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Checklist checklist = checklistService.findByTaskId(task.getId());

    checklistService.validateChecklistModificationState(checklist.getId(), checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());
    validateParameterData(parameterCreateRequest.getType(), parameterCreateRequest.getData());

    var parameter = getParameterFromCreateRequest(checklist, parameterCreateRequest, Type.ParameterTargetEntityType.TASK, principalUserEntity);
    if (!Utility.isEmpty(parameterCreateRequest.getId())) {
      parameter.setId(Long.valueOf(parameterCreateRequest.getId()));
    }
    parameter.setTask(task);

    return parameterMapper.toDto(parameterRepository.save(parameter));
  }

  @Override
  public ParameterDto createParameter(Long checklistId, ParameterCreateRequest parameterCreateRequest) throws ResourceNotFoundException, StreemException, IOException {
    log.info("[createParameter] Request to create a new unmapped parameter, checklistId: {}, parameterCreateRequest: {}", checklistId, parameterCreateRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Checklist checklist = checklistService.findById(checklistId);

    checklistService.validateChecklistModificationState(checklist.getId(), checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());
    validateParameterData(parameterCreateRequest.getType(), parameterCreateRequest.getData());

    var parameter = getParameterFromCreateRequest(checklist, parameterCreateRequest, Type.ParameterTargetEntityType.UNMAPPED, principalUserEntity);
    if (!Utility.isEmpty(parameterCreateRequest.getId())) {
      parameter.setId(Long.valueOf(parameterCreateRequest.getId()));
    }

    return parameterMapper.toDto(parameterRepository.save(parameter));
  }

  @Override
  public BasicDto reorderParameters(Long checklistId, Long stageId, Long taskId, ParameterReorderRequest parameterReorderRequest) throws ResourceNotFoundException, StreemException {
    log.info("[reorderParameters] Request reorder parameters, checklistId: {}, stageId: {}, taskId: {}, parameterReorderRequest: {}",
      checklistId, stageId, taskId, parameterReorderRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Checklist checklist = checklistService.findById(checklistId);

    checklistService.validateChecklistModificationState(checklist.getId(), checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());

    // TODO process in batch
    parameterReorderRequest.getParametersOrder().forEach((parameterId, order) -> parameterRepository.reorderParameter(parameterId, order, principalUser.getId(), DateTimeUtils.now()));

    BasicDto basicDto = new BasicDto();
    basicDto.setMessage("success");
    return basicDto;
  }

  @Override
  public ParameterDto getParameter(Long parameterId) throws ResourceNotFoundException {
    log.info("[getParameter] Request to get a new parameter, parameterId: {}", parameterId);
    Parameter parameter = parameterRepository.findById(parameterId)
      .orElseThrow(() -> new ResourceNotFoundException(parameterId, ErrorCode.PARAMETER_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    return parameterMapper.toDto(parameter);
  }

  @Override
  public ParameterInfoDto unmapParameter(Long parameterId) throws ResourceNotFoundException, StreemException {
    log.info("[unmapParameter] Request to unmap an parameter, parameterId: {}", parameterId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Parameter parameter = parameterRepository.findById(parameterId)
      .orElseThrow(() -> new ResourceNotFoundException(parameterId, ErrorCode.PARAMETER_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Checklist checklist = checklistService.findById(parameter.getChecklistId());

    checklistService.validateChecklistModificationState(checklist.getId(), checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());

    String taskId = String.valueOf(parameter.getTaskId());
    String stageId = Utility.isEmpty(taskId) ? null : String.valueOf(stageRepository.findStageIdByTaskId(parameter.getTaskId()));

    parameter.setTargetEntityType(Type.ParameterTargetEntityType.UNMAPPED);
    parameter.setTask(null);
    parameter.setModifiedBy(principalUserEntity);

    var parameterInfoDto = parameterMapper.toBasicDto(parameterRepository.save(parameter));
    parameterInfoDto.setTaskId(taskId);
    parameterInfoDto.setStageId(stageId);

    return parameterInfoDto;
  }

  @Override
  public ParameterDto mapParameterToTask(Long checklistId, Long taskId, MapParameterToTaskRequest mapParameterToTaskRequest) throws ResourceNotFoundException, StreemException {
    log.info("[mapParameterToTask] Request to map an parameter to task, checklistId: {}, taskId: {}, mapParameterToTaskRequest: {}", checklistId, taskId, mapParameterToTaskRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Checklist checklist = checklistService.findById(checklistId);
    Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Long parameterId = mapParameterToTaskRequest.getParameterId();

    checklistService.validateChecklistModificationState(checklist.getId(), checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());

    Parameter parameter = parameterRepository.findById(parameterId)
      .orElseThrow(() -> new ResourceNotFoundException(parameterId, ErrorCode.PARAMETER_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    if (!Type.ParameterTargetEntityType.UNMAPPED.equals(parameter.getTargetEntityType())) {
      ValidationUtils.invalidate(parameterId, ErrorCode.PARAMETER_ALREADY_MAPPED);
    }
    parameter.setOrderTree(mapParameterToTaskRequest.getOrderTree());
    parameter.setTargetEntityType(Type.ParameterTargetEntityType.TASK);
    parameter.setTask(task);
    parameter.setModifiedBy(principalUserEntity);
    return parameterMapper.toDto(parameterRepository.save(parameter));
  }

  @Override
  public ParameterDto updateParameter(Long parameterId, ParameterUpdateRequest parameterUpdateRequest) throws ResourceNotFoundException, StreemException, IOException {
    log.info("[updateParameter] Request to update an parameter, parameterId: {}, parameterUpdateRequest: {}", parameterId, parameterUpdateRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Parameter parameter = parameterRepository.findById(parameterId)
      .orElseThrow(() -> new ResourceNotFoundException(parameterId, ErrorCode.PARAMETER_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Checklist checklist = parameter.getChecklist();
    checklistService.validateChecklistModificationState(checklist.getId(), checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());
    validateParameterData(parameter.getType(), parameterUpdateRequest.getData());

    if (!Utility.isEmpty(parameterUpdateRequest.getVerificationType())) {
      validateParameterVerification(parameter, parameterUpdateRequest);
      parameter.setVerificationType(parameterUpdateRequest.getVerificationType());
    }

    if (null != parameterUpdateRequest.getLabel()) {
      parameter.setLabel(parameterUpdateRequest.getLabel());
    }

    if (null != parameterUpdateRequest.getDescription()) {
      parameter.setDescription(parameterUpdateRequest.getDescription());
    }

    if (null != parameterUpdateRequest.getAutoInitialize()) {
      parameter.setAutoInitialize(parameterUpdateRequest.getAutoInitialize());
    }
    if (null != parameterUpdateRequest.getRules()) {
      validateRules(checklist.getId(), parameterUpdateRequest.getRules());
      parameter.setRules(parameterUpdateRequest.getRules());
    }

    if (null != parameterUpdateRequest.getData()) {
      if (Type.Parameter.MATERIAL.equals(parameter.getType())) {
        List<MaterialMediaDto> materialMedias = new ArrayList<>();

        List<ParameterMediaMapping> parameterMediaMappings = parameter.getMedias();

        Map<Long, ParameterMediaMapping> parameterMediaMappingMap = parameterMediaMappings.stream().collect(Collectors.toMap(am -> am.getMedia().getId(), Function.identity()));
        Set<Long> existingMedias = parameterMediaMappings.stream().map(am -> am.getMedia().getId()).collect(Collectors.toSet());
        Set<Long> mediasInRequest = new HashSet<>();

        List<MaterialParameter> materialParameters = JsonUtils.readValue(parameterUpdateRequest.getData().toString(),
          new TypeReference<List<MaterialParameter>>() {
          });

        for (MaterialParameter materialParameter : materialParameters) {

          MaterialMediaDto materialMediaDto = new MaterialMediaDto();
          materialMediaDto.setId(materialParameter.getId());
          materialMediaDto.setMediaId(materialParameter.getMediaId());
          materialMediaDto.setName(materialParameter.getName());
          materialMediaDto.setQuantity(materialParameter.getQuantity());
          materialMedias.add(materialMediaDto);

          if (!Utility.isEmpty(materialParameter.getMediaId())) {
            mediasInRequest.add(Long.valueOf(materialParameter.getMediaId()));

            if (!existingMedias.contains(Long.valueOf(materialParameter.getMediaId()))) {
              Media media = mediaRepository.getOne(Long.valueOf(materialParameter.getMediaId()));
              media.setName(materialParameter.getName());
              media.setDescription(materialParameter.getDescription());
              media = mediaRepository.save(media);
              parameter.addMedia(media, principalUserEntity);
            } else {
              Media media = parameterMediaMappingMap.get(Long.valueOf(materialParameter.getMediaId())).getMedia();
              media.setName(materialParameter.getName());
              media.setDescription(materialParameter.getDescription());
            }
          }
        }

        existingMedias.removeAll(mediasInRequest);
        //TODO delete media from acitivity media mapping instead of archiving
        for (Long id : existingMedias) {
          parameterMediaMappingMap.get(id).setArchived(true);
        }

        parameter.setData(JsonUtils.valueToNode(materialMedias));
      } else {
        parameter.setData(parameterUpdateRequest.getData());
      }
    }

    if (null != parameterUpdateRequest.getValidations()) {
      parameter.setValidations(parameterUpdateRequest.getValidations());
    }

    parameter.setMandatory(parameterUpdateRequest.isMandatory());
    parameter.setAutoInitialized(parameterUpdateRequest.isAutoInitialized());
    parameter.setModifiedBy(principalUserEntity);

    return parameterMapper.toDto(parameterRepository.save(parameter));
  }

  @Override
  public ParameterInfoDto archiveParameter(Long parameterId) throws ResourceNotFoundException, StreemException {
    log.info("[archiveParameter] Request to archive an parameter, parameterId: {}", parameterId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Parameter parameter = parameterRepository.findById(parameterId)
      .orElseThrow(() -> new ResourceNotFoundException(parameterId, ErrorCode.PARAMETER_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Checklist checklist = parameter.getChecklist();
    checklistService.validateChecklistModificationState(checklist.getId(), checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());

    String taskId = String.valueOf(parameter.getTaskId());
    String stageId = Utility.isEmpty(parameter.getTaskId()) ? null : String.valueOf(stageRepository.findStageIdByTaskId(parameter.getTaskId()));

    boolean isLinkedParameterExists = parameterRepository.isLinkedParameterExistsByParameterId(checklist.getId(), parameterId.toString());
    if (isLinkedParameterExists) {
      ValidationUtils.invalidate(parameterId, ErrorCode.PROCESS_PARAMETER_LINKED_TO_ANOTHER_PROCESS_PARAMETER);
    }

    parameter.setArchived(true);
    parameter.setModifiedBy(principalUserEntity);

    var parameterInfoDto = parameterMapper.toBasicDto(parameterRepository.save(parameter));
    parameterInfoDto.setTaskId(taskId);
    parameterInfoDto.setStageId(stageId);

    return parameterInfoDto;
  }

  @Override
  public Page<ParameterInfoDto> getAllParameters(Long checklistId, String filters, Pageable pageable) {
    log.info("[getAllParameters] Request to get all parameters of checklist, checklistId: {}", checklistId);
    SearchCriteria checklistIdCriteria = (new SearchCriteria()).setField(Parameter.CHECKLIST_ID).setOp(Operator.Search.EQ.toString()).setValues(Collections.singletonList(checklistId));

    Specification<Parameter> specification = ParameterSpecificationBuilder.createSpecification(filters, Collections.singletonList(checklistIdCriteria));
    Page<Parameter> parameters = parameterRepository.findAll(specification, pageable);

    return new PageImpl<>(parameterMapper.toBasicDto(parameters.getContent()), pageable, parameters.getTotalElements());
  }

  @Override
  public BasicDto updateParameterVisibility(ParameterVisibilityRequest parameterVisibilityRequest) throws StreemException {
    log.info("[updateParameterVisibility] Request to update parameter visibility, parameterVisibilityRequest: {}", parameterVisibilityRequest);
    Set<Long> hiddenParameterIds = parameterVisibilityRequest.hide().stream().map(Long::valueOf).collect(Collectors.toSet());
    Set<Long> visibleParameterids = parameterVisibilityRequest.show().stream().map(Long::valueOf).collect(Collectors.toSet());

    parameterRepository.updateParameterVisibility(hiddenParameterIds, visibleParameterids);

    return new BasicDto("message", null, null);
  }

  private Parameter getParameterFromCreateRequest(Checklist checklist, ParameterCreateRequest parameterCreateRequest, Type.ParameterTargetEntityType targetEntityType, User principalUserEntity) {
    Parameter parameter = parameterMapper.toEntity(parameterCreateRequest);

    parameter.setChecklistId(checklist.getId());
    parameter.setTargetEntityType(targetEntityType);
    parameter.setCreatedBy(principalUserEntity);
    parameter.setModifiedBy(principalUserEntity);
    if (Utility.isEmpty(parameterCreateRequest.getVerificationType())) {
      parameter.setVerificationType(Type.VerificationType.NONE);
    } else {
      parameter.setVerificationType(parameterCreateRequest.getVerificationType());
    }

    return parameter;
  }

  private void validateParameterData(Type.Parameter parameterType, JsonNode data) throws IOException, StreemException {
    switch (parameterType) {
      case RESOURCE, MULTI_RESOURCE -> validateResourceParameterData(data);
      case CALCULATION -> validateCalculationParameter(data);
      case MATERIAL -> validateMaterialParameter(data);

    }
  }

  private void validateMaterialParameter(JsonNode data) throws JsonProcessingException, StreemException {
    List<MaterialParameter> materialParameters = JsonUtils.readValue(data.toString(), new TypeReference<>() {
    });
    for (MaterialParameter materialParameter : materialParameters) {
      String fileExtension = materialParameter.getFilename().split("\\.")[1];
      if (Utility.isEmpty(fileExtension) || !Misc.MATERIAL_PARAMETER_EXTENSION_TYPES.contains(fileExtension)) {
        ValidationUtils.invalidate(fileExtension, ErrorCode.MATERIAL_PARAMETER_INVALID_FILE_EXTENSION);
      }
    }
  }

  private void validateResourceParameterData (JsonNode data) throws StreemException, JsonProcessingException {
    ResourceParameter resourceParameter = JsonUtils.readValue(data.toString(), ResourceParameter.class);
    if (Utility.isEmpty(resourceParameter.getObjectTypeId())) {
      ValidationUtils.invalidate(ErrorCode.RESOURCE_PARAMETER_OBJECT_TYPE_CANNOT_BE_EMPTY.getDescription(), ErrorCode.RESOURCE_PARAMETER_OBJECT_TYPE_CANNOT_BE_EMPTY);
    }

    try {
      ResourceParameterFilter resourceParameterFilter = resourceParameter.getPropertyFilters();
      if (!Utility.isEmpty(resourceParameterFilter)) {
        if (null == resourceParameterFilter.getOp()) {
          ValidationUtils.invalidate(ErrorCode.RESOURCE_PARAMETER_OBJECT_TYPE_CANNOT_BE_EMPTY.getDescription(), ErrorCode.INVALID_FILTER_CONFIGURATIONS);
        }

        List<ResourceParameterFilterField> resourceParameterFilterFields = resourceParameterFilter.getFields();
        for (ResourceParameterFilterField resourceParameterFilterField : resourceParameterFilterFields) {
          if (null == resourceParameterFilterField.getOp() || null == resourceParameterFilterField.getSelector() || Utility.isEmpty(resourceParameterFilterField.getField())) {
            ValidationUtils.invalidate(ErrorCode.RESOURCE_PARAMETER_OBJECT_TYPE_CANNOT_BE_EMPTY.getDescription(), ErrorCode.INVALID_FILTER_CONFIGURATIONS);
          }
          if (!Utility.isEmpty(resourceParameterFilterField.getReferencedParameterId())) {
            Long.valueOf(resourceParameterFilterField.getReferencedParameterId());
          } else {
            if (resourceParameterFilterField.getValues().contains(null)) {
              ValidationUtils.invalidate(ErrorCode.RESOURCE_PARAMETER_OBJECT_TYPE_CANNOT_BE_EMPTY.getDescription(), ErrorCode.INVALID_FILTER_CONFIGURATIONS);
            }
            if (Utility.isEmpty(String.valueOf(resourceParameterFilterField.getValues().get(0)))) {
              ValidationUtils.invalidate(ErrorCode.RESOURCE_PARAMETER_OBJECT_TYPE_CANNOT_BE_EMPTY.getDescription(), ErrorCode.INVALID_FILTER_CONFIGURATIONS);
            }
          }
        }
      }
    } catch (Exception e) {
      ValidationUtils.invalidate(ErrorCode.RESOURCE_PARAMETER_OBJECT_TYPE_CANNOT_BE_EMPTY.getDescription(), ErrorCode.INVALID_FILTER_CONFIGURATIONS);
    }
  }

  private void validateParameterVerification(Parameter parameter, ParameterUpdateRequest parameterUpdateRequest) throws StreemException {
    if (parameter.getTargetEntityType().equals(Type.ParameterTargetEntityType.PROCESS) && !parameterUpdateRequest.getVerificationType().equals(Type.VerificationType.NONE)) {
      ValidationUtils.invalidate(parameter.getId(), ErrorCode.VERIFICATION_CANNOT_BE_ENABLED_FOR_CREATE_JOB_FORM_PARAMETER);
    }
  }
  private void validateRules(Long checklistId, JsonNode presentRules) throws StreemException, IOException {

    List<RuleDto> ruleDtoList = new ArrayList<>();

    if (!Utility.isEmpty(presentRules)) {
      ruleDtoList.addAll(JsonUtils.jsonToCollectionType(presentRules, List.class, RuleDto.class));
    }


    Set<RuleCheckHelper> ruleCheckDtoSet = new HashSet<>();

    for (RuleDto ruleDto : ruleDtoList) {
      if (Utility.isEmpty(ruleDto.getId())) {
        ValidationUtils.invalidate(checklistId, ErrorCode.RULE_ID_CANNOT_BE_EMPTY);
      }
      if (Utility.isEmpty(ruleDto.getConstraint())) {
        ValidationUtils.invalidate(checklistId, ErrorCode.RULE_CONSTRAINT_CANNOT_BE_EMPTY);
      }
      if (Utility.isEmpty(ruleDto.getInput())) {
        ValidationUtils.invalidate(checklistId, ErrorCode.RULE_INPUT_CANNOT_BE_EMPTY);
      }
      if (Utility.isEmpty(ruleDto.getHide()) && Utility.isEmpty(ruleDto.getShow())) {
        ValidationUtils.invalidate(checklistId, ErrorCode.RULE_HIDE_OR_SHOW_BOTH_CANNOT_BE_EMPTY);
      }
      if (!Utility.isEmpty(ruleDto.getHide())) {
        RuleCheckHelper ruleCheckDto = new RuleCheckHelper(ruleDto.getConstraint(), Arrays.asList(ruleDto.getInput()), true);
        if (ruleCheckDtoSet.contains(ruleCheckDto)) {
          ValidationUtils.invalidate(checklistId, ErrorCode.INCORRECT_RULE_CONFIGURED);
        }
        ruleCheckDtoSet.add(ruleCheckDto);
      }
      if (!Utility.isEmpty(ruleDto.getShow())) {
        RuleCheckHelper ruleCheckDto = new RuleCheckHelper(ruleDto.getConstraint(), Arrays.asList(ruleDto.getInput()), false);
        if (ruleCheckDtoSet.contains(ruleCheckDto)) {
          ValidationUtils.invalidate(checklistId, ErrorCode.INCORRECT_RULE_CONFIGURED);
        }
        ruleCheckDtoSet.add(ruleCheckDto);
      }
    }
  }

  private void validateCalculationParameter(JsonNode data) throws IOException, StreemException {
    List<Error> errorList = new ArrayList<>();
    CalculationParameter calculationParameter = JsonUtils.readValue(data.toString(), CalculationParameter.class);

    if (Utility.isEmpty(calculationParameter.getExpression())) {
      ValidationUtils.addError(errorList, ErrorCode.CALCULATION_PARAMETER_EXPRESSION_CANNOT_BE_EMPTY);
    }
    if (Utility.isEmpty(calculationParameter.getUom())) {
      ValidationUtils.addError(errorList, ErrorCode.CALCULATION_PARAMETER_UOM_CANNOT_BE_EMPTY);
    }
    if (Utility.isEmpty(calculationParameter.getVariables())) {
      ValidationUtils.addError(errorList, ErrorCode.CALCULATION_PARAMETER_VARIABLE_SET_CANNOT_BE_EMPTY);
    }

    Set<String> variableSet = new HashSet<>();
    Set<Long> parameterIds = new HashSet<>();

    if (!Utility.isEmpty(calculationParameter.getVariables())) {
      variableSet = calculationParameter.getVariables().keySet();
      ConstraintValidator validator = new RegexValidator(Misc.VARIABLE_NAME_REGEX);
      for (String variable : variableSet) {
        validator.validate(variable);
        if (!validator.isValid()) {
          ValidationUtils.addError(errorList, ErrorCode.CALCULATION_PARAMETER_INVALID_VARIABLE_NAME);
        }
      }
    }

    if (!Utility.isEmpty(calculationParameter.getExpression())) {
      boolean isExpressionValid = false;
      try {
        Expression e = new ExpressionBuilder(calculationParameter.getExpression())
          .variables(variableSet)
          .build();
        isExpressionValid = e.validate(false).isValid();
      } catch (Exception ex) {
        log.error("[validateCalculationParameter] Invalid evaluation expression", ex);
        ValidationUtils.addError(errorList, ErrorCode.CALCULATION_PARAMETER_INVALID_EXPRESSION);
      }
      if (!isExpressionValid) {
        ValidationUtils.addError(errorList, ErrorCode.CALCULATION_PARAMETER_INVALID_EXPRESSION);
      }

      int count = parameterRepository.getEnabledParametersCountByTypeAndIdIn(parameterIds, Type.ALLOWED_PARAMETER_TYPES_FOR_CALCULATION_PARAMETER);
      if (count != parameterIds.size()) {
        ValidationUtils.addError(errorList, ErrorCode.CALCULATION_PARAMETER_DEPENDENT_PARAMETERS_NOT_FOUND);
      }
    }

    if (!Utility.isEmpty(errorList)) {
      ValidationUtils.invalidate("Invalid calculation parameter configurations", errorList);
    }
  }
}
