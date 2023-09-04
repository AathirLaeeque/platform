package com.leucine.streem.dto;

import com.leucine.streem.constant.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDto implements Serializable {
  private static final long serialVersionUID = 1305472892372611136L;

  private String id;
  private String code;
  private State.Job state;
  //TODO update this, workaround in mapper implemented
  private int totalTasks;
  private long completedTasks;
  private ChecklistJobDto checklist;
  private Long schedulerId;
  private Long expectedStartDate;
  private Long expectedEndDate;
  private List<RelationValueDto> relations;
  private List<ParameterDto> parameterValues;
  private Long startedAt;
  private Long endedAt;
}
