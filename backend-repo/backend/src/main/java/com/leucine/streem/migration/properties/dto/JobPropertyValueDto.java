package com.leucine.streem.migration.properties.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(of = {"fupmId", "jobId"})
@AllArgsConstructor
@NoArgsConstructor
public class JobPropertyValueDto {
  private Long fupmId;
  private String value;
  private Long jobId;
  private Long createdAt;
  private Long createdBy;
  private Long modifiedAt;
  private Long modifiedBy;

  public JobPropertyValueDto(Long fupmId, Long jobId) {
    this.fupmId = fupmId;
    this.jobId = jobId;
  }
}
