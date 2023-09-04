package com.leucine.streem.repository;

import com.leucine.streem.constant.Queries;
import com.leucine.streem.model.Automation;
import com.leucine.streem.model.TaskAutomationMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Set;

@Repository
public interface ITaskAutomationMappingRepository extends JpaRepository<TaskAutomationMapping, Long> {
  @Transactional
  @Modifying
  @Query(value = Queries.DELETE_TASK_AUTOMATION_MAPPING, nativeQuery = true)
  void deleteByTaskIdAndAutomationId(@Param("taskId") Long taskId, @Param("automationsId") Long automationId);

  @Query(value = Queries.GET_ALL_AUTOMATIONS_IN_TASK_AUTOMATION_MAPPING_BY_TASK_ID)
  Set<Automation> findAllAutomationsByTaskId(@Param("taskId") Long taskId);
}
