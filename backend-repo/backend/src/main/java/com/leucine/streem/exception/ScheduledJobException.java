package com.leucine.streem.exception;

import com.leucine.streem.dto.response.Error;
import java.util.List;

public class ScheduledJobException extends RuntimeException {
  private List<Error> errorList;

  public ScheduledJobException(final String message) {
    super(message);
  }

  public ScheduledJobException(final String message, final List<Error> errors) {
    super(message);
    this.errorList = errors;
  }

  public ScheduledJobException(final String message, Throwable throwable) {
    super(message, throwable);
  }

  public List<Error> getErrorList() {
    return errorList;
  }
}
