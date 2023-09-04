package com.leucine.streem.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.File;

@Configuration
@EnableWebMvc
public class StreemConfig implements WebMvcConfigurer {
  private static final String MEDIAS_PATH_PATTERN = "/medias/**/**/*";

  @Autowired
  private MediaConfig mediaConfig;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedHeaders("*")
            .allowedMethods("*")
            .allowedOrigins("*")
            .maxAge(3600);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // Resource handler for medias
    registry
      .addResourceHandler(MEDIAS_PATH_PATTERN)
      .addResourceLocations("file:" + File.separator + File.separator + mediaConfig.getLocation() + File.separator, "/")
      .setCachePeriod(3600)
      .resourceChain(true)
      .addResolver(new PathResourceResolver())
      .addResolver(new EncodedResourceResolver())
    ;

  }
}
