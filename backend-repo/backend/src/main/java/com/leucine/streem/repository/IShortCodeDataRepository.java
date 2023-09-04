package com.leucine.streem.repository;

import com.leucine.streem.collections.shortcode.ShortCode;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface IShortCodeDataRepository extends MongoRepository<ShortCode, String> {
  ShortCode findByData_ObjectId(String objectId);

  Optional<ShortCode> findByShortCode(String shortCode);

}
