package com.leucine.streem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leucine.streem.collections.EntityObject;
import com.leucine.streem.collections.partial.PartialEntityObject;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.projection.JobProcessInfoView;
import com.leucine.streem.dto.request.ArchiveObjectRequest;
import com.leucine.streem.dto.request.EntityObjectValueRequest;
import com.leucine.streem.dto.request.UnarchiveObjectRequest;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IEntityObjectService {
  EntityObject findById(String collectionName, String id) throws ResourceNotFoundException;

  EntityObject save(EntityObjectValueRequest entityObjectValueRequest, JobProcessInfoView jobProcessInfoView) throws ResourceNotFoundException, StreemException, JsonProcessingException;

  EntityObject update(String objectId, EntityObjectValueRequest entityObjectValueRequest, JobProcessInfoView jobProcessInfoView) throws ResourceNotFoundException, StreemException, JsonProcessingException;

  Page<EntityObject> findAllByUsageStatus(String collectionName, int usageStatus, String propertyExternalId, String propertyValue, Pageable pageable);

  Page<PartialEntityObject> findPartialByUsageStatus(String collectionName, int usageStatus, String propertyExternalId, String propertyValue, Pageable pageable, String filters);

  BasicDto archiveObject(ArchiveObjectRequest archiveObjectRequest, String objectId, JobProcessInfoView jobProcessInfoView) throws ResourceNotFoundException, StreemException, JsonProcessingException;

  BasicDto unarchiveObject(UnarchiveObjectRequest unarchiveObjectRequest, String objectId, JobProcessInfoView jobProcessInfoView) throws ResourceNotFoundException, StreemException, JsonProcessingException;

  BasicDto enableSearchable();
}
