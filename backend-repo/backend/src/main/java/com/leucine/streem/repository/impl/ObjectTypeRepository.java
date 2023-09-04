package com.leucine.streem.repository.impl;


import com.leucine.streem.collections.ObjectType;
import com.leucine.streem.collections.Property;
import com.leucine.streem.collections.Relation;
import com.leucine.streem.constant.CollectionKey;
import com.leucine.streem.constant.CollectionName;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.repository.IObjectTypeRepository;
import com.leucine.streem.util.Utility;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ObjectTypeRepository implements IObjectTypeRepository {

  private final MongoTemplate mongoTemplate;

  @Override
  public List<ObjectType> findAll() {
    Query query = new Query();
    return mongoTemplate.find(query, ObjectType.class, CollectionName.OBJECT_TYPES);
  }

  @Override
  public Page<ObjectType> findAll(int usageStatus, String name, Pageable pageable) throws StreemException {
    Query query = new Query();
    Criteria criteria = new Criteria();
    List<Criteria> criteriaList = new ArrayList<>();
    criteriaList.add(Criteria.where(CollectionKey.USAGE_STATUS).is(usageStatus));
    if (!Utility.isEmpty(name)) {
      criteriaList.add(Criteria.where(CollectionKey.DISPLAY_NAME).regex(name, "i"));
    }
    criteria.andOperator(criteriaList);
    query.addCriteria(criteria);

    long count = mongoTemplate.count(query, CollectionName.OBJECT_TYPES);
    query.with(pageable);
    List<ObjectType> objectTypes = mongoTemplate.find(query, ObjectType.class, CollectionName.OBJECT_TYPES);
    return PageableExecutionUtils.getPage(objectTypes, pageable, () -> count);
  }

  @Override
  public Page<Property> getAllObjectTypeProperties(String objectTypeId, int usageStatus, String name, Pageable pageable) throws StreemException {
    MatchOperation matchStage = Aggregation.match(Criteria.where("_id").is(objectTypeId));

    int filterSize = 1;
    if (!Utility.isEmpty(name)) {
      filterSize++;
    }
    AggregationExpression[] filterConditions = new AggregationExpression[filterSize];
    filterSize = 0;
    filterConditions[filterSize++] = ComparisonOperators.Eq.valueOf(CollectionKey.OBJECT_TYPE_PROPERTY_USAGE_STATUS).equalToValue(usageStatus);
    if (!Utility.isEmpty(name)) {
      filterConditions[filterSize] = AggregationExpression.from(MongoExpression.create("""
        $regexMatch: {input: "$$property.displayName", regex: ?0, options: "i"}
        """, name));
    }

    AggregationExpression filterExpression = BooleanOperators.And.and(filterConditions);
    ProjectionOperation projectStage = Aggregation.project()
      .and(ArrayOperators.Filter.filter(CollectionKey.PROPERTIES)
        .as(CollectionKey.PROPERTY)
        .by(filterExpression)
      ).as(CollectionKey.PROPERTIES);

    //this will give total count of properties
    List<AggregationOperation> aggregationOperations = new ArrayList<>();
    aggregationOperations.add(matchStage);
    aggregationOperations.add(projectStage);
    aggregationOperations.add(Aggregation.project().andExclude("_id").and(CollectionKey.PROPERTIES));
    aggregationOperations.add(Aggregation.unwind(CollectionKey.PROPERTIES));
    aggregationOperations.add(Aggregation.replaceRoot(CollectionKey.PROPERTIES));
    aggregationOperations.add(Aggregation.sort(Sort.Direction.ASC, CollectionKey.SORT_ORDER));
    aggregationOperations.add(Aggregation.count().as(CollectionKey.TOTAL_SIZE));

    Aggregation countAggregation = Aggregation.newAggregation(aggregationOperations);
    AggregationResults<Map> aggregationResults = mongoTemplate.aggregate(countAggregation, CollectionName.OBJECT_TYPES, Map.class);
    Map customProps = aggregationResults.getUniqueMappedResult();
    if (Utility.isEmpty(customProps)) {
      return Page.empty();
    }
    //here we are removing count aggregation operations and adding skip and limit operations to get actual data for page
    aggregationOperations.remove(aggregationOperations.size() - 1);
    aggregationOperations.add(Aggregation.skip((long) pageable.getPageSize() * pageable.getPageNumber()));
    aggregationOperations.add(Aggregation.limit(pageable.getPageSize()));
    aggregationOperations.add(Aggregation.sort(Sort.Direction.ASC, CollectionKey.SORT_ORDER));
    Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
    AggregationResults<Property> aggregationResults1 = mongoTemplate.aggregate(aggregation, CollectionName.OBJECT_TYPES, Property.class);
    List<Property> properties = aggregationResults1.getMappedResults();
    if (Utility.isEmpty(properties)) {
      return Page.empty();
    }

    return PageableExecutionUtils.getPage(properties, pageable, () -> (int) customProps.get(CollectionKey.TOTAL_SIZE));
  }

  @Override
  public Page<Relation> getAllObjectTypeRelations(String objectTypeId, int usageStatus, String name, Pageable pageable) throws StreemException {
    MatchOperation matchStage = Aggregation.match(Criteria.where("_id").is(objectTypeId));

    int filterSize = 1;
    if (!Utility.isEmpty(name)) {
      filterSize++;
    }
    AggregationExpression[] filterConditions = new AggregationExpression[filterSize];
    filterSize = 0;
    filterConditions[filterSize++] = ComparisonOperators.Eq.valueOf(CollectionKey.OBJECT_TYPE_RELATION_USAGE_STATUS).equalToValue(usageStatus);
    if (!Utility.isEmpty(name)) {
      filterConditions[filterSize] = AggregationExpression.from(MongoExpression.create("""
        $regexMatch: {input: "$$relation.displayName", regex: ?0, options: "i"}
        """, name));
    }

    AggregationExpression filterExpression = BooleanOperators.And.and(filterConditions);
    ProjectionOperation projectStage = Aggregation.project()
      .and(ArrayOperators.Filter.filter(CollectionKey.RELATIONS)
        .as(CollectionKey.RELATION)
        .by(filterExpression)
      ).as(CollectionKey.RELATIONS);

    List<AggregationOperation> aggregationOperations = new ArrayList<>();
    aggregationOperations.add(matchStage);
    aggregationOperations.add(projectStage);
    aggregationOperations.add(Aggregation.project().andExclude("_id").and(CollectionKey.RELATIONS));
    aggregationOperations.add(Aggregation.unwind(CollectionKey.RELATIONS));
    aggregationOperations.add(Aggregation.replaceRoot(CollectionKey.RELATIONS));
    aggregationOperations.add(Aggregation.sort(Sort.Direction.ASC, CollectionKey.SORT_ORDER));
    aggregationOperations.add(Aggregation.count().as(CollectionKey.TOTAL_SIZE));

    Aggregation countAggregation = Aggregation.newAggregation(aggregationOperations);
    AggregationResults<Map> aggregationResults = mongoTemplate.aggregate(countAggregation, CollectionName.OBJECT_TYPES, Map.class);
    Map customProps = aggregationResults.getUniqueMappedResult();
    if (Utility.isEmpty(customProps)) {
      return Page.empty();
    }
    //here we are removing count aggregation operations and adding skip and limit operations to get actual data for page
    aggregationOperations.remove(aggregationOperations.size() - 1);
    aggregationOperations.add(Aggregation.skip((long) pageable.getPageSize() * pageable.getPageNumber()));
    aggregationOperations.add(Aggregation.limit(pageable.getPageSize()));
    aggregationOperations.add(Aggregation.sort(Sort.Direction.ASC, CollectionKey.SORT_ORDER));
    Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
    AggregationResults<Relation> aggregationResults1 = mongoTemplate.aggregate(aggregation, CollectionName.OBJECT_TYPES, Relation.class);
    List<Relation> relations = aggregationResults1.getMappedResults();
    if (Utility.isEmpty(relations)) {
      return Page.empty();

    }

    return PageableExecutionUtils.getPage(relations, pageable, () -> (int) customProps.get(CollectionKey.TOTAL_SIZE));

  }

  public Optional<Property> findPropertyByIdAndObjectTypeExternalId(String objectTypeExternalId, ObjectId id) {
    MatchOperation matchStage = Aggregation.match(Criteria.where("externalId").is(objectTypeExternalId));
    ProjectionOperation projectStage = Aggregation.project()
      .and(ArrayOperators.Filter.filter("properties")
        .as("property")
        .by(ComparisonOperators.Eq.valueOf("$$property._id").equalToValue(id))
      ).as("properties");

    UnwindOperation unwindStage = Aggregation.unwind("properties");
    ReplaceRootOperation replaceRootStage = Aggregation.replaceRoot("properties");
    Aggregation aggregation = Aggregation.newAggregation(matchStage, projectStage, unwindStage, replaceRootStage);
    AggregationResults<Property> aggregationResults = mongoTemplate.aggregate(
      aggregation, CollectionName.OBJECT_TYPES, Property.class);
    List<Property> properties = aggregationResults.getMappedResults();
    return properties.isEmpty() ? Optional.empty() : Optional.of(properties.get(0));
  }



  @Override
  public void save(ObjectType objectType) {
    Document fields = new Document();
    fields.put(CollectionKey.USAGE_STATUS, 1);
    fields.put(CollectionKey.EXTERNAL_ID, 1);
    mongoTemplate.indexOps("objectTypes")
      .ensureIndex(new CompoundIndexDefinition(fields).unique());
    mongoTemplate.save(objectType, "objectTypes");
  }

  @Override
  public Optional<ObjectType> findById(String id) {
    Query query = new Query();
    query.addCriteria(Criteria.where(CollectionKey.ID).is(id));
    return Optional.ofNullable(mongoTemplate.findOne(query, ObjectType.class, CollectionName.OBJECT_TYPES));
  }
}
