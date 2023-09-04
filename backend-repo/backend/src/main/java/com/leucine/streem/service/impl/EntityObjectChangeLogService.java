package com.leucine.streem.service.impl;

import com.leucine.streem.collections.*;
import com.leucine.streem.collections.changelogs.*;
import com.leucine.streem.collections.helper.MongoFilter;
import com.leucine.streem.constant.CollectionMisc;
import com.leucine.streem.dto.mapper.IEntityObjectChangeLogMapper;
import com.leucine.streem.dto.mapper.IUserMapper;
import com.leucine.streem.dto.projection.JobProcessInfoView;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.exception.ExceptionType;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.repository.IEntityObjectChangeLogMongoRepository;
import com.leucine.streem.repository.IObjectTypeRepository;
import com.leucine.streem.service.IEntityObjectChangeLogService;
import com.leucine.streem.util.DateTimeUtils;
import com.leucine.streem.util.Utility;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.leucine.streem.constant.Misc.CREATE_PROPERTIES;

@Service
@Slf4j
@AllArgsConstructor
public class EntityObjectChangeLogService implements IEntityObjectChangeLogService {
  private final MongoTemplate mongoTemplate;
  private final IEntityObjectChangeLogMongoRepository entityObjectChangeLogMongoRepository;
  private final IObjectTypeRepository objectTypeRepository;
  private final IUserMapper userMapper;
  private final IEntityObjectChangeLogMapper entityObjectChangeLogMapper;

  /**
   * @param principalUser
   * @param oldEntityObject     (Object before change)
   * @param updatedEntityObject (Object after modifying)
   * @param reason              (reason for changing)
   * @param jobProcessInfoView  (reason & job/process info of object change due to automation
   * @throws ResourceNotFoundException
   */
  @Override
  public void save(PrincipalUser principalUser, EntityObject oldEntityObject, EntityObject updatedEntityObject, String reason, JobProcessInfoView jobProcessInfoView) throws ResourceNotFoundException {
    log.info("[save] Request to save changes in properties & relations of old object: {}, new object: {} due to reason: {}, job/process info: {}", oldEntityObject, updatedEntityObject, reason, jobProcessInfoView);
    ObjectType objectType = getObjectType(updatedEntityObject.getObjectTypeId().toString());
    Map<ObjectId, List<EntityDataDto>> oldPropertiesAndRelationMap = new HashMap<>();
    Map<ObjectId, List<EntityDataDto>> updatedPropertiesAndRelationMap = new HashMap<>();
    findModifiedProperties(oldEntityObject.getProperties(), updatedEntityObject.getProperties(), objectType, updatedPropertiesAndRelationMap, oldPropertiesAndRelationMap);
    findModifiedRelations(oldEntityObject.getRelations(), updatedEntityObject.getRelations(), objectType, updatedPropertiesAndRelationMap, oldPropertiesAndRelationMap);
    EntityObjectUsageStatus modifiedUsageStatus = findModifiedUsageStatus(oldEntityObject.getUsageStatus(), updatedEntityObject.getUsageStatus());
    save(principalUser, updatedPropertiesAndRelationMap, oldPropertiesAndRelationMap, updatedEntityObject, reason, modifiedUsageStatus, jobProcessInfoView);
  }

  /**
   * @param principalUser
   * @param updatedPropertyValueAndRelationMap modified property values or relations
   * @param oldPropertyRelationData            Old property or relation data
   * @param entityObject
   * @param reason
   */

  @Override
  public void save(PrincipalUser principalUser, Map<ObjectId, List<EntityDataDto>> updatedPropertyValueAndRelationMap,
                   Map<ObjectId, List<EntityDataDto>> oldPropertyRelationData, EntityObject entityObject, String reason, EntityObjectUsageStatus entityObjectUsageStatus, JobProcessInfoView jobProcessInfoView) {
    log.info("[save] Request to save change logs with updated property or relation values : {}, old property or relations  values: {}", updatedPropertyValueAndRelationMap, oldPropertyRelationData);
    //Here usage status is null because we don't want usage status to be present in each change log it is handled separately
    ChangeLogDataDto changeLogDataDto = getChangeLogData(entityObject, reason, null);
    EntityObjectChangeLog changeLog = entityObjectChangeLogMapper.toEntityObjectChangeLog(changeLogDataDto, null, principalUser.getCurrentFacilityId());
    entityObjectChangeLogMapper.toEntityObjectChangeLog(changeLogDataDto, null, principalUser.getCurrentFacilityId());
    if (!Utility.isEmpty(updatedPropertyValueAndRelationMap)) {
      List<EntityObjectChangeLog> changeLogsList = new ArrayList<>();

      for (Map.Entry<ObjectId, List<EntityDataDto>> entry : updatedPropertyValueAndRelationMap.entrySet()) {
        var updatedPropertyValueOrRelationData = updatedPropertyValueAndRelationMap.get(entry.getKey()).get(0);
        changeLog = entityObjectChangeLogMapper.toEntityObjectChangeLog(changeLogDataDto, entry.getKey().toHexString(), principalUser.getCurrentFacilityId());
        changeLog.setModifiedBy(userMapper.toUserAuditDto(principalUser));
        changeLog.setCreatedAt(DateTimeUtils.now());
        changeLog.setModifiedAt(DateTimeUtils.now());
        changeLog.setEntityType(getEntityType(entry.getValue().get(0).getInputType()));
        changeLog.setEntityDisplayName(updatedPropertyValueOrRelationData.getDisplayName());
        changeLog.setEntityCollection(updatedPropertyValueOrRelationData.getCollection());
        changeLog.setEntityExternalId(updatedPropertyValueOrRelationData.getExternalId());
        if (!Utility.isEmpty(jobProcessInfoView)) {
          changeLog.setInfo(new Info(jobProcessInfoView.getJobId(), jobProcessInfoView.getJobCode(), jobProcessInfoView.getProcessName(), jobProcessInfoView.getProcessId(), jobProcessInfoView.getProcessCode()));
        }
        if (!Utility.isEmpty(oldPropertyRelationData.get(entry.getKey()))) {
          changeLog.setOldEntityData(IEntityObjectChangeLogMapper.toChangeLogInputData(oldPropertyRelationData.get(entry.getKey())));
        }
        if (!Utility.isEmpty(updatedPropertyValueAndRelationMap.get(entry.getKey()))) {
          changeLog.setNewEntityData(IEntityObjectChangeLogMapper.toChangeLogInputData(updatedPropertyValueAndRelationMap.get(entry.getKey())));
        }
        changeLog.setEntityInputType(entry.getValue().get(0).getInputType());
        changeLogsList.add(changeLog);
      }
      entityObjectChangeLogMongoRepository.saveAll(changeLogsList);
    }

    if (!Utility.isEmpty(entityObjectUsageStatus) && entityObjectUsageStatus.getNewStatus() != null) {
      changeLogDataDto = getChangeLogData(entityObject, reason, entityObjectUsageStatus);
      changeLog = entityObjectChangeLogMapper.toEntityObjectChangeLog(changeLogDataDto, null, principalUser.getCurrentFacilityId());
      changeLog.setUsageStatus(entityObjectUsageStatus);
      changeLog.setCreatedAt(DateTimeUtils.now());
      changeLog.setModifiedAt(DateTimeUtils.now());
      changeLog.setModifiedBy(userMapper.toUserAuditDto(principalUser));
      if (!Utility.isEmpty(jobProcessInfoView)) {
        changeLog.setInfo(new Info(jobProcessInfoView.getJobId(), jobProcessInfoView.getJobCode(), jobProcessInfoView.getProcessName(), jobProcessInfoView.getProcessId(), jobProcessInfoView.getProcessCode()));
      }
      entityObjectChangeLogMongoRepository.save(changeLog);
    }
  }

  private EntityObjectUsageStatus findModifiedUsageStatus(int oldUsageStatus, int newUsageStatus) {
    if (oldUsageStatus != newUsageStatus) {
      return new EntityObjectUsageStatus(oldUsageStatus, newUsageStatus);
    }
    return null;
  }

  private void findModifiedRelations(List<MappedRelation> oldRelations, List<MappedRelation> newEntityObjectRelations, ObjectType objectType, Map<ObjectId, List<EntityDataDto>> updatedPropertiesAndRelationMap, Map<ObjectId, List<EntityDataDto>> oldPropertiesAndRelationMap) {
    Map<ObjectId, MappedRelation> oldMappedRelationMap = oldRelations.stream().collect(Collectors.toMap(MappedRelation::getId, Function.identity()));
    Map<ObjectId, Relation> relationMap = objectType.getRelations().stream().collect(Collectors.toMap(Relation::getId, Function.identity()));
    for (MappedRelation mappedRelation : newEntityObjectRelations) {
      compareRelations(oldMappedRelationMap.get(mappedRelation.getId()), mappedRelation, relationMap.get(mappedRelation.getId()), updatedPropertiesAndRelationMap, oldPropertiesAndRelationMap);
    }
  }

  private void compareRelations(MappedRelation oldMappedRelation, MappedRelation newMappedRelation, Relation relation, Map<ObjectId, List<EntityDataDto>> updatedPropertiesAndRelationMap, Map<ObjectId, List<EntityDataDto>> oldPropertiesAndRelationMap) {
    if (!Utility.isEmpty(oldMappedRelation) && !Objects.equals(oldMappedRelation.getTargets(), newMappedRelation.getTargets())) {
      List<EntityDataDto> newInputs = getRelationEntityData(newMappedRelation, relation);
      List<EntityDataDto> oldInputs = getRelationEntityData(oldMappedRelation, relation);
      updatedPropertiesAndRelationMap.put(newMappedRelation.getId(), newInputs);
      oldPropertiesAndRelationMap.put(oldMappedRelation.getId(), oldInputs);
    } else if (Utility.isEmpty(oldMappedRelation) && !Utility.isEmpty(newMappedRelation)) {
      List<EntityDataDto> newInputs = getRelationEntityData(newMappedRelation, relation);
      updatedPropertiesAndRelationMap.put(newMappedRelation.getId(), newInputs);
    }

  }


  private static List<EntityDataDto> getRelationEntityData(MappedRelation newMappedRelation, Relation relation) {
    if (!Utility.isEmpty(newMappedRelation) && !Utility.isEmpty(newMappedRelation.getTargets())) {
      return newMappedRelation.getTargets().stream()
        .map(mp -> EntityDataDto.builder()
          .collection(mp.getCollection())
          .displayName(newMappedRelation.getDisplayName())
          .externalId(newMappedRelation.getExternalId())
          .entityId(mp.getId().toHexString())
          .input(mp.getDisplayName())
          .inputType(CollectionMisc.ChangeLogInputType.valueOf(relation.getTarget().getCardinality().name()))
          .build())
        .toList();
    }
    return new ArrayList<>();
  }

  private void findModifiedProperties(List<PropertyValue> oldProperties, List<PropertyValue> updatedEntityObjectProperties, ObjectType objectType, Map<ObjectId, List<EntityDataDto>> updatedPropertiesAndRelationMap, Map<ObjectId, List<EntityDataDto>> oldPropertiesAndRelationMap) {
    Map<ObjectId, PropertyValue> oldPropertyValueMap = oldProperties.stream().collect(Collectors.toMap(PropertyValue::getId, Function.identity()));
    Map<ObjectId, Property> propertyMap = objectType.getProperties().stream().collect(Collectors.toMap(Property::getId, Function.identity()));
    for (PropertyValue propertyValue : updatedEntityObjectProperties) {
      compareProperties(oldPropertyValueMap.get(propertyValue.getId()), propertyValue, propertyMap.get(propertyValue.getId()).getInputType(), updatedPropertiesAndRelationMap, oldPropertiesAndRelationMap);
    }
  }

  private void compareProperties(PropertyValue oldPropertyValue, PropertyValue newPropertyValue, CollectionMisc.PropertyType inputType, Map<ObjectId, List<EntityDataDto>> updatedPropertyValueMap, Map<ObjectId, List<EntityDataDto>> oldPropertiesAndRelationMap) {
    String externalId = newPropertyValue.getExternalId();
    if (!CREATE_PROPERTIES.contains(externalId)) {
      switch (inputType) {
        case MULTI_SELECT, SINGLE_SELECT -> {
          List<EntityDataDto> newChangedChoice = getPropertyChoices(newPropertyValue, inputType);
          List<EntityDataDto> oldChangedChoice = getPropertyChoices(oldPropertyValue, inputType);
          if (!Utility.isEmpty(oldPropertyValue) && !Objects.equals(oldPropertyValue.getChoices(), newPropertyValue.getChoices())) {
            updatedPropertyValueMap.put(newPropertyValue.getId(), newChangedChoice);
            oldPropertiesAndRelationMap.put(oldPropertyValue.getId(), oldChangedChoice);

          } else if (Utility.isEmpty(oldPropertyValue) && !Utility.isEmpty(newPropertyValue)) {
            updatedPropertyValueMap.put(newPropertyValue.getId(), newChangedChoice);
          }
        }
        case DATE, DATE_TIME, NUMBER, MULTI_LINE, SINGLE_LINE -> {
          if (!Utility.isEmpty(oldPropertyValue) && !Objects.equals(oldPropertyValue.getValue(), newPropertyValue.getValue())) {
            updatedPropertyValueMap.put(newPropertyValue.getId(), getPropertyValue(newPropertyValue, inputType));
            oldPropertiesAndRelationMap.put(oldPropertyValue.getId(), getPropertyValue(oldPropertyValue, inputType));
          } else if (Utility.isEmpty(oldPropertyValue) && !Utility.isEmpty(newPropertyValue)) {
            updatedPropertyValueMap.put(newPropertyValue.getId(), getPropertyValue(newPropertyValue, inputType));
          }
        }
      }
    }
  }

  private static List<EntityDataDto> getPropertyValue(PropertyValue newPropertyValue, CollectionMisc.PropertyType inputType) {
    return List.of(EntityDataDto.builder()
      .input(newPropertyValue.getValue())
      .displayName(newPropertyValue.getDisplayName())
      .externalId(newPropertyValue.getExternalId())
      .inputType(CollectionMisc.ChangeLogInputType.valueOf(inputType.toString()))
      .build());
  }

  private static List<EntityDataDto> getPropertyChoices(PropertyValue newPropertyValue, CollectionMisc.PropertyType inputType) {
    if (!Utility.isEmpty(newPropertyValue)) {
      return newPropertyValue.getChoices().stream()
        .map(po -> EntityDataDto.builder()
          .entityId(po.getId().toHexString())
          .input(po.getDisplayName())
          .externalId(newPropertyValue.getExternalId())
          .displayName(newPropertyValue.getDisplayName())
          .inputType(CollectionMisc.ChangeLogInputType.valueOf(inputType.toString()))
          .build())
        .toList();
    }
    return new ArrayList<>();
  }

  @Override
  public Page<EntityObjectChangeLog> findAllChangeLogs(String filters, Pageable pageable) {
    log.info("[findAllChangeLogs] Request to get all change logs with filters: {}", filters);
    var query = MongoFilter.buildQuery(filters);
    long count = mongoTemplate.count(query, EntityObjectChangeLog.class);
    query.with(pageable);
    var entityObjects = mongoTemplate.find(query, EntityObjectChangeLog.class);
    return PageableExecutionUtils.getPage(entityObjects, pageable, () -> count);
  }

  private CollectionMisc.ChangeLogType getEntityType(CollectionMisc.ChangeLogInputType inputType) {
    return switch (inputType) {
      case ONE_TO_MANY, ONE_TO_ONE -> CollectionMisc.ChangeLogType.RELATION;
      default -> CollectionMisc.ChangeLogType.PROPERTY;
    };
  }

  private ChangeLogDataDto getChangeLogData(EntityObject entityObject, String reason, EntityObjectUsageStatus entityObjectUsageStatus) {
    return ChangeLogDataDto.builder()
      .objectTypeId(entityObject.getObjectTypeId().toString())
      .objectId(entityObject.getId().toString())
      .collection(entityObject.getCollection())
      .externalId(entityObject.getExternalId())
      .oldUsageStatus(entityObjectUsageStatus == null ? null : entityObjectUsageStatus.getOldStatus())
      .newUsageStatus(entityObjectUsageStatus == null ? null : entityObjectUsageStatus.getNewStatus())
      .reason(reason)
      .build();
  }

  private ObjectType getObjectType(String objectTypeId) throws ResourceNotFoundException {
    return objectTypeRepository.findById(objectTypeId)
      .orElseThrow(() -> new ResourceNotFoundException(objectTypeId, ErrorCode.ENTITY_OBJECT_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
  }
}
