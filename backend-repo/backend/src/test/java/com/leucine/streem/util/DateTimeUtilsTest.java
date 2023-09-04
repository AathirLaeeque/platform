package com.leucine.streem.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DateTimeUtilsTest {

  @Test
  public void test_localDateTime() {
    Long epochSecond = 1320105600L;
    String expectedTime = "2011-11-01T00:00";
    LocalDateTime localDateTime = DateTimeUtils.getLocalDateTime(epochSecond);

    Assert.assertEquals(localDateTime.toString(), expectedTime);
  }

  @Test
  public void test_localDateTimeWithOffset() {
    Long epochSecond = 1320105600L;
    ZoneOffset zoneOffSet = ZoneOffset.of("+02:00");
    String expectedTime = "2011-11-01T02:00";
    LocalDateTime localDateTime = DateTimeUtils.getLocalDateTime(epochSecond, zoneOffSet);

    Assert.assertEquals(localDateTime.toString(), expectedTime);
  }
}
