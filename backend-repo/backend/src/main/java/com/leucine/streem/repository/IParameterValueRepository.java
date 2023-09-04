package com.leucine.streem.repository;

import com.leucine.streem.constant.Queries;
import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.projection.JobLogMigrationParameterValueView;
import com.leucine.streem.model.ParameterValue;
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
public interface IParameterValueRepository extends JpaRepository<ParameterValue, Long> {

  @EntityGraph(value = "readParameterValue", type = EntityGraph.EntityGraphType.FETCH)
  Optional<ParameterValue> readByParameterIdAndJobId(@Param("parameterId") Long parameterId, @Param("jobId") Long jobId);

  @EntityGraph(value = "readParameterValue", type = EntityGraph.EntityGraphType.FETCH)
  List<ParameterValue> readByJobIdAndParameterIdIn(@Param("jobId") Long jobId, @Param("parameterIds") List<Long> parameterIds);

  @EntityGraph(value = "readParameterValue", type = EntityGraph.EntityGraphType.FETCH)
  List<ParameterValue> readByJobId(Long jobId);

  @EntityGraph(value = "readParameterValue", type = EntityGraph.EntityGraphType.FETCH)
  @Query(Queries.READ_PARAMETER_VALUE_BY_JOB_ID_AND_STAGE_ID)
  List<ParameterValue> readByJobIdAndStageId(@Param("jobId") Long jobId, @Param("stageId") Long stageId);

  @Query(value = Queries.GET_INCOMPLETE_PARAMETER_IDS_BY_JOB_ID_AND_TASK_ID, nativeQuery = true)
  List<Long> findIncompleteMandatoryParameterIdsByJobIdAndTaskId(@Param("jobId") Long jobId, @Param("taskId") Long taskId);

  @Query(value = Queries.GET_EXECUTABLE_PARAMETER_IDS_BY_TASK_ID)
  List<Long> findExecutableParameterIdsByTaskId(@Param("taskId") Long taskId);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query(value = Queries.UPDATE_PARAMETER_VALUE_STATE, nativeQuery = true)
  void updateStateForParameters(@Param("jobId") Long jobId, @Param("state") String state, @Param("parameterIds") List<Long> parameterIds);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query(value = Queries.UPDATE_PARAMETER_VALUES, nativeQuery = true)
  void updateParameterValues(@Param("jobId") Long jobId, @Param("parameterId") Long parameterId, @Param("state") String state, @Param("value") String value, @Param("choices") String choices, @Param("reason") String reason, @Param("modifiedBy")Long modifiedBy, @Param("modifiedAt") Long modifiedAt);

  @Query(value = Queries.GET_PARAMETER_VALUES_BY_JOB_ID_AND_TASK_ID_AND_PARAMETER_TYPE_IN)
  List<ParameterValue> findByJobIdAndTaskIdParameterTypeIn(@Param("jobId") Long jobId, @Param("taskIds") List<Long> taskIds, @Param("parameterTypes") List<Type.Parameter> parameterTypes);

  @Query(value = Queries.GET_PARAMETER_VALUES_BY_JOB_ID_AND_PARAMETER_TARGET_ENTITY_TYPE_IN)
  List<ParameterValue> findByJobIdAndParameterTargetEntityTypeIn(@Param("jobId") Long jobId, @Param("targetEntityTypes") List<Type.ParameterTargetEntityType> targetEntityTypes);

  @Query(value = Queries.GET_PARAMETER_VALUES_BY_JOB_ID_AND_PARAMETER_ID_IN)
  List<ParameterValue> findByJobIdAndParameterIdsIn(@Param("jobId") Long jobId, @Param("parameterIds") List<Long> parameterIds);

  @Query(value = Queries.GET_PARAMETER_VALUES_BY_JOB_ID_AND_PARAMETER_ID_IN)
  List<ParameterValue> findByJobIdAndParameterIdsIn(@Param("jobId") Long jobId, @Param("parameterIds") Set<Long> parameterIds);

  @Query(value = Queries.GET_PARAMETER_VALUE_BY_JOB_ID_AND_PARAMETER_ID)
  ParameterValue findByJobIdAndParameterId(@Param("jobId") Long jobId, @Param("parameterId") Long parameterId);

  @Modifying
  @Transactional
  @Query(value = Queries.UPDATE_PARAMETER_VALUE_VISIBILITY, nativeQuery = true)
  void updateParameterValueVisibility(@Param("parameterIds") Set<Long> parameterIds, @Param("visibility") boolean visibility, @Param("jobId") Long jobId);

  @Query(value = Queries.GET_ALL_JOB_IDS_BY_TARGET_ENTITY_TYPE_AND_OBJECT_TYPE_IN_DATA, nativeQuery = true)
  Set<Long> getJobIdsByTargetEntityTypeAndObjectInChoices(@Param("targetEntityType") String targetEntityType, @Param("objectId") String objectId);

  @Query(value = Queries.GET_VERIFICATION_INCOMPLETE_PARAMETER_IDS_BY_JOB_ID_AND_TASK_ID, nativeQuery = true)
  List<Long> findVerificationIncompleteParameterIdsByJobIdAndTaskId(@Param("jobId") Long jobId, @Param("taskId") Long taskId);

  @Query(value = "select p from ParameterValue p where jobs_id = :jobId")
  List<ParameterValue> findAllByJobId(@Param("jobId") Long jobId);
}
