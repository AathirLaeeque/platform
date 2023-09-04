package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StageExecutionReportDto implements Serializable {
  private static final long serialVersionUID = -7763918369576703380L;

  private String stageId;
  private String stageName;
  private int totalTasks;
  private int completedTasks;
  private boolean tasksInProgress;
}
