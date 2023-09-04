package com.leucine.streem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterStateChangeRequest {
  private Long jobId;
  private Long parameterId;
  private String reason;
}
