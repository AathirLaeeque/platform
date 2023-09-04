package com.leucine.streem.quartz;

import com.leucine.streem.util.RecurrenceRuleUtils;
import net.fortuna.ical4j.model.Recur;
import org.quartz.CronTrigger;
import org.quartz.ScheduleBuilder;
import org.quartz.spi.MutableTrigger;

public class RecurrenceRuleScheduleBuilder extends ScheduleBuilder<RecurrenceRuleTrigger> {

  private final Recur recurrenceRule;
  private final String recurrenceRuleExpression;

  private final Long startDateTime;

  private int misfireInstruction = RecurrenceRuleTrigger.MISFIRE_INSTRUCTION_SMART_POLICY;

  protected RecurrenceRuleScheduleBuilder(final Recur rrule, final String recurrenceRuleExpression, final Long startDateTime) {
    if (rrule == null) {
      throw new NullPointerException("recurrence rule cannot be null");
    }
    this.startDateTime = startDateTime;
    this.recurrenceRule = rrule;
    this.recurrenceRuleExpression = recurrenceRuleExpression;
  }

  @Override
  protected MutableTrigger build () {
    RecurrenceRuleTriggerImpl recurrenceRuleTrigger = new RecurrenceRuleTriggerImpl();

    recurrenceRuleTrigger.setRecurrence(recurrenceRule);
    recurrenceRuleTrigger.setRecurrenceRuleExpression(recurrenceRuleExpression);
    recurrenceRuleTrigger.setStartDateTime(startDateTime);
    recurrenceRuleTrigger.setMisfireInstruction(misfireInstruction);

    return recurrenceRuleTrigger;
  }

  public static RecurrenceRuleScheduleBuilder recurrenceRuleSchedule (final String recurrenceRuleExpression, final Long startDateTime) {
    return recurrenceRuleSchedule(RecurrenceRuleUtils.parseRecurrenceRuleExpression(recurrenceRuleExpression), recurrenceRuleExpression, startDateTime);
  }

  public static RecurrenceRuleScheduleBuilder recurrenceRuleSchedule (final Recur rrule, final String recurrenceRuleExpression, final Long startDateTime) {
    return new RecurrenceRuleScheduleBuilder(rrule, recurrenceRuleExpression, startDateTime);
  }


  public RecurrenceRuleScheduleBuilder withMisfireHandlingInstructionDoNothing () {
    misfireInstruction = CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING;
    return this;
  }

  public RecurrenceRuleScheduleBuilder withMisfireHandlingInstructionFireAndProceed () {
    misfireInstruction = CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW;
    return this;
  }

}
