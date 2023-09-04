package com.leucine.streem.repository;

import com.leucine.streem.constant.Queries;
import com.leucine.streem.model.TaskExecutionTimer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ITaskExecutionTimerRepository extends JpaRepository<TaskExecutionTimer, Long> {
  @Query(Queries.FIND_TASK_EXECUTION_TIMER_AT_PAUSE)
  TaskExecutionTimer findPausedTimerByTaskExecutionIdAndJobId(@Param("taskExecutionId") Long taskExecutionId);

  List<TaskExecutionTimer> findAllByTaskExecutionIdOrderByIdDesc(Long taskExecutionId);

}
