package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskTimerDto implements Serializable {
  private static final long serialVersionUID = 813614100960964249L;

  private Long startedAt;
  private Long endedAt;
  private String timerOperator;
  private Long minPeriod;
  private Long maxPeriod;
}
