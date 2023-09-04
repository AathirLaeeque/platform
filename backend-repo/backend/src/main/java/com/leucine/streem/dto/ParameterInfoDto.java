package com.leucine.streem.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.constant.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterInfoDto implements Serializable {
  private static final long serialVersionUID = -1748108650842727750L;

  private String id;
  private String stageId;
  private String taskId;
  private String type;
  private JsonNode data; // TODO these two may not be required, separate out to different API
  private JsonNode validations;
  private Type.ParameterTargetEntityType targetEntityType;
  private String label;
  private Integer orderTree;
  private boolean isMandatory;
  private boolean isAutoInitialized;
  private JsonNode autoInitialize;
  private Type.VerificationType verificationType;
  private boolean hidden;
}
