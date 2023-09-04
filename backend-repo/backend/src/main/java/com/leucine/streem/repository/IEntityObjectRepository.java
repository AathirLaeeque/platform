package com.leucine.streem.repository;

import com.leucine.streem.collections.EntityObject;
import com.leucine.streem.collections.partial.PartialEntityObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IEntityObjectRepository {
  Optional<EntityObject> findById(String collectionName, String id);
  List<EntityObject> findByObjectTypeId(String collectionName, String id);

  List<EntityObject> findAll(String collectionName);

  PartialEntityObject findPartialById(String collectionName, String id);

  List<EntityObject> findByIds(String collectionName, List<String> ids);

  List<PartialEntityObject> findPartialByIds(String collectionName, List<String> ids);

  List<PartialEntityObject> findPartialByIdsAndUsageStatus(String collectionName, List<String> ids, int usageStatus);

  Page<EntityObject> findAllByUsageStatus(String collectionName, int usageStatus, String propertyExternalId, String propertyValue, Long facilityId, Pageable pageable);

  Page<PartialEntityObject> findPartialByUsageStatus(String collectionName, int usageStatus, String propertyExternalId, String propertyValue, Long facilityId, String filters, Pageable pageable);

  EntityObject save(EntityObject entityObject, String id);

  List<EntityObject> saveAll(List<EntityObject> entityObjects, String collectionName);
}
