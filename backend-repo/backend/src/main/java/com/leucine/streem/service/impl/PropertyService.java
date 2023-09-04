package com.leucine.streem.service.impl;

import com.leucine.streem.constant.Misc;
import com.leucine.streem.constant.Operator;
import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.PropertyDto;
import com.leucine.streem.model.FacilityUseCasePropertyMapping;
import com.leucine.streem.model.Property;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.model.helper.SpecificationBuilder;
import com.leucine.streem.model.helper.search.SearchCriteria;
import com.leucine.streem.model.helper.search.SearchOperator;
import com.leucine.streem.repository.IFacilityUseCasePropertyMappingRepository;
import com.leucine.streem.service.IPropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyService implements IPropertyService {
  private final IFacilityUseCasePropertyMappingRepository facilityUseCasePropertyMappingRepository;

  @Override
  public List<PropertyDto> getAllPropertiesByType(Type.PropertyType type, Long useCaseId, boolean archived, String filters, Pageable pageable) {
    log.info("[getAllPropertiesByType] Request to get all properties by type, type: {}, archived {}, filters {}, pageable: {}", type, archived, filters, pageable);
    // TODO Handle for All Facility
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long currentFacilityId = principalUser.getCurrentFacilityId();
    List<Object> facilityIds = null;
    if (currentFacilityId != null && !currentFacilityId.equals(Misc.ALL_FACILITY_ID)) {
      facilityIds = Collections.singletonList(currentFacilityId);
    } else {
      facilityIds = Collections.unmodifiableList(principalUser.getFacilities().stream().map(r -> (Object) Long.valueOf(r.getId())).toList());
    }
    SearchCriteria facilitySearchCriteria = (new SearchCriteria()).setField(FacilityUseCasePropertyMapping.FACILITY_ID).setOp(Operator.Search.ANY.toString()).setValues(facilityIds);
    SearchCriteria useCaseIdSearchCriteria = (new SearchCriteria()).setField(FacilityUseCasePropertyMapping.USECASE_ID).setOp(Operator.Search.ANY.toString()).setValues(Collections.singletonList(useCaseId));
    SearchCriteria propertyTypeCriteria = (new SearchCriteria()).setField(FacilityUseCasePropertyMapping.TYPE).setOp(SearchOperator.EQ.toString()).setValues(Collections.singletonList(type.toString()));
    SearchCriteria propertyArchivedCriteria = (new SearchCriteria()).setField(FacilityUseCasePropertyMapping.PROPERTY_ARCHIVED).setOp(SearchOperator.EQ.toString()).setValues(Collections.singletonList(archived));
    Specification<FacilityUseCasePropertyMapping> specification = SpecificationBuilder.createSpecification("", Arrays.asList(facilitySearchCriteria, useCaseIdSearchCriteria, propertyTypeCriteria, propertyArchivedCriteria));
    List<FacilityUseCasePropertyMapping> facilityUseCasePropertyMappings = facilityUseCasePropertyMappingRepository.findAll(specification);
    // TODO Rather than sorting here, move this into the query
    List<Property> properties = facilityUseCasePropertyMappings.stream().sorted(Comparator.comparing(FacilityUseCasePropertyMapping::getOrderTree))
      .map(FacilityUseCasePropertyMapping::getProperty)
      .toList();
    List<PropertyDto> propertyDtos = new ArrayList<>(facilityUseCasePropertyMappings.size());
    Map<Long, FacilityUseCasePropertyMapping> propertyIdFacilityUseCasePropertyMappingMap = new HashMap<>();
    for (FacilityUseCasePropertyMapping facilityUseCasePropertyMapping : facilityUseCasePropertyMappings) {
      if (!propertyIdFacilityUseCasePropertyMappingMap.containsKey(facilityUseCasePropertyMapping.getPropertiesId())) {
        propertyIdFacilityUseCasePropertyMappingMap.put(facilityUseCasePropertyMapping.getPropertiesId(), facilityUseCasePropertyMapping);
      }
    }
    for (Property property : properties) {
      FacilityUseCasePropertyMapping facilityUseCasePropertyMapping = propertyIdFacilityUseCasePropertyMappingMap.get(property.getId());
      PropertyDto propertyDto = new PropertyDto();
      propertyDto.setId(property.getIdAsString());
      propertyDto.setName(property.getName());
      propertyDto.setLabel(facilityUseCasePropertyMapping.getLabelAlias());
      propertyDto.setPlaceHolder(facilityUseCasePropertyMapping.getPlaceHolderAlias());
      propertyDto.setMandatory(facilityUseCasePropertyMapping.isMandatory());
      propertyDtos.add(propertyDto);
    }
    return propertyDtos.stream().distinct().toList();
  }

  @Override
  public List<FacilityUseCasePropertyMapping> getPropertiesByFacilityIdAndUseCaseIdAndPropertyType(Long facilityId, Long useCaseId, Type.PropertyType propertyType) {
    SearchCriteria facilitySearchCriteria = (new SearchCriteria()).setField(FacilityUseCasePropertyMapping.FACILITY_ID).setOp(Operator.Search.EQ.toString()).setValues(Collections.singletonList(facilityId));
    SearchCriteria useCaseIdSearchCriteria = (new SearchCriteria()).setField(FacilityUseCasePropertyMapping.USECASE_ID).setOp(Operator.Search.ANY.toString()).setValues(Collections.singletonList(useCaseId));
    SearchCriteria propertyTypeCriteria = (new SearchCriteria()).setField(FacilityUseCasePropertyMapping.TYPE).setOp(SearchOperator.EQ.toString()).setValues(Collections.singletonList(propertyType.toString()));
    SearchCriteria archivedCriteria = (new SearchCriteria()).setField(FacilityUseCasePropertyMapping.PROPERTY_ARCHIVED).setOp(SearchOperator.EQ.toString()).setValues(Collections.singletonList(false));
    Specification<FacilityUseCasePropertyMapping> specification = SpecificationBuilder.createSpecification("", Arrays.asList(facilitySearchCriteria, useCaseIdSearchCriteria, propertyTypeCriteria, archivedCriteria));
    return facilityUseCasePropertyMappingRepository.findAll(specification);
  }
}
