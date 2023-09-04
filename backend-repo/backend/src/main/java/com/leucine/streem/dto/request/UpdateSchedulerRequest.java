package com.leucine.streem.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class UpdateSchedulerRequest {
  private String name;
  private String description;
  private Long expectedStartDate;
  private boolean isRepeated;
  private String recurrence;
  private boolean isCustomRecurrence;
  private Integer dueDateInterval;
  private JsonNode dueDateDuration;
}
