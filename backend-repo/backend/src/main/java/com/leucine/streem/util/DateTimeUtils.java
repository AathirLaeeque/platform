package com.leucine.streem.util;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public final class DateTimeUtils {

  public static final String DEFAULT_DATE_FORMAT = "MMM dd, yyyy";
  public static final String DEFAULT_DATE_TIME_FORMAT = "MMM dd, yyyy, HH:mm";
  private static final String ZONE_OFFSET = "+00:00";
  private static final String WORKBOOK_DATE_FORMAT = "m/d/yy h:mm";

  private DateTimeUtils() {
    throw new IllegalStateException("DateTime class");
  }

  public static LocalDateTime getLocalDataTime() {
    return LocalDateTime.now();
  }

  public static LocalDateTime getLocalDateTime(@NotNull long epochSecond) {
    return LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.UTC);
  }

  public static LocalDateTime getLocalDateTime(@NotNull long epochSecond, @NotNull ZoneOffset zoneOffset) {
    return LocalDateTime.ofEpochSecond(epochSecond, 0, zoneOffset);
  }

  public static String getFormattedDate(LocalDate localDate, String pattern) {
    return localDate.format(DateTimeFormatter.ofPattern(pattern)).toUpperCase();
  }

  public static String getFormattedDate(Long date) {
    ZoneOffset zoneOffSet = ZoneOffset.of(ZONE_OFFSET);
    LocalDate localDate = DateTimeUtils.getLocalDateTime(date, zoneOffSet).toLocalDate();
    return localDate.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)).toUpperCase();
  }

  public static String getFormattedDateTime(Long date) {
    ZoneOffset zoneOffSet = ZoneOffset.of(ZONE_OFFSET);
    LocalDateTime localDateTime = DateTimeUtils.getLocalDateTime(date, zoneOffSet);
    return localDateTime.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)).toUpperCase();
  }

  public static long now() {
    return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
  }

  public static long getEpochTime(LocalDateTime localDateTime) {
    return localDateTime.toEpochSecond(ZoneOffset.UTC);
  }

  public static long getEpochTime(Date date) {
    return date.getTime() / 1000; // getTime returns value in milliseconds
  }

  public static String getNumericMonth() {
    return LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
  }

  public static String getYear() {
    return String.valueOf(LocalDate.now().getYear());
  }

  public static long getEpochSecondsDifference(long timestamp) {
    return now() - timestamp;
  }

  public static String getWorkbookDateFormat() {
    return WORKBOOK_DATE_FORMAT;
  }

  public static Date getDateFromEpoch(Long epochSecond) {
    LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.UTC);
    return Date.from(localDateTime.atZone(ZoneId.of("UTC")).toInstant());
  }

  public static boolean isDateInPast(long compareWithDate) {
    LocalDateTime currentDateTime = getLocalDataTime();
    LocalDateTime compareWithDateLocalDateTime = DateTimeUtils.getLocalDateTime(compareWithDate);

    return compareWithDateLocalDateTime.isBefore(currentDateTime);
  }

  // TODO refactor
  public static Date getTheRightDate(String recurrenceExpression, Date nextFireTime) {
    // Extract the time from the expression
    String time = recurrenceExpression.substring(recurrenceExpression.indexOf("DTSTART:") + 8, recurrenceExpression.indexOf("Z"));

    // Format the current date as yyyyMMdd
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    String formattedDate = dateFormat.format(nextFireTime);

    // Concatenate the formatted date and extracted time
    String dateTimeString = formattedDate + "T" + time.substring(9);

    // Parse the concatenated date and time
    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    Date modifiedDate = nextFireTime;
    try {
      modifiedDate = dateTimeFormat.parse(dateTimeString);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return modifiedDate;
  }

  public static boolean isDateWithinNext24Hours(long compareWithDate) {
    // Get the current date and time
    LocalDateTime currentDateTime = LocalDateTime.now();

    // Convert the given date to LocalDateTime using the provided getLocalDateTime() function
    LocalDateTime compareWithDateTime = getLocalDateTime(compareWithDate);

    // Calculate the difference in minutes between the two dates
    long minutesDifference = ChronoUnit.MINUTES.between(currentDateTime, compareWithDateTime);

    // Check if the difference is within the next 24 hours
    return minutesDifference >= 0 && minutesDifference < (24 * 60);
  }

  public static boolean isDateAfter(long compareWithDate, long compareToDate) {
    LocalDateTime compareWithDateTime = getLocalDateTime(compareWithDate);
    LocalDateTime compareToDateTime = getLocalDateTime(compareToDate);

    return compareWithDateTime.isAfter(compareToDateTime);
  }
  public static long getEpochFromDate(String date) {
    return LocalDate.parse(date).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()/1000;

  }
}
