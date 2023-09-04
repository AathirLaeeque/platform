package com.leucine.streem.repository;

import com.leucine.streem.model.JobCweDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IJobCweDetailRepository extends JpaRepository<JobCweDetail, Long>, JpaSpecificationExecutor<JobCweDetail> {
  Optional<JobCweDetail> findByJobId(Long jobId);
}
