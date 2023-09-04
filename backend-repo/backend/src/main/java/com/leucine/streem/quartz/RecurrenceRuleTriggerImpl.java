package com.leucine.streem.quartz;

import com.leucine.streem.util.DateTimeUtils;
import com.leucine.streem.util.RecurrenceRuleUtils;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.model.Recur;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.triggers.AbstractTrigger;
import org.quartz.impl.triggers.CoreTrigger;

import java.io.Serial;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * Overriden methods sets following columns in the qrtz_triggers entity
 * start_time is the first time the trigger will fire
 * next_fire_time is the next time the trigger will fire
 * prev_fire_time is the last time the trigger fired
 * end_time is the last time the trigger will fire
 */
public class RecurrenceRuleTriggerImpl extends AbstractTrigger<RecurrenceRuleTrigger> implements RecurrenceRuleTrigger, CoreTrigger {

  @Serial
  private static final long serialVersionUID = 1233337060012692693L;

  private static final int YEAR_TO_GIVEUP_SCHEDULING_AT = Calendar.getInstance().get(Calendar.YEAR) + 100;

  private String recurrenceRuleExpression = null;
  private transient Recur recurrence = null;
  private Long startDateTime; // In epoch seconds
  private Date nextFireTime = null;
  private Date previousFireTime = null;

  private Date fromDate = null;

  public RecurrenceRuleTriggerImpl() {
    super();
  }

  @Override
  public Object clone () {
    RecurrenceRuleTriggerImpl copy = (RecurrenceRuleTriggerImpl) super.clone();
    if (getRecurrenceRuleExpression() != null) {
      copy.setRecurrenceRuleExpression(getRecurrenceRuleExpression());
    }
    return copy;
  }

  @Override
  public Recur getRecurrence() {
    if (this.recurrence == null && StringUtils.isNotBlank(this.recurrenceRuleExpression)) {
      recurrence = RecurrenceRuleUtils.parseRecurrenceRuleExpression(recurrenceRuleExpression);
    }
    return this.recurrence;
  }

  @Override
  public String getRecurrenceRuleExpression() {
    if (StringUtils.isBlank(this.recurrenceRuleExpression) && this.recurrence != null) {
      recurrenceRuleExpression = RecurrenceRuleUtils.generateRecurrenceRuleExpression(recurrence);
    }
    return recurrenceRuleExpression;
  }

  @Override
  public Date getStartTime() {
    return fromDate;
  }

  @Override
  public void setStartTime(Date startTime) {
    if (startTime == null) {
      throw new IllegalArgumentException("Start time cannot be null");
    }

    Date eTime = getEndTime();
    if (eTime != null && eTime.before(startTime)) {
      throw new IllegalArgumentException("End time cannot be before start time");
    }

    // round off millisecond...
    // Note timeZone is not needed here as parameter for
    // Calendar.getInstance(),
    // since time zone is implicit when using a Date in the setTime method.
    Calendar cl = Calendar.getInstance();
    cl.setTime(startTime);
    cl.set(Calendar.MILLISECOND, 0);

    this.fromDate = cl.getTime();
  }

  @Override
  public Date getEndTime() {
    if (getRecurrence() == null) {
      return null;
    } else {
      return getRecurrence().getUntil();
    }
  }

  @Override
  public void setEndTime(Date endTime) {
    Date sTime = getStartTime();
    if (sTime != null && endTime != null && sTime.after(endTime)) {
      throw new IllegalArgumentException("End time cannot be before start time");
    }

    // N/A currently we don't set an end time for the recurrence rule
  }

  @Override
  public Date getNextFireTime() {
    return this.nextFireTime;
  }

  @Override
  public Date getPreviousFireTime() {
    return this.previousFireTime;
  }

  @Override
  public void setNextFireTime(Date fireTime) {
    this.nextFireTime = fireTime;
  }

  @Override
  public void setPreviousFireTime(Date fireTime) {
    this.previousFireTime = fireTime;
  }

  @Override
  public Date getFireTimeAfter(Date afterTime) {
    Date after;
    if (afterTime == null) {
      after = new Date();
    } else {
      after = (Date) afterTime.clone();
    }

    if (getEndTime() != null && (after.compareTo(getEndTime()) >= 0)) {
      return null;
    }
    Recur recurrence = getRecurrence();
    String recurrenceRuleExpression = getRecurrenceRuleExpression();
    Date nextEventAfter = RecurrenceRuleUtils.getNextEventAfter(recurrence, recurrenceRuleExpression, after);

    // We want the scheduler to trigger a day before the event
    Date dayMinusOne = null;
    if (null != nextEventAfter) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(nextEventAfter);
      calendar.add(Calendar.DAY_OF_MONTH, -1);
      dayMinusOne = calendar.getTime();
    }

    if (getEndTime() != null && dayMinusOne != null && nextEventAfter.after(getEndTime())) {
      return null;
    }

    return nextEventAfter;
  }

  @Override
  public Date getFinalFireTime() {
    return getRecurrence().getUntil();
  }

  @Override
  public boolean mayFireAgain() {
    return getNextFireTime() != null;
  }

  @Override
  protected boolean validateMisfireInstruction(int misfireInstruction) {
    if (misfireInstruction < MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY) {
      return false;
    }

    return misfireInstruction <= MISFIRE_INSTRUCTION_DO_NOTHING;
  }

  @Override
  public void updateAfterMisfire(org.quartz.Calendar cal) {
    int instr = getMisfireInstruction();

    if (instr == Trigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY) {
      return;
    }

    if (instr == MISFIRE_INSTRUCTION_SMART_POLICY) {
      instr = MISFIRE_INSTRUCTION_FIRE_ONCE_NOW;
    }

    if (instr == MISFIRE_INSTRUCTION_DO_NOTHING) {
      Date newFireTime = getFireTimeAfter(new Date());

      // TODO code until setNextFireTime will not be required
      // For some reason misfired jobs are getting created so we are moving to future date with this logic
      // TODO: This is a quick hack and needs to be fixed
      // Also create scheduler and create next immediate job is running in two separate transactions
      // either think of locking on scheduler (worst case) or fix misfires, try daily and weekly
      while (DateTimeUtils.isDateInPast(DateTimeUtils.getEpochTime(newFireTime))) {
        newFireTime = getFireTimeAfter(newFireTime);
      }

      // We want the scheduler to trigger a day before the event
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(newFireTime);
      calendar.add(Calendar.DAY_OF_MONTH, -1);
      newFireTime = calendar.getTime();

      setNextFireTime(newFireTime);
    } else if (instr == MISFIRE_INSTRUCTION_FIRE_ONCE_NOW) {
      // this is okay because misfire ones are triggered separtely from normal ones.
      // we set current time so it starts behaving normally again,
      // but the next day will be calculated from now so if there are misfires since long in terms of days only one fire will be triggered
      setNextFireTime(new Date());
    }
  }

  @Override
  public void triggered(org.quartz.Calendar calendar) {
    // if the system is down for 4 days, and it was supposed to fire every day
    // after the 4 days it will go back to normal state to fire from next day onwards
    previousFireTime = nextFireTime;
    nextFireTime = getFireTimeAfter(nextFireTime);

    while (nextFireTime != null && calendar != null && !calendar.isTimeIncluded(nextFireTime.getTime())) {
      nextFireTime = getFireTimeAfter(nextFireTime);
    }
  }

  @Override
  public void updateWithNewCalendar(org.quartz.Calendar calendar, long misfireThreshold) {
    nextFireTime = getFireTimeAfter(previousFireTime);

    if (nextFireTime == null || calendar == null) {
      return;
    }

    LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
    ZonedDateTime utcDateTime = tomorrow.atZone(ZoneId.of("UTC"));
    Date now = Date.from(utcDateTime.toInstant());

    while (nextFireTime != null && !calendar.isTimeIncluded(nextFireTime.getTime())) {

      nextFireTime = getFireTimeAfter(nextFireTime);

      if (nextFireTime == null) {
        break;
      }

      // avoid infinite loop
      // Use gregorian only because the constant is based on Gregorian
      Calendar c = new java.util.GregorianCalendar();
      c.setTime(nextFireTime);
      if (c.get(Calendar.YEAR) > YEAR_TO_GIVEUP_SCHEDULING_AT) {
        nextFireTime = null;
      }

      if (nextFireTime != null && nextFireTime.before(now)) {
        long diff = now.getTime() - nextFireTime.getTime();
        if (diff >= misfireThreshold) {
          nextFireTime = getFireTimeAfter(nextFireTime);
        }
      }
    }
  }

  @Override
  public Date computeFirstFireTime(org.quartz.Calendar calendar) {
    Date startDate = DateTimeUtils.getDateFromEpoch(this.startDateTime);
    nextFireTime = startDate;
    // Here we get the start time as -1 day because we want the scheduler to run before 24 hours
    // this logic is current use case specific (job schedulers) and should be changed for any other kind of schedulers
    return startDate;
  }

  @Override
  public boolean hasAdditionalProperties() {
    return false;
  }

  @Override
  public ScheduleBuilder<RecurrenceRuleTrigger> getScheduleBuilder() {

    RecurrenceRuleScheduleBuilder rrsb = RecurrenceRuleScheduleBuilder.recurrenceRuleSchedule(getRecurrence(), getRecurrenceRuleExpression(), startDateTime);

    if (MISFIRE_INSTRUCTION_DO_NOTHING == getMisfireInstruction()) {
      rrsb.withMisfireHandlingInstructionDoNothing();
    } else if (MISFIRE_INSTRUCTION_FIRE_ONCE_NOW == getMisfireInstruction()) {
      rrsb.withMisfireHandlingInstructionFireAndProceed();
    }

    return rrsb;
  }

  protected void setRecurrence(Recur rrule) {
    this.recurrence = rrule;
    recurrenceRuleExpression = RecurrenceRuleUtils.generateRecurrenceRuleExpression(recurrence);
  }

  protected void setStartDateTime(long startDateTime) {
    this.startDateTime = startDateTime;
  }

  protected void setRecurrenceRuleExpression(String rruleExpression) {
    this.recurrenceRuleExpression = rruleExpression;
    recurrence = RecurrenceRuleUtils.parseRecurrenceRuleExpression(rruleExpression);
  }

}
