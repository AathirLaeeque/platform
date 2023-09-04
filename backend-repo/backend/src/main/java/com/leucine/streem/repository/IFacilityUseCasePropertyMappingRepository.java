package com.leucine.streem.repository;

import com.leucine.streem.model.FacilityUseCasePropertyMapping;
import com.leucine.streem.model.compositekey.FacilityUseCasePropertyCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFacilityUseCasePropertyMappingRepository extends JpaRepository<FacilityUseCasePropertyMapping, FacilityUseCasePropertyCompositeKey>, JpaSpecificationExecutor<FacilityUseCasePropertyMapping> {
  List<FacilityUseCasePropertyMapping> findAllByFacilityIdAndUseCaseId(Long facilityId, Long useCaseId);
}
