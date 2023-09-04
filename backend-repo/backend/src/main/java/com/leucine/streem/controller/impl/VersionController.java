package com.leucine.streem.controller.impl;

import com.leucine.streem.config.VersionProperties;
import com.leucine.streem.controller.IVersionController;
import com.leucine.streem.dto.ProjectVersionDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class VersionController implements IVersionController {

  private final VersionProperties properties;

  @Override
  public ProjectVersionDto get() {
    return ProjectVersionDto.builder().version(properties.getVersion()).branch(properties.getBranch()).commit(properties.getCommit()).build();
  }
}