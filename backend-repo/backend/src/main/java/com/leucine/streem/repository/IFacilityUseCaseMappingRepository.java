package com.leucine.streem.repository;

import com.leucine.streem.model.FacilityUseCaseMapping;
import com.leucine.streem.model.compositekey.FacilityUseCaseCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IFacilityUseCaseMappingRepository extends JpaRepository<FacilityUseCaseMapping, FacilityUseCaseCompositeKey>, JpaSpecificationExecutor<FacilityUseCaseMapping> {
  FacilityUseCaseMapping findByFacilityIdAndUseCaseId(Long facilityId, Long useCaseId);
}
