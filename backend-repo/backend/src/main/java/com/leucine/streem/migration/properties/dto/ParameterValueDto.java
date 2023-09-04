package com.leucine.streem.migration.properties.dto;

import lombok.Data;

@Data
public class ParameterValueDto {
  private Long id;
  private String value;
  private String reason;
  private String state;
  private String choices;
  private Long jobId;
  private Long parameterId;
  private Long createdAt;
  private Long modifiedAt;
  private Long modifiedBy;
  private Long createdBy;
  private boolean hidden;
}
