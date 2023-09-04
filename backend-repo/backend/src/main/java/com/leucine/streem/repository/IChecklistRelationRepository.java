package com.leucine.streem.repository;

import com.leucine.streem.model.Relation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IChecklistRelationRepository extends JpaRepository<Relation, Long> {
  List<Relation> findByChecklistId(Long checklistId);
  Relation findByIdAndChecklistId(Long id, Long checklistId);
}
