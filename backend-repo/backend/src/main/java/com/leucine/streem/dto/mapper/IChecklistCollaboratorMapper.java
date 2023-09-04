package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.ChecklistCollaboratorDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.ChecklistCollaboratorMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IChecklistCollaboratorMapper extends IBaseMapper<ChecklistCollaboratorDto, ChecklistCollaboratorMapping> {
  @Override
  @Mapping(source = "user.id", target = "id")
  @Mapping(source = "user.firstName", target = "firstName")
  @Mapping(source = "user.lastName", target = "lastName")
  @Mapping(source = "user.email", target = "email")
  @Mapping(source = "user.employeeId", target = "employeeId")
  ChecklistCollaboratorDto toDto(ChecklistCollaboratorMapping checklistCollaboratorMapping);

}
