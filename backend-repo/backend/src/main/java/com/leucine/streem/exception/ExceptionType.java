package com.leucine.streem.exception;

public enum ExceptionType {
  ENTITY_NOT_FOUND("not.found"),
  BAD_REQUEST("bad.request"),
  BAD_CREDENTIALS("bad.credentials"),
  UNAUTHORIZED("unauthorized"),
  SERVICE_ERROR("service.error");

  private final String value;

  ExceptionType(String value) {
    this.value = value;
  }

  public String get() {
    return this.value;
  }
}
