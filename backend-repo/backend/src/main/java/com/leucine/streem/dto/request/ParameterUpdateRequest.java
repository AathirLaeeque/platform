package com.leucine.streem.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.constant.Type;
import lombok.Data;

@Data
public class ParameterUpdateRequest {
  private int orderTree;
  private boolean isMandatory;
  private String label;
  private String description;
  private JsonNode data;
  private JsonNode validations;
  private boolean isAutoInitialized;
  private JsonNode autoInitialize;
  private JsonNode rules;
  private Type.VerificationType verificationType;
}
