package com.leucine.streem.dto.projection;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface JobAssigneeView {
  @JsonIgnore
  String getJobId();
  String getId();
  String getFirstName();
  String getLastName();
  String getEmployeeId();
}
