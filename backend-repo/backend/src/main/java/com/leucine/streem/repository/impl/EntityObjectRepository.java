package com.leucine.streem.repository.impl;

import com.leucine.streem.collections.EntityObject;
import com.leucine.streem.collections.helper.MongoFilter;
import com.leucine.streem.collections.partial.PartialEntityObject;
import com.leucine.streem.constant.CollectionKey;
import com.leucine.streem.model.helper.search.SearchCriteria;
import com.leucine.streem.model.helper.search.SearchOperator;
import com.leucine.streem.repository.IEntityObjectRepository;
import com.leucine.streem.util.Utility;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EntityObjectRepository implements IEntityObjectRepository {
  private final MongoTemplate mongoTemplate;

  @Override
  public List<EntityObject> findAll(String collectionName) {
    return mongoTemplate.findAll(EntityObject.class, collectionName);
  }

  @Override
  public Optional<EntityObject> findById(String collectionName, String id) {
    Query query = new Query();
    query.addCriteria(Criteria.where(CollectionKey.ID).is(id));
    return Optional.ofNullable(mongoTemplate.findOne(query, EntityObject.class, collectionName));
  }

  @Override
  public List<EntityObject> findByObjectTypeId(String collectionName, String id) {
    Query query = new Query();
    query.addCriteria(Criteria.where(CollectionKey.OBJECT_TYPE_ID).is(new ObjectId(id)));
    return mongoTemplate.find(query, EntityObject.class, collectionName);
  }

  @Override
  public PartialEntityObject findPartialById(String collectionName, String id) {
    Query query = new Query();
    query.fields().include(CollectionKey.ID, CollectionKey.COLLECTION, CollectionKey.EXTERNAL_ID, CollectionKey.DISPLAY_NAME);
    query.addCriteria(Criteria.where(CollectionKey.ID).is(id));
    return mongoTemplate.findOne(query, PartialEntityObject.class, collectionName);
  }

  @Override
  public List<EntityObject> findByIds(String collectionName, List<String> ids) {
    Query query = new Query();
    query.addCriteria(Criteria.where(CollectionKey.ID).in(ids));
    return mongoTemplate.find(query, EntityObject.class, collectionName);
  }

  @Override
  public List<PartialEntityObject> findPartialByIds(String collectionName, List<String> ids) {
    Query query = new Query();
    query.fields().include(CollectionKey.ID, CollectionKey.COLLECTION, CollectionKey.EXTERNAL_ID, CollectionKey.DISPLAY_NAME);
    query.addCriteria(Criteria.where(CollectionKey.ID).in(ids));
    return mongoTemplate.find(query, PartialEntityObject.class, collectionName);
  }

  @Override
  public List<PartialEntityObject> findPartialByIdsAndUsageStatus(String collectionName, List<String> ids, int usageStatus) {
    Query query = new Query();
    query.fields().include(CollectionKey.ID, CollectionKey.COLLECTION, CollectionKey.EXTERNAL_ID, CollectionKey.DISPLAY_NAME);
    query.addCriteria(Criteria.where(CollectionKey.ID).in(ids))
      .addCriteria(Criteria.where(CollectionKey.USAGE_STATUS).is(usageStatus));
    return mongoTemplate.find(query, PartialEntityObject.class, collectionName);
  }

  @Override
  public Page<EntityObject> findAllByUsageStatus(String collectionName, int usageStatus, String propertyExternalId, String propertyValue, Long facilityId, Pageable pageable) {
    Query query = new Query();
    Criteria criteria = new Criteria();
    List<Criteria> criteriaList = new ArrayList<>();
    criteriaList.add(Criteria.where(CollectionKey.USAGE_STATUS).is(usageStatus));
    if (!Utility.isEmpty(propertyExternalId) && !Utility.isEmpty(propertyValue)) {
      criteriaList.add(Criteria.where(CollectionKey.PROPERTY_EXTERNAL_ID).is(propertyExternalId));
      criteriaList.add(Criteria.where(CollectionKey.PROPERTY_VALUE).is(propertyValue));
    }
    if (facilityId != null) {
      criteriaList.add(Criteria.where(CollectionKey.FACILITY_ID).is(facilityId.toString()));
    }
    criteria.andOperator(criteriaList);
    query.addCriteria(criteria);
    long count = mongoTemplate.count(query, collectionName);
    query.with(pageable);
    var entityObjects = mongoTemplate.find(query, EntityObject.class, collectionName);
    return PageableExecutionUtils.getPage(entityObjects, pageable, () -> count);
  }

  @Override
  public Page<PartialEntityObject> findPartialByUsageStatus(String collectionName, int usageStatus, String propertyExternalId, String propertyValue, Long facilityId, String filters, Pageable pageable) {
    List<SearchCriteria> criteriaList = new ArrayList<>();
    SearchCriteria usageStatusCriteria = new SearchCriteria();
    usageStatusCriteria.setOp(SearchOperator.EQ.name());
    usageStatusCriteria.setField(CollectionKey.USAGE_STATUS);
    usageStatusCriteria.setValues(List.of(usageStatus));
    criteriaList.add(usageStatusCriteria);

    if (!Utility.isEmpty(propertyExternalId) && !Utility.isEmpty(propertyValue)) {
      SearchCriteria propertyExternalIdSearchCriteria = new SearchCriteria();
      propertyExternalIdSearchCriteria.setOp(SearchOperator.EQ.name());
      propertyExternalIdSearchCriteria.setField(CollectionKey.PROPERTY_EXTERNAL_ID);
      propertyExternalIdSearchCriteria.setValues(List.of(propertyExternalId));

      criteriaList.add(propertyExternalIdSearchCriteria);
    }
    if (facilityId != null) {
      SearchCriteria facilitySearchCriteria = new SearchCriteria();
      facilitySearchCriteria.setOp(SearchOperator.EQ.name());
      facilitySearchCriteria.setField(CollectionKey.FACILITY_ID);
      facilitySearchCriteria.setValues(List.of(String.valueOf(facilityId)));
      criteriaList.add(facilitySearchCriteria);
    }
    Query filteredQuery = MongoFilter.buildQuery(filters, criteriaList);
    filteredQuery.fields().include(CollectionKey.ID, CollectionKey.COLLECTION, CollectionKey.EXTERNAL_ID, CollectionKey.DISPLAY_NAME);
    long count = mongoTemplate.count(filteredQuery, collectionName);
    filteredQuery.with(pageable);
    var entityObjects = mongoTemplate.find(filteredQuery, PartialEntityObject.class, collectionName);
    return PageableExecutionUtils.getPage(entityObjects, pageable, () -> count);
  }

  @Override
  public List<EntityObject> saveAll(List<EntityObject> entityObjects, String collectionName) {
    List<EntityObject> savedObjects = new ArrayList<>();
    for (EntityObject entityObject : entityObjects) {
     EntityObject savedEntityObject = save(entityObject, collectionName);
     savedObjects.add(savedEntityObject);
    }
    return savedObjects;
  }

  @Override
  public EntityObject save(EntityObject entityObject, String collectionName) {
    Document fields = new Document();
    fields.put(CollectionKey.USAGE_STATUS, 1);
    fields.put(CollectionKey.FACILITY_ID, 1);
    fields.put(CollectionKey.EXTERNAL_ID, 1);
    mongoTemplate.indexOps(collectionName)
      .ensureIndex(new CompoundIndexDefinition(fields).unique());
    return mongoTemplate.save(entityObject, collectionName);
  }
}
