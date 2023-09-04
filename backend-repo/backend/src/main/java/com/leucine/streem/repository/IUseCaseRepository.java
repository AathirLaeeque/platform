package com.leucine.streem.repository;

import com.leucine.streem.model.UseCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUseCaseRepository extends JpaRepository<UseCase, Long>, JpaSpecificationExecutor<UseCase> {
  List<UseCase> findAllByArchivedOrderByOrderTree(boolean archived);
}
