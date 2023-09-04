package com.leucine.streem.dto.request;

import lombok.Data;

@Data
public class UpdateJobRequest {
  private Long expectedStartDate;
  private Long expectedEndDate;
}
