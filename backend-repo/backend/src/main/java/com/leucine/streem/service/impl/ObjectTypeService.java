package com.leucine.streem.service.impl;

import com.leucine.streem.collections.*;
import com.leucine.streem.constant.CollectionKey;
import com.leucine.streem.constant.CollectionMisc;
import com.leucine.streem.constant.Misc;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.ObjectTypePropertyOptionCreateRequest;
import com.leucine.streem.dto.ObjectTypeRelationTargetCreateRequest;
import com.leucine.streem.dto.mapper.IObjectTypePropertyMapper;
import com.leucine.streem.dto.mapper.IUserInfoMapper;
import com.leucine.streem.dto.request.ObjectTypeCreateRequest;
import com.leucine.streem.dto.request.ObjectTypePropertyCreateRequest;
import com.leucine.streem.dto.request.ObjectTypeRelationCreateRequest;
import com.leucine.streem.dto.request.ObjectTypeUpdateRequest;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.exception.ExceptionType;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.User;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.repository.IEntityObjectRepository;
import com.leucine.streem.repository.IObjectTypeMongoRepository;
import com.leucine.streem.repository.IObjectTypeRepository;
import com.leucine.streem.repository.IUserRepository;
import com.leucine.streem.service.IObjectTypeService;
import com.leucine.streem.util.DateTimeUtils;
import com.leucine.streem.util.Utility;
import com.leucine.streem.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.CaseUtils;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.leucine.streem.constant.CollectionKey.DISPLAY_NAME;
import static com.leucine.streem.constant.CollectionKey.EXTERNAL_ID;
import static com.leucine.streem.constant.Misc.STRING_CHAR_REGEX;

@Slf4j
@Service
@RequiredArgsConstructor
public class ObjectTypeService implements IObjectTypeService {
  private final IObjectTypeRepository objectTypeRepository;
  private final IUserRepository userRepository;
  private final IEntityObjectRepository entityObjectRepository;
  private final IUserInfoMapper userInfoMapper;
  private final IObjectTypePropertyMapper objectTypePropertyMapper;
  private final MongoTemplate mongoTemplate;
  private final IObjectTypeMongoRepository objectTypeMongoRepository;
  private static final Pattern VALID_STRING_PATTERN = Pattern.compile(STRING_CHAR_REGEX);

  @Override
  public ObjectType findById(String id) throws ResourceNotFoundException {
    log.info("[findById] Request to get an Object Type, id: {}", id);
    ObjectType objectType = objectTypeRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException(id, ErrorCode.OBJECT_TYPE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    var properties = objectType.getProperties();

    if (!Utility.isEmpty(properties)) {
      List<Property> activeProperties = properties.stream().filter(property -> property.getUsageStatus() == CollectionMisc.UsageStatus.ACTIVE.get() && property.getFlags() != 1).toList();
      objectType.setProperties(activeProperties);
    }
    var relations = objectType.getRelations();
    if (!Utility.isEmpty(relations)) {
      List<Relation> activeRelations = relations.stream().filter(relation -> relation.getUsageStatus() == CollectionMisc.UsageStatus.ACTIVE.get()).toList();
      objectType.setRelations(activeRelations);
    }
    return objectType;
  }

  @Override
  public Page<ObjectType> findAll(int usageStatus, String name, Pageable pageable) throws StreemException {
    log.info("[findAll] Request to get all Object Type by Usage Status, usageStatus: {}", usageStatus);
    validateFiltersDisplayName(name, false, false, true);
    return objectTypeRepository.findAll(usageStatus, name, pageable);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public BasicDto createObjectType(ObjectTypeCreateRequest objectTypeCreateRequest) throws StreemException {
    log.info("[createObjectType] Request to add Object Type, objectTypeCreateRequest: {}", objectTypeCreateRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getReferenceById(principalUser.getId());

    ObjectType newObjectType = createNewObjectType(objectTypeCreateRequest, principalUserEntity);
    newObjectType.setModifiedAt(DateTimeUtils.now());
    newObjectType.setModifiedBy(userInfoMapper.toUserInfo(principalUser));
    objectTypeRepository.save(newObjectType);

    BasicDto basicDto = new BasicDto();
    basicDto.setMessage("success");
    return basicDto;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public BasicDto addObjectTypeProperty(String objectTypeId, ObjectTypePropertyCreateRequest objectTypePropertyCreateRequest) throws ResourceNotFoundException, StreemException {
    log.info("[addObjectTypeProperties] Request to add Object Type properties, objectTypeId: {}, objectTypePropertyCreateRequest: {}", objectTypeId, objectTypePropertyCreateRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    ObjectType objectType = objectTypeRepository.findById(objectTypeId)
      .orElseThrow(() -> new ResourceNotFoundException(objectTypeId, ErrorCode.OBJECT_TYPE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    List<Property> existingProperties = objectType.getProperties();
    String displayName = objectTypePropertyCreateRequest.getDisplayName();
    displayName = displayName.trim();
    validatePropertyDisplayName(existingProperties, displayName);
    Property property = createProperty(new Property(), objectTypePropertyCreateRequest, displayName, true);
    property.setId(new ObjectId());
    List<PropertyOption> propertyOptionList = new ArrayList<>();
    for (ObjectTypePropertyOptionCreateRequest propertyOptionCreateRequest : objectTypePropertyCreateRequest.getOptions()) {
      PropertyOption newPropertyOption = new PropertyOption();
      newPropertyOption.setId(new ObjectId());
      String optionDisplayName = propertyOptionCreateRequest.getDisplayName();
      optionDisplayName = optionDisplayName.trim();
      validateOptionDisplayName(propertyOptionList, optionDisplayName);
      newPropertyOption.setDisplayName(optionDisplayName);
      propertyOptionList.add(newPropertyOption);
    }
    property.setOptions(propertyOptionList);


    existingProperties.add(property);

    objectType.setModifiedAt(DateTimeUtils.now());
    objectType.setModifiedBy(userInfoMapper.toUserInfo(principalUser));
    objectType.setProperties(existingProperties);
    objectTypeRepository.save(objectType);

    addPropertyValueInEntityObjects(objectType, property);
    BasicDto basicDto = new BasicDto();
    basicDto.setId(objectType.getId().toString())
      .setMessage("success");
    return basicDto;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public BasicDto addObjectTypeRelation(String objectTypeId, ObjectTypeRelationCreateRequest objectTypeRelationCreateRequest) throws ResourceNotFoundException, StreemException {
    log.info("[addObjectTypeRelations] Request to add Object Type relations, objectTypeRelationCreateRequest: {}", objectTypeRelationCreateRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    ObjectType objectType = objectTypeRepository.findById(objectTypeId)
      .orElseThrow(() -> new ResourceNotFoundException(objectTypeId, ErrorCode.OBJECT_TYPE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    List<Relation> existingRelations = Utility.isEmpty(objectType.getRelations()) ? new ArrayList<>() : objectType.getRelations();
    ObjectType relationObjectType = objectTypeRepository.findById(objectTypeRelationCreateRequest.getObjectTypeId())
      .orElseThrow(() -> new ResourceNotFoundException(objectTypeId, ErrorCode.OBJECT_TYPE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    String displayName = objectTypeRelationCreateRequest.getDisplayName();
    displayName = displayName.trim();
    validateRelationDisplayName(existingRelations, displayName);

    Relation relation = createRelation(relationObjectType, new Relation(), objectTypeRelationCreateRequest, displayName);
    relation.setId(new ObjectId());
    relation.setUsageStatus(objectTypeRelationCreateRequest.getUsageStatus());
    existingRelations.add(relation);


    objectType.setModifiedAt(DateTimeUtils.now());
    objectType.setModifiedBy(userInfoMapper.toUserInfo(principalUser));
    objectType.setRelations(existingRelations);
    objectTypeRepository.save(objectType);

    addRelationValueInEntityObjects(objectType, relation);
    BasicDto basicDto = new BasicDto();
    basicDto.setId(objectType.getId().toString())
      .setMessage("success");
    return basicDto;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public BasicDto editObjectType(String objectTypeId, ObjectTypeUpdateRequest objectTypeUpdateRequest) throws ResourceNotFoundException, StreemException {
    log.info("[editObjectType] Request to edit object type, objectTypeId: {}, objectTypeUpdateRequest: {}", objectTypeId, objectTypeUpdateRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    ObjectType objectType = objectTypeRepository.findById(objectTypeId)
      .orElseThrow(() -> new ResourceNotFoundException(objectTypeId, ErrorCode.OBJECT_TYPE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    String displayName = objectTypeUpdateRequest.getDisplayName();
    displayName = displayName.trim();
    //TODO: discuss-> here might be a case display name and external id are different
    validateEditObjectType(objectType, displayName, objectTypeUpdateRequest, objectTypeId);
    objectType.setDisplayName(displayName);
    objectType.setDescription(objectTypeUpdateRequest.getDescription());
    objectType.setModifiedAt(DateTimeUtils.now());
    objectType.setModifiedBy(userInfoMapper.toUserInfo(principalUser));
    objectTypeRepository.save(objectType);

    BasicDto basicDto = new BasicDto();
    basicDto.setId(objectType.getId().toString())
      .setMessage("success");
    return basicDto;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public BasicDto editObjectTypeProperty(String objectTypeId, String propertyId, ObjectTypePropertyCreateRequest objectTypePropertyCreateRequest) throws ResourceNotFoundException, StreemException {
    log.info("[editObjectTypeProperty] Request to edit Object Type properties, objectTypeId: {}, propertyId: {}, objectTypePropertyRequests: {}", objectTypeId, propertyId, objectTypePropertyCreateRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    ObjectType objectType = objectTypeRepository.findById(objectTypeId)
      .orElseThrow(() -> new ResourceNotFoundException(objectTypeId, ErrorCode.OBJECT_TYPE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    List<Property> existingProperties = objectType.getProperties();
    Map<String, Property> existingPropertiesMap = existingProperties.stream().collect(Collectors.toMap(p -> p.getId().toString(), Function.identity()));
    Property existingProperty = existingPropertiesMap.get(propertyId);
    if (!Utility.isEmpty(existingProperty)) {
      String displayName = objectTypePropertyCreateRequest.getDisplayName();
      displayName = displayName.trim();
      validateEditProperty(existingProperties, displayName, propertyId, existingProperty, objectTypePropertyCreateRequest);
      createProperty(existingProperty, objectTypePropertyCreateRequest, displayName, false);

//      if (!Utility.isEmpty(objectTypePropertyCreateRequest.getValidations())) {
//        List<PropertyValidation> existingPropertyValidations = existingProperty.getValidations();
//        if (!Utility.isEmpty(existingPropertyValidations)) {
//          existingPropertyValidations.addAll(objectTypePropertyCreateRequest.getValidations());
//        } else {
//          existingProperty.setValidations(objectTypePropertyCreateRequest.getValidations());
//        }
//      }
      List<ObjectTypePropertyOptionCreateRequest> propertyOptions = objectTypePropertyCreateRequest.getOptions();
      if (!Utility.isEmpty(propertyOptions)) {
        List<PropertyOption> filteredOptions = new ArrayList<>();
        for (ObjectTypePropertyOptionCreateRequest option : propertyOptions) {
          if (Utility.isEmpty(option.getId())) {
            option.setId(new ObjectId());
          }
          PropertyOption newPropertyOption = new PropertyOption();
          newPropertyOption.setId(option.getId());
          String optionDisplayName = option.getDisplayName();
          optionDisplayName = optionDisplayName.trim();
          validateOptionDisplayName(filteredOptions, optionDisplayName);
          newPropertyOption.setDisplayName(optionDisplayName);
          filteredOptions.add(newPropertyOption);
        }
        existingProperty.setOptions(filteredOptions);
      } else {
        existingProperty.setOptions(new ArrayList<>());
      }
    }
    objectType.setProperties(existingProperties);
    objectType.setModifiedAt(DateTimeUtils.now());
    objectType.setModifiedBy(userInfoMapper.toUserInfo(principalUser));
    objectTypeRepository.save(objectType);
    updateEntityObjectProperty(objectType.getExternalId(), objectTypeId, existingProperty);

    BasicDto basicDto = new BasicDto();
    basicDto.setId(objectType.getId().toString())
      .setMessage("success");
    return basicDto;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public BasicDto editObjectTypeRelation(String objectTypeId, String relationId, ObjectTypeRelationCreateRequest objectTypeRelationCreateRequest) throws ResourceNotFoundException, StreemException {
    log.info("[editObjectTypeRelation] Request to edit Object Type relations, objectTypeId: {}, relationId: {}, objectTypeRelationCreateRequest: {}", objectTypeId, relationId, objectTypeRelationCreateRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    ObjectType objectType = objectTypeRepository.findById(objectTypeId)
      .orElseThrow(() -> new ResourceNotFoundException(objectTypeId, ErrorCode.OBJECT_TYPE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    List<Relation> existingRelations = objectType.getRelations();
    Map<String, Relation> existingRelationsMap = existingRelations.stream().collect(Collectors.toMap(p -> p.getId().toString(), Function.identity()));
    Relation existingRelation = existingRelationsMap.get(relationId);
    if (!Utility.isEmpty(existingRelation)) {
      String displayName = objectTypeRelationCreateRequest.getDisplayName();
      displayName = displayName.trim();
      validateEditRelation(existingRelations, displayName, relationId, existingRelation, objectTypeRelationCreateRequest);
      ObjectType relationObjectType = objectTypeRepository.findById(objectTypeRelationCreateRequest.getObjectTypeId())
        .orElseThrow(() -> new ResourceNotFoundException(objectTypeId, ErrorCode.OBJECT_TYPE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

      createRelation(relationObjectType, existingRelation, objectTypeRelationCreateRequest, displayName);
    }
    objectType.setRelations(existingRelations);
    objectType.setModifiedAt(DateTimeUtils.now());
    objectType.setModifiedBy(userInfoMapper.toUserInfo(principalUser));
    objectTypeRepository.save(objectType);
    updateEntityObjectRelation(objectType.getExternalId(), objectTypeId, relationId, existingRelation);

    BasicDto basicDto = new BasicDto();
    basicDto.setId(objectType.getId().toString())
      .setMessage("success");
    return basicDto;

  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public BasicDto archiveObjectTypeProperty(String objectTypeId, String propertyId) throws ResourceNotFoundException, StreemException {
    log.info("[archiveObjectTypeProperty] Request to archive Object Type properties, objectTypeId: {}, propertyId: {}", objectTypeId, propertyId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    // TODO : check if property exists in the object type or find by object type id and property id
    ObjectType objectType = objectTypeRepository.findById(objectTypeId)
      .orElseThrow(() -> new ResourceNotFoundException(objectTypeId, ErrorCode.OBJECT_TYPE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    List<Property> existingProperties = objectType.getProperties();
    if (!Utility.isEmpty(existingProperties)) {
      for (Property existingProperty : existingProperties) {
        if (existingProperty.getId().toString().equals(propertyId)) {
          if (existingProperty.getExternalId().equals(CollectionKey.EXTERNAL_ID) || existingProperty.getExternalId().equals(DISPLAY_NAME)) {
            ValidationUtils.invalidate(propertyId, ErrorCode.OBJECT_TYPE_PROPERTY_CANNOT_BE_ARCHIVED);
          } else {
            existingProperty.setUsageStatus(CollectionMisc.UsageStatus.DEPRECATED.get());
          }
        }
      }
    }
    objectType.setProperties(existingProperties);
    objectType.setModifiedAt(DateTimeUtils.now());
    objectType.setModifiedBy(userInfoMapper.toUserInfo(principalUser));
    objectTypeRepository.save(objectType);

    BasicDto basicDto = new BasicDto();
    basicDto.setId(objectType.getId().toString())
      .setMessage("success");
    return basicDto;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public BasicDto archiveObjectTypeRelation(String objectTypeId, String relationId) throws ResourceNotFoundException {
    log.info("[archiveObjectTypeRelations] Request to archive Object Type relations, objectTypeId: {}, relationId: {}", objectTypeId, relationId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    // TODO : check if relation exists in the object type or find by object type id and relation id
    ObjectType objectType = objectTypeRepository.findById(objectTypeId)
      .orElseThrow(() -> new ResourceNotFoundException(objectTypeId, ErrorCode.OBJECT_TYPE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    List<Relation> existingRelations = objectType.getRelations();
    if (!Utility.isEmpty(existingRelations)) {
      for (Relation relation : existingRelations) {
        if (relation.getId().toString().equals(relationId)) {
          relation.setUsageStatus(CollectionMisc.UsageStatus.DEPRECATED.get());
        }
      }
    }
    objectType.setRelations(existingRelations);
    objectType.setModifiedAt(DateTimeUtils.now());
    objectType.setModifiedBy(userInfoMapper.toUserInfo(principalUser));
    objectTypeRepository.save(objectType);

    BasicDto basicDto = new BasicDto();
    basicDto.setId(objectType.getId().toString())
      .setMessage("success");
    return basicDto;

  }

  @Override
  public Page<Property> getAllObjectTypeProperties(String objectTypeId, int usageStatus, String displayName, Pageable pageable) throws StreemException {
    log.info("[getAllObjectTypeProperties] Request to get all Object Type properties by objectTypeId: {}, usageStatus: {}, displayName:{}", objectTypeId, usageStatus, displayName);
    validateFiltersDisplayName(displayName, true, false, false);
    return objectTypeRepository.getAllObjectTypeProperties(objectTypeId, usageStatus, displayName, pageable);
  }

  @Override
  public Page<Relation> getAllObjectTypeRelations(String objectTypeId, int usageStatus, String displayName, Pageable pageable) throws StreemException {
    log.info("[getAllObjectTypeRelations] Request to get all Object Type relations by objectTypeId: {}, usageStatus: {}, displayName:{}", objectTypeId, usageStatus, displayName);
    validateFiltersDisplayName(displayName, false, true, false);
    return objectTypeRepository.getAllObjectTypeRelations(objectTypeId, usageStatus, displayName, pageable);
  }

  private ObjectType createNewObjectType(ObjectTypeCreateRequest objectTypeCreateRequest, User principalUserEntity) throws StreemException {
    ObjectType objectType = new ObjectType();

    objectType.setId(new ObjectId());
    objectType.setExternalId(CaseUtils.toCamelCase(objectTypeCreateRequest.getPluralName(), false));
    // TODO create a version object
    objectType.setVersion(1);
    // TODO why are we taking this from UI ?
    objectType.setCollection(objectTypeCreateRequest.getCollection());
    objectType.setDescription(objectTypeCreateRequest.getDescription());
    String displayName = objectTypeCreateRequest.getDisplayName();
    displayName = displayName.trim();
    validateObjectTypeDisplayName(displayName);
    objectType.setDisplayName(displayName);
    objectType.setPluralName(objectTypeCreateRequest.getPluralName());
    objectType.setUsageStatus(CollectionMisc.UsageStatus.ACTIVE.get());
    objectType.setCreatedAt(DateTimeUtils.now());
    objectType.setCreatedBy(userInfoMapper.toDto(principalUserEntity));
    List<ObjectTypePropertyCreateRequest> properties = objectTypeCreateRequest.getProperties();
    if (!Utility.isEmpty(properties)) {
      List<Property> propertyList = new ArrayList<>();
      for (ObjectTypePropertyCreateRequest propertyCreateRequest : properties) {
        String propertyDisplayName = propertyCreateRequest.getDisplayName();
        propertyDisplayName = propertyDisplayName.trim();
        validatePropertyDisplayName(propertyList, propertyDisplayName);
        Property property = objectTypePropertyMapper.toEntity(propertyCreateRequest);
        property.setId(new ObjectId());
        property.setUsageStatus(CollectionMisc.UsageStatus.ACTIVE.get());
        propertyList.add(property);
      }

      createSystemProperties(propertyList);
      objectType.setProperties(propertyList);
    }
    return objectType;
  }

  // TODO optimize make readable, very hard to read and resolve bugs
  private Property createProperty(Property property, ObjectTypePropertyCreateRequest objectTypePropertyCreateRequest, String displayName, boolean isCreated) throws StreemException {
    // TODO WHY THIS CHECK ? Seems of no point
    if (!Utility.isEmpty(property) && !Utility.isEmpty(objectTypePropertyCreateRequest.getExternalId())
      && !property.getExternalId().equals(objectTypePropertyCreateRequest.getExternalId())) {
      ValidationUtils.invalidate(String.valueOf(objectTypePropertyCreateRequest.getExternalId()), ErrorCode.EXTERNAL_ID_CANNOT_BE_CHANGED);
    }

    // TODO Importantly remove this condition
    if (isCreated || !property.getExternalId().equals(DISPLAY_NAME)) {
      property.setExternalId(CaseUtils.toCamelCase(displayName, false));
    }


    property.setPlaceHolder(objectTypePropertyCreateRequest.getPlaceHolder());
    property.setSortOrder(objectTypePropertyCreateRequest.getSortOrder());
    property.setUsageStatus(CollectionMisc.UsageStatus.ACTIVE.get());

    property.setInputType(objectTypePropertyCreateRequest.getInputType());
    property.setDisplayName(displayName);
    property.setDescription(objectTypePropertyCreateRequest.getDescription());
    property.setFlags(objectTypePropertyCreateRequest.getFlags());

    return property;
  }

  private Relation createRelation(ObjectType objectType, Relation relation, ObjectTypeRelationCreateRequest objectTypeRelationCreateRequest, String displayName) {
    relation.setExternalId(CaseUtils.toCamelCase(objectType.getPluralName(), false));
    ObjectTypeRelationTargetCreateRequest relationTargetCreateRequest = objectTypeRelationCreateRequest.getTarget();
    RelationTarget relationTarget = new RelationTarget();
    relationTarget.setUrlPath(Misc.RELATION_TARGET_PATH.concat(objectType.getExternalId()));
    relationTarget.setType(relationTargetCreateRequest.getType());
    relationTarget.setCardinality(relationTargetCreateRequest.getCardinality());
    relation.setTarget(relationTarget);
    relation.setDisplayName(displayName);
    relation.setObjectTypeId(objectTypeRelationCreateRequest.getObjectTypeId());
    relation.setDescription(objectTypeRelationCreateRequest.getDescription());
    relation.setSortOrder(objectTypeRelationCreateRequest.getSortOrder());
    relation.setVariables(objectTypeRelationCreateRequest.getVariables());
    relation.setFlags(objectTypeRelationCreateRequest.getFlags());

    return relation;
  }

  private void updateEntityObjectProperty(String externalId, String objectTypeId, Property existingProperty) {
    String propertyId = String.valueOf(existingProperty.getId());
    List<EntityObject> entityObjects = entityObjectRepository.findByObjectTypeId(externalId, objectTypeId);

    for (EntityObject entityObject : entityObjects) {
      List<PropertyValue> existingPropertyValues = entityObject.getProperties();
      Map<String, PropertyValue> existingPropertyValuesMap = existingPropertyValues.stream().collect(Collectors.toMap(p -> p.getId().toString(), Function.identity()));
      if (!Utility.isEmpty(existingPropertyValues)) {
        PropertyValue propertyValue = existingPropertyValuesMap.get(propertyId);
        if (!Utility.isEmpty(propertyValue)) {
          //TODO: add validations for system properties and non editable properties
          propertyValue.setDisplayName(existingProperty.getDisplayName());
          propertyValue.setExternalId(existingProperty.getExternalId());
          propertyValue.setChoices(existingProperty.getOptions());
        }
      }
      entityObject.setProperties(existingPropertyValues);
      entityObjectRepository.save(entityObject, externalId);
    }
  }

  private void updateEntityObjectRelation(String externalId, String objectTypeId, String relationId, Relation existingRelation) throws StreemException {
    List<EntityObject> entityObjects = entityObjectRepository.findByObjectTypeId(externalId, objectTypeId);
    if (!Utility.isEmpty(entityObjects)) {
      for (EntityObject entityObject : entityObjects) {
        List<MappedRelation> existingMappedRelations = entityObject.getRelations();
        Map<String, MappedRelation> existinEntityObjectRelationMap = existingMappedRelations.stream().collect(Collectors.toMap(r -> r.getId().toString(), Function.identity()));
        MappedRelation existingEntityObjectRelation = existinEntityObjectRelationMap.get(relationId);
        if (!Utility.isEmpty(existingEntityObjectRelation)) {
          existingEntityObjectRelation.setFlags(existingRelation.getFlags());
          existingEntityObjectRelation.setDisplayName(existingRelation.getDisplayName());
          existingEntityObjectRelation.setExternalId(existingRelation.getExternalId());
          existingEntityObjectRelation.setObjectTypeId(existingRelation.getObjectTypeId());
          List<MappedRelationTarget> mappedRelationTargets = existingEntityObjectRelation.getTargets();
          for (MappedRelationTarget mappedRelationTarget : mappedRelationTargets) {
            mappedRelationTarget.setType(existingRelation.getTarget().getType());
            mappedRelationTarget.setCollection(existingRelation.getExternalId());
          }
          existingEntityObjectRelation.setTargets(mappedRelationTargets);
          entityObject.setRelations(existingMappedRelations);
          entityObjectRepository.save(entityObject, externalId);
        }
      }
    }
  }

  private void createSystemProperties(List<Property> properties) {

    Property createdAtProperty = new Property();
    createdAtProperty.setId(new ObjectId());
    createdAtProperty.setExternalId(Misc.CREATED_AT_EXTERNAL_ID);
    createdAtProperty.setDisplayName(Misc.CREATED_AT);
    createdAtProperty.setPlaceHolder(Misc.CREATED_AT_TIME);
    createdAtProperty.setUsageStatus(CollectionMisc.UsageStatus.ACTIVE.get());
    createdAtProperty.setFlags(CollectionMisc.Flag.IS_PRIMARY.get());
    createdAtProperty.setSortOrder(99990);
    createdAtProperty.setInputType(CollectionMisc.PropertyType.DATE_TIME);
    properties.add(createdAtProperty);

    Property updatedAtProperty = new Property();
    updatedAtProperty.setId(new ObjectId());
    updatedAtProperty.setExternalId(Misc.UPDATED_AT_EXTERNAL_ID);
    updatedAtProperty.setDisplayName(Misc.UPDATED_AT);
    updatedAtProperty.setPlaceHolder(Misc.UPDATED_AT_TIME);
    updatedAtProperty.setFlags(CollectionMisc.Flag.IS_PRIMARY.get());
    updatedAtProperty.setUsageStatus(CollectionMisc.UsageStatus.ACTIVE.get());
    updatedAtProperty.setSortOrder(99991);
    updatedAtProperty.setInputType(CollectionMisc.PropertyType.DATE_TIME);
    properties.add(updatedAtProperty);

    Property createdByProperty = new Property();
    createdByProperty.setId(new ObjectId());
    createdByProperty.setExternalId(Misc.CREATED_BY_EXTERNAL_ID);
    createdByProperty.setDisplayName(Misc.CREATED_BY);
    createdByProperty.setPlaceHolder(Misc.CREATED_BY_USER);
    createdByProperty.setFlags(CollectionMisc.Flag.IS_PRIMARY.get());
    createdByProperty.setUsageStatus(CollectionMisc.UsageStatus.ACTIVE.get());
    createdByProperty.setSortOrder(99992);
    createdByProperty.setInputType(CollectionMisc.PropertyType.SINGLE_LINE);
    properties.add(createdByProperty);

    Property updatedByProperty = new Property();
    updatedByProperty.setId(new ObjectId());
    updatedByProperty.setExternalId(Misc.UPDATED_BY_EXTERNAL_ID);
    updatedByProperty.setDisplayName(Misc.UPDATED_BY);
    updatedByProperty.setPlaceHolder(Misc.UPDATED_BY_USER);
    updatedByProperty.setFlags(CollectionMisc.Flag.IS_PRIMARY.get());
    updatedByProperty.setUsageStatus(CollectionMisc.UsageStatus.ACTIVE.get());
    updatedByProperty.setSortOrder(99993);
    updatedByProperty.setInputType(CollectionMisc.PropertyType.SINGLE_LINE);
    properties.add(updatedByProperty);

    Property usagestatusProperty = new Property();
    usagestatusProperty.setId(new ObjectId());
    usagestatusProperty.setUsageStatus(CollectionMisc.UsageStatus.ACTIVE.get());
    usagestatusProperty.setExternalId(Misc.USAGE_STATUS_EXTERNAL_ID);
    usagestatusProperty.setDisplayName(Misc.USAGE_STATUS);
    usagestatusProperty.setFlags(CollectionMisc.Flag.IS_PRIMARY.get());
    usagestatusProperty.setPlaceHolder(Misc.USAGE_STATUS_PLACE_HOLDER);
    usagestatusProperty.setSortOrder(99994);
    usagestatusProperty.setInputType(CollectionMisc.PropertyType.NUMBER);
    properties.add(usagestatusProperty);
  }

  // TODO: handle it better
  private void validateFiltersDisplayName(String name, boolean isProperty, boolean isRelation, boolean isObjectType) throws StreemException {
    if (isObjectType && !Utility.isEmpty(name) && validateDisplayNameSpecialChar(name)) {
      ValidationUtils.invalidate(name, ErrorCode.OBJECT_TYPE_DISPLAY_NAME_INVALID);
    } else if (isProperty && !Utility.isEmpty(name) && validateDisplayNameSpecialChar(name)) {
      ValidationUtils.invalidate(name, ErrorCode.OBJECT_TYPE_PROPERTY_DISPLAY_NAME_INVALID);
    } else if (isRelation && !Utility.isEmpty(name) && validateDisplayNameSpecialChar(name)) {
      ValidationUtils.invalidate(name, ErrorCode.OBJECT_TYPE_RELATION_DISPLAY_NAME_INVALID);
    }
  }

  private void validatePropertyDisplayName(List<Property> existingProperty, String displayName) throws StreemException {
    if (Utility.isEmpty(displayName)) {
      ValidationUtils.invalidate(displayName, ErrorCode.OBJECT_TYPE_PROPERTY_DISPLAY_NAME_INVALID);
    }
    if (validateDisplayNameSpecialChar(displayName)) {
      ValidationUtils.invalidate(displayName, ErrorCode.OBJECT_TYPE_PROPERTY_DISPLAY_NAME_INVALID);
    }
    if (!Utility.isEmpty(existingProperty)) {
      boolean propertyDisplayNameExists = existingProperty.stream()
        .filter(ex -> ex.getUsageStatus() == CollectionMisc.UsageStatus.ACTIVE.get())
        .anyMatch(ex -> ex.getDisplayName().equalsIgnoreCase(displayName));
      if (propertyDisplayNameExists) {
        ValidationUtils.invalidate(displayName, ErrorCode.OBJECT_TYPE_PROPERTY_DISPLAY_NAME_ALREADY_EXISTS);
      }
    }
  }

  private void validateRelationDisplayName(List<Relation> existingRelations, String displayName) throws StreemException {
    if (Utility.isEmpty(displayName)) {
      ValidationUtils.invalidate(displayName, ErrorCode.OBJECT_TYPE_RELATION_DISPLAY_NAME_INVALID);
    }
    if (validateDisplayNameSpecialChar(displayName)) {
      ValidationUtils.invalidate(displayName, ErrorCode.OBJECT_TYPE_RELATION_DISPLAY_NAME_INVALID);
    }
    if (!Utility.isEmpty(existingRelations)) {
      boolean relationDisplayNameExists = existingRelations.stream()
        .filter(ex -> ex.getUsageStatus() == CollectionMisc.UsageStatus.ACTIVE.get())
        .anyMatch(ex -> ex.getDisplayName().equalsIgnoreCase(displayName));

      if (relationDisplayNameExists) {
        ValidationUtils.invalidate(displayName, ErrorCode.OBJECT_TYPE_RELATION_DISPLAY_NAME_ALREADY_EXISTS);
      }
    }
  }

  private void validateObjectTypeDisplayName(String displayName) throws StreemException {
    if (Utility.isEmpty(displayName)) {
      ValidationUtils.invalidate(displayName, ErrorCode.OBJECT_TYPE_DISPLAY_NAME_INVALID);
    }
    if (validateDisplayNameSpecialChar(displayName)) {
      ValidationUtils.invalidate(displayName, ErrorCode.OBJECT_TYPE_DISPLAY_NAME_INVALID);
    }
    boolean objectTypeExists = objectTypeMongoRepository.existsByDisplayNameIgnoreCaseAndUsageStatus(displayName, CollectionMisc.UsageStatus.ACTIVE.get());
    if (objectTypeExists) {
      ValidationUtils.invalidate(displayName, ErrorCode.OBJECT_TYPE_DISPLAY_NAME_ALREADY_EXISTS);
    }
  }

  private void validateEditObjectType(ObjectType objectType, String displayName, ObjectTypeUpdateRequest objectTypeUpdateRequest, String objectTypeId) throws StreemException {
    if (!Utility.isEmpty(objectTypeUpdateRequest.getExternalId()) && !objectType.getExternalId().equals(objectTypeUpdateRequest.getExternalId())) {
      ValidationUtils.invalidate(objectTypeId, ErrorCode.EXTERNAL_ID_CANNOT_BE_CHANGED);
    }
    if (Utility.isEmpty(displayName)) {
      ValidationUtils.invalidate(displayName, ErrorCode.OBJECT_TYPE_DISPLAY_NAME_INVALID);
    }
    if (validateDisplayNameSpecialChar(displayName)) {
      ValidationUtils.invalidate(displayName, ErrorCode.OBJECT_TYPE_DISPLAY_NAME_INVALID);
    }
    // We are doing this because if current object type display name is same as new display name then we don't need to check for duplicate display name
    boolean sameDisplayName = objectType.getDisplayName().equalsIgnoreCase(displayName);
    if (!sameDisplayName) {
      boolean objectTypeExists = objectTypeMongoRepository.existsByDisplayNameIgnoreCaseAndUsageStatus(displayName, CollectionMisc.UsageStatus.ACTIVE.get());
      if (objectTypeExists) {
        ValidationUtils.invalidate(displayName, ErrorCode.OBJECT_TYPE_DISPLAY_NAME_ALREADY_EXISTS);
      }
    }
  }

  private void validateEditProperty(List<Property> existingProperties, String displayName, String propertyId, Property existingProperty, ObjectTypePropertyCreateRequest objectTypePropertyCreateRequest) throws StreemException {
    if (Misc.CREATE_PROPERTIES.contains(existingProperty.getExternalId())) {
      ValidationUtils.invalidate(propertyId, ErrorCode.FIXED_PROPERTY_CANNOT_BE_CHANGED);
    }
    if (!objectTypePropertyCreateRequest.getInputType().equals(existingProperty.getInputType())) {
      ValidationUtils.invalidate(existingProperty.getId().toString(), ErrorCode.OBJECT_TYPE_PROPERTY_INPUT_TYPE_CANNOT_BE_CHANGED);
    }
    if (CollectionMisc.UsageStatus.DEPRECATED.get() == existingProperty.getUsageStatus()) {
      ValidationUtils.invalidate(existingProperty.getId().toString(), ErrorCode.OBJECT_TYPE_ARCHIVED_PROPERTY_CANNOT_BE_CHANGED);
    }
    if (Utility.isEmpty(displayName)) {
      ValidationUtils.invalidate(displayName, ErrorCode.OBJECT_TYPE_PROPERTY_DISPLAY_NAME_INVALID);
    }
    if (validateDisplayNameSpecialChar(displayName)) {
      ValidationUtils.invalidate(displayName, ErrorCode.OBJECT_TYPE_PROPERTY_DISPLAY_NAME_INVALID);
    }
    if (!Utility.isEmpty(existingProperties)) {
      boolean sameDisplayNameExists = existingProperties.stream()
        .filter(ex -> String.valueOf(ex.getId()).equals(propertyId))
        .anyMatch(ex -> ex.getDisplayName().equalsIgnoreCase(displayName));
      if (!sameDisplayNameExists) {
        boolean propertyDisplayNameExists = existingProperties.stream()
          .filter(ex -> ex.getUsageStatus() == CollectionMisc.UsageStatus.ACTIVE.get())
          .anyMatch(ex -> ex.getDisplayName().equalsIgnoreCase(displayName));
        if (propertyDisplayNameExists) {
          ValidationUtils.invalidate(displayName, ErrorCode.OBJECT_TYPE_PROPERTY_DISPLAY_NAME_ALREADY_EXISTS);
        }
      }
    }
  }

  private void validateEditRelation(List<Relation> existingRelations, String displayName, String relationId, Relation existingRelation, ObjectTypeRelationCreateRequest objectTypeRelationCreateRequest) throws StreemException {
    if (!existingRelation.getTarget().getType().equals(objectTypeRelationCreateRequest.getTarget().getType())) {
      ValidationUtils.invalidate(existingRelation.getId().toString(), ErrorCode.OBJECT_TYPE_RELATION_TYPE_CANNOT_BE_CHANGED);
    }
    var cardinality = objectTypeRelationCreateRequest.getTarget().getCardinality();
    if (!Utility.isEmpty(cardinality) && !existingRelation.getTarget().getCardinality().equals(cardinality)) {
      ValidationUtils.invalidate(String.valueOf(cardinality), ErrorCode.RELATION_TARGET_CARDINALITY_CANNOT_BE_CHANGED);
    }
    if (CollectionMisc.UsageStatus.DEPRECATED.get() == existingRelation.getUsageStatus()) {
      ValidationUtils.invalidate(existingRelation.getId().toString(), ErrorCode.OBJECT_TYPE_ARCHIVED_RELATION_CANNOT_BE_CHANGED);
    }
    if (Utility.isEmpty(displayName)) {
      ValidationUtils.invalidate(displayName, ErrorCode.OBJECT_TYPE_RELATION_DISPLAY_NAME_INVALID);
    }
    if (validateDisplayNameSpecialChar(displayName)) {
      ValidationUtils.invalidate(displayName, ErrorCode.OBJECT_TYPE_RELATION_DISPLAY_NAME_INVALID);
    }
    if (!Utility.isEmpty(existingRelations)) {
      boolean sameDisplayNameExists = existingRelations.stream()
        .filter(ex -> String.valueOf(ex.getId()).equals(relationId))
        .anyMatch(ex -> ex.getDisplayName().equalsIgnoreCase(displayName));
      if (!sameDisplayNameExists) {
        boolean relationDisplayNameExists = existingRelations.stream()
          .filter(ex -> ex.getUsageStatus() == CollectionMisc.UsageStatus.ACTIVE.get())
          .anyMatch(ex -> ex.getDisplayName().equalsIgnoreCase(displayName));

        if (relationDisplayNameExists) {
          ValidationUtils.invalidate(displayName, ErrorCode.OBJECT_TYPE_RELATION_DISPLAY_NAME_ALREADY_EXISTS);
        }
      }
    }
  }

  private void validateOptionDisplayName(List<PropertyOption> propertyOptionList, String optionDisplayName) throws StreemException {
    if (Utility.isEmpty(optionDisplayName)) {
      ValidationUtils.invalidate(optionDisplayName, ErrorCode.OBJECT_TYPE_PROPERTY_DROPDOWN_OPTION_DISPLAY_NAME_INVALID);
    }
    if (validateDisplayNameSpecialChar(optionDisplayName)) {
      ValidationUtils.invalidate(optionDisplayName, ErrorCode.OBJECT_TYPE_PROPERTY_DROPDOWN_OPTION_DISPLAY_NAME_INVALID);
    }
    if (!Utility.isEmpty(propertyOptionList)) {
      boolean propertyOptionNameExists = propertyOptionList.stream()
        .anyMatch(propertyOption -> propertyOption.getDisplayName().equalsIgnoreCase(optionDisplayName));
      if (propertyOptionNameExists) {
        ValidationUtils.invalidate(optionDisplayName, ErrorCode.OBJECT_TYPE_PROPERTY_DROPDOWN_OPTION_DISPLAY_NAME_EXISTS);
      }
    }
  }

  private boolean validateDisplayNameSpecialChar(String displayName) {
    return !VALID_STRING_PATTERN.matcher(displayName).matches();
  }

  //TODO: remove this function when revision comes in picture
  private void addPropertyValueInEntityObjects(ObjectType objectType, Property property) {
    Query query = new Query();
    query.addCriteria(Criteria.where("objectTypeId").is(objectType.getId())
      .and("properties.id").ne(property.getId()));

    Update update = new Update();
    PropertyValue propertyValue = new PropertyValue();
    propertyValue.setId(property.getId());
    propertyValue.setDisplayName(property.getDisplayName());
    propertyValue.setExternalId(property.getExternalId());
    propertyValue.setChoices(new ArrayList<>());
    update.addToSet("properties", propertyValue);

    mongoTemplate.updateFirst(query, update, objectType.getExternalId());
  }

  //TODO: to remove this when we have revisions in picture
  private void addRelationValueInEntityObjects(ObjectType objectType, Relation relation) {
    Query query = new Query();
    query.addCriteria(Criteria.where("objectTypeId").is(objectType.getId())
      .and("relations.id").ne(relation.getId()));

    Update update = new Update();
    MappedRelation relationValue = new MappedRelation();
    relationValue.setId(relation.getId());
    relationValue.setDisplayName(relation.getDisplayName());
    relationValue.setExternalId(relation.getExternalId());
    relationValue.setTargets(new ArrayList<>());
    relationValue.setObjectTypeId(relation.getObjectTypeId());

    update.addToSet("relations", relationValue);

    mongoTemplate.updateFirst(query, update, objectType.getExternalId());
  }
}
