package com.leucine.streem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Map;

@Getter
@Setter
@Configuration
@PropertySource(value = "classpath:version.json", ignoreResourceNotFound = true, factory = VersionProperties.JsonLoader.class)
public class VersionProperties {

  @Value("${version}")
  private String version;

  @Value("${branch}")
  private String branch;

  @Value("${commit}")
  private String commit;

  public static class JsonLoader implements PropertySourceFactory {
    @Override
    public org.springframework.core.env.PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
      var readValue = new ObjectMapper().readValue(resource.getInputStream(), Map.class);
      return new MapPropertySource("json-source", readValue);
    }
  }
}