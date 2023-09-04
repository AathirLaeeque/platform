package com.leucine.streem.controller.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leucine.streem.controller.IMigrationController;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.migration.AutoInitializedParameterMigration202308121736;
import com.leucine.streem.migration.JobLogMigration202212161515;
import com.leucine.streem.migration.RelationFilter270720231706;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MigrationController implements IMigrationController {
  private final RelationFilter270720231706 relationFilter270720231706;
  private final JobLogMigration202212161515 jobLogMigration202212161515;
  private final AutoInitializedParameterMigration202308121736 autoInitializedParameterMigration202308121736;

  @Override
  public Response<BasicDto> runJobLogMigration202212161515() {
    return Response.builder().data(jobLogMigration202212161515.execute()).build();
  }

  @Override
  public Response<BasicDto> runRelationFilter270720231706() throws JsonProcessingException {
    return Response.builder().data(relationFilter270720231706.execute()).build();
  }

  @Override
  public Response<BasicDto> runAutoInitializedParameterMigration202308121736() throws JsonProcessingException {
    return Response.builder().data(autoInitializedParameterMigration202308121736.execute()).build();

  }

}
