package com.leucine.streem.dto.request;

import lombok.Data;

@Data
public class TaskSignOffRequest {
  private Long jobId;
  private String token;
}
