package com.leucine.streem.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public AuthenticationFilter authenticationFilter() {
    return new AuthenticationFilter();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.cors().and().csrf().disable()
      .sessionManagement(httpSecuritySessionManagementConfigurer ->
        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeRequests(authorizeRequests ->
        authorizeRequests
          .antMatchers(HttpMethod.GET, "/version.json").permitAll()
          .antMatchers("/api-docs.html", "/swagger-ui/*", "/**/api-docs", "/**/api-docs/*").permitAll()
          .antMatchers(HttpMethod.POST, "/v1/auth/login").permitAll()
          .antMatchers(HttpMethod.POST, "/v1/auth/re-login").permitAll()
          .antMatchers(HttpMethod.POST, "/v1/auth/logout").permitAll()
          .antMatchers(HttpMethod.POST, "/v1/users/username/check").permitAll()
          .antMatchers(HttpMethod.PATCH, "/v1/auth/register").permitAll()
          .antMatchers(HttpMethod.POST, "/v1/auth/token/refresh").permitAll()
          .antMatchers(HttpMethod.PATCH, "/v1/auth/password").permitAll()
          .antMatchers(HttpMethod.PATCH, "/v1/auth/token/validate").permitAll()
          .antMatchers(HttpMethod.PATCH, "/v1/auth/token/reset").permitAll()
          .antMatchers(HttpMethod.PATCH, "/v1/auth/identity/validate").permitAll()
          .antMatchers(HttpMethod.PATCH, "/v1/auth/additional/verification").permitAll()
          .antMatchers(HttpMethod.PATCH, "/v1/auth/challenge-questions/validate").permitAll()
          .antMatchers(HttpMethod.PATCH, "/v1/auth/admin/notify").permitAll()
          .antMatchers(HttpMethod.GET, "/v1/auth/account/lookup").permitAll()
          .antMatchers(HttpMethod.GET, "/v1/auth/extras").permitAll()
          .antMatchers(HttpMethod.GET, "/v1/challenge-questions").permitAll()
          .antMatchers(HttpMethod.GET, "/medias/**").permitAll()
          .antMatchers(HttpMethod.GET, "/actuator/health/liveness").permitAll()
          .antMatchers(HttpMethod.GET, "/actuator/health/readiness").permitAll()
          .antMatchers(HttpMethod.PATCH, "/v1/migrations/**").permitAll()
          .anyRequest().authenticated()
      )
      .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
      .build();
  }
}
