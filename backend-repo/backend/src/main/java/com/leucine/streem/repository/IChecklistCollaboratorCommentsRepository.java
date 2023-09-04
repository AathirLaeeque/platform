package com.leucine.streem.repository;

import com.leucine.streem.constant.Queries;
import com.leucine.streem.model.ChecklistCollaboratorComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

@Repository
public interface IChecklistCollaboratorCommentsRepository extends JpaRepository<ChecklistCollaboratorComments, Long> {
  @Transactional
  @Modifying(clearAutomatically = true)
  @Query(value = Queries.DELETE_COLLABORATOR_COMMENTS_BY_CHECKLIST_COLLABORATOR_MAPPING, nativeQuery = true)
  void deleteByChecklistCollaboratorMappingId(@Param("checklistCollaboratorMappingId") Long checklistCollaboratorMappingId);
}
