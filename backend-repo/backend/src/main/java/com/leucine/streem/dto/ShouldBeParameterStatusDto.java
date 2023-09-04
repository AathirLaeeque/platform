package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShouldBeParameterStatusDto {
  private String ParameterName;
  private String TaskName;
  private String ProcessName;
  private String jobId;
  private long modifiedAt;

}
