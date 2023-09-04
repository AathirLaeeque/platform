package com.leucine.streem.dto.request;

import lombok.Data;

@Data

public class ParameterExecuteRequest {
  private Long jobId;
  // TODO reason is also going inside parameter.data
  private String reason;
  private ParameterRequest parameter;
}
