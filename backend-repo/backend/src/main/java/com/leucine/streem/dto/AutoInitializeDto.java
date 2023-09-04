package com.leucine.streem.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AutoInitializeDto {
  private String parameterId;
  private AutoInitializeRelationDto relation;
  private AutoInitializeObjectPropertyDto property;
}
