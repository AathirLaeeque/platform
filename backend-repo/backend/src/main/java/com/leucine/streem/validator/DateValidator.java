package com.leucine.streem.validator;

import com.leucine.streem.constant.CollectionMisc;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class DateValidator implements ConstraintValidator {
  private boolean isValid;
  private final Long value;
  private final String errorMessage;
  private final CollectionMisc.DateUnit dateUnit;
  private final Long difference;
  private final CollectionMisc.PropertyValidationConstraint constraint;

  public DateValidator(Long value, Long difference, String errorMessage, CollectionMisc.DateUnit dateUnit, CollectionMisc.PropertyValidationConstraint constraint) {
    this.value = value;
    this.difference = difference;
    this.errorMessage = errorMessage;
    this.dateUnit = dateUnit;
    this.constraint = constraint;
  }

  @Override
  public void validate(Object value) {
    LocalDateTime compareToDate = new Timestamp(Long.parseLong((String) value) * 1000).toLocalDateTime();
    LocalDateTime compareWithDate = new Timestamp(this.value * 1000).toLocalDateTime();

    Long actualTimeDifference = null;
    switch (dateUnit) {
      case DAYS -> actualTimeDifference = ChronoUnit.DAYS.between(compareWithDate, compareToDate);
      case HOURS -> actualTimeDifference = ChronoUnit.HOURS.between(compareWithDate, compareToDate);
      case WEEKS -> actualTimeDifference = ChronoUnit.WEEKS.between(compareWithDate, compareToDate);
      case YEARS -> actualTimeDifference = ChronoUnit.YEARS.between(compareWithDate, compareToDate);
    }

    switch (this.constraint) {
      case EQ -> this.isValid = Objects.equals(this.difference, actualTimeDifference);
      case LT -> this.isValid = actualTimeDifference < this.difference ;
      case GT -> this.isValid = actualTimeDifference > this.difference ;
      case LTE -> this.isValid = actualTimeDifference <= this.difference;
      case GTE -> this.isValid = actualTimeDifference >= this.difference;
    }
  }

  @Override
  public boolean isValid() {
    return this.isValid;
  }

  @Override
  public String getErrorMessage() {
    return this.errorMessage;
  }
}
