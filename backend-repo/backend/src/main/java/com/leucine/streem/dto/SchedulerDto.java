package com.leucine.streem.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerDto implements Serializable {
  private static final long serialVersionUID = 7228319057126145700L;
  private String id;
  private String code;
  private String name;
  private String description;
  private Long checklistId;
  private String checklistName;
  private Long expectedStartDate;
  private Integer dueDateInterval;
  private JsonNode dueDateDuration;
  private boolean isRepeated = false;
  private String recurrenceRule;
  private boolean isCustomRecurrence;
  private boolean enabled = false;
  private Integer versionNumber;
  private JsonNode data;
  private AuditDto audit;
  private Long deprecatedAt;

}
