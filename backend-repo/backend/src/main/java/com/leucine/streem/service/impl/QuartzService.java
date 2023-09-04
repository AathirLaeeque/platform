package com.leucine.streem.service.impl;

import com.leucine.streem.constant.ErrorMessage;
import com.leucine.streem.constant.SchedulerMisc;
import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.request.QuartzSchedulerRequest;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.Scheduler;
import com.leucine.streem.quartz.ProcessJob;
import com.leucine.streem.quartz.RecurrenceRuleScheduleBuilder;
import com.leucine.streem.service.IQuartzService;
import com.leucine.streem.util.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@RequiredArgsConstructor
@Service
@Slf4j
public class QuartzService implements IQuartzService {
  private final org.quartz.Scheduler scheduler;

  /**
   * Build a job with identity, jobType and jobGroup
   * @param quartzSchedulerRequest
   * @param scheduler
   * @return
   * @throws StreemException
   */
  @Override
  public Date scheduleJob(QuartzSchedulerRequest quartzSchedulerRequest, Scheduler scheduler) throws StreemException {
    log.info("[scheduleJob] Request to create a new scheduler, createSchedulerRequest: {}, scheduler: {}", quartzSchedulerRequest, scheduler);
    try {
      JobDetail jobDetail = buildJob(quartzSchedulerRequest.getIdentity(), quartzSchedulerRequest.getScheduledJobType(), quartzSchedulerRequest.getJobGroup());
      Trigger buildTrigger = buildTrigger(scheduler.getExpectedStartDate(), jobDetail, quartzSchedulerRequest.getJobGroup(),
        quartzSchedulerRequest.getRecurrence());
      return this.scheduler.scheduleJob(jobDetail, buildTrigger);
    } catch (Exception ex) {
      log.error(ErrorMessage.ERROR_CREATING_A_SCHEDULER, ex);
      throw new StreemException(ErrorMessage.ERROR_CREATING_A_SCHEDULER, ex);
    }
  }

  /**
   * Stop a scheduler by identity and jobGroup
   * @param identity
   * @param jobGroup
   * @return
   * @throws StreemException
   */
  @Override
  public boolean stopScheduler(String identity, Type.ScheduledJobGroup jobGroup) throws StreemException {
    log.info("[scheduleJob] Request to stop a scheduler with identity: {}, jobGroup: {}", identity, jobGroup);
    try {
      JobKey jobKey = JobKey.jobKey(identity, jobGroup.name());
      return scheduler.deleteJob(jobKey);
    } catch (Exception ex) {
      log.error(ErrorMessage.ERROR_STOPPING_A_SCHEDULER, ex);
      throw new StreemException(ErrorMessage.ERROR_STOPPING_A_SCHEDULER, ex);
    }
  }

  private JobDetail buildJob(String identity, Type.ScheduledJobType jobType, Type.ScheduledJobGroup jobGroup) {
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put(SchedulerMisc.JOB_TYPE, jobType.name());
    return JobBuilder.newJob(ProcessJob.class).withIdentity(identity, jobGroup.name()).usingJobData(jobDataMap).storeDurably().build();
  }

  private Trigger buildTrigger(Long startAt, JobDetail jobDetail, Type.ScheduledJobGroup jobGroup, String recurrencePattern) {
    LocalDateTime oneDayBefore = DateTimeUtils.getLocalDateTime(startAt).minusDays(1);
    long oneDayBeforeInEpoch = DateTimeUtils.getEpochTime(oneDayBefore);
    return TriggerBuilder.newTrigger()
      .forJob(jobDetail)
      .withIdentity(jobDetail.getKey().getName(), jobGroup.name())
      .withSchedule(
        RecurrenceRuleScheduleBuilder
          .recurrenceRuleSchedule(recurrencePattern, oneDayBeforeInEpoch)
          .withMisfireHandlingInstructionDoNothing()
      ).build();
  }

}
