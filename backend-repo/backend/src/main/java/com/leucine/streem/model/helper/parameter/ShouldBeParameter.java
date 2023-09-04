package com.leucine.streem.model.helper.parameter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShouldBeParameter extends ValueParameterBase {
  private String uom;
  //TODO: This is not enum because it can be empty, handle with default case if possible
  private String operator;
  private String type;
  private String lowerValue;
  private String upperValue;
  private String value;
}
