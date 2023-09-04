package com.leucine.streem.repository;

import com.leucine.streem.model.Automation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAutomationRepository extends JpaRepository<Automation, Long> {
}
