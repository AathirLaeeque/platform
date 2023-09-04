package com.leucine.streem.dto.projection;

public interface JobLogTaskExecutionView {
  Long getTaskId();
  Long getStartedAt();

  Long getEndedAt();

  String getTaskStartedByFirstName();

  String getTaskStartedByLastName();

  String getTaskModifiedByFirstName();

  String getTaskModifiedByLastName();
  String getName();
  Long getJobId();
}
