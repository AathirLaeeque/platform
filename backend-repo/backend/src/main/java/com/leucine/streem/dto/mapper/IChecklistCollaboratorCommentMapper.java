package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.CollaboratorCommentDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.ChecklistCollaboratorComments;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IChecklistCollaboratorCommentMapper extends IBaseMapper<CollaboratorCommentDto, ChecklistCollaboratorComments> {
  @Override
  @Mapping(source = "reviewState", target = "state")
  @Mapping(source = "createdAt", target = "commentedAt")
  @Mapping(source = "checklistCollaboratorMapping.phase", target = "phase")
  @Mapping(source = "createdBy.id", target = "commentedBy.id")
  @Mapping(source = "createdBy.firstName", target = "commentedBy.firstName")
  @Mapping(source = "createdBy.lastName", target = "commentedBy.lastName")
  @Mapping(source = "createdBy.employeeId", target = "commentedBy.employeeId")
  CollaboratorCommentDto toDto(ChecklistCollaboratorComments checklistCollaboratorComments);

}
