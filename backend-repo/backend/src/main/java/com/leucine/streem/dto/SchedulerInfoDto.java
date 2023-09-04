package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerInfoDto implements Serializable {
  private static final long serialVersionUID = 8783398471296039607L;
  private Long checklistId;
  private String checklistName;
  private List<VersionDto> versions;
  private AuditDto audit;
}
