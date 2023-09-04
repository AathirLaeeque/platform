package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.TempParameterValueDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.TempParameterValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {IMediaMapper.class, IAuditMapper.class, ITempParameterValueMediaMapper.class})
public interface ITempParameterValueMapper extends IBaseMapper<TempParameterValueDto, TempParameterValue> {
  @Mapping(source = "modifiedAt", target = "audit.modifiedAt")
  @Mapping(source = "modifiedBy", target = "audit.modifiedBy")
  TempParameterValueDto toDto(TempParameterValue parameterValue);

}
