package com.leucine.streem.repository;

import com.leucine.streem.model.RelationValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface IRelationValueRepository extends JpaRepository<RelationValue, Long> {
  RelationValue findByRelationIdAndJobId(Long id, Long jobId);
}
