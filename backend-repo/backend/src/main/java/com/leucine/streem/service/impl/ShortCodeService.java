package com.leucine.streem.service.impl;

import com.leucine.commons.id.IdGenerator;
import com.leucine.streem.collections.EntityObject;
import com.leucine.streem.collections.ObjectType;
import com.leucine.streem.collections.changelogs.UserInfo;
import com.leucine.streem.collections.shortcode.ShortCode;
import com.leucine.streem.collections.shortcode.ShortCodeData;
import com.leucine.streem.constant.CollectionMisc;
import com.leucine.streem.constant.UsageStatus;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.ShortCodeDto;
import com.leucine.streem.dto.mapper.IUserInfoMapper;
import com.leucine.streem.dto.request.GenerateShortCodeRequest;
import com.leucine.streem.dto.request.ShortCodeModifyRequest;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.exception.ExceptionType;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.repository.IEntityObjectRepository;
import com.leucine.streem.repository.IObjectTypeRepository;
import com.leucine.streem.repository.IShortCodeDataRepository;
import com.leucine.streem.service.IShortCodeService;
import com.leucine.streem.util.DateTimeUtils;
import com.leucine.streem.util.EncodingUtils;
import com.leucine.streem.util.Utility;
import com.leucine.streem.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.leucine.streem.constant.Misc.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShortCodeService implements IShortCodeService {
  private final IShortCodeDataRepository shortCodeDataRepository;
  private final IEntityObjectRepository entityObjectRepository;
  private final IObjectTypeRepository objectTypeRepository;
  private final IUserInfoMapper userInfoMapper;


  @Override
  public void generateAndSaveShortCode(PrincipalUser principalUser, EntityObject entityObject) {
    log.info("[generateAndSaveShortCode] Generating short code for entity object: {}", entityObject);
    String shortCodeValue = EncodingUtils.getBase62(IdGenerator.getInstance().generateUnique());

    var shortCode = shortCodeDataRepository.findByData_ObjectId(entityObject.getId().toString());
    if (Utility.isEmpty(shortCode)) {
      shortCode = ShortCode.builder()
        .facilityId(principalUser.getCurrentFacilityId().toString())
        .createdAt(DateTimeUtils.now())
        .build();
    }
    shortCode.setData(buildShortCodeData(entityObject));
    shortCode.setShortCode(Utility.isEmpty(entityObject.getShortCode()) ? shortCodeValue : entityObject.getShortCode());
    shortCode.setModifiedAt(DateTimeUtils.now());
    UserInfo systemUser = new UserInfo(SYSTEM_USER_ID, SYSTEM_USER_EMPLOYEE_ID, SYSTEM_USER_FIRST_NAME, null);
    shortCode.setCreatedBy(systemUser);
    shortCode.setModifiedBy(systemUser);
    entityObject.setShortCode(shortCode.getShortCode());
    shortCodeDataRepository.save(shortCode);
  }

  @Override
  public ShortCodeData getShortCodeData(String shortCode) throws StreemException, ResourceNotFoundException {
    log.info("[getShortCodeData] Fetching short code data for short code: {}", shortCode);
    Optional<ShortCode> optionalShortCodeData = shortCodeDataRepository.findByShortCode(shortCode);
    optionalShortCodeData.orElseThrow(() -> new ResourceNotFoundException(shortCode, ErrorCode.ENTITY_OBJECT_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    validateEntityObjectArchivalStatus(optionalShortCodeData.get().getData().getObjectId(), optionalShortCodeData.get().getData().getObjectTypeId());
    return optionalShortCodeData.get().getData();
  }

  @Override
  public BasicDto editShortCode(ShortCodeModifyRequest shortCodeModifyRequest) throws ResourceNotFoundException, StreemException {
    log.info("[editShortCode] Editing short code for short code modify request: {}", shortCodeModifyRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    ShortCode shortCode = shortCodeDataRepository.findByData_ObjectId(shortCodeModifyRequest.objectId());
    if (shortCode.getShortCode().equals(shortCodeModifyRequest.data())) {
      ValidationUtils.invalidate(shortCodeModifyRequest.objectId(), ErrorCode.SHORT_CODE_ALREADY_MAPPED_TO_OBJECT);
    }

    if (!Utility.isEmpty(shortCodeModifyRequest.data())) {
      shortCode.setShortCode(shortCodeModifyRequest.data());
    } else {
      ValidationUtils.invalidate(shortCodeModifyRequest.objectId(), ErrorCode.EMPTY_SHORT_CODE_DATA);
    }
    shortCode.setModifiedAt(DateTimeUtils.now());
    shortCode.setModifiedBy(userInfoMapper.toUserInfo(principalUser));
    shortCodeDataRepository.save(shortCode);
    ObjectType objectType = findObjectType(shortCodeModifyRequest.objectTypeId());
    EntityObject entityObject = findEntityObject(objectType.getExternalId(), shortCodeModifyRequest.objectId());
    entityObject.setShortCode(shortCode.getShortCode());
    entityObjectRepository.save(entityObject, objectType.getExternalId());
    return new BasicDto(null, "success", null);
  }

  @Override
  public ShortCodeDto generateShortCode(GenerateShortCodeRequest generateShortCodeRequest) throws ResourceNotFoundException {
    log.info("[generateShortCode] Generating short code for generate short code request: {}", generateShortCodeRequest);
    ObjectType objectType = findObjectType(generateShortCodeRequest.objectTypeId());
    EntityObject entityObject = findEntityObject(objectType.getExternalId(), generateShortCodeRequest.objectId());

    var shortCode = entityObject.getShortCode();
    if (!Utility.isEmpty(shortCode)) {
      return new ShortCodeDto(shortCodeDataRepository.findByShortCode(shortCode)
        .map(ShortCode::getShortCode)
        .orElseThrow(() -> new ResourceNotFoundException(shortCode, ErrorCode.ENTITY_OBJECT_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND))
      );
    } else {
      ShortCodeData data = buildShortCodeData(entityObject);
      ShortCode shortCodeData = ShortCode.builder()
        .facilityId(entityObject.getFacilityId())
        .data(data)
        .shortCode(EncodingUtils.getBase62(IdGenerator.getInstance().generateUnique()))
        .createdAt(DateTimeUtils.now())
        .modifiedAt(DateTimeUtils.now())
        .build();
      entityObject.setShortCode(shortCodeData.getShortCode());
      entityObjectRepository.save(entityObject, objectType.getExternalId());
      shortCodeDataRepository.save(shortCodeData);
      return new ShortCodeDto(shortCodeData.getShortCode());
    }
  }

  private void validateEntityObjectArchivalStatus(String objectId, String objectTypeId) throws ResourceNotFoundException, StreemException {
    ObjectType objectType = findObjectType(objectTypeId);
    EntityObject entityObject = findEntityObject(objectType.getExternalId(), objectId);
    if (entityObject.getUsageStatus() == UsageStatus.DEPRECATED.getCode()) {
      ValidationUtils.invalidate(objectId, ErrorCode.OBJECT_ALREADY_ARCHIVED);
    }
  }

  private EntityObject findEntityObject(String externalId, String objectId) throws ResourceNotFoundException {
    return entityObjectRepository.findById(externalId, objectId)
      .orElseThrow(() -> new ResourceNotFoundException(objectId, ErrorCode.ENTITY_OBJECT_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
  }

  private ObjectType findObjectType(String objectTypeId) throws ResourceNotFoundException {
    return objectTypeRepository.findById(objectTypeId)
      .orElseThrow(() -> new ResourceNotFoundException(objectTypeId, ErrorCode.OBJECT_TYPE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
  }

  private static ShortCodeData buildShortCodeData(EntityObject newEntityObject) {
    return ShortCodeData.builder()
      .objectId(newEntityObject.getId().toString())
      .collection(newEntityObject.getCollection())
      .externalId(newEntityObject.getExternalId())
      .displayName(newEntityObject.getDisplayName())
      .entityType(CollectionMisc.RelationType.OBJECTS)
      .objectTypeId(newEntityObject.getObjectTypeId().toString())
      .build();
  }
}
