package com.leucine.streem.model.helper.parameter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueParameterBase {
  private String input;
}
