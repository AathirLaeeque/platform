package com.leucine.streem.controller.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leucine.streem.collections.EntityObject;
import com.leucine.streem.collections.changelogs.EntityObjectChangeLog;
import com.leucine.streem.collections.partial.PartialEntityObject;
import com.leucine.streem.controller.IEntityObjectController;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.request.ArchiveObjectRequest;
import com.leucine.streem.dto.request.EntityObjectValueRequest;
import com.leucine.streem.dto.request.UnarchiveObjectRequest;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.service.IEntityObjectChangeLogService;
import com.leucine.streem.service.IEntityObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EntityObjectController implements IEntityObjectController {
  private final IEntityObjectService entityObjectService;
  private final IEntityObjectChangeLogService entityObjectChangeLogService;

  @Autowired
  public EntityObjectController(IEntityObjectService entityObjectService, IEntityObjectChangeLogService entityObjectChangeLogService) {
    this.entityObjectService = entityObjectService;
    this.entityObjectChangeLogService = entityObjectChangeLogService;
  }

  @Override
  public Response<Page<EntityObject>> findAll(String collection, int usageStatus, String propertyExternalId, String propertyValue, Pageable pageable) {
    return Response.builder().data(entityObjectService.findAllByUsageStatus(collection, usageStatus, propertyExternalId, propertyValue, pageable)).build();
  }

  @Override
  public Response<Page<PartialEntityObject>> findAllPartial(String collection, int usageStatus, String propertyExternalId, String propertyValue, String filters, Pageable pageable) {
    return Response.builder().data(entityObjectService.findPartialByUsageStatus(collection, usageStatus, propertyExternalId, propertyValue, pageable, filters)).build();
  }

  @Override
  public Response<List<EntityObject>> saveObject(EntityObjectValueRequest entityObjectValueRequest) throws StreemException, ResourceNotFoundException, JsonProcessingException {
    return Response.builder().data(entityObjectService.save(entityObjectValueRequest, entityObjectValueRequest.getInfo())).build();
  }

  @Override
  public Response<List<EntityObject>> updateEntityObject(String objectId, EntityObjectValueRequest entityObjectValueRequest) throws StreemException, ResourceNotFoundException, JsonProcessingException {
    return Response.builder().data(entityObjectService.update(objectId, entityObjectValueRequest, entityObjectValueRequest.getInfo())).build();
  }

  @Override
  public Response<Page<EntityObjectChangeLog>> findAllChangeLogs(String filters, Pageable pageable) {
    return Response.builder().data(entityObjectChangeLogService.findAllChangeLogs(filters, pageable)).build();
  }

  @Override
  public Response<EntityObject> findObjectById(String objectId, String collection) throws ResourceNotFoundException {
    return Response.builder().data(entityObjectService.findById(collection, objectId)).build();
  }

  @Override
  public Response<BasicDto> updateSearchable() {
    return Response.builder().data(entityObjectService.enableSearchable()).build();
  }

  @Override
  public Response<BasicDto> unarchiveObject(UnarchiveObjectRequest unarchiveObjectRequest, String objectId) throws ResourceNotFoundException, StreemException, JsonProcessingException {
    return Response.builder().data(entityObjectService.unarchiveObject(unarchiveObjectRequest, objectId, null)).build();
  }

  @Override
  public Response<BasicDto> archiveObject(ArchiveObjectRequest archiveObjectRequest, String objectId) throws StreemException, ResourceNotFoundException, JsonProcessingException {
    return Response.builder().data(entityObjectService.archiveObject(archiveObjectRequest, objectId, null)).build();
  }
}
