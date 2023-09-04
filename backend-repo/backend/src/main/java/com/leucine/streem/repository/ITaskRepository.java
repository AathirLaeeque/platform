package com.leucine.streem.repository;

import com.leucine.streem.constant.Queries;
import com.leucine.streem.dto.projection.TaskAssigneeView;
import com.leucine.streem.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
@Transactional
public interface ITaskRepository extends JpaRepository<Task, Long> {
  @Query(value = Queries.GET_TASK_BY_PARMETER_ID)
  Task findByParameterId(@Param("parameterId") Long parameterId);

  @Transactional
  @Modifying
  @Query(value = Queries.UPDATE_TASK_ORDER, nativeQuery = true)
  void reorderTask(@Param("taskId") Long taskId, @Param("order") Long order, @Param("userId") Long userId, @Param("modifiedAt") Long modifiedAt);

  @Query(value = Queries.GET_TASKS_BY_STAGE_ID_IN_AND_ORDER_BY_ORDER_TREE)
  List<Task> findByStageIdInOrderByOrderTree(@Param("stageIds") Set<Long> stageIds);

  @Query(value = Queries.GET_TASK_USER_MAPPING_BY_TASK_IN, nativeQuery = true)
  List<TaskAssigneeView> findByTaskIdIn(@Param("checklistId") Long checklistId, @Param("taskIds") Set<Long> taskIds, @Param("totalTaskIds") int totalTaskIds, @Param("facilityId") Long facilityId);

  List<Task> findAllByIdInAndArchived(Set<Long> ids, boolean archived);

}
