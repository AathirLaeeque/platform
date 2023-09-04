package com.leucine.streem.repository;

import com.leucine.streem.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IFacilityRepository extends JpaRepository<Facility, Long>, JpaSpecificationExecutor<Facility> {
}
