package com.leucine.streem.repository;

import com.leucine.streem.model.AutoInitializedParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface IAutoInitializedParameterRepository extends JpaRepository<AutoInitializedParameter, Long>, JpaSpecificationExecutor<AutoInitializedParameter> {
  @Modifying
  void deleteByChecklistId(Long checklistId);

  List<AutoInitializedParameter> findByReferencedParameterId(Long referencedParameterId);
}
