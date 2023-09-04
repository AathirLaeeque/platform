package com.leucine.streem.dto.projection;

public interface StageTotalTasksView {
  Long getStageId();
  String getStageName();
  Integer getTotalTasks();
}
