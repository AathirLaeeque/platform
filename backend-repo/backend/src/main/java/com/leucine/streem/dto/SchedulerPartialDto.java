package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerPartialDto implements Serializable {
  private static final long serialVersionUID = 8783398471296039607L;

  private String id;
  private String code;
  private String name;
  private String recurrenceRule;
  private boolean isCustomRecurrence;
  private String checklistId;
  private String checklistName;
  private boolean archived;
  private AuditDto audit;
  private Long deprecatedAt;
}

