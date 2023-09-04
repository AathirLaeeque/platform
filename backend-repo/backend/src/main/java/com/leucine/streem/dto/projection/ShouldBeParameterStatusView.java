package com.leucine.streem.dto.projection;

public interface ShouldBeParameterStatusView {
  String getParameterId();

  String getParameterValueId();

  String getJobId();

  String getParameterName();

  String getTaskName();

  String getProcessName();

  long getModifiedAt();

  String getJobCode();

  String getStageId();

  String getTaskId();

  long getCreatedAt();

}
