package com.leucine.streem.dto;

import com.leucine.streem.constant.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobReportDto implements Serializable {
  @Serial
  private static final long serialVersionUID = -5932386946533575107L;

  private String id;
  private String code;
  private State.Job state;
  private Long createdAt;
  private UserAuditDto createdBy;
  private Long startedAt;
  private UserAuditDto startedBy;
  private Long endedAt;
  private Long totalStages;
  private Long totalTask;
  private UserAuditDto endedBy;
  @Deprecated
  private UserAuditDto completedBy;
  private int totalAssignees;
  private int totalTaskExceptions;
  private Long totalDuration;
  private Long totalStageDuration;
  private ChecklistInfoJobReportDto checklist;
  private List<StageReportDto> stages;
  private List<AssigneeSignOffDto> assignees;
  private JobCweDto cweDetails;
  private List<PropertyValueDto> properties;
  private List<RelationValueDto> relations;
  private List<ParameterDto> parameterValues;
}
