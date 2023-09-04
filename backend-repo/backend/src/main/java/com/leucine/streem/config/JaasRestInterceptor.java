package com.leucine.streem.config;

import com.leucine.streem.model.helper.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JaasRestInterceptor implements ClientHttpRequestInterceptor {
  private static final String SERVICE_ID = "X-Service-Id";
  private static final String AUTHORIZATION = "Authorization";
  private static final String ACCEPT = "Accept";
  private static final String BEARER_TOKEN_PREFIX = "Bearer ";

  private final JaasServiceProperty jaasServiceProperty;

  @Autowired
  public JaasRestInterceptor(JaasServiceProperty jaasServiceProperty) {
    this.jaasServiceProperty = jaasServiceProperty;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    HttpHeaders headers = request.getHeaders();
    PrincipalUser principalUser = null;
    if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof PrincipalUser) {
      principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    if (null != principalUser && null != principalUser.getToken()) {
      headers.add(AUTHORIZATION, BEARER_TOKEN_PREFIX + principalUser.getToken());
    }
    headers.add(SERVICE_ID, jaasServiceProperty.getServiceId());
    headers.set(ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    return execution.execute(request, body);
  }
}
