package com.leucine.streem.repository;

import com.leucine.streem.constant.Queries;
import com.leucine.streem.constant.State;
import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.projection.ChecklistCollaboratorView;
import com.leucine.streem.model.Checklist;
import com.leucine.streem.model.ChecklistCollaboratorMapping;
import com.leucine.streem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IChecklistCollaboratorMappingRepository extends JpaRepository<ChecklistCollaboratorMapping, Long> {

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query(value = Queries.UNASSIGN_REVIEWERS_FROM_CHECKLIST, nativeQuery = true)
  void deleteAll(@Param("checklistId") Long checklistId, @Param("phase") Integer phase, @Param("userIds") Set<Long> userIds);

  @Query(value = Queries.GET_ALL_COLLABORATORS_BY_CHECKLIST_ID_AND_PHAST_TYPE, nativeQuery = true)
  List<ChecklistCollaboratorView> findAllByChecklistIdAndPhaseType(@Param("checklistId") Long checklistId, @Param("phaseType") State.ChecklistCollaboratorPhaseType phaseType);

  @Query(value = Queries.GET_ALL_COLLABORATORS_BY_CHECKLIST_ID_AND_TYPE, nativeQuery = true)
  List<ChecklistCollaboratorView> findAllByChecklistIdAndType(@Param("checklistId") Long checklistId, @Param("type") String type);

  @Query(value = Queries.GET_ALL_COLLABORATORS_BY_CHECKLIST_ID_AND_TYPE_ORDER_BY_AND_MODIFIED_AT_ORDER_TREE, nativeQuery = true)
  List<ChecklistCollaboratorView> findAllByTypeOrderByOrderTreeAndModifiedAt(@Param("checklistId") Long checklistId, @Param("type") String type);

  @Query(value = Queries.GET_ALL_COLLABORATORS_BY_CHECKLIST_ID_AND_TYPE_IN, nativeQuery = true)
  List<ChecklistCollaboratorView> findAllByChecklistIdAndTypeIn(@Param("checklistId") Long checklistId, @Param("types") List<String> types);

  Optional<ChecklistCollaboratorMapping> findByChecklistAndPhaseTypeAndPhaseAndUser(Checklist checklist, State.ChecklistCollaboratorPhaseType phaseType, Integer phase, User user);

  Optional<ChecklistCollaboratorMapping> findFirstByChecklistAndPhaseTypeAndUserAndStateOrderByOrderTreeAsc(Checklist checklist, State.ChecklistCollaboratorPhaseType phaseType, User user, State.ChecklistCollaborator State);

  @Query(value = Queries.IS_COLLABORATOR_MAPPING_EXISTS_BY_CHECKLIST_AND_USER_ID_AND_COLLBORATOR_TYPE)
  boolean isCollaboratorMappingExistsByChecklistAndUserIdAndCollaboratorType(@Param("checklistId") Long checklistId, @Param("userId") Long userId, @Param("types") Set<Type.Collaborator> types);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query(value = Queries.DELETE_AUTHOR_FROM_CHECKLIST, nativeQuery = true)
  void deleteAuthors(@Param("checklistId") Long checklistId, @Param("userIds") Set<Long> userIds);


}
