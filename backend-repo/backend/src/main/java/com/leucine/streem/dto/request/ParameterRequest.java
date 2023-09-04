package com.leucine.streem.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class ParameterRequest {
  private Long id;
  private String label;
  private JsonNode data;
}
