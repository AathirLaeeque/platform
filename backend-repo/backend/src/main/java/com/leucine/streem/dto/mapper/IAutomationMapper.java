package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.AutomationDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.dto.request.AutomationRequest;
import com.leucine.streem.model.Automation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface IAutomationMapper extends IBaseMapper<AutomationDto, Automation> {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "actionDetails", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "modifiedAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  Automation clone(Automation automation);

  Automation toEntity(AutomationRequest AutomationRequest);

  void update(AutomationRequest automationRequest, @MappingTarget Automation automation);
}
