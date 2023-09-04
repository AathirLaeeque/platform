package com.leucine.streem.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterDeviationDto implements Serializable {
  private static final long serialVersionUID = 2259384558678832016L;

  private JsonNode parameter;
  private double userInput;
}
