package com.leucine.streem.dto.request;

import com.leucine.streem.constant.Operator;
import lombok.Data;

@Data
public class TimerRequest {
  private Operator.Timer timerOperator;
  private Long maxPeriod;
  private Long minPeriod;
}
