package com.leucine.streem.service;

import com.leucine.streem.collections.EntityObject;
import com.leucine.streem.collections.changelogs.EntityDataDto;
import com.leucine.streem.collections.changelogs.EntityObjectChangeLog;
import com.leucine.streem.collections.changelogs.EntityObjectUsageStatus;
import com.leucine.streem.dto.projection.JobProcessInfoView;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.model.helper.PrincipalUser;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IEntityObjectChangeLogService {
  void save(PrincipalUser principalUser, Map<ObjectId, List<EntityDataDto>> updatedPropertyValueAndRelationMap, Map<ObjectId, List<EntityDataDto>> oldPropertyRelationData, EntityObject entityObject, String reason, EntityObjectUsageStatus entityObjectUsageStatus, JobProcessInfoView jobProcessInfoView);

  Page<EntityObjectChangeLog> findAllChangeLogs(String filters, Pageable pageable);

  void save(PrincipalUser principalUser, EntityObject oldEntityObject, EntityObject updatedEntityObject, String reason, JobProcessInfoView jobProcessInfoView) throws ResourceNotFoundException;
}
