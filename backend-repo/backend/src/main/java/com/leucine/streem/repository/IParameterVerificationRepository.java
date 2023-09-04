package com.leucine.streem.repository;

import com.leucine.streem.constant.Queries;
import com.leucine.streem.model.ParameterVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IParameterVerificationRepository extends JpaRepository<ParameterVerification, Long>, JpaSpecificationExecutor<ParameterVerification> {
  @Query(value = Queries.FIND_BY_JOB_ID_AND_PARAMETER_VALUES_ID_AND_VERIFICATION_TYPE, nativeQuery = true)
  ParameterVerification findByJobIdAndParameterValueIdAndVerificationType(@Param("jobId") Long jobId, @Param("parameterValueId") Long parameterValueId, @Param("verificationType") String verificationType);

  List<ParameterVerification> findByJobId(Long jobId);

  @Query(value = Queries.FIND_BY_JOB_ID_AND_PARAMETER_ID_AND_PARAMETER_VERIFICATION_TYPE, nativeQuery = true)
  ParameterVerification findByJobIdAndParameterIdAndVerificationType(@Param("jobId") Long jobId, @Param("parameterId") Long parameterId, @Param("verificationType") String verificationType);
}
