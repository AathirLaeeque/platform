package com.leucine.streem.email.repository;

import com.leucine.streem.email.model.EmailAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEmailAuditRepository extends JpaRepository<EmailAudit, Long> {
}
