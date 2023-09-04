package com.leucine.streem.service;

import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.PropertyDto;
import com.leucine.streem.model.FacilityUseCasePropertyMapping;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPropertyService {
  List<PropertyDto> getAllPropertiesByType(Type.PropertyType type, Long useCaseId, boolean archived, String filters, Pageable pageable);

  List<FacilityUseCasePropertyMapping> getPropertiesByFacilityIdAndUseCaseIdAndPropertyType(Long facilityId, Long useCaseId, Type.PropertyType propertyType);
}
