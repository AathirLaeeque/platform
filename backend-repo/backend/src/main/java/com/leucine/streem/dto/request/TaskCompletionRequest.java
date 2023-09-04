package com.leucine.streem.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class TaskCompletionRequest {
  private Long jobId;
  private List<ParameterCompletionRequest> parameters;
  private String correctionReason;
  private String reason;
  private String automationReason;
}
