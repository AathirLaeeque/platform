package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistRelationValidationDto implements Serializable {
  private static final long serialVersionUID = -6655524185182351334L;

  private List<ChecklistRelationPropertyValidationDto> relationPropertyValidations;
}
