package com.leucine.streem.exception;

import com.leucine.streem.dto.response.Error;

import java.util.List;

public class MultiStatusException extends Exception {
  private List<Error> errorList;

  public MultiStatusException(final String message) {
    super(message);
  }

  public MultiStatusException(final String message, final List<Error> errors) {
    super(message);
    this.errorList = errors;
  }

  public List<Error> getErrorList() {
    return errorList;
  }
}
