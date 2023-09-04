package com.leucine.streem.dto.projection;

public interface TaskExecutionAssigneeDetailsView {
  String getId();
  String getFirstName();
  String getLastName();
  String getEmployeeId();
  boolean getCompletelyAssigned();
  int getAssignedTasks();
  int getSignedOffTasks();
  int getPendingSignOffs();
}
