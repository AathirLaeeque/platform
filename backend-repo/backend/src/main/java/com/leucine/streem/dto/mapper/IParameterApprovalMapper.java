package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.ParameterValueApprovalDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.ParameterValueApproval;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IParameterApprovalMapper extends IBaseMapper<ParameterValueApprovalDto, ParameterValueApproval> {
  @Override
  @Mapping(source = "user.id", target = "approver.id")
  @Mapping(source = "user.employeeId", target = "approver.employeeId")
  @Mapping(source = "user.firstName", target = "approver.firstName")
  @Mapping(source = "user.lastName", target = "approver.lastName")
  ParameterValueApprovalDto toDto(ParameterValueApproval parameterValueApproval);
}
