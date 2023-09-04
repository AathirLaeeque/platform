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
public class AutomationDto implements Serializable {
  private static final long serialVersionUID = 1904098881393450409L;
  private String id;
  private Type.AutomationType type;
  private Type.AutomationActionType actionType;
  private JsonNode actionDetails;
  private Type.AutomationTriggerType triggerType;
  private Type.TargetEntityType targetEntityType;
  private JsonNode triggerDetails;
  private Integer orderTree;
  private String displayName;
}
