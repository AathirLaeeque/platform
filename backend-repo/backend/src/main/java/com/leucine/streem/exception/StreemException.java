package com.leucine.streem.exception;

import com.leucine.streem.dto.response.Error;
import java.util.List;

public class StreemException extends Exception {
  private List<Error> errorList;

  public StreemException(final String message) {
    super(message);
  }

  public StreemException(final String message, final List<Error> errors) {
    super(message);
    this.errorList = errors;
  }

  public StreemException(final String message, Throwable throwable) {
    super(message, throwable);
  }

  public List<Error> getErrorList() {
    return errorList;
  }
}
