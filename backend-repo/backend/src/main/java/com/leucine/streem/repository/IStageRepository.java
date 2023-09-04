package com.leucine.streem.repository;

import com.leucine.streem.constant.Queries;
import com.leucine.streem.constant.State;
import com.leucine.streem.dto.projection.StageTotalTasksView;
import com.leucine.streem.model.Stage;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.List;
import java.util.Set;

@Repository
public interface IStageRepository extends JpaRepository<Stage, Long>, JpaSpecificationExecutor<Stage> {

  @Transactional
  @Modifying
  @Query(value = Queries.UPDATE_STAGE_ORDER_BY_STAGE_ID, nativeQuery = true)
  void reorderStage(@Param("stageId") Long stageId, @Param("order") Long order, @Param("userId") Long userId, @Param("modifiedAt") Long modifiedAt);

  @Query(value = Queries.GET_STAGE_BY_TASK_ID)
  Stage findByTaskId(@Param("taskId") Long taskId);

  @Query(value = Queries.GET_STAGE_ID_BY_TASK_ID)
  Long findStageIdByTaskId(@Param("taskId") Long taskId);

  @EntityGraph("readStage")
  Optional<Stage> readById(Long id);

  @Query(value = Queries.GET_TOTAL_TASKS_VIEW_BY_CHECKLIST_ID,  nativeQuery = true)
  List<StageTotalTasksView> findByChecklistId(@Param("checklistId") Long checklistId);

  @Query(value = Queries.GET_STAGES_BY_CHECKLIST_ID_AND_ORDER_BY_ORDER_TREE)
  List<Stage> findByChecklistIdOrderByOrderTree(@Param("checklistId") Long checklistId);


  @Query(value = Queries.GET_STAGES_BY_JOB_ID_WHERE_ALL_TASK_EXECUTION_STATE_IN)
  List<Stage> findStagesByJobIdAndAllTaskExecutionStateIn(@Param("jobId") Long jobId, @Param("taskExecutionStates") Set<State.TaskExecution> taskExecutionStates);

}
