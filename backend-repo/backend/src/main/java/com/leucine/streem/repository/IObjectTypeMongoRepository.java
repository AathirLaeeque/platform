package com.leucine.streem.repository;

import com.leucine.streem.collections.ObjectType;
import org.springframework.data.mongodb.repository.MongoRepository;

// TODO remove and use existing objectTypeRepository or improvise to support both the types
public interface IObjectTypeMongoRepository extends MongoRepository<ObjectType, String> {
  boolean existsByDisplayNameIgnoreCaseAndUsageStatus(String displayName, int usageStatus);
}
