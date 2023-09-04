package com.leucine.streem.model.helper.parameter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.leucine.streem.dto.ParameterRelationPropertyValidationDto;
import com.leucine.streem.dto.ResourceParameterChoiceDto;
import com.leucine.streem.dto.ResourceParameterFilter;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceParameter extends ValueParameterBase {
  private String objectTypeId;
  private String objectTypeExternalId;
  private String objectTypeDisplayName;
  private List<ParameterRelationPropertyValidationDto> propertyValidations;
  private ResourceParameterFilter propertyFilters;
  private List<ResourceParameterChoiceDto> choices = new ArrayList<>();
  private String urlPath;
  private String collection;
}
