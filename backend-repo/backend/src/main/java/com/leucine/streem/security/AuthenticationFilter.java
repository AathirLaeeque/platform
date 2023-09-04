package com.leucine.streem.security;

import com.leucine.streem.config.JaasServiceProperty;
import com.leucine.streem.dto.RoleDto;
import com.leucine.streem.dto.request.AuthorizationRequest;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.util.DateTimeUtils;
import com.leucine.streem.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {
  private static final String AUTHORIZATION = "Authorization";
  private static final String BEARER_TOKEN_PREFIX = "Bearer ";

  @Autowired
  private final JaasServiceProperty jaasServiceProperty = new JaasServiceProperty();

  @Autowired
  @Qualifier("authenticationFilterRestTemplate")
  private RestTemplate authenticationFilterRestTemplate;

  public AuthenticationFilter() {
  }

  private String obtainToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION);
    if (Utility.containsText(bearerToken) && bearerToken.startsWith(BEARER_TOKEN_PREFIX)) {
      return bearerToken.substring(7);
    }
    return null;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      String token = obtainToken(request);
      if (!Utility.isEmpty(token)) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(AUTHORIZATION, BEARER_TOKEN_PREFIX + token);
        HttpEntity<AuthorizationRequest> requestEntity = new HttpEntity<>(AuthorizationRequest.builder().path(request.getServletPath()).method(request.getMethod()).build(),
            requestHeaders);
        ResponseEntity<PrincipalUser> jaasResponse = authenticationFilterRestTemplate.exchange(jaasServiceProperty.getAuthorizeUrl(), HttpMethod.PATCH, requestEntity,
            PrincipalUser.class);
        PrincipalUser principalUser = jaasResponse.getBody();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principalUser, null, getAuthorities(principalUser.getRoles()));
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (HttpClientErrorException ex) {
      handleHttpClientErrorException(response, ex);
      return;
    } catch (Exception ex) {
      handleException(response, ex);
      return;
    }
    try {
      filterChain.doFilter(request, response);
    } catch (Exception ex) {
      handleException(response, ex);
      return;
    }
  }

  private void handleException(HttpServletResponse response, Exception ex) {
    long now = DateTimeUtils.now();
    String message = "{\"object\":\"LIST\",\"status\":\"SERVICE_UNAVAILABLE\",\"message\":\"ERROR\",\"timestamp\":" + now + " ,\"errors\":[{\"timestamp\":" + now + ",\"code" +
        "\":-1,\"message\":\"One of our critical service is down. Our team is working on it. For emergency, please write to us at support@leucinetech.com\"," +
        "\"ignore\":\"" + ex.getMessage() + "\"}]}";
    response.setStatus(503);
    response.setContentType("application/json");
    try {
      Writer writer = response.getWriter();
      writer.write(message);
      writer.flush();
    } catch (IOException e) {
      log.info("[handleException] error", e);
    }
  }

  private void handleHttpClientErrorException(HttpServletResponse response, HttpClientErrorException ex) {
    response.setStatus(ex.getRawStatusCode());
    response.setContentType("application/json");
    try {
      Writer writer = response.getWriter();
      writer.write(ex.getResponseBodyAsString());
      writer.flush();
    } catch (IOException e) {
      log.info("[handleHttpClientErrorException] error", e);
    }
  }

  public Collection<? extends GrantedAuthority> getAuthorities(List<RoleDto> roles) {
    return roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName())).collect(Collectors.toSet());
  }
}
