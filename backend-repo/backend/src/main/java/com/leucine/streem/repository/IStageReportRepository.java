package com.leucine.streem.repository;

import com.leucine.streem.constant.Queries;
import com.leucine.streem.model.StageExecutionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface IStageReportRepository extends JpaRepository<StageExecutionReport, Long> {

  @Modifying
  @Transactional
  @Query(value = Queries.INCREMENT_TASK_COMPLETE_COUNT_BY_JOB_ID_AND_STAGE_ID, nativeQuery = true)
  void incrementTaskCompleteCount(@Param("jobId") Long jobId, @Param("stageId") Long stageId);

  @Query(value = Queries.GET_STAGE_EXECUTION_REPORT_BY_JOB_ID)
  List<StageExecutionReport> findByJobId(@Param("jobId") Long jobId);

  @Modifying
  @Transactional
  @Query(value = Queries.UPDATE_STATE_TO_IN_PROGRESS_IN_STAGE_REPORT_BY_JOB_ID_AND_STAGE_ID, nativeQuery = true)
  void updateStageToInProgress(@Param("jobId") Long jobId, @Param("stageId") Long stageId);

  @Modifying
  @Transactional
  @Query(value = Queries.DELETE_STAGE_EXECUTION_BY_JOB_ID, nativeQuery = true)
  void deleteStagesForJob(@Param("jobId") Long jobId);
}
