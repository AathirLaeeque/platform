package com.leucine.streem.controller.impl;

import com.leucine.streem.collections.ObjectType;
import com.leucine.streem.collections.Property;
import com.leucine.streem.collections.Relation;
import com.leucine.streem.controller.IObjectTypeController;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.request.ObjectTypeCreateRequest;
import com.leucine.streem.dto.request.ObjectTypePropertyCreateRequest;
import com.leucine.streem.dto.request.ObjectTypeRelationCreateRequest;
import com.leucine.streem.dto.request.ObjectTypeUpdateRequest;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.service.IObjectTypeService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ObjectTypeController implements IObjectTypeController {
  private final IObjectTypeService objectTypeService;

  @Override
  public Response<Page<ObjectType>> findAll(int usageStatus, String name, Pageable pageable) throws StreemException {
    return Response.builder().data(objectTypeService.findAll(usageStatus, name, pageable)).build();
  }

  @Override
  public Response<ObjectType> findById(String objectTypeId) throws ResourceNotFoundException {
    return Response.builder().data(objectTypeService.findById(objectTypeId)).build();
  }

  @Override
  public Response<BasicDto> createObjectType(ObjectTypeCreateRequest objectTypeCreateRequest) throws StreemException {
    return Response.builder().data(objectTypeService.createObjectType(objectTypeCreateRequest)).build();
  }

  @Override
  public Response<BasicDto> addObjectTypeProperty(String objectTypeId, ObjectTypePropertyCreateRequest objectTypePropertyCreateRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(objectTypeService.addObjectTypeProperty(objectTypeId, objectTypePropertyCreateRequest)).build();
  }

  @Override
  public Response<BasicDto> addObjectTypeRelation(String objectTypeId, ObjectTypeRelationCreateRequest objectTypeRelationCreateRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(objectTypeService.addObjectTypeRelation(objectTypeId, objectTypeRelationCreateRequest)).build();
  }

  @Override
  public Response<BasicDto> editObjectType(String objectTypeId, ObjectTypeUpdateRequest objectTypeUpdateRequest) throws StreemException, ResourceNotFoundException {
    return Response.builder().data(objectTypeService.editObjectType(objectTypeId, objectTypeUpdateRequest)).build();
  }

  @Override
  public Response<BasicDto> editObjectTypeProperty(String objectTypeId, String propertyId, ObjectTypePropertyCreateRequest objectTypePropertyCreateRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(objectTypeService.editObjectTypeProperty(objectTypeId, propertyId, objectTypePropertyCreateRequest)).build();
  }

  @Override
  public Response<BasicDto> editObjectTypeRelation(String objectTypeId, String relationId, ObjectTypeRelationCreateRequest objectTypeRelationCreateRequest) throws StreemException, ResourceNotFoundException {
    return Response.builder().data(objectTypeService.editObjectTypeRelation(objectTypeId, relationId, objectTypeRelationCreateRequest)).build();
  }

  @Override
  public Response<BasicDto> archiveObjectTypeProperty(String objectTypeId, String propertyId) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(objectTypeService.archiveObjectTypeProperty(objectTypeId, propertyId)).build();
  }

  @Override
  public Response<BasicDto> archiveObjectTypeRelation(String objectTypeId, String relationId) throws ResourceNotFoundException {
    return Response.builder().data(objectTypeService.archiveObjectTypeRelation(objectTypeId, relationId)).build();
  }

  @Override
  public Response<Page<Property>> getAllObjectTypeProperties(String objectTypeId, int usageStatus, String displayName, Pageable pageable) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(objectTypeService.getAllObjectTypeProperties(objectTypeId, usageStatus, displayName, pageable)).build();
  }

  @Override
  public Response<Page<Relation>> getAllObjectTypeRelations(String objectTypeId, int usageStatus, String displayName, Pageable pageable) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(objectTypeService.getAllObjectTypeRelations(objectTypeId, usageStatus, displayName, pageable)).build();
  }
}
