package com.leucine.streem.repository;

import com.leucine.streem.constant.Queries;
import com.leucine.streem.model.TaskMediaMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ITaskMediaMappingRepository extends JpaRepository<TaskMediaMapping, Long> {
  @Transactional
  @Modifying
  @Query(value = Queries.DELETE_TASK_MEDIA_MAPPING, nativeQuery = true)
  void deleteByTaskIdAndMediaId(@Param("taskId") Long taskId, @Param("mediaId") Long mediaId);

  Optional<TaskMediaMapping> getByTaskIdAndMediaId(@Param("taskId") Long taskId, @Param("mediaId") Long mediaId);
}
