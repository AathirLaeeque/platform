package com.leucine.streem.exception;

public class AlreadyExistsException extends Exception {
  private static final long serialVersionUID = -6550600628591964144L;

  public AlreadyExistsException(final String message) {
    super(message);
  }
}
