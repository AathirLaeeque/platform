package com.leucine.streem.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.constant.Type;
import lombok.Data;

@Data
public class AutomationRequest {
  private String id;
  private Type.AutomationType type;
  private Type.AutomationActionType actionType;
  private Type.TargetEntityType targetEntityType;
  private Type.AutomationTriggerType triggerType;
  private JsonNode actionDetails;
  private JsonNode triggerDetails;
  private Integer orderTree;
  private String displayName;
}
