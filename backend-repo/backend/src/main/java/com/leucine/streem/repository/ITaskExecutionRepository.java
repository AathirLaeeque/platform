package com.leucine.streem.repository;

import com.leucine.streem.constant.Queries;
import com.leucine.streem.dto.projection.JobLogTaskExecutionView;
import com.leucine.streem.dto.projection.TaskExecutionCountView;
import com.leucine.streem.model.TaskExecution;
import org.springframework.data.jpa.repository.EntityGraph;
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
@Transactional
public interface ITaskExecutionRepository extends JpaRepository<TaskExecution, Long> {
  @EntityGraph("readTaskExecution")
  Optional<TaskExecution> readByTaskIdAndJobId(@Param("taskId") Long taskId, @Param("jobId") Long jobId);

  @EntityGraph("readTaskExecution")
  List<TaskExecution> readByJobId(Long jobId);

  @EntityGraph("readTaskExecution")
  @Query(Queries.READ_TASK_EXECUTION_BY_JOB_AND_STAGE_ID)
  List<TaskExecution> readByJobIdAndStageId(@Param("jobId") Long jobId, @Param("stageId") Long stageId);

  @Modifying(clearAutomatically = true)
  @Query(value = Queries.UPDATE_TASK_EXECUTION_STATE_AND_CORRECTION_REASON, nativeQuery = true)
  void updateStateAndCorrectionReason(@Param("state") String state, @Param("correctionReason") String correctionReason, @Param("id") Long id);

  @Modifying(clearAutomatically = true)
  @Query(value = Queries.UPDATE_TASK_EXECUTION_ENABLE_CORRECTION, nativeQuery = true)
  void enableCorrection(@Param("correctionReason") String correctionReason, @Param("id") Long id);

  @Modifying(clearAutomatically = true)
  @Query(value = Queries.UPDATE_TASK_EXECUTION_COMPLETE_CORRECTION, nativeQuery = true)
  void completeCorrection(@Param("id") Long id);

  @Modifying(clearAutomatically = true)
  @Query(value = Queries.UPDATE_TASK_EXECUTION_CANCEL_CORRECTION, nativeQuery = true)
  void cancelCorrection(@Param("id") Long id);

  @Modifying(clearAutomatically = true)
  @Query(value = Queries.UPDATE_TASK_EXECUTION_STATE, nativeQuery = true)
  void updateState(@Param("state") String state, @Param("id") Long id);

  @EntityGraph("readTaskExecution")
  List<TaskExecution> readByJobIdAndTaskIdIn(@Param("jobId") Long jobId, @Param("taskIds") List<Long> taskIds);

  @Query(value = Queries.GET_NON_SIGNED_OFF_TASKS_BY_JOB_ID, nativeQuery = true)
  List<Long> findNonSignedOffTaskIdsByJobId(@Param("jobId") Long jobId);

  @Query(value = Queries.GET_NON_COMPLETED_TASKS_BY_JOB_ID, nativeQuery = true)
  List<Long> findNonCompletedTaskIdsByJobId(@Param("jobId") Long jobId);

  @Query(value = Queries.GET_ENABLED_FOR_CORRECTION_TASKS_BY_JOB_ID, nativeQuery = true)
  List<Long> findEnabledForCorrectionTaskIdsByJobId(@Param("jobId") Long jobId);

  @Query(value = Queries.GET_NON_SIGNED_OFF_TASKS_BY_JOB_AND_USER_ID, nativeQuery = true)
  List<Long> findNonSignedOffTaskIdsByJobIdAndUserId(@Param("jobId") Long jobId, @Param("userId") Long userId);

  @Query(value = Queries.GET_TASK_EXECUTION_STATUS_COUNT_BY_JOB_IDS, nativeQuery = true)
  List<TaskExecutionCountView> findCompletedAndTotalTaskExecutionCountByJobIds(@Param("jobIds") Set<Long> ids);

  @Query(value = Queries.GET_TASK_EXECUTION_COUNT_BY_JOB_ID, nativeQuery = true)
  Integer getTaskExecutionCountByJobId(@Param("jobId") Long jobId);

  @EntityGraph("readTaskExecution")
  TaskExecution readByJobIdAndTaskId(@Param("jobId") Long jobId, @Param("taskId") Long taskId);

  TaskExecution findByTaskIdAndJobId(@Param("jobId") Long jobId, @Param("taskId") Long taskId);

  @EntityGraph("readTaskExecutionWithTask")
  @Query(value = Queries.GET_TASK_EXECUTIONS_BY_JOB_ID_AND_STAGE_ID_IN)
  List<TaskExecution> findByJobIdAndStageIdIn(@Param("jobId") Long jobId, @Param("stageIds") Set<Long> stageIds);

  @Query(value = Queries.GET_ALL_NON_COMPLETED_TASKS_OF_JOB, nativeQuery = true)
  Set<Long> findNonCompletedTasksByJobId(@Param("jobId") Long jobId);

  @Query(value = """
      select te.tasks_id   as taskId,
             te.started_at as startedAt,
             te.ended_at   as endedAt,
             u1.first_name as taskStartedByFirstName,
             u1.last_name  as taskStartedByLastName,
             u2.first_name as taskModifiedByFirstName,
             u2.last_name  as taskModifiedByLastName,
             t.name as name,
             te.jobs_id as jobId


      from task_executions te
               left join public.users u1 on te.started_by = u1.id
               inner join public.users u2 on te.modified_by = u2.id
               inner join public.tasks t on t.id = te.tasks_id

      where jobs_id in :jobIds
                """,nativeQuery = true)
  List<JobLogTaskExecutionView> findTaskExecutionDetailsByJobId(@Param("jobIds") Set<Long> jobIds);
}
