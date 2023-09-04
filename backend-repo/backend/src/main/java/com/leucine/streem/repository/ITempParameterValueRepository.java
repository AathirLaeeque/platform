package com.leucine.streem.repository;

import com.leucine.streem.constant.Queries;
import com.leucine.streem.model.TempParameterValue;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ITempParameterValueRepository extends JpaRepository<TempParameterValue, Long> {

  @Query(Queries.GET_PARAMTER_VALUE_BY_PARAMETER_ID_AND_JOB_ID)
  Optional<TempParameterValue> findByParameterIdAndJobId(@Param("parameterId") Long parameterId, @Param("jobId") Long jobId);

  @EntityGraph(value = "readTempParameterValue", type = EntityGraph.EntityGraphType.FETCH)
  List<TempParameterValue> readByJobId(Long id);

  @EntityGraph(value = "readTempParameterValue", type = EntityGraph.EntityGraphType.FETCH)
  @Query(Queries.READ_TEMP_PARAMETER_VALUE_BY_JOB_AND_STAGE_ID)
  List<TempParameterValue> readByJobIdAndStageId(@Param("jobId") Long jobId, @Param("stageId") Long stageId);

  @EntityGraph(value = "readTempParameterValue", type = EntityGraph.EntityGraphType.FETCH)
  List<TempParameterValue> readByJobIdAndParameterIdIn(@Param("jobId") Long jobId, @Param("parameterIds") List<Long> parameterIds);

  @Modifying(clearAutomatically = true)
  @Transactional
  @Query(value = Queries.UPDATE_TEMP_PARAMETER_CHOICES_AND_STATE_BY_PARAMETER_AND_JOB_ID, nativeQuery = true)
  void updateParameterChoicesAndState(@Param("jobId") Long jobId, @Param("parameterId") Long parameterId, @Param("choices") String choices, @Param("state") String state, @Param("modifiedBy") Long modifiedBy, @Param("modifiedAt") Long modifiedAt);

  @Modifying(clearAutomatically = true)
  @Transactional
  @Query(value = Queries.UPDATE_TEMP_PARAMETER_VALUE_AND_STATE_BY_PARAMETER_AND_JOB_ID, nativeQuery = true)
  void updateParameterValuesAndState(@Param("jobId") Long jobId, @Param("parameterId") Long parameterId, @Param("value") String value, @Param("state") String state, @Param("modifiedBy") Long modifiedBy, @Param("modifiedAt") Long modifiedAt);

  //TODO check performance impact of clear automatically
  @Modifying(clearAutomatically = true)
  @Transactional
  @Query(value = Queries.UPDATE_TEMP_PARAMETER_VALUE_AND_REASON_BY_PARAMETER_AND_JOB_ID, nativeQuery = true)
  void updateParameterValuesWithReason(@Param("jobId") Long jobId, @Param("parameterId") Long parameterId, @Param("value") String value, @Param("reason") String reason, @Param("modifiedBy") Long modifiedBy, @Param("modifiedAt") Long modifiedAt);

  @Modifying(clearAutomatically = true)
  @Transactional
  @Query(value = Queries.UPDATE_PARAMETER_CHOICES_REASON_AND_STATE, nativeQuery = true)
  void updateParameterChoicesAndReasonAndState(@Param("jobId") Long jobId, @Param("parameterId") Long parameterId, @Param("choices") String choices,
                                               @Param("reason") String reason, @Param("state") String state, @Param("modifiedBy") Long modifiedBy, @Param("modifiedAt") Long modifiedAt);

  @Query(value = Queries.GET_TEMP_PARAMETER_VALUES_BY_JOB_ID_AND_PARAMETER_ID_IN)
  List<TempParameterValue> findByJobIdAndParameterIdsIn(@Param("jobId") Long jobId, @Param("parameterIds") List<Long> parameterIds);

  @EntityGraph(value = "readTempParameterValue", type = EntityGraph.EntityGraphType.FETCH)
  Optional<TempParameterValue> readByParameterIdAndJobId(@Param("parameterId") Long parameterId, @Param("jobId") Long jobId);

}
