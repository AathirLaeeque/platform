package com.leucine.streem.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "medias")
public class MediaConfig {
  private String location;
  private String cdn;
  private String fileTypes;
}
