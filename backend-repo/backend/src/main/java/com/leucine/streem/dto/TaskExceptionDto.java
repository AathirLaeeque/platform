package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskExceptionDto implements Serializable {
  private static final long serialVersionUID = -5553705029599670819L;

  private String type;
  private String remark;
  private TaskTimerDto timer;
  private ParameterDeviationDto parameterDeviation;
  private UserAuditDto initiator;
}
