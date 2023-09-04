package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistInfoJobReportDto implements Serializable {
  @Serial
  private static final long serialVersionUID = 595174049964591830L;
  private String id;
  private String name;
  private String code;
  private Integer versionNumber;
  private boolean archived = false;
  private List<PropertyValueDto> properties;
  private boolean isGlobal;
}
