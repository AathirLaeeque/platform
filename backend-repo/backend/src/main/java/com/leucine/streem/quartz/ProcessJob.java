package com.leucine.streem.quartz;

import com.leucine.streem.model.Scheduler;
import com.leucine.streem.repository.ISchedulerRepository;
import com.leucine.streem.service.ICreateJobService;
import com.leucine.streem.util.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// TODO make this generic, include a factory to call schedulers by its JOB_TYPE maybe ?
@Component
@AllArgsConstructor
@Slf4j
public class ProcessJob extends QuartzJobBean {

  private final ISchedulerRepository schedulerRepository;
  private final ICreateJobService createJobService;

  @Override
  protected void executeInternal(JobExecutionContext context) {
    try {
      log.info("[executeInternal] scheduler trigger request, context: {}, executedAt: {} ", context, LocalDateTime.now());

      JobDetail jobDetail = context.getJobDetail();
      String jobKey = jobDetail.getKey().getName();

      Scheduler scheduler = schedulerRepository.findById(Long.valueOf(jobKey)).get();

      createJobService.createScheduledJob(scheduler.getId(), DateTimeUtils.now());

    } catch (Exception ex) {
      //TODO retry ?
      log.error("[executeInternal] scheduler trigger request error", ex);
    }
  }

}
