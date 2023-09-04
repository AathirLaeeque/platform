package com.leucine.streem.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.constant.Type;
import lombok.Data;

@Data
public class ParameterCreateRequest {
  private String id;
  private int orderTree;
  private boolean isMandatory;
  private Type.Parameter type;
  private String label;
  private String description;
  private JsonNode data;
  private JsonNode validations;
  private boolean isAutoInitialized;
  private JsonNode autoInitialize;
  private boolean hidden;
  private JsonNode rules;
  private Type.VerificationType verificationType;
}
