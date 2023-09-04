package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterRelationValidationDto implements Serializable {
  private static final long serialVersionUID = -4529982386862705157L;

  private List<ResourceParameterPropertyValidationDto> resourceParameterValidations;
  private List<ParameterRelationPropertyValidationDto> relationPropertyValidations;
  private List<CustomRelationPropertyValidationDto> customValidations;
}
