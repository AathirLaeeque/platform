package com.leucine.streem.service;

import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.request.QuartzSchedulerRequest;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.Scheduler;

import java.util.Date;

public interface IQuartzService {
  Date scheduleJob(QuartzSchedulerRequest quartzSchedulerRequest, Scheduler scheduler) throws StreemException;

  boolean stopScheduler(String identity, Type.ScheduledJobGroup jobGroup) throws StreemException;
}
