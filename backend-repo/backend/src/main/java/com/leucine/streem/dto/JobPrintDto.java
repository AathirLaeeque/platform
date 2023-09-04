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
public class JobPrintDto implements Serializable {
  @Serial
  private static final long serialVersionUID = 1305472892372711136L;

  private String id;
  private String code;
  private State.Job state;
  private Long createdAt;
  private UserAuditDto createdBy;
  private Long startedAt;
  private UserAuditDto startedBy;
  private Long endedAt;
  private UserAuditDto endedBy;
  private Long totalDuration;
  private Long totalStages;
  private Long totalTask;
  private List<AssigneeSignOffDto> assignees;
  private JobCweDto cweDetails;
  private ChecklistJobDto checklist;
  private List<PropertyValueDto> properties;
  private List<RelationValueDto> relations;
  private List<ParameterDto> parameterValues;
  private SchedulerPartialDto scheduler;
  private Long expectedStartDate;
  private Long expectedEndDate;
}
