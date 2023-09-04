package com.leucine.streem.repository;

import com.leucine.streem.constant.Queries;
import com.leucine.streem.constant.State;
import com.leucine.streem.dto.projection.JobProcessInfoView;
import com.leucine.streem.dto.projection.ShouldBeParameterStatusView;
import com.leucine.streem.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public interface IJobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
  @EntityGraph("readJob")
  Optional<Job> readById(Long id);

  @Override
  Page<Job> findAll(@Nullable Specification<Job> specification, Pageable pageable);

  @Override
  long count(@Nullable Specification<Job> specification);

  @EntityGraph("jobInfo")
  List<Job> readAllByIdIn(Set<Long> ids, Sort sort);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query(value = Queries.SET_JOB_TO_UNASSIGNED_IF_NO_USER_IS_ASSIGNED, nativeQuery = true)
  void updateJobToUnassignedIfNoUserAssigned();

  @Query(value = Queries.IS_ACTIVE_JOB_EXIST_FOR_GIVEN_CHECKLIST)
  boolean findByChecklistIdWhereStateNotIn(@Param("checklistId") Long checklistId, @Param("jobStates") Set<State.Job> jobStates);

  @Query(value = Queries.FIND_JOB_PROCESS_INFO, nativeQuery = true)
  JobProcessInfoView findJobProcessInfo(@Param("jobId") Long jobId);

  @Query(value = Queries.IS_JOB_EXISTS_BY_SCHEDULER_ID_AND_DATE_GREATER_THAN_EXPECTED_START_DATE)
  boolean isJobExistsBySchedulerIdAndDateGreaterThanOrEqualToExpectedStartDate(@Param("schedulerId") Long schedulerId, @Param("date") Long date);

  @Query(value = Queries.GET_ALL_SHOULD_BE_PARAMETER_STATUS, nativeQuery = true)
  Page<ShouldBeParameterStatusView> getAllShouldBeParameterStatus(@Param("facilityId") long facilityId, @Param("parameterName") String parameterName, @Param("processName") String processName, Pageable pageable);

  List<Job> findAllByChecklistId(Long checklistId);
}
