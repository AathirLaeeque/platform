package com.leucine.streem.dto.projection;

public interface TaskExecutionAssigneeView {
  String getId();
  String getFirstName();
  String getLastName();
  String getEmployeeId();
  boolean getCompletelyAssigned();
  int getAssignedTasks();
}
