package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.ParameterValueDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.ParameterValue;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {IMediaMapper.class, IAuditMapper.class, IParameterValueMediaMapper.class, IParameterApprovalMapper.class})
public interface IParameterValueMapper extends IBaseMapper<ParameterValueDto, ParameterValue> {

  //TODO in dto change activityValueApprovalDto to activityValueApproval
  //this needs changes from ui
  @Override
  @Mapping(source = "modifiedAt", target = "audit.modifiedAt")
  @Mapping(source = "modifiedBy", target = "audit.modifiedBy")
  ParameterValueDto toDto(ParameterValue parameterValue);

}
