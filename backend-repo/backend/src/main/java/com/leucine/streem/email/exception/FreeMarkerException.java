package com.leucine.streem.email.exception;

public class FreeMarkerException extends Exception {
  private static final long serialVersionUID = -2507732700366913479L;

  public FreeMarkerException(Throwable cause) {
    super(cause);
  }

  public FreeMarkerException(String message) {
    super(message);
  }

  public FreeMarkerException(String message, Throwable cause) {
    super(message, cause);
  }
}
