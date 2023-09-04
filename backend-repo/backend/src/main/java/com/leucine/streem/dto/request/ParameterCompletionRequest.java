package com.leucine.streem.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.constant.Type;
import lombok.Data;

@Data
public class ParameterCompletionRequest {
  private Long id;
  private JsonNode data;
  private String reason;
  private Type.VerificationType verificationType;
}
