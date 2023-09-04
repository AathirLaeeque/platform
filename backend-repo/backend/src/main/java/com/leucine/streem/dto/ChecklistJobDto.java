package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistJobDto implements Serializable {
  private static final long serialVersionUID = 5263696173758482219L;

  private String id;
  private String name;
  private String code;
  private Integer versionNumber;
  private boolean archived = false;
  private List<StageDto> stages;
  private List<PropertyValueDto> properties;
  private AuditDto audit;
  private boolean isGlobal;
}
