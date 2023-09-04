package com.leucine.streem.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.constant.Type;
import com.leucine.streem.model.ParameterVerification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterDto implements Serializable {
  private static final long serialVersionUID = 5441644236668634120L;

  private String id;
  private int orderTree;
  private boolean isMandatory;
  private String type;
  private Type.ParameterTargetEntityType targetEntityType;
  private String label;
  private String description;
  private JsonNode data;
  private JsonNode validations;
  private ParameterValueDto response;
  private boolean isAutoInitialized;
  private JsonNode autoInitialize;
  private JsonNode rules;
  private boolean hidden;
  private Set<String> hide;
  private Set<String> show;
  private Type.VerificationType verificationType;
}
