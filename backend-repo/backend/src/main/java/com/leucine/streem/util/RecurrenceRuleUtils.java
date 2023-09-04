package com.leucine.streem.util;

import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.parameter.Value;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public final class RecurrenceRuleUtils {

  public static Recur parseRecurrenceRuleExpression(String recurrenceRuleExpression) {
    String recurrenceRule = recurrenceRuleExpression.substring(recurrenceRuleExpression.indexOf("RRULE:") + 6);
    try {
      return new Recur(recurrenceRule);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String generateRecurrenceRuleExpression(Recur recur) {
    return recur.toString();
  }

  public static Date getNextEventAfter(Recur recur, String recurrenceRuleExpression, Date afterTime) {
    String freqValue = extractParameterValue(recurrenceRuleExpression, "FREQ");

    String dateString = recurrenceRuleExpression.substring(recurrenceRuleExpression.indexOf(':') + 1, recurrenceRuleExpression.indexOf('Z') + 1);
    DateTime dateTime = convertToDateTime(dateString);

    java.util.Calendar today = java.util.Calendar.getInstance();
    today.setTime(afterTime);
    java.util.Calendar future = java.util.Calendar.getInstance();
    if (freqValue.equals("MONTHLY")) {
      future.add(Calendar.MONTH, 10);
    } else if (freqValue.equals("YEARLY")) {
      future.add(Calendar.YEAR, 10);
    } else if (freqValue.equals("DAILY")) {
      future.add(Calendar.DAY_OF_YEAR, 10);
    } else if (freqValue.equals("WEEKLY")) {
      future.add(Calendar.WEEK_OF_YEAR, 10);
    } else if (freqValue.equals("HOURLY")) {
      future.add(Calendar.HOUR, 10);
    } else {
      // TODO unsupported exception
    }

    DateTime periodEnd = new DateTime(future.getTime());
    DateList recurDates = recur.getDates(dateTime, periodEnd, Value.DATE_TIME);
    int i = 0;
    for (Object date : recurDates) {
      i++;
      if (((Date)date).after(afterTime)) {
        return (Date)date;
      }
    }
    return null;
  }

  private static DateTime convertToDateTime(String dateString) {
    try {
      return new DateTime(dateString);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private static String extractParameterValue(String recurrenceRule, String parameterName) {
    String patternString = parameterName + "=([^;]+)";
    Pattern pattern = Pattern.compile(patternString);
    Matcher matcher = pattern.matcher(recurrenceRule);

    if (matcher.find()) {
      return matcher.group(1);
    } else {
      return null;
    }
  }
}
