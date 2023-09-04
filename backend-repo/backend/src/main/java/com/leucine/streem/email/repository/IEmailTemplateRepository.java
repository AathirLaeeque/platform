package com.leucine.streem.email.repository;

import com.leucine.streem.email.model.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
}
