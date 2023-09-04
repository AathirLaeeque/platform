package com.leucine.streem.service.impl;

import com.leucine.commons.id.IdGenerator;
import com.leucine.streem.collections.Property;
import com.leucine.streem.collections.*;
import com.leucine.streem.collections.partial.PartialEntityObject;
import com.leucine.streem.constant.CollectionMisc;
import com.leucine.streem.constant.Misc;
import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.*;
import com.leucine.streem.dto.mapper.IAutomationMapper;
import com.leucine.streem.dto.mapper.ITaskMapper;
import com.leucine.streem.dto.projection.JobProcessInfoView;
import com.leucine.streem.dto.request.ArchiveObjectRequest;
import com.leucine.streem.dto.request.AutomationRequest;
import com.leucine.streem.dto.request.EntityObjectValueRequest;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.exception.ExceptionType;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.*;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.model.helper.parameter.ResourceParameter;
import com.leucine.streem.repository.*;
import com.leucine.streem.service.IEntityObjectService;
import com.leucine.streem.service.ITaskAutomationService;
import com.leucine.streem.util.DateTimeUtils;
import com.leucine.streem.util.JsonUtils;
import com.leucine.streem.util.Utility;
import com.leucine.streem.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.util.Pair;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskAutomationService implements ITaskAutomationService {
  private final ITaskRepository taskRepository;
  private final IAutomationMapper automationMapper;
  private final IAutomationRepository automationRepository;
  private final IEntityObjectRepository entityObjectRepository;
  private final IParameterRepository parameterRepository;
  private final IParameterValueRepository parameterValueRepository;
  private final ITaskAutomationMappingRepository taskAutomationMappingRepository;
  private final IUserRepository userRepository;
  private final ITaskMapper taskMapper;
  private final ITaskExecutionRepository taskExecutionRepository;
  private final IJobRepository jobRepository;
  private final IEntityObjectService entityObjectService;
  private final IObjectTypeRepository objectTypeRepository;

  @Override
  public TaskDto addTaskAutomations(Long taskId, List<AutomationRequest> automationRequests) throws ResourceNotFoundException {
    log.info("[addTaskAutomations] Request to add task automations, taskId: {}, automationRequests: {}", taskId, automationRequests);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findById(principalUser.getId())
      .orElseThrow(() -> new ResourceNotFoundException(principalUser.getId(), ErrorCode.USER_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Task task = taskRepository.getReferenceById(taskId);

    List<Automation> automations = new ArrayList<>();
    Map<Long, Pair<Automation, AutomationRequest>> automationAndAutomationRequestMap = new HashMap<>();

    for (AutomationRequest automationRequest : automationRequests) {
      Automation automation = automationMapper.toEntity(automationRequest);

      // TODO: currently considering all the saves as modified by user, need to change this logic
      automation.setModifiedBy(user);
      automations.add(automation);
      // TODO: currently considering all the saves as modified by user, need to change this logic
      // TODO: not consistent with how we add many to many mappings change this logic
      if (Utility.isEmpty(automation.getId())) {
        automation.setCreatedBy(user);
        automation.setId(IdGenerator.getInstance().nextId());
      }
      automationAndAutomationRequestMap.put(automation.getId(), Pair.of(automation, automationRequest));
    }
    automationRepository.saveAll(automations);

    task.addAutomations(automationAndAutomationRequestMap, user);
    return taskMapper.toDto(taskRepository.save(task));
  }

  @Override
  public AutomationDto updateAutomation(Long automationId, AutomationRequest automationRequest) throws ResourceNotFoundException {
    log.info("[updateTaskAutomation] Request to update automation, automationId: {}, automationRequest: {}", automationId, automationRequest);

    Automation automation = automationRepository.findById(automationId)
      .orElseThrow(() -> new ResourceNotFoundException(automationId, ErrorCode.AUTOMATION_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    automationMapper.update(automationRequest, automation);
    return automationMapper.toDto(automationRepository.save(automation));
  }

  @Override
  public TaskDto deleteTaskAutomation(Long taskId, Long automationId) throws ResourceNotFoundException {
    log.info("[deleteTaskAutomation] Request to delete task automation, taskId: {}, automationId: {}", taskId, automationId);

    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.findById(principalUser.getId())
      .orElseThrow(() -> new ResourceNotFoundException(principalUser.getId(), ErrorCode.USER_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    taskAutomationMappingRepository.deleteByTaskIdAndAutomationId(taskId, automationId);

    Automation automation = automationRepository.findById(automationId)
      .orElseThrow(() -> new ResourceNotFoundException(automationId, ErrorCode.AUTOMATION_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    automation.setArchived(true);
    automation.setModifiedBy(principalUserEntity);
    automationRepository.save(automation);

    return taskMapper.toDto(taskRepository.findById(taskId).get());
  }

  @Override
  public void completeTaskAutomations(Long taskId, Long jobId, String automationReason, PrincipalUser principalUser) throws IOException, ResourceNotFoundException, StreemException {
    log.info("[completeTaskAutomations] Request to complete task automation, taskId: {}, jobId:{}, automationReason: {}", taskId, jobId, automationReason);

    Set<Automation> automations = taskAutomationMappingRepository.findAllAutomationsByTaskId(taskId);
    JobProcessInfoView jobInfo = jobRepository.findJobProcessInfo(jobId);

    String reason = Misc.CHANGED_AS_PER_PROCESS;

    for (Automation automation : automations) {
      // TODO Handle these with switch cases
      if (Type.AutomationTriggerType.TASK_COMPLETED.equals(automation.getTriggerType())) {
        switch (automation.getActionType()) {
          case INCREASE_PROPERTY, DECREASE_PROPERTY -> {
            // TODO needs testing, add comments
            if (Type.TargetEntityType.RESOURCE_PARAMETER.equals(automation.getTargetEntityType())) {
              AutomationActionForResourceParameterDto resourceParameterAction = JsonUtils.readValue(automation.getActionDetails().toString(), AutomationActionForResourceParameterDto.class);

              Long parameterId = Long.valueOf(resourceParameterAction.getReferencedParameterId());
              ParameterValue parameterValue = parameterValueRepository.findByJobIdAndParameterId(jobId, parameterId);
              Parameter parameter = parameterRepository.findById(parameterId).get();
              ResourceParameter resourceParameter = JsonUtils.readValue(parameter.getData().toString(), ResourceParameter.class);
              List<ResourceParameterChoiceDto> parameterChoices = JsonUtils.jsonToCollectionType(parameterValue.getChoices(), List.class, ResourceParameterChoiceDto.class);
              if (!Utility.isEmpty(parameterChoices)) {
                for (ResourceParameterChoiceDto resourceParameterChoice : parameterChoices) {
                  EntityObject entityObject = getEntityObject(resourceParameter.getObjectTypeExternalId(), resourceParameterChoice.getObjectId());
                  Map<String, PropertyValue> propertyValueMap = entityObject.getProperties().stream().collect(Collectors.toMap(pv -> pv.getId().toString()
                    , Function.identity()));

                  PropertyValue propertyValue = propertyValueMap.get(resourceParameterAction.getPropertyId());

                  if (Utility.isNull(propertyValue) || Utility.isNull(propertyValue.getValue())) {
                    ValidationUtils.invalidate(automation.getId(), ErrorCode.RESOURCE_PARAMETER_AUTOMATION_ACTION_ERROR);
                  }
                  ParameterValue referencedParameterValue = parameterValueRepository.findByJobIdAndParameterId(jobId, Long.valueOf(resourceParameterAction.getParameterId()));
                  if (Utility.isNull(referencedParameterValue) || Utility.isNull(referencedParameterValue.getValue())) {
                    ValidationUtils.invalidate(automation.getId(), ErrorCode.PROCESS_RELATION_AUTOMATION_ACTION_ERROR);
                  }

                  Optional<Property> optionalProperty = objectTypeRepository.findPropertyByIdAndObjectTypeExternalId(resourceParameter.getObjectTypeExternalId(), propertyValue.getId());
                  if (!Utility.isEmpty(optionalProperty)) {
                    Property property = optionalProperty.get();
                    if (property.getUsageStatus() == CollectionMisc.UsageStatus.DEPRECATED.get()) {
                      ValidationUtils.invalidate(automation.getId(), ErrorCode.CANNOT_PERFORM_AUTOMATION_ACTION_ON_ARCHIVED_PROPERTY);
                    }
                  }

                  switch (automation.getActionType()) {
                    case INCREASE_PROPERTY -> {
                      double value = Double.parseDouble(propertyValue.getValue());
                      value += Double.parseDouble(referencedParameterValue.getValue());
                      propertyValue.setValue(String.valueOf(value));
                    }
                    case DECREASE_PROPERTY -> {
                      double value = Double.parseDouble(propertyValue.getValue());
                      value -= Double.parseDouble(referencedParameterValue.getValue());
                      propertyValue.setValue(String.valueOf(value));
                    }
                  }
                  EntityObjectValueRequest entityObjectValueRequest = new EntityObjectValueRequest();
                  Map<String, List<PartialEntityObject>> partialEntityObjectMap = new HashMap<>();
                  getSelectedRelations(entityObject, partialEntityObjectMap);
                  entityObjectValueRequest.setRelations(partialEntityObjectMap);
                  entityObjectValueRequest.setProperties(new HashMap<>(Map.of(resourceParameterAction.getPropertyId(), propertyValue.getValue())));
                  entityObjectValueRequest.setObjectTypeId(resourceParameter.getObjectTypeId());

                  entityObjectValueRequest.setReason(reason);

                  entityObjectService.update(entityObject.getId().toString(), entityObjectValueRequest, jobInfo);
                }
              } else {
                ValidationUtils.invalidate(automation.getId(), ErrorCode.AUTOMATION_WILL_NOT_RUN_DUE_TO_MISSING_RESOURCES);
              }
            }
          }
          case SET_PROPERTY -> {
            // TODO task gets completed
            AutomationSetPropertyBaseDto automationBase = JsonUtils.readValue(automation.getActionDetails().toString(), AutomationSetPropertyBaseDto.class);

            if (automationBase.getPropertyInputType().equals(CollectionMisc.PropertyType.DATE_TIME) || automationBase.getPropertyInputType().equals(CollectionMisc.PropertyType.DATE)) {
              AutomationActionDateTimeDto automationSetProperty = JsonUtils.readValue(automation.getActionDetails().toString(), AutomationActionDateTimeDto.class);
              Long parameterId = Long.valueOf(automationSetProperty.getReferencedParameterId());
              ParameterValue parameterValue = parameterValueRepository.findByJobIdAndParameterId(jobId, parameterId);

              Parameter parameter = parameterRepository.findById(parameterId).get();
              ResourceParameter resourceParameter = JsonUtils.readValue(parameter.getData().toString(), ResourceParameter.class);

              List<ResourceParameterChoiceDto> parameterChoices = JsonUtils.jsonToCollectionType(parameterValue.getChoices(), List.class, ResourceParameterChoiceDto.class);

              if (!Utility.isEmpty(parameterChoices)) {
                for (ResourceParameterChoiceDto resourceParameterChoice : parameterChoices) {

                  EntityObject entityObject = getEntityObject(resourceParameter.getObjectTypeExternalId(), resourceParameterChoice.getObjectId());
                  ObjectType objectType = objectTypeRepository.findById(entityObject.getObjectTypeId().toString()).get();

                  Map<String, PropertyValue> propertyValueMap = entityObject.getProperties().stream().collect(Collectors.toMap(pv -> pv.getId().toString()
                    , Function.identity()));

                  Set<String> propertyIds = getAllPropertyIdsOfObjectType(objectType);

                  if (!propertyIds.contains(automationSetProperty.getPropertyId())) {
                    ValidationUtils.invalidate(automation.getId(), ErrorCode.RESOURCE_PARAMETER_AUTOMATION_ACTION_ERROR);
                  }

                  PropertyValue propertyValue = propertyValueMap.get(automationSetProperty.getPropertyId());

                  Long timeRequired = 0L;
                  switch (automationSetProperty.getEntityType()) {
                    case TASK -> {
                      TaskExecution taskExecution = taskExecutionRepository.findByTaskIdAndJobId(Long.valueOf(automationSetProperty.getEntityId()), jobId);
                      if (Type.AutomationDateTimeCaptureType.START_TIME.equals(automationSetProperty.getCaptureProperty())) {
                        timeRequired = taskExecution.getStartedAt();
                      } else {
                        timeRequired = taskExecution.getEndedAt();
                      }
                    }
                  }
                  Long dateTimeValue = null;
                  //TODO: As per desired behavior, when  date unit is months it has to be the same day of the upcoming month (e.g. if date is 3rd July, and set property is 3 months, then the date should be 3rd October) currently its 1st Oct
                  switch (automationSetProperty.getDateUnit()) {
                    case SECONDS, WEEKS, MINUTES, HOURS, DAYS ->
                      dateTimeValue = timeRequired + Duration.of(automationSetProperty.getValue(), ChronoUnit.valueOf(automationSetProperty.getDateUnit().name())).toSeconds();
                    case MONTHS ->
                      dateTimeValue = timeRequired + DateTimeUtils.getLocalDateTime(0).plusMonths(automationSetProperty.getValue()).toEpochSecond(ZoneOffset.UTC);
                    case YEARS ->
                      dateTimeValue = timeRequired + DateTimeUtils.getLocalDateTime(0).plusYears(automationSetProperty.getValue()).toEpochSecond(ZoneOffset.UTC);
                    default ->
                      ValidationUtils.invalidate(automation.getId(), ErrorCode.RESOURCE_PARAMETER_AUTOMATION_ACTION_ERROR);
                  }
                  propertyValue.setValue(String.valueOf(dateTimeValue));
                  EntityObjectValueRequest entityObjectValueRequest = new EntityObjectValueRequest();
                  Map<String, List<PartialEntityObject>> partialEntityObjectMap = new HashMap<>();
                  getSelectedRelations(entityObject, partialEntityObjectMap);
                  entityObjectValueRequest.setRelations(partialEntityObjectMap);
                  entityObjectValueRequest.setProperties(new HashMap<>(Map.of(automationSetProperty.getPropertyId(), propertyValue.getValue())));
                  entityObjectValueRequest.setObjectTypeId(resourceParameter.getObjectTypeId());
                  entityObjectValueRequest.setReason(reason);

                  entityObjectService.update(entityObject.getId().toString(), entityObjectValueRequest, jobInfo);

                }
              } else {
                ValidationUtils.invalidate(automation.getId(), ErrorCode.AUTOMATION_WILL_NOT_RUN_DUE_TO_MISSING_RESOURCES);
              }
            } else {
              AutomationActionSetPropertyDto automationSetProperty = JsonUtils.readValue(automation.getActionDetails().toString(), AutomationActionSetPropertyDto.class);

              Long parameterId = Long.valueOf(automationSetProperty.getReferencedParameterId());
              ParameterValue parameterValue = parameterValueRepository.findByJobIdAndParameterId(jobId, parameterId);
              Parameter parameter = parameterRepository.findById(parameterId).get();
              ResourceParameter resourceParameter = JsonUtils.readValue(parameter.getData().toString(), ResourceParameter.class);
              List<ResourceParameterChoiceDto> parameterChoices = JsonUtils.jsonToCollectionType(parameterValue.getChoices(), List.class, ResourceParameterChoiceDto.class);
              if (!Utility.isEmpty(parameterChoices)) {
                for (ResourceParameterChoiceDto resourceParameterChoice : parameterChoices) {
                  EntityObject entityObject = getEntityObject(resourceParameter.getObjectTypeExternalId(), resourceParameterChoice.getObjectId());

                  ObjectType objectType = objectTypeRepository.findById(entityObject.getObjectTypeId().toString()).get();

                  Set<String> propertyIds = getAllPropertyIdsOfObjectType(objectType);

                  if (!propertyIds.contains(automationSetProperty.getPropertyId())) {
                    ValidationUtils.invalidate(automation.getId(), ErrorCode.RESOURCE_PARAMETER_AUTOMATION_ACTION_ERROR);
                  }

                  EntityObjectValueRequest entityObjectValueRequest = new EntityObjectValueRequest();

                  Object value = automationSetProperty.getValue() == null ? automationSetProperty.getChoices().stream()
                    .map(propertyOption -> propertyOption.getId().toString()).toList()
                    : automationSetProperty.getValue();

                  Map<String, List<PartialEntityObject>> partialEntityObjectMap = new HashMap<>();
                  getSelectedRelations(entityObject, partialEntityObjectMap);
                  entityObjectValueRequest.setRelations(partialEntityObjectMap);
                  entityObjectValueRequest.setProperties(new HashMap<>(Map.of(automationSetProperty.getPropertyId(), value)));
                  entityObjectValueRequest.setObjectTypeId(resourceParameter.getObjectTypeId());
                  entityObjectValueRequest.setReason(reason);

                  entityObjectService.update(entityObject.getId().toString(), entityObjectValueRequest, jobInfo);

                }
              } else {
                ValidationUtils.invalidate(automation.getId(), ErrorCode.AUTOMATION_WILL_NOT_RUN_DUE_TO_MISSING_RESOURCES);
              }
            }
          }
          case ARCHIVE_OBJECT -> {
            AutomationActionArchiveObjectDto automationActionArchiveObjectDto = JsonUtils.readValue(automation.getActionDetails().toString(), AutomationActionArchiveObjectDto.class);
            ParameterValue referencedParameterValue = parameterValueRepository.findByJobIdAndParameterId(jobId, Long.valueOf(automationActionArchiveObjectDto.getReferencedParameterId()));
            List<ResourceParameterChoiceDto> parameterChoices = JsonUtils.jsonToCollectionType(referencedParameterValue.getChoices(), List.class, ResourceParameterChoiceDto.class);

            if (Utility.isEmpty(parameterChoices)) {
              ValidationUtils.invalidate(automation.getId(), ErrorCode.RESOURCE_PARAMETER_AUTOMATION_ACTION_ERROR);
            }
            for (ResourceParameterChoiceDto resourceParameterChoice : parameterChoices) {
              entityObjectService.archiveObject(new ArchiveObjectRequest(resourceParameterChoice.getCollection(), reason), resourceParameterChoice.getObjectId(), jobInfo);
            }
          }

          case SET_RELATION -> {
            //TODO: add validations & refactor
            AutomationActionMappedRelationDto automationActionMappedRelationDto = JsonUtils.readValue(automation.getActionDetails().toString(), AutomationActionMappedRelationDto.class);
            ParameterValue parameterValue = parameterValueRepository.findByJobIdAndParameterId(jobId, Long.valueOf(automationActionMappedRelationDto.getParameterId()));
            ParameterValue referencedParameterValue = parameterValueRepository.findByJobIdAndParameterId(jobId, Long.valueOf(automationActionMappedRelationDto.getReferencedParameterId()));

            Parameter parameter = parameterRepository.findById(referencedParameterValue.getParameterId()).get();
            ResourceParameter resourceParameter = JsonUtils.readValue(parameter.getData().toString(), ResourceParameter.class);
            List<ResourceParameterChoiceDto> referencedParameterChoices = JsonUtils.jsonToCollectionType(referencedParameterValue.getChoices(), List.class, ResourceParameterChoiceDto.class);
            List<ResourceParameterChoiceDto> parameterChoices = JsonUtils.jsonToCollectionType(parameterValue.getChoices(), List.class, ResourceParameterChoiceDto.class);

            if (Utility.isEmpty(referencedParameterChoices) || Utility.isEmpty(parameterChoices)) {
              ValidationUtils.invalidate(automation.getId(), ErrorCode.AUTOMATION_WILL_NOT_RUN_DUE_TO_MISSING_RESOURCES);
            }

            for (ResourceParameterChoiceDto resourceParameterChoiceDto : referencedParameterChoices) {
              EntityObject entityObject = getEntityObject(resourceParameter.getObjectTypeExternalId(), resourceParameterChoiceDto.getObjectId());

              Map<String, List<PartialEntityObject>> partialEntityObjectMap = new HashMap<>();
              // Setting the current relations also in request so these relations doesn't get reset
              getSelectedRelations(entityObject, partialEntityObjectMap);
              partialEntityObjectMap.remove(automationActionMappedRelationDto.getRelationId());

              for (ResourceParameterChoiceDto parameterChoiceDto : parameterChoices) {
                getPartialEntityObjectMap(partialEntityObjectMap, parameterChoiceDto, new ObjectId(automationActionMappedRelationDto.getRelationId()));
              }

              EntityObjectValueRequest entityObjectValueRequest = new EntityObjectValueRequest();
              entityObjectValueRequest.setObjectTypeId(resourceParameter.getObjectTypeId());
              entityObjectValueRequest.setRelations(partialEntityObjectMap);
              entityObjectValueRequest.setReason(reason);

              entityObjectService.update(entityObject.getId().toString(), entityObjectValueRequest, jobInfo);

            }
          }
        }
      }


    }
  }

  private static void getSelectedRelations(EntityObject entityObject, Map<String, List<PartialEntityObject>> partialEntityObjectMap) {
    for (MappedRelation mappedRelation : entityObject.getRelations()) {
      List<PartialEntityObject> partialEntityObjects = new ArrayList<>();
      for (MappedRelationTarget mappedRelationTarget : mappedRelation.getTargets()) {
        PartialEntityObject partialEntityObject = new PartialEntityObject();
        partialEntityObject.setCollection(mappedRelationTarget.getCollection());
        partialEntityObject.setId(mappedRelationTarget.getId());
        partialEntityObject.setExternalId(mappedRelationTarget.getExternalId());
        partialEntityObject.setDisplayName(mappedRelationTarget.getDisplayName());
        partialEntityObjects.add(partialEntityObject);
      }
      partialEntityObjectMap.put(mappedRelation.getId().toString(), partialEntityObjects);
    }
  }

  private static void getPartialEntityObjectMap(Map<String, List<PartialEntityObject>> partialEntityObjectMap, ResourceParameterChoiceDto parameterChoiceDto, ObjectId relationId) {
    //TODO: convert to mapper
    PartialEntityObject partialEntityObject = new PartialEntityObject();
    partialEntityObject.setId(new ObjectId(parameterChoiceDto.getObjectId()));
    partialEntityObject.setDisplayName(parameterChoiceDto.getObjectDisplayName());
    partialEntityObject.setCollection(parameterChoiceDto.getCollection());
    partialEntityObject.setExternalId(parameterChoiceDto.getObjectExternalId());

    List<PartialEntityObject> partialEntityObjects = partialEntityObjectMap.get(relationId.toString());
    if (partialEntityObjects == null) {
      partialEntityObjects = new ArrayList<>();
    }
    partialEntityObjects.add(partialEntityObject);
    partialEntityObjectMap.put(relationId.toString(), partialEntityObjects);
  }

  private static Set<String> getAllPropertyIdsOfObjectType(ObjectType objectType) {
    return objectType.getProperties().stream()
      .filter(property -> property.getUsageStatus() == CollectionMisc.UsageStatus.ACTIVE.get())
      .map(property -> property.getId().toString()).collect(Collectors.toSet());
  }

  private EntityObject getEntityObject(String externalId, String objectId) throws ResourceNotFoundException {
    return entityObjectRepository.findById(externalId, objectId)
      .orElseThrow(() -> new ResourceNotFoundException(objectId, ErrorCode.ENTITY_OBJECT_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
  }

}
