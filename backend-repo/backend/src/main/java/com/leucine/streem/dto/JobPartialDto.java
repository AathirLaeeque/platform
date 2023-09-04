package com.leucine.streem.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.leucine.streem.constant.State;
import com.leucine.streem.dto.projection.JobAssigneeView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobPartialDto implements Serializable {
  private static final long serialVersionUID = -5497775858832748583L;

  private String id;
  private String code;
  //TODO this is workaround in mapper remove this
  private long totalTasks;
  private long completedTasks;
  private State.Job state;
  private ChecklistBasicDto checklist;
  private String schedulerId;
  private SchedulerPartialDto scheduler;
  private Long expectedStartDate;
  private Long expectedEndDate;
  private List<PropertyValueDto> properties;
  private List<ParameterDto> parameterValues;
  private List<JobAssigneeView> assignees;
  private List<RelationValueDto> relations;
  private Long startedAt;
  private Long endedAt;
}
