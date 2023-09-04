package com.leucine.streem.dto.request;

import lombok.Data;

@Data
public class TaskExecutionRequest {
  private Long jobId;
  private String correctionReason;
  private String reason;
}
