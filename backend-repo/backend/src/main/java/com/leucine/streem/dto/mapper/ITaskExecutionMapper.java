package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.TaskExecutionDto;
import com.leucine.streem.dto.TaskPauseReasonOrComment;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.TaskExecution;
import com.leucine.streem.model.helper.PrincipalUser;
import org.mapstruct.*;

import java.util.List;

@Mapper(uses = {ITaskExecutionAssigneeMapper.class})
public interface ITaskExecutionMapper extends IBaseMapper<TaskExecutionDto, TaskExecution> {
  @Override
  @Mapping(source = "modifiedAt", target = "audit.modifiedAt")
  @Mapping(source = "modifiedBy", target = "audit.modifiedBy")
  TaskExecutionDto toDto(TaskExecution taskExecution);

  TaskExecutionDto toDto(TaskExecution taskExecution, @Context PrincipalUser principalUser);

  TaskExecutionDto toDto(TaskExecution taskExecution, List<TaskPauseReasonOrComment> pauseReasons);

  @AfterMapping
  default void setAudit(TaskExecution taskExecution, @MappingTarget TaskExecutionDto taskExecutionDto,
                        @Context PrincipalUser principalUser) {
    taskExecutionDto.setAudit(IAuditMapper.createAuditDtoFromPrincipalUser(principalUser, taskExecution.getModifiedAt()));
  }

}
