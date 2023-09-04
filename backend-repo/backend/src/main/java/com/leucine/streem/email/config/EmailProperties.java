package com.leucine.streem.email.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("email")
@Getter
@Setter
public class EmailProperties {
  private String host;
  private String port;
  private String username;
  private String password;
  private String fromAddress;
  private String protocol;
  private String tlsEnabled;
  private String authEnabled;
  private String debugEnabled;
}
