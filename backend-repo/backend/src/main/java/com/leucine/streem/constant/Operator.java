package com.leucine.streem.constant;

public final class Operator {

  public enum Parameter {
    EQUAL_TO,
    LESS_THAN,
    LESS_THAN_EQUAL_TO,
    MORE_THAN,
    MORE_THAN_EQUAL_TO,
    BETWEEN,
  }

  public enum Search {
    AND,
    ANY,
    EQ,
    GT,
    GOE,
    LT,
    LOE,
    LIKE,
    NE,
    OR;
  }

  public enum Timer {
    NOT_LESS_THAN,
    LESS_THAN
  }

}
