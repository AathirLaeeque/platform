package com.leucine.streem.repository.impl;

import com.leucine.streem.collections.Code;
import com.leucine.streem.repository.ICollectionCodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@AllArgsConstructor
@Repository
public class CollectionCodeRepository implements ICollectionCodeRepository {
  private MongoTemplate mongoTemplate;

  @Override
  public Code getCode(String type, Integer clause) {
    Query query = new Query().addCriteria(Criteria.where("type").is(type).andOperator(Criteria.where("clause").is(clause)));
    Update updateDefinition = new Update().inc("counter", 1);
    FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(true);
    return mongoTemplate.findAndModify(query, updateDefinition, options, Code.class);
  }
}
