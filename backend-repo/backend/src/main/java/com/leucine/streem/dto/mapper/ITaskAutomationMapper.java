package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.AutomationDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.TaskAutomationMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class ITaskAutomationMapper implements IBaseMapper<AutomationDto, TaskAutomationMapping> {

  @Override
  @Mapping(source = "automation.id", target = "id")
  @Mapping(source = "automation.type", target = "type")
  @Mapping(source = "automation.actionType", target = "actionType")
  @Mapping(source = "automation.actionDetails", target = "actionDetails")
  @Mapping(source = "automation.triggerType", target = "triggerType")
  @Mapping(source = "automation.triggerDetails", target = "triggerDetails")
  @Mapping(source = "automation.targetEntityType", target = "targetEntityType")
  @Mapping(source = "orderTree", target = "orderTree")
  @Mapping(source = "displayName", target = "displayName")
  public abstract AutomationDto toDto(TaskAutomationMapping entity);


}
