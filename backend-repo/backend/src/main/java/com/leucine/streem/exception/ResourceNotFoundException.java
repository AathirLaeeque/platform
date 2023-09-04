package com.leucine.streem.exception;

import com.leucine.streem.dto.response.ErrorCode;

public class ResourceNotFoundException extends Exception {
  private static final long serialVersionUID = 800058926206084436L;
  private final ExceptionType type;
  private final ErrorCode errorCode;
  private final String id;

  public ResourceNotFoundException(final Long id, final ErrorCode errorCode, final ExceptionType exceptionType) {
    super();
    this.id = id.toString();
    this.errorCode = errorCode;
    this.type = exceptionType;
  }

  public ResourceNotFoundException(final String id, final ErrorCode errorCode, final ExceptionType exceptionType) {
    super();
    this.id = id;
    this.errorCode = errorCode;
    this.type = exceptionType;
  }

  public ResourceNotFoundException(final Long id, final ErrorCode errorCode, final ExceptionType exceptionType, Throwable cause) {
    super(cause);
    this.id = id.toString();
    this.errorCode = errorCode;
    this.type = exceptionType;
  }

  public String getId() {
    return id;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

  public ExceptionType getType() {
    return type;
  }
}