package com.leucine.streem.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDto implements Serializable {
  private static final long serialVersionUID = -4597062794980467226L;

  private String id;
  private int orderTree;
  private String name;
  private Long maxPeriod;
  private Long minPeriod;
  private String timerOperator;
  private boolean hasStop;
  private boolean isTimed;
  private boolean isMandatory;
  private List<ParameterDto> parameters;
  private List<MediaDto> medias;
  private List<AutomationDto> automations;
  private TaskExecutionDto taskExecution;
}
