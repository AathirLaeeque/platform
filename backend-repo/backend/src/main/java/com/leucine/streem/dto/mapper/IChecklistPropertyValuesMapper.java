package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.PropertyValueDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.ChecklistPropertyValue;
import com.leucine.streem.model.FacilityUseCasePropertyMapping;
import com.leucine.streem.model.Property;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.*;

@Mapper
public interface IChecklistPropertyValuesMapper extends IBaseMapper<PropertyValueDto, ChecklistPropertyValue> {

  @Named("toPropertyValueList")
  static List<PropertyValueDto> toPropertyValueList(Set<ChecklistPropertyValue> propertyValues) {
    List<PropertyValueDto> propertyValueDtos = new ArrayList<>(propertyValues.size());
    List<FacilityUseCasePropertyMapping> facilityUseCasePropertyMappings = new LinkedList<>();
    Map<Long, String> propertyValueMap = new HashMap<>(propertyValues.size());
    // Cannot use Lambda or stream because of null values
    for (ChecklistPropertyValue checklistPropertyValue : propertyValues) {
      FacilityUseCasePropertyMapping facilityUseCasePropertyMapping = checklistPropertyValue.getFacilityUseCasePropertyMapping();
      facilityUseCasePropertyMappings.add(facilityUseCasePropertyMapping);
      propertyValueMap.put(facilityUseCasePropertyMapping.getPropertiesId(), checklistPropertyValue.getValue());
    }
    facilityUseCasePropertyMappings.sort(Comparator.comparing(FacilityUseCasePropertyMapping::getOrderTree));
    for (FacilityUseCasePropertyMapping facilityUseCasePropertyMapping : facilityUseCasePropertyMappings) {
      Property property = facilityUseCasePropertyMapping.getProperty();
      PropertyValueDto propertyValueDto = new PropertyValueDto();
      propertyValueDto.setId(property.getIdAsString());
      propertyValueDto.setName(property.getName());
      propertyValueDto.setLabel(facilityUseCasePropertyMapping.getLabelAlias());
      propertyValueDto.setValue(propertyValueMap.get(property.getId()));
      propertyValueDtos.add(propertyValueDto);
    }
    return propertyValueDtos;
  }

  @Mapping(source = "facilityUseCasePropertyMapping.property.id", target = "id")
  @Mapping(source = "facilityUseCasePropertyMapping.property.name", target = "name")
  @Mapping(source = "facilityUseCasePropertyMapping.labelAlias", target = "label")
  PropertyValueDto toDto(ChecklistPropertyValue checklistPropertyValue);

}
