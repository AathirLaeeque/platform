package com.leucine.streem.exception;

import org.springframework.http.HttpStatus;

public class JaasServiceException extends RuntimeException {
  private static final long serialVersionUID = 9205915768744687200L;
  private String payload;
  private HttpStatus status;

  public JaasServiceException(String payload, HttpStatus status) {
    super(payload);
    this.payload = payload;
    this.status = status;
  }

  public String getPayload() {
    return payload;
  }

  public HttpStatus getStatus() {
    return status;
  }

}
