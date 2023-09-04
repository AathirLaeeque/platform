package com.leucine.streem.email.exception;

public class EmailException extends Exception {
  private static final long serialVersionUID = 4147342737723189154L;

  public EmailException(Throwable cause) {
    super(cause);
  }

  public EmailException(String message) {
    super(message);
  }

  public EmailException(String message, Throwable cause) {
    super(message, cause);
  }
}
