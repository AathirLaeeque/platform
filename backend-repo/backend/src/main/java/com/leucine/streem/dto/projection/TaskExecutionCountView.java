package com.leucine.streem.dto.projection;

public interface TaskExecutionCountView {
  String getJobId();
  Long getTotalTasks();
  Long getCompletedTasks();
}
