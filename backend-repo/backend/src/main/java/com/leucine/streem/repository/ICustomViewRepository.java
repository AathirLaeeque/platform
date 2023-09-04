package com.leucine.streem.repository;

import com.leucine.streem.collections.CustomView;
import com.leucine.streem.constant.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ICustomViewRepository extends MongoRepository<CustomView, String> {
  Page<CustomView> findByTargetType(Type.ConfigurableViewTargetType targetType, Pageable pageable);

  Page<CustomView> findByProcessIdAndTargetType(String processId, Type.ConfigurableViewTargetType targetType, Pageable pageable);
}
