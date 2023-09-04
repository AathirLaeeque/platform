package com.leucine.streem.quartz;

import net.fortuna.ical4j.model.Recur;
import org.quartz.Trigger;

import java.text.ParseException;

public interface RecurrenceRuleTrigger extends Trigger {
  int MISFIRE_INSTRUCTION_FIRE_ONCE_NOW = 1;

  int MISFIRE_INSTRUCTION_DO_NOTHING = 2;

  String getRecurrenceRuleExpression ();

  Recur getRecurrence() throws ParseException;
}
