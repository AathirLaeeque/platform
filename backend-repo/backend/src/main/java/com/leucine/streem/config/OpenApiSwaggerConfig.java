package com.leucine.streem.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
public class OpenApiSwaggerConfig {
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI().addServersItem(new Server().url("/"))
        .addSecurityItem(new SecurityRequirement().addList("BearerToken"))
        .components(new Components().addSecuritySchemes("BearerToken", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
        .info(new Info().title("CLEEN DWI API").version("V 1.0.0").license(new License().name("Apache 2.0").url("http://springdoc.org")));
  }

}
