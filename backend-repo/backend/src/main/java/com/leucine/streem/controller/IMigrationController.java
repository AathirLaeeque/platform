package com.leucine.streem.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.response.Response;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/migrations")
public interface IMigrationController {

  @PatchMapping("/202212161515")
  @ResponseBody
  Response<BasicDto> runJobLogMigration202212161515();

  @PatchMapping("/270720231706")
  @ResponseBody
  Response<BasicDto> runRelationFilter270720231706() throws JsonProcessingException;

  @PatchMapping("/202308121736")
  Response<BasicDto> runAutoInitializedParameterMigration202308121736() throws JsonProcessingException;

}
