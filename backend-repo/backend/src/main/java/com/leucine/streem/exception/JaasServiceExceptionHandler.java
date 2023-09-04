package com.leucine.streem.exception;

import lombok.SneakyThrows;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@ControllerAdvice
public class JaasServiceExceptionHandler implements ResponseErrorHandler {

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    return (response.getStatusCode().series() == CLIENT_ERROR || response.getStatusCode().series() == SERVER_ERROR);
  }

  @SneakyThrows
  @Override
  public void handleError(ClientHttpResponse response) throws IOException {
    InputStreamReader isReader = new InputStreamReader(response.getBody());
    BufferedReader reader = new BufferedReader(isReader);
    StringBuilder sb = new StringBuilder();
    String str;
    while ((str = reader.readLine()) != null) {
      sb.append(str);
    }
    throw new JaasServiceException(sb.toString(), response.getStatusCode());
  }
}
