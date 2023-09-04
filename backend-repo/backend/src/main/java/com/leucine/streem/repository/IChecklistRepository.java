package com.leucine.streem.repository;

import com.leucine.streem.constant.Queries;
import com.leucine.streem.constant.State;
import com.leucine.streem.dto.projection.JobLogMigrationChecklistView;
import com.leucine.streem.model.Checklist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IChecklistRepository extends JpaRepository<Checklist, Long>, JpaSpecificationExecutor<Checklist> {

  @Override
  Page<Checklist> findAll(Specification specification, Pageable pageable);

  @EntityGraph(value = "checklistInfo", type = EntityGraph.EntityGraphType.FETCH)
  List<Checklist> readAllByIdIn(Set<Long> ids, Sort sort);

  @EntityGraph(value = "readChecklist", type = EntityGraph.EntityGraphType.FETCH)
  Optional<Checklist> readById(Long id);

  @Query(value = Queries.GET_CHECKLIST_BY_TASK_ID)
  Optional<Checklist> findByTaskId(@Param("taskId") Long taskId);

  @Modifying(clearAutomatically = true)
  @Query(value = Queries.UPDATE_CHECKLIST_STATE)
  void updateState(@Param("state") State.Checklist state, @Param("checklistId") Long checklistId);

  @Query(value = Queries.GET_CHECKLIST_CODE)
  String getChecklistCodeByChecklistId(@Param("checklistId") Long checklistId);

  @Transactional
  @Modifying
  @Query(value = Queries.DELETE_CHECKLIST_FACILITY_MAPPING, nativeQuery = true)
  void removeChecklistFacilityMapping(@Param("checklistId") Long checklistId, @Param("facilityIds") Set<Long> facilityIds);

  @Query(value = Queries.GET_CHECKLIST_STATE_BY_STAGE_ID)
  State.Checklist findByStageId(@Param("stageId") Long stageId);

  List<Checklist> findByUseCaseId(Long useCaseId);

  @Query(value = Queries.GET_CHECKLIST_BY_STATE)
  Set<Long> findByStateIn(@Param("state") Set<State.Checklist> stateSet);

  @Query(value = Queries.GET_CHECKLIST_BY_STATE_NOT)
  Set<Long> findByStateNot(@Param("state") State.Checklist state);

  @Query(value = "SELECT c.id as id, c.name as name, c.code as code, c.state as state from checklists c where id = :id", nativeQuery = true)
  JobLogMigrationChecklistView findChecklistInfoById(@Param("id") Long id);
}
