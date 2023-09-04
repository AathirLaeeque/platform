package com.leucine.streem.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.commons.id.IdGenerator;
import com.leucine.streem.constant.CollectionMisc;
import com.leucine.streem.constant.State;
import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.*;
import com.leucine.streem.dto.mapper.IAutomationMapper;
import com.leucine.streem.dto.mapper.IChecklistMapper;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.exception.ExceptionType;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.*;
import com.leucine.streem.model.helper.BaseEntity;
import com.leucine.streem.model.helper.ChecklistRevisionHelper;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.model.helper.parameter.CalculationParameter;
import com.leucine.streem.model.helper.parameter.CalculationParameterVariable;
import com.leucine.streem.model.helper.parameter.ResourceParameter;
import com.leucine.streem.repository.*;
import com.leucine.streem.service.IChecklistAuditService;
import com.leucine.streem.service.IChecklistRevisionService;
import com.leucine.streem.service.ICodeService;
import com.leucine.streem.service.IVersionService;
import com.leucine.streem.util.DateTimeUtils;
import com.leucine.streem.util.JsonUtils;
import com.leucine.streem.util.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.json.JsonObject;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChecklistRevisionService implements IChecklistRevisionService {
  private final IAutomationMapper automationMapper;
  private final IAutomationRepository automationRepository;
  private final IChecklistAuditService checklistAuditService;
  private final IChecklistRelationRepository relationRepository;
  private final IChecklistMapper checklistMapper;
  private final IChecklistRepository checklistRepository;
  private final ICodeService codeService;
  private final IParameterRepository parameterRepository;
  private final ITaskAutomationMappingRepository taskAutomationMappingRepository;
  private final IUserRepository userRepository;
  private final IVersionService versionService;
  private final IChecklistDefaultUsersRepository checklistDefaultUsersRepository;
  private final ITaskRepository taskRepository;

  @Transactional
  @Override
  public ChecklistDto createChecklistRevision(Long checklistId) throws ResourceNotFoundException, StreemException, JsonProcessingException {
    log.info("[createChecklistRevision] Request to create revision of checklist, checklistId: {}", checklistId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Checklist parentChecklist = checklistRepository.readById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    versionService.validateForChecklistRevision(parentChecklist);

    // TODO check if we are able to duplicate the entity using deep cloning
    Checklist revisedChecklist = new Checklist();
    Long id = IdGenerator.getInstance().nextId();
    revisedChecklist.setId(id);
    revisedChecklist.setName(parentChecklist.getName());
    revisedChecklist.setDescription(parentChecklist.getDescription());
    revisedChecklist.setOrganisation(parentChecklist.getOrganisation());
    revisedChecklist.setUseCase(parentChecklist.getUseCase());
    revisedChecklist.setCreatedBy(principalUserEntity);
    revisedChecklist.setModifiedBy(principalUserEntity);
    revisedChecklist.setState(State.Checklist.BEING_BUILT);
    revisedChecklist.setCode(codeService.getCode(Type.EntityType.CHECKLIST, principalUser.getOrganisationId()));
    revisedChecklist.setGlobal(parentChecklist.isGlobal());
    revisedChecklist.addFacility(parentChecklist.getFacilities().stream().map(ChecklistFacilityMapping::getFacility).collect(Collectors.toSet()), principalUserEntity);

    var revisedParametersOldAndNewIdMap = new HashMap<Long, Long>();
    var revisedTaskOldAndNewMap = new HashMap<Long, Task>();
    var revisedStageOldAndNewMap = new HashMap<Long, Stage>();
    List<Task> taskHavingAutomations = new ArrayList<>();
    List<Parameter> parametersHavingAutoInitialize = new ArrayList<>();
    Map<Long, Parameter> parametersHavingRules = new HashMap<>();
    List<Parameter> allResourceParameters = new ArrayList<>();

    ChecklistRevisionHelper checklistRevisionHelper = new ChecklistRevisionHelper();

    Map<Long, Long> oldTaskIdNewTaskIdMapping = new HashMap<>();
    Set<Long> defaultUserTaskIds = checklistDefaultUsersRepository.findTaskIdsByChecklistId(parentChecklist.getId());

    copyStages(parentChecklist, revisedChecklist, revisedParametersOldAndNewIdMap, revisedStageOldAndNewMap, revisedTaskOldAndNewMap, taskHavingAutomations, parametersHavingAutoInitialize, parametersHavingRules, principalUserEntity, oldTaskIdNewTaskIdMapping, defaultUserTaskIds, allResourceParameters, checklistRevisionHelper);
    addPrimaryAuthor(revisedChecklist, principalUserEntity);

    for (ChecklistPropertyValue checklistPropertyValue : parentChecklist.getChecklistPropertyValues()) {
      Property property = checklistPropertyValue.getFacilityUseCasePropertyMapping().getProperty();
      if (!property.isArchived()) {
        revisedChecklist.addProperty(checklistPropertyValue.getFacilityUseCasePropertyMapping(), checklistPropertyValue.getValue(), principalUserEntity);
      }
    }
    copyChecklistRelation(parentChecklist.getId(), revisedChecklist, principalUserEntity);

    Map<Long, JsonNode> parameterHavingValidations = checklistRevisionHelper.getParameterHavingValidations();
    copyProcessParameters(parentChecklist, revisedChecklist, revisedParametersOldAndNewIdMap, parametersHavingAutoInitialize, parametersHavingRules, principalUserEntity, allResourceParameters, parameterHavingValidations);
    // TODO Pass the checklistRevisionHelper created to the calculate parameter method and throughought this revision logic
    Map<Long, Task> revisedParameterTaskMap = checklistRevisionHelper.getRevisedParameterTaskMap();
    Map<Long, Task> parameterToBeRevisedTaskMap = checklistRevisionHelper.getParameterToBeRevisedTaskMap();
    List<Parameter> calculationParameterList = checklistRevisionHelper.getCalculationParameterList();
    Map<Long, Parameter> calculationParametersMap = checklistRevisionHelper.getCalculationParametersMap();
    Map<Long, Parameter> revisedParameters = checklistRevisionHelper.getRevisedParameters();
    copyCalculationParameters(revisedChecklist.getId(), revisedParameterTaskMap, parameterToBeRevisedTaskMap, revisedParametersOldAndNewIdMap, calculationParameterList,
      calculationParametersMap, revisedParameters, principalUserEntity);

    revisedChecklist = checklistRepository.save(revisedChecklist);

    copyParameterValidations(parameterHavingValidations, revisedParametersOldAndNewIdMap, revisedParameters);
    reviseParameterAutoInitialize(parametersHavingAutoInitialize, revisedParametersOldAndNewIdMap);
    reviseParameterRules(parametersHavingRules, revisedStageOldAndNewMap, revisedTaskOldAndNewMap, revisedParametersOldAndNewIdMap);
    copyChecklistDefaultUsers(parentChecklist.getId(), revisedChecklist, oldTaskIdNewTaskIdMapping);
    copyTaskAutomation(taskHavingAutomations, revisedTaskOldAndNewMap, revisedParametersOldAndNewIdMap, principalUserEntity);

    Version version = versionService.createNewVersionFromParent(revisedChecklist.getId(), Type.EntityType.CHECKLIST, parentChecklist.getVersion(), parentChecklist.getId());
    revisedChecklist.setVersion(version);
    checklistRepository.save(revisedChecklist);

    setIdsForProcessParameterFilters(allResourceParameters, revisedParametersOldAndNewIdMap);

    checklistAuditService.revise(checklistId, revisedChecklist.getCode(), principalUser);

    return checklistMapper.toDto(revisedChecklist);
  }

  private void copyChecklistDefaultUsers(Long parentChecklistId, Checklist revisedChecklist, Map<Long, Long> oldTaskIdNewTaskIdMapping) {
    List<ChecklistDefaultUsers> parentChecklistDefaultUsers = checklistDefaultUsersRepository.findByChecklistId(parentChecklistId);
    Map<Long, Task> revisedTaskMap = taskRepository.findAllByIdInAndArchived(new HashSet<>(oldTaskIdNewTaskIdMapping.values()), false)
      .stream().collect(Collectors.toMap(BaseEntity::getId, Function.identity()));

    List<ChecklistDefaultUsers> revisedChecklistDefaultUsers = new ArrayList<>();

    for (ChecklistDefaultUsers defaultUser : parentChecklistDefaultUsers) {
      Long newTaskId = oldTaskIdNewTaskIdMapping.get(defaultUser.getTaskId());
      Task task = revisedTaskMap.get(newTaskId);

      if (task != null) {
        revisedChecklistDefaultUsers.add(
          ChecklistDefaultUsers.builder()
            .checklist(revisedChecklist)
            .user(defaultUser.getUser())
            .facility(defaultUser.getFacility())
            .task(task)
            .build()
        );
      }
    }

    checklistDefaultUsersRepository.saveAll(revisedChecklistDefaultUsers);
  }

  private void copyStages(Checklist checklistRevisionOf, Checklist revisedChecklist, HashMap<Long, Long> revisedParametersOldAndNewIdMap,
                          HashMap<Long, Stage> revisedStageOldAndNewMap,
                          HashMap<Long, Task> revisedTaskOldAndNewMap, List<Task> taskHavingAutomations,
                          List<Parameter> parametersHavingAutoInitialize,
                          Map<Long, Parameter> parametersHavingRules,
                          User principalUserEntity, Map<Long, Long> oldTaskIdNewTaskIdMapping, Set<Long> defaultUserTaskIds,
                          List<Parameter> allResourceParameters, ChecklistRevisionHelper checklistRevisionHelper) throws JsonProcessingException {
    var parameterHavingValidations = checklistRevisionHelper.getParameterHavingValidations();

    Map<Long, Task> revisedParameterTaskMap = checklistRevisionHelper.getRevisedParameterTaskMap();
    Map<Long, Task> parameterToBeRevisedTaskMap = checklistRevisionHelper.getParameterToBeRevisedTaskMap();
    List<Parameter> calculationParameterList = checklistRevisionHelper.getCalculationParameterList();
    Map<Long, Parameter> calculationParametersMap = checklistRevisionHelper.getCalculationParametersMap();
    Map<Long, Parameter> revisedParameters = checklistRevisionHelper.getRevisedParameters();

    for (Stage stageRevisionOf : checklistRevisionOf.getStages()) {
      Stage revisedStage = new Stage();
      revisedStage.setId(IdGenerator.getInstance().nextId());
      revisedStage.setOrderTree(stageRevisionOf.getOrderTree());
      revisedStage.setName(stageRevisionOf.getName());
      revisedStage.setModifiedBy(principalUserEntity);
      revisedStage.setCreatedBy(principalUserEntity);
      revisedChecklist.addStage(revisedStage);

      copyTasks(revisedChecklist.getId(), stageRevisionOf, revisedStage, revisedParametersOldAndNewIdMap, calculationParametersMap, revisedParameterTaskMap,
        parameterToBeRevisedTaskMap, calculationParameterList, parameterHavingValidations, revisedParameters, revisedTaskOldAndNewMap,
        taskHavingAutomations, parametersHavingAutoInitialize, parametersHavingRules, principalUserEntity, oldTaskIdNewTaskIdMapping, defaultUserTaskIds, allResourceParameters);
      revisedStageOldAndNewMap.put(stageRevisionOf.getId(), revisedStage);

    }
  }

  private void copyTasks(Long revisedChecklistId, Stage stageRevisionOf, Stage revisedStage, HashMap<Long, Long> revisedParametersOldAndNewIdMap,
                         Map<Long, Parameter> calculationParametersMap, Map<Long, Task> revisedParamterTaskMap,
                         Map<Long, Task> parameterToBeRevisedTaskMap, List<Parameter> calculationParameterList,
                         Map<Long, JsonNode> parameterHavingValidations, Map<Long, Parameter> revisedParameters,
                         Map<Long, Task> revisedTaskOldAndNewMap, List<Task> taskHavingAutomations,
                         List<Parameter> parametersHavingAutoInitialize,
                         Map<Long, Parameter> parametersHavingRules,
                         User principalUserEntity, Map<Long, Long> oldTaskIdNewTaskIdMapping, Set<Long> defaultUserTaskIds,
                         List<Parameter> allResourceParameters) {
    for (Task taskRevisionOf : stageRevisionOf.getTasks()) {
      Task revisedTask = new Task();
      revisedTask.setId(IdGenerator.getInstance().nextId());
      revisedTask.setOrderTree(taskRevisionOf.getOrderTree());
      revisedTask.setName(taskRevisionOf.getName());
      revisedTask.setTimed(taskRevisionOf.isTimed());
      revisedTask.setTimerOperator(taskRevisionOf.getTimerOperator());
      revisedTask.setMinPeriod(taskRevisionOf.getMinPeriod());
      revisedTask.setMaxPeriod(taskRevisionOf.getMaxPeriod());
      revisedTask.setHasStop(taskRevisionOf.isHasStop());
      revisedTask.setModifiedBy(principalUserEntity);
      revisedTask.setCreatedBy(principalUserEntity);
      revisedStage.addTask(revisedTask);

      revisedTaskOldAndNewMap.put(taskRevisionOf.getId(), revisedTask);

      if (!Utility.isEmpty(taskRevisionOf.getAutomations())) {
        taskHavingAutomations.add(taskRevisionOf);
      }

      copyTaskMedias(taskRevisionOf, revisedTask, principalUserEntity);
      copyParameters(revisedChecklistId, taskRevisionOf, revisedTask, principalUserEntity, revisedParametersOldAndNewIdMap,
        calculationParametersMap, revisedParamterTaskMap, parameterToBeRevisedTaskMap, calculationParameterList,
        parameterHavingValidations, revisedParameters, parametersHavingAutoInitialize, parametersHavingRules, allResourceParameters);
      if (defaultUserTaskIds.contains(taskRevisionOf.getId())) {
        oldTaskIdNewTaskIdMapping.put(taskRevisionOf.getId(), revisedTask.getId());
      }
    }
  }

  private void copyTaskMedias(Task taskRevisionOf, Task revisedTask, User principalUser) {
    for (TaskMediaMapping taskMediaMapping : taskRevisionOf.getMedias()) {
      revisedTask.addMedia(taskMediaMapping.getMedia(), principalUser);
    }
  }

  private void copyParameters(Long revisedChecklistId, Task taskRevisionOf, Task revisedTask, User principalUserEntity, Map<Long, Long> revisedParametersOldAndNewIdMap,
                              Map<Long, Parameter> calculationParametersMap, Map<Long, Task> revisedParameterTaskMap,
                              Map<Long, Task> parameterToBeRevisedTaskMap, List<Parameter> calculationParameterList,
                              Map<Long, JsonNode> parameterHavingValidations, Map<Long, Parameter> revisedParameters,
                              List<Parameter> parametersHavingAutoInitialize, Map<Long, Parameter> parametersHavingRules,
                              List<Parameter> allResourceParameters) {
    for (var parameterRevisionOf : taskRevisionOf.getParameters()) {
      JsonObject jsonObject = new JsonObject(parameterRevisionOf.getValidations().toString());
      if (!jsonObject.toBsonDocument().isEmpty()) {
        parameterHavingValidations.put(parameterRevisionOf.getId(), parameterRevisionOf.getValidations());
      }
      if (parameterRevisionOf.isAutoInitialized()) {
        parametersHavingAutoInitialize.add(parameterRevisionOf);
      }

      if (null != parameterRevisionOf.getRules()) {
        parametersHavingRules.put(parameterRevisionOf.getId(), parameterRevisionOf);
      }

      if (Type.Parameter.CALCULATION.equals(parameterRevisionOf.getType())) {
        // all the calculation parameters for the revised checklist are created at the end
        // this is because there may be parameters (parameter, number) inside calculation parameter which positioned in the later stages or tasks
        // so we create revision for them first and then create revision for all the calculation parameters in the end using this list
        calculationParameterList.add(parameterRevisionOf);
        calculationParametersMap.put(parameterRevisionOf.getId(), parameterRevisionOf);
        parameterToBeRevisedTaskMap.put(parameterRevisionOf.getId(), revisedTask);
        revisedParameterTaskMap.put(parameterRevisionOf.getId(), revisedTask);
      } else {
        Parameter revisedParameter = new Parameter();
        //TODO not setting IDs generates duplicate entry, check
        Long newId = IdGenerator.getInstance().nextId();
        revisedParameter.setId(newId);
        revisedParameter.setChecklistId(revisedChecklistId);
        revisedParameter.setOrderTree(parameterRevisionOf.getOrderTree());
        revisedParameter.setMandatory(parameterRevisionOf.isMandatory());
        revisedParameter.setType(parameterRevisionOf.getType());
        revisedParameter.setVerificationType(parameterRevisionOf.getVerificationType());
        revisedParameter.setAutoInitialize(parameterRevisionOf.getAutoInitialize());
        revisedParameter.setHidden(parameterRevisionOf.isHidden());
        revisedParameter.setAutoInitialized(parameterRevisionOf.isAutoInitialized());
        revisedParameter.setTargetEntityType(parameterRevisionOf.getTargetEntityType());
        if (Utility.isEmpty(parameterRevisionOf.getVerificationType())) {
          revisedParameter.setVerificationType(Type.VerificationType.NONE);
        } else {
          revisedParameter.setVerificationType(parameterRevisionOf.getVerificationType());
        }
        // TODO refactor
        if (revisedParameter.getType().equals(Type.Parameter.RESOURCE)) {
          allResourceParameters.add(revisedParameter);
        }
        revisedParameter.setData(parameterRevisionOf.getData());
        revisedParameter.setLabel(parameterRevisionOf.getLabel());
        revisedParameter.setModifiedBy(principalUserEntity);
        revisedParameter.setCreatedBy(principalUserEntity);
        revisedParameter.setValidations(JsonUtils.createObjectNode());

        revisedParametersOldAndNewIdMap.put(parameterRevisionOf.getId(), newId);
        revisedParameterTaskMap.put(parameterRevisionOf.getId(), revisedTask);

        if (Type.Parameter.MATERIAL.equals(parameterRevisionOf.getType())) {
          for (ParameterMediaMapping parameterMediaMapping : parameterRevisionOf.getMedias()) {
            if (!parameterMediaMapping.isArchived()) {
              revisedParameter.addMedia(parameterMediaMapping.getMedia(), principalUserEntity);
              if (Utility.isEmpty(parameterRevisionOf.getVerificationType())) {
                revisedParameter.setVerificationType(Type.VerificationType.NONE);
              } else {
                revisedParameter.setVerificationType(parameterRevisionOf.getVerificationType());
              }
            }
          }
        }

        revisedParameters.put(revisedParameter.getId(), revisedParameter);
        revisedTask.addParameter(revisedParameter);
      }
    }
  }

  /**
   * @param revisedChecklistId              Id of the newly revised checklist
   * @param revisedParameterTaskMap         holds map of parameter id (for which revision is done already) and the task which it is mapped to
   * @param parameterToBeRevisedTaskMap     holds map of parameter id for which revision needs to be done and the task which it is mapped to
   * @param revisedParametersOldAndNewIdMap holds map of parameter id old (for the checklist which is to be revisied) and new parameter id for which revision is created
   * @param parametersList                  hold list of all the calculation parameters for which revision needs to be created
   * @param calculationParametersMap        map of all the calculation parameters for which revision needs to be created
   * @param revisedParameters               map of all the revised parameters with key as its new parameter id
   * @param principalUserEntity
   * @throws JsonProcessingException
   */
  private void copyCalculationParameters(Long revisedChecklistId, Map<Long, Task> revisedParameterTaskMap, Map<Long, Task> parameterToBeRevisedTaskMap,
                                         HashMap<Long, Long> revisedParametersOldAndNewIdMap, List<Parameter> parametersList, Map<Long, Parameter> calculationParametersMap,
                                         Map<Long, Parameter> revisedParameters, User principalUserEntity) throws JsonProcessingException {
    for (var parameterRevisionOf : parametersList) {
      Parameter revisedParameter = new Parameter();
      Long newId = IdGenerator.getInstance().nextId();

      revisedParametersOldAndNewIdMap.put(parameterRevisionOf.getId(), newId);

      CalculationParameter calculationParameter = JsonUtils.readValue(parameterRevisionOf.getData().toString(), CalculationParameter.class);
      CalculationParameter revisedCalculationParameter = new CalculationParameter();
      revisedCalculationParameter.setExpression(calculationParameter.getExpression());
      revisedCalculationParameter.setUom(calculationParameter.getUom());

      Map<String, CalculationParameterVariable> newVariables = new HashMap<>();
      if (!Utility.isEmpty(calculationParameter.getVariables())) {
        for (var variableEntrySet : calculationParameter.getVariables().entrySet()) {
          String key = variableEntrySet.getKey();
          CalculationParameterVariable oldVariable = variableEntrySet.getValue();
          Long oldParameterId = Long.valueOf(oldVariable.getParameterId());

          if (!revisedParametersOldAndNewIdMap.containsKey(Long.valueOf(oldVariable.getParameterId()))) {
            copyCalculationParameters(revisedChecklistId, revisedParameterTaskMap, parameterToBeRevisedTaskMap,
              revisedParametersOldAndNewIdMap, Collections.singletonList(calculationParametersMap.get(oldParameterId)),
              calculationParametersMap, revisedParameters, principalUserEntity);
          }

          CalculationParameterVariable newVariable = new CalculationParameterVariable();
          Long newParameterId = revisedParametersOldAndNewIdMap.get(Long.valueOf(oldVariable.getParameterId()));
          newVariable.setParameterId(newParameterId.toString());
          // TODO this logic needs to be changed, this check is here because CJF parameters do not have task id
          // TODO instead what we need to do is we check the type of old parameter id
          if (!Utility.isEmpty(revisedParameterTaskMap.get(oldParameterId))) {
            newVariable.setTaskId(revisedParameterTaskMap.get(oldParameterId).getId().toString());
          }
          newVariable.setLabel(oldVariable.getLabel());

          newVariables.put(key, newVariable);
        }
      }
      revisedCalculationParameter.setVariables(newVariables);

      revisedParameter.setId(newId);
      revisedParameter.setChecklistId(revisedChecklistId);
      revisedParameter.setOrderTree(parameterRevisionOf.getOrderTree());
      revisedParameter.setMandatory(parameterRevisionOf.isMandatory());
      revisedParameter.setType(parameterRevisionOf.getType());
      revisedParameter.setAutoInitialize(parameterRevisionOf.getAutoInitialize());
      revisedParameter.setAutoInitialized(parameterRevisionOf.isAutoInitialized());
      revisedParameter.setHidden(parameterRevisionOf.isHidden());
      revisedParameter.setTargetEntityType(parameterRevisionOf.getTargetEntityType());
      if (Utility.isEmpty(parameterRevisionOf.getVerificationType())) {
        revisedParameter.setVerificationType(Type.VerificationType.NONE);
      } else {
        revisedParameter.setVerificationType(parameterRevisionOf.getVerificationType());
      }
      JsonNode jsonNode = JsonUtils.valueToNode(revisedCalculationParameter);
      revisedParameter.setData(jsonNode);
      revisedParameter.setLabel(parameterRevisionOf.getLabel());
      revisedParameter.setModifiedBy(principalUserEntity);
      revisedParameter.setCreatedBy(principalUserEntity);
      revisedParameter.setValidations(JsonUtils.createObjectNode());

      revisedParameters.put(newId, revisedParameter);
      revisedParametersOldAndNewIdMap.put(parameterRevisionOf.getId(), newId);
      parameterToBeRevisedTaskMap.get(parameterRevisionOf.getId()).addParameter(revisedParameter);
    }
  }

  private void copyChecklistRelation(Long parentChecklistId, Checklist revisedChecklist, User principalUserEntity) {
    List<Relation> relations = relationRepository.findByChecklistId(parentChecklistId);
    for (Relation relation : relations) {
      Relation revised = new Relation();
      revised.setId(IdGenerator.getInstance().nextId());
      revised.setExternalId(relation.getExternalId());
      revised.setDisplayName(relation.getDisplayName());
      revised.setUrlPath(relation.getUrlPath());
      revised.setVariables(relation.getVariables());
      revised.setValidations(relation.getValidations());
      revised.setCardinality(relation.getCardinality());
      revised.setObjectTypeId(relation.getObjectTypeId());
      revised.setCollection(relation.getCollection());
      revised.setOrderTree(relation.getOrderTree());
      revised.setCreatedAt(DateTimeUtils.now());
      revised.setModifiedAt(DateTimeUtils.now());
      revised.setModifiedBy(principalUserEntity);
      revised.setCreatedBy(principalUserEntity);

      revisedChecklist.addRelation(revised);
    }
  }

  private void copyParameterValidations(Map<Long, JsonNode> parameterHavingValidations, Map<Long, Long> revisedParameterOldAndNewIdMap, Map<Long, Parameter> revisedParameters) throws JsonProcessingException {
    for (Map.Entry<Long, JsonNode> entry : parameterHavingValidations.entrySet()) {
      Long parameterRevisionOfId = entry.getKey();
      Long newParameterId = revisedParameterOldAndNewIdMap.get(parameterRevisionOfId);
      ParameterRelationValidationDto parameterRelationValidationDto = JsonUtils.readValue(entry.getValue().toString(), ParameterRelationValidationDto.class);

      ParameterRelationValidationDto revisedParameterRelationValidationDto = new ParameterRelationValidationDto();
      List<ResourceParameterPropertyValidationDto> resourceParameterPropertyValidationDtosNew = new ArrayList<>();

      for (ResourceParameterPropertyValidationDto resourceParameterValidation : parameterRelationValidationDto.getResourceParameterValidations()) {
        var newResourceParameterValidation = new ResourceParameterPropertyValidationDto();
        Long linkedParameterNewId = revisedParameterOldAndNewIdMap.get(Long.valueOf(resourceParameterValidation.getParameterId()));
        newResourceParameterValidation.setId(resourceParameterValidation.getId());
        newResourceParameterValidation.setParameterId(String.valueOf(linkedParameterNewId));
        newResourceParameterValidation.setPropertyId(resourceParameterValidation.getPropertyId());
        newResourceParameterValidation.setPropertyExternalId(resourceParameterValidation.getPropertyExternalId());
        newResourceParameterValidation.setPropertyDisplayName(resourceParameterValidation.getPropertyDisplayName());
        newResourceParameterValidation.setPropertyInputType(resourceParameterValidation.getPropertyInputType());
        newResourceParameterValidation.setConstraint(resourceParameterValidation.getConstraint());
        newResourceParameterValidation.setErrorMessage(resourceParameterValidation.getErrorMessage());

        resourceParameterPropertyValidationDtosNew.add(newResourceParameterValidation);
      }

      revisedParameterRelationValidationDto.setResourceParameterValidations(resourceParameterPropertyValidationDtosNew);

      parameterRepository.updateParameterValidationByParameterId(newParameterId, JsonUtils.valueToNode(revisedParameterRelationValidationDto).toString());
    }
  }

  private void copyTaskAutomation(List<Task> taskHavingAutomations, Map<Long, Task> revisedTaskMap, HashMap<Long, Long> revisedParametersOldAndNewIdMap, User principalUser) throws JsonProcessingException {

    List<Automation> newAutomations = new ArrayList<>();
    List<TaskAutomationMapping> newTaskAutomationMappings = new ArrayList<>();

    for (Task task : taskHavingAutomations) {
      Task revisedTask = revisedTaskMap.get(task.getId());
      for (TaskAutomationMapping taskAutomationMapping : task.getAutomations()) {
        Automation existingAutomation = taskAutomationMapping.getAutomation();
        Automation automation = automationMapper.clone(existingAutomation);
        automation.setId(IdGenerator.getInstance().nextId());

        switch (existingAutomation.getActionType()) {
          case INCREASE_PROPERTY, DECREASE_PROPERTY -> {
            AutomationActionForResourceParameterDto resourceParameterAction = JsonUtils.readValue(existingAutomation.getActionDetails().toString(), AutomationActionForResourceParameterDto.class);
            AutomationActionForResourceParameterDto updatedResourceParameterAction = new AutomationActionForResourceParameterDto();
            Long newParameterId = revisedParametersOldAndNewIdMap.get(Long.valueOf(resourceParameterAction.getParameterId()));
            Long newReferenceParameterId = revisedParametersOldAndNewIdMap.get(Long.valueOf(resourceParameterAction.getReferencedParameterId()));

            updatedResourceParameterAction.setParameterId(String.valueOf(newParameterId));
            updatedResourceParameterAction.setReferencedParameterId(String.valueOf(newReferenceParameterId));


            updatedResourceParameterAction.setObjectTypeDisplayName(resourceParameterAction.getObjectTypeDisplayName());
            updatedResourceParameterAction.setPropertyId(resourceParameterAction.getPropertyId());
            updatedResourceParameterAction.setPropertyExternalId(resourceParameterAction.getPropertyExternalId());
            updatedResourceParameterAction.setPropertyDisplayName(resourceParameterAction.getPropertyDisplayName());
            updatedResourceParameterAction.setPropertyInputType(resourceParameterAction.getPropertyInputType());

            automation.setActionDetails(JsonUtils.valueToNode(updatedResourceParameterAction));
          }
          case SET_PROPERTY -> {
            AutomationSetPropertyBaseDto automationBase = JsonUtils.readValue(existingAutomation.getActionDetails().toString(), AutomationSetPropertyBaseDto.class);
            if (automationBase.getPropertyInputType().equals(CollectionMisc.PropertyType.DATE_TIME) || automationBase.getPropertyInputType().equals(CollectionMisc.PropertyType.DATE)) {
              AutomationActionDateTimeDto setPropertyActionDto = JsonUtils.readValue(existingAutomation.getActionDetails().toString(), AutomationActionDateTimeDto.class);
              AutomationActionDateTimeDto updatedSetPropertyAction = new AutomationActionDateTimeDto();

              Long newReferenceParameterId = revisedParametersOldAndNewIdMap.get(Long.valueOf(setPropertyActionDto.getReferencedParameterId()));
              // TODO currently assuming its just TASK
              Task revisedTaskNew = revisedTaskMap.get(Long.valueOf(setPropertyActionDto.getEntityId()));
              Long newEntityId = revisedTaskNew.getId();
              updatedSetPropertyAction.setCaptureProperty(setPropertyActionDto.getCaptureProperty());
              updatedSetPropertyAction.setValue(setPropertyActionDto.getValue());
              updatedSetPropertyAction.setDateUnit(setPropertyActionDto.getDateUnit());
              updatedSetPropertyAction.setEntityId(String.valueOf(newEntityId));
              updatedSetPropertyAction.setEntityType(setPropertyActionDto.getEntityType());
              updatedSetPropertyAction.setReferencedParameterId(String.valueOf(newReferenceParameterId));
              updatedSetPropertyAction.setPropertyId(setPropertyActionDto.getPropertyId());
              updatedSetPropertyAction.setPropertyExternalId(setPropertyActionDto.getPropertyExternalId());
              updatedSetPropertyAction.setPropertyDisplayName(setPropertyActionDto.getPropertyDisplayName());
              updatedSetPropertyAction.setPropertyInputType(setPropertyActionDto.getPropertyInputType());
              automation.setActionDetails(JsonUtils.valueToNode(updatedSetPropertyAction));
            } else {
              AutomationActionSetPropertyDto setPropertyActionDto = JsonUtils.readValue(existingAutomation.getActionDetails().toString(), AutomationActionSetPropertyDto.class);
              AutomationActionSetPropertyDto updatedSetPropertyAction = new AutomationActionSetPropertyDto();

              Long newReferenceParameterId = revisedParametersOldAndNewIdMap.get(Long.valueOf(setPropertyActionDto.getReferencedParameterId()));

              updatedSetPropertyAction.setReferencedParameterId(String.valueOf(newReferenceParameterId));
              updatedSetPropertyAction.setPropertyId(setPropertyActionDto.getPropertyId());
              updatedSetPropertyAction.setPropertyExternalId(setPropertyActionDto.getPropertyExternalId());
              updatedSetPropertyAction.setPropertyDisplayName(setPropertyActionDto.getPropertyDisplayName());
              updatedSetPropertyAction.setPropertyInputType(setPropertyActionDto.getPropertyInputType());
              updatedSetPropertyAction.setValue(setPropertyActionDto.getValue());
              updatedSetPropertyAction.setChoices(setPropertyActionDto.getChoices());

              automation.setActionDetails(JsonUtils.valueToNode(updatedSetPropertyAction));
            }
          }
          case ARCHIVE_OBJECT -> {
            AutomationActionArchiveObjectDto archiveObjectDto = JsonUtils.readValue(existingAutomation.getActionDetails().toString(), AutomationActionArchiveObjectDto.class);
            AutomationActionArchiveObjectDto updatedArchiveObjectDto = new AutomationActionArchiveObjectDto();
            Long newReferenceParameterId = revisedParametersOldAndNewIdMap.get(Long.valueOf(archiveObjectDto.getReferencedParameterId()));
            updatedArchiveObjectDto.setReferencedParameterId(String.valueOf(newReferenceParameterId));
            automation.setActionDetails(JsonUtils.valueToNode(updatedArchiveObjectDto));
          }
          case CREATE_OBJECT -> automation.setActionDetails(existingAutomation.getActionDetails());

          case SET_RELATION -> {
            AutomationActionMappedRelationDto automationActionMappedRelationDto = JsonUtils.readValue(existingAutomation.getActionDetails().toString(), AutomationActionMappedRelationDto.class);
            Long newReferenceParameterId = revisedParametersOldAndNewIdMap.get(Long.valueOf(automationActionMappedRelationDto.getReferencedParameterId()));
            Long parameterId = revisedParametersOldAndNewIdMap.get(Long.valueOf(automationActionMappedRelationDto.getParameterId()));
            automationActionMappedRelationDto.setReferencedParameterId(String.valueOf(newReferenceParameterId));
            automationActionMappedRelationDto.setParameterId(String.valueOf(parameterId));
            automation.setActionDetails(JsonUtils.valueToNode(automationActionMappedRelationDto));
          }
          default -> log.error("Automation action type not supported for revision yet");
        }

        automation.setCreatedBy(principalUser);
        automation.setModifiedBy(principalUser);

        newAutomations.add(automation);
        TaskAutomationMapping newTaskAutomationMapping = new TaskAutomationMapping(revisedTask, automation, taskAutomationMapping.getOrderTree(), taskAutomationMapping.getDisplayName(), principalUser);

        newTaskAutomationMappings.add(newTaskAutomationMapping);
      }
    }

    automationRepository.saveAll(newAutomations);
    taskAutomationMappingRepository.saveAll(newTaskAutomationMappings);
  }


  private void copyProcessParameters(Checklist parentChecklist, Checklist revisedChecklist,
                                     Map<Long, Long> revisedParametersOldAndNewIdMap,
                                     List<Parameter> parametersHavingAutoInitialize,
                                     Map<Long, Parameter> parametersHavingRules,
                                     User principalUserEntity,
                                     List<Parameter> allResourceParameters,
                                     Map<Long, JsonNode> parameterHavingValidations) {
    List<Parameter> processParameters = parameterRepository.getParametersByChecklistIdAndTargetEntityType(parentChecklist.getId(), Type.ParameterTargetEntityType.PROCESS);
    var revisedParametersMap = new HashMap<Long, Parameter>();
    var revisedParameters = new ArrayList<Parameter>();

    for (var parameterRevisionOf : processParameters) {
      JsonObject jsonObject = new JsonObject(parameterRevisionOf.getValidations().toString());
      if (!jsonObject.toBsonDocument().isEmpty()) {
        parameterHavingValidations.put(parameterRevisionOf.getId(), parameterRevisionOf.getValidations());
      }
      if (parameterRevisionOf.isAutoInitialized()) {
        parametersHavingAutoInitialize.add(parameterRevisionOf);
      }
      if (null != parameterRevisionOf.getRules()) {
        parametersHavingRules.put(parameterRevisionOf.getId(), parameterRevisionOf);
      }

      Parameter revisedParameter = new Parameter();
      Long newId = IdGenerator.getInstance().nextId();
      revisedParameter.setId(newId);
      revisedParameter.setChecklistId(revisedChecklist.getId());
      revisedParameter.setOrderTree(parameterRevisionOf.getOrderTree());
      revisedParameter.setMandatory(parameterRevisionOf.isMandatory());
      revisedParameter.setType(parameterRevisionOf.getType());
      if (Utility.isEmpty(parameterRevisionOf.getVerificationType())) {
        revisedParameter.setVerificationType(Type.VerificationType.NONE);
      } else {
        revisedParameter.setVerificationType(parameterRevisionOf.getVerificationType());
      }
      revisedParameter.setDescription(parameterRevisionOf.getDescription());
      revisedParameter.setAutoInitialize(parameterRevisionOf.getAutoInitialize());
      revisedParameter.setAutoInitialized(parameterRevisionOf.isAutoInitialized());
      revisedParameter.setHidden(parameterRevisionOf.isHidden());
      revisedParameter.setTargetEntityType(parameterRevisionOf.getTargetEntityType());
      if (Utility.isEmpty(parameterRevisionOf.getVerificationType())) {
        revisedParameter.setVerificationType(Type.VerificationType.NONE);
      } else {
        revisedParameter.setVerificationType(parameterRevisionOf.getVerificationType());
      }
      revisedParameter.setData(parameterRevisionOf.getData());
      revisedParameter.setLabel(parameterRevisionOf.getLabel());
      revisedParameter.setModifiedBy(principalUserEntity);
      revisedParameter.setCreatedBy(principalUserEntity);
      revisedParameter.setValidations(JsonUtils.createObjectNode());

      // TODO refactor
      if (revisedParameter.getType().equals(Type.Parameter.RESOURCE)) {
        allResourceParameters.add(revisedParameter);
      }

      revisedParametersOldAndNewIdMap.put(parameterRevisionOf.getId(), newId);

      if (Type.Parameter.MATERIAL.equals(parameterRevisionOf.getType())) {
        for (ParameterMediaMapping parameterMediaMapping : parameterRevisionOf.getMedias()) {
          if (!parameterMediaMapping.isArchived()) {
            revisedParameter.addMedia(parameterMediaMapping.getMedia(), principalUserEntity);
          }
        }
      }

      revisedParameters.add(revisedParameter);
      revisedParametersMap.put(revisedParameter.getId(), revisedParameter);
    }

    parameterRepository.saveAll(revisedParameters);
  }

  void reviseParameterAutoInitialize(List<Parameter> parametersHavingAutoInitialize, HashMap<Long, Long> revisedActivitiesOldAndNewIdMap) throws JsonProcessingException {
    for (Parameter parameter : parametersHavingAutoInitialize) {
      if (null != parameter.getAutoInitialize()) {
        AutoInitializeDto autoInitializeDto = JsonUtils.readValue(parameter.getAutoInitialize().toString(), AutoInitializeDto.class);
        autoInitializeDto.setParameterId(String.valueOf(revisedActivitiesOldAndNewIdMap.get(Long.valueOf(autoInitializeDto.getParameterId()))));
        parameterRepository.updateParameterAutoInitializeById(revisedActivitiesOldAndNewIdMap.get(parameter.getId()), JsonUtils.valueToNode(autoInitializeDto).toString());
      }
    }

  }

  private void addPrimaryAuthor(Checklist checklist, User user) {
    checklist.addPrimaryAuthor(user, checklist.getReviewCycle(), user);
  }

  void reviseParameterRules(Map<Long, Parameter> parametersHavingRules, HashMap<Long, Stage> revisedStageOldAndNewMap,
                            HashMap<Long, Task> revisedTaskOldAndNewMap, HashMap<Long, Long> revisedActivitiesOldAndNewIdMap) throws JsonProcessingException {
    for (Map.Entry<Long, Parameter> entry : parametersHavingRules.entrySet()) {
      Parameter parameter = entry.getValue();
      if (null != parameter.getRules()) {
        List<RuleDto> ruleDtos = JsonUtils.readValue(parameter.getRules().toString(),
          new TypeReference<List<RuleDto>>() {
          });
        // TODO if this fails job still gets created
        if (!Utility.isEmpty(ruleDtos)) {
          for (RuleDto ruleDto : ruleDtos) {
            if (null != ruleDto.getHide()) {
              updateRule(ruleDto.getHide(), revisedStageOldAndNewMap, revisedTaskOldAndNewMap,
                revisedActivitiesOldAndNewIdMap);
            }

            if (null != ruleDto.getShow()) {
              updateRule(ruleDto.getShow(), revisedStageOldAndNewMap, revisedTaskOldAndNewMap,
                revisedActivitiesOldAndNewIdMap);
            }
          }
        }
        parameterRepository.updateParameterRulesById(revisedActivitiesOldAndNewIdMap.get(parameter.getId()),
          JsonUtils.valueToNode(ruleDtos).toString());
      }
    }
  }

  void updateRule(RuleEntityIdDto rule, HashMap<Long, Stage> revisedStageOldAndNewMap,
                  HashMap<Long, Task> revisedTaskOldAndNewMap, HashMap<Long, Long> revisedActivitiesOldAndNewIdMap) {
    List<String> newStageIds = new ArrayList<>();
    List<String> newTaskIds = new ArrayList<>();
    List<String> newParameterIds = new ArrayList<>();

    for (String stageId : rule.getStages()) {
      newStageIds.add(String.valueOf(revisedStageOldAndNewMap.get(Long.valueOf(stageId)).getId()));
    }
    for (String taskId : rule.getTasks()) {
      newTaskIds.add(String.valueOf(revisedTaskOldAndNewMap.get(Long.valueOf(taskId)).getId()));
    }

    for (String parameterId : rule.getParameters()) {
      newParameterIds.add(String.valueOf(revisedActivitiesOldAndNewIdMap.get(Long.valueOf(parameterId))));
    }

    rule.setParameters(newParameterIds);
    rule.setStages(newStageIds);
    rule.setTasks(newTaskIds);
  }

  void setIdsForProcessParameterFilters(List<Parameter> allResourceParameters, HashMap<Long, Long> revisedParametersOldAndNewIdMap) throws JsonProcessingException {
    for (Parameter parameter : allResourceParameters) {
      ResourceParameter resourceParameter = JsonUtils.readValue(parameter.getData().toString(), ResourceParameter.class);
      ResourceParameterFilter resourceParameterFilter = resourceParameter.getPropertyFilters();
      if (!Utility.isNull(resourceParameterFilter)) {
        List<ResourceParameterFilterField> resourceParameterFilterFields = resourceParameterFilter.getFields();
        if (!Utility.isEmpty(resourceParameterFilterFields)) {
          for (ResourceParameterFilterField resourceParameterFilterField : resourceParameterFilterFields) {
            if (!Utility.isEmpty(resourceParameterFilterField.getReferencedParameterId())) {
              resourceParameterFilterField.setReferencedParameterId(String.valueOf(revisedParametersOldAndNewIdMap.get(Long.valueOf(resourceParameterFilterField.getReferencedParameterId()))));
            }
          }
        }
      }
      parameterRepository.updateParameterDataById(parameter.getId(), JsonUtils.valueToNode(resourceParameter).toString());
    }
  }

}
