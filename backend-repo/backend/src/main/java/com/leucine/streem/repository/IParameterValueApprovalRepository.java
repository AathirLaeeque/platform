package com.leucine.streem.repository;

import com.leucine.streem.model.ParameterValueApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface IParameterValueApprovalRepository extends JpaRepository<ParameterValueApproval, Long> {
}
