package com.leucine.streem.constant;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class CollectionMisc {

  public static final Set<PropertyType> PROPERTY_DROPDOWN_TYPES = Collections.unmodifiableSet(EnumSet.of(PropertyType.SINGLE_SELECT, PropertyType.MULTI_SELECT));

  public enum UsageStatus {
    ACTIVE(1),
    DRAFT(2),
    BEING_REVIEWED(3),
    READY_FOR_APPROVAL(4),
    REQUESTED_CHANGES(5),
    APPROVED(6),
    DEPRECATED(7),
    DELETED(8);
    private final int value;

    UsageStatus(int value) {
      this.value = value;
    }

    public int get() {
      return value;
    }
  }

  public enum Flag {
    IS_SYSTEM(0),
    IS_PRIMARY(1),
    IS_TITLE(2),
    IS_SEARCHABLE(3),
    IS_MANDATORY(4),
    IS_AUTOGENERATE(5);
    private final int position;

    Flag(int position) {
      this.position = position;
    }

    public int get() {
      return position;
    }
  }

  public enum PropertyType {
    DATE,
    DATE_TIME,
    NUMBER,
    MULTI_LINE,
    MULTI_SELECT,
    SINGLE_LINE,
    SINGLE_SELECT
  }

  public enum PropertyValidationConstraint {
    EQ,
    LT,
    GT,
    LTE,
    GTE,
    NE,
    MIN,
    MAX,
    PATTERN
  }

  public enum Cardinality {
    ONE_TO_ONE,
    ONE_TO_MANY
  }

  public enum RelationType {
    OBJECTS,
    OBJECT_TYPES
  }

  public enum DateUnit {
    HOURS,
    DAYS,
    MINUTES,
    SECONDS,
    WEEKS,
    MONTHS,
    YEARS
  }
  public enum ChangeLogType {
    PROPERTY,
    RELATION
  }
  public enum ChangeLogInputType {
    DATE,
    DATE_TIME,
    NUMBER,
    ONE_TO_ONE,
    ONE_TO_MANY,
    SINGLE_SELECT,
    MULTI_SELECT,
    SINGLE_LINE,
    MULTI_LINE

  }
}
