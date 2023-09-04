package com.leucine.streem.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CreateProcessSchedulerRequest {
  private Long checklistId;
  private Map<Long, ParameterExecuteRequest> parameterValues = new HashMap<>();

  private String name;
  private String description;
  private Long expectedStartDate;
  private boolean isRepeated;
  private String recurrence;
  private boolean isCustomRecurrence;
  private Integer dueDateInterval;
  private JsonNode dueDateDuration;
}
