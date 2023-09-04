package com.leucine.streem.dto.request;

import com.leucine.streem.constant.Type;
import lombok.Data;

@Data
public class QuartzSchedulerRequest {
  private String identity;
  private Type.ScheduledJobType scheduledJobType;
  private Type.ScheduledJobGroup jobGroup;
  private Long expectedStartDate;
  private String recurrence;
}
