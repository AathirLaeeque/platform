package com.leucine.streem.repository;

import com.leucine.streem.model.Scheduler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISchedulerRepository extends JpaRepository<Scheduler, Long>, JpaSpecificationExecutor<Scheduler> {
  @Override
  Page<Scheduler> findAll(Specification specification, Pageable pageable);

  List<Scheduler> findByChecklistId(Long checklistId);

}
