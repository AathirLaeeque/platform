package com.leucine.streem.constant;

public enum ResponseStatus {
  OK("ok"),
  ERROR("error"),
  SUCCESS("success");

  private final String value;

  ResponseStatus(final String value) {
    this.value = value;
  }

  public String get() {
    return value;
  }
}