package com.leucine.streem.repository;

import com.leucine.streem.constant.Queries;
import com.leucine.streem.dto.projection.JobAssigneeView;
import com.leucine.streem.dto.projection.TaskExecutionAssigneeDetailsView;
import com.leucine.streem.dto.projection.TaskExecutionAssigneeView;
import com.leucine.streem.dto.projection.TaskExecutionAssigneeBasicView;
import com.leucine.streem.model.TaskExecution;
import com.leucine.streem.model.TaskExecutionUserMapping;
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
public interface ITaskExecutionAssigneeRepository extends JpaRepository<TaskExecutionUserMapping, Long> {
  @Query(value = Queries.IS_USER_ASSIGNED_TO_ANY_TASK, nativeQuery = true)
  boolean isUserAssignedToAnyTask(@Param("jobId") Long jobId, @Param("userId") Long userId);

  @Query(value = Queries.IS_ALL_TASK_UNASSIGNED, nativeQuery = true)
  boolean isAllTaskUnassigned(@Param("jobId") Long jobId);

  @Query(value = Queries.GET_ALL_TASK_ASSIGNEES_DETAILS_BY_JOB_ID, nativeQuery = true)
  List<TaskExecutionAssigneeDetailsView> findByJobId(@Param("jobId") Long jobId, @Param("totalExecutionIds") Integer totalExecutionIds);

  @Query(value = Queries.GET_ALL_JOB_ASSIGNEES, nativeQuery = true)
  List<JobAssigneeView> getJobAssignees(@Param("jobIds") Set<Long> jobIds);

  @Query(value = Queries.GET_ALL_JOB_ASSIGNEES_COUNT, nativeQuery = true)
  Integer getJobAssigneesCount(@Param("jobId") Long jobId);

  @Query(value = Queries.IS_USER_ASSIGNED_TO_IN_PROGRESS_TASKS, nativeQuery = true)
  Boolean isUserAssignedToInProgressTasks(@Param("userId") Long userId);

  @Query(value = Queries.GET_TASK_EXECUTION_USER_MAPPING_BY_TASK_EXECUTION_AND_USER_ID_IN, nativeQuery = true)
  List<TaskExecutionAssigneeBasicView> findByTaskExecutionIdAndUserIdIn(@Param("taskExecutionId") Long taskExecutionId,
                                                                        @Param("userIds") Set<Long> userIds);

  @Query(value = Queries.GET_TASK_EXECUTION_USER_MAPPING_BY_TASK_EXECUTION_IN_AND_USER_ID_IN, nativeQuery = true)
  List<TaskExecutionAssigneeBasicView> findByTaskExecutionIdInAndUserIdIn(@Param("taskExecutionIds") Set<Long> taskExecutionIds,
                                                                          @Param("userIds") Set<Long> userIds);

  @Query(value = Queries.GET_TASK_EXECUTION_USER_MAPPING_BY_TASK_EXECUTION_IN, nativeQuery = true)
  List<TaskExecutionAssigneeView> findByTaskExecutionIdIn(@Param("taskExecutionIds") Set<Long> taskExecutionIds, @Param("totalExecutionIds") Integer totalExecutionIds);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query(value = Queries.UNASSIGN_USERS_FROM_NON_STARTED_TASKS, nativeQuery = true)
  void unassignUsersFromNonStartedTasks(@Param("userId") Long userId);

  Optional<TaskExecutionUserMapping> findByTaskExecutionAndUser(TaskExecution taskExecution, User user);

  @Modifying(clearAutomatically = true)
  @Transactional
  @Query(value = Queries.UNASSIGN_USERS_FROM_TASK_EXECUTIONS, nativeQuery = true)
  void unassignUsersByTaskExecutions(@Param("taskExecutionIds") Set<Long> taskExecutionIds, @Param("userIds") Set<Long> userIds);

  @Modifying(clearAutomatically = true)
  @Transactional
  @Query(value = Queries.UPDATE_TASK_ASSIGNEE_STATE, nativeQuery = true)
  void updateAssigneeState(@Param("state") String state, @Param("userId") Long userId, @Param("taskExecutionIds") Set<Long> taskExecutionIds,
                           @Param("modifiedBy") Long modifiedBy, @Param("modifiedAt") Long modifiedAt);

}
