package com.leucine.streem.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.text.MessageFormat;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppUrl {
  private Path path;

  public String getLoginPath() {
    return path.login;
  }

  public String getJobPath(Long jobId) {
    return MessageFormat.format(path.job, String.valueOf(jobId));
  }

  public String getChecklistPath(Long checklistId) {
    return MessageFormat.format(path.checklist, String.valueOf(checklistId));
  }

  @Setter
  static class Path {
    private String login;
    private String job;
    private String checklist;
  }
}
