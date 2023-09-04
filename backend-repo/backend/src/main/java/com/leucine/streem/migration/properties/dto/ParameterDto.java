package com.leucine.streem.migration.properties.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ParameterDto {
  private Long id;
  private String type;
  private String targetEntityType;
  private String label;
  private String description;
  private Integer orderTree;
  private boolean isMandatory;
  private boolean archived;
  private String data;
  private Long checklistId;
  private String validations;
  private boolean isAutoInitialized;
  private String autoInitialize;
  private String rules;
  private Long createdAt;
  private Long modifiedAt;
  private Long createdBy;
  private Long modifiedBy;
}
