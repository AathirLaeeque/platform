package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.ParameterVerificationDto;
import com.leucine.streem.dto.ParameterVerificationListViewDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.ParameterVerification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class IParameterVerificationMapper implements IBaseMapper<ParameterVerificationDto, ParameterVerification> {

  @Override
  @Mapping(source = "user", target = "requestedTo")
  @Mapping(source = "parameterValue.state", target = "evaluationState")
  public abstract ParameterVerificationDto toDto(ParameterVerification parameterVerification);

  @Mapping(source = "parameterValue.parameter.taskId", target = "taskId")
  @Mapping(source = "parameterValue.parameter.task.name", target = "taskName")
  @Mapping(source = "parameterValue.parameter.task.stageId", target = "stageId")
  @Mapping(source = "parameterValue.id", target = "parameterValueId")
  @Mapping(source = "parameterValue.jobId", target = "jobId")
  @Mapping(source = "parameterValue.job.code", target = "code")
  @Mapping(source = "parameterValue.parameter.checklist.name", target = "processName")
  @Mapping(source = "parameterValue.parameter.label", target = "parameterName")
  @Mapping(source = "user", target = "requestedTo")
  public abstract ParameterVerificationListViewDto toParameterListViewDto(ParameterVerification parameterVerification);
}
