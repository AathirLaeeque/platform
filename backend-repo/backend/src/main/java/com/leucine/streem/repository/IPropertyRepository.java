package com.leucine.streem.repository;

import com.leucine.streem.model.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface IPropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {
  @Override
  Page<Property> findAll(@Nullable Specification<Property> specification, Pageable pageable);
}
