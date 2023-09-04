package com.leucine.streem.email.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

  @Autowired
  private EmailProperties emailProperties;

  @Bean
  public JavaMailSender getMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    mailSender.setHost(emailProperties.getHost());
    mailSender.setPort(Integer.parseInt(emailProperties.getPort()));
    mailSender.setUsername(emailProperties.getUsername());
    mailSender.setPassword(emailProperties.getPassword());
    mailSender.setDefaultEncoding("UTF-8");

    Properties javaMailProperties = new Properties();
    javaMailProperties.put("mail.smtp.starttls.enable", emailProperties.getTlsEnabled());
    javaMailProperties.put("mail.smtp.auth", emailProperties.getAuthEnabled());
    javaMailProperties.put("mail.transport.protocol", emailProperties.getProtocol());
    javaMailProperties.put("mail.debug", emailProperties.getDebugEnabled());

    mailSender.setJavaMailProperties(javaMailProperties);
    return mailSender;
  }

}
