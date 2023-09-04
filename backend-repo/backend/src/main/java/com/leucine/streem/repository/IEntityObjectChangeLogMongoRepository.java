package com.leucine.streem.repository;

import com.leucine.streem.collections.changelogs.EntityObjectChangeLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IEntityObjectChangeLogMongoRepository extends MongoRepository<EntityObjectChangeLog, String> {
}
