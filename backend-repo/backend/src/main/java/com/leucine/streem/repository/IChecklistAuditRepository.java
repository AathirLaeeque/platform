package com.leucine.streem.repository;

import com.leucine.streem.model.ChecklistAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IChecklistAuditRepository extends JpaRepository<ChecklistAudit, Long>, JpaSpecificationExecutor<ChecklistAudit> {
}