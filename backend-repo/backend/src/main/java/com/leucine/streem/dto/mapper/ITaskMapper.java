package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.*;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.*;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper(uses = {IParameterMapper.class, ITaskMediaMapper.class, ITaskAutomationMapper.class})
public interface ITaskMapper extends IBaseMapper<TaskDto, Task> {
  @Named(value = "toTaskDto")
  @Mapping(target = "parameters", source = "parameters", qualifiedByName = "toParameterDtoList")
  TaskDto toDto(Task task, @Context Map<Long, ParameterValue> parameterValueMap,
                @Context Map<Long, TaskExecution> taskExecutionMap,
                @Context Map<Long, TempParameterValue> tempParameterValueMap,
                @Context Map<Long, List<TaskPauseReasonOrComment>> pauseReasonOrCommentMap,
                @Context Map<Long, List<ParameterVerification>> parameterVerificationMapPeerAndSelf
                );

  @Named(value = "toTaskDtoList")
  @IterableMapping(qualifiedByName = "toTaskDto")
  List<TaskDto> toDto(Set<Task> tasks, @Context Map<Long, ParameterValue> parameterValueMap,
                      @Context Map<Long, TaskExecution> taskExecutionMap,
                      @Context Map<Long, TempParameterValue> tempParameterValueMap,
                      @Context Map<Long, List<TaskPauseReasonOrComment>> pauseReasonOrCommentMap,
                      @Context Map<Long, List<ParameterVerification>> parameterVerificationMapPeerAndSelf
  );

  @Mapping(target = "parameters", source = "parameters")
  TaskDto toDto(Task task);

  TaskReportDto toTaskReportDto(Task task);

  @AfterMapping
  default void setTaskExecutions(Task task, @MappingTarget TaskDto taskDto,
                                 @Context Map<Long, ParameterValue> parameterValueMap,
                                 @Context Map<Long, TaskExecution> taskExecutionMap,
                                 @Context Map<Long, TaskExecution> tempParameterValue,
                                 @Context Map<Long, List<TaskPauseReasonOrComment>> pauseReasonOrCommentMap) {
    TaskExecutionDto taskExecutionDto = new TaskExecutionDto();

    TaskExecution taskExecution = taskExecutionMap.get(task.getId());
    if (null != taskExecution) {
      taskExecutionDto.setState(taskExecution.getState());
      taskExecutionDto.setStartedAt(taskExecution.getStartedAt());
      taskExecutionDto.setEndedAt(taskExecution.getEndedAt());
      taskExecutionDto.setCorrectionReason(taskExecution.getCorrectionReason());
      taskExecutionDto.setCorrectionEnabled(taskExecution.isCorrectionEnabled());
      taskExecutionDto.setReason(taskExecution.getReason());
      taskExecutionDto.setId(taskExecution.getIdAsString());
      taskExecutionDto.setDuration(taskExecution.getDuration());
      taskExecutionDto.setPauseReasons(pauseReasonOrCommentMap.get(taskExecution.getId()));
      taskExecutionDto.setAudit(IAuditMapper.createAuditDto(taskExecution.getModifiedBy(), taskExecution.getModifiedAt()));
      UserAuditDto startedBy = new UserAuditDto();
      if (null != taskExecution.getStartedBy()) {
        startedBy.setId(taskExecution.getStartedBy().getIdAsString());
        startedBy.setEmployeeId(taskExecution.getStartedBy().getEmployeeId());
        startedBy.setFirstName(taskExecution.getStartedBy().getFirstName());
        startedBy.setLastName(taskExecution.getStartedBy().getLastName());
      }
      taskExecutionDto.setStartedBy(startedBy);
      UserAuditDto endedByDto = new UserAuditDto();
      if (null != taskExecution.getEndedBy()) {
        User taskExecutionEndedBy = taskExecution.getEndedBy();
        endedByDto.setId(taskExecutionEndedBy.getIdAsString());
        endedByDto.setEmployeeId(taskExecutionEndedBy.getEmployeeId());
        endedByDto.setFirstName(taskExecutionEndedBy.getFirstName());
        endedByDto.setLastName(taskExecutionEndedBy.getLastName());
      }
      taskExecutionDto.setEndedBy(endedByDto);
      List<TaskExecutionAssigneeDto> taskExecutionAssigneeDtos = new ArrayList<>();
      for (TaskExecutionUserMapping taskExecutionUserMapping : taskExecution.getAssignees()) {
        User user = taskExecutionUserMapping.getUser();
        TaskExecutionAssigneeDto taskExecutionAssigneeDto = new TaskExecutionAssigneeDto();
        taskExecutionAssigneeDto.setId(user.getIdAsString());
        taskExecutionAssigneeDto.setState(taskExecutionUserMapping.getState());
        taskExecutionAssigneeDto.setActionPerformed(taskExecutionUserMapping.isActionPerformed());
        taskExecutionAssigneeDto.setEmployeeId(user.getEmployeeId());
        taskExecutionAssigneeDto.setFirstName(user.getFirstName());
        taskExecutionAssigneeDto.setLastName(user.getLastName());
        taskExecutionAssigneeDto.setEmail(user.getEmail());
        taskExecutionAssigneeDtos.add(taskExecutionAssigneeDto);
      }
      taskExecutionDto.setAssignees(taskExecutionAssigneeDtos);
      User correctedBy = taskExecution.getCorrectedBy();
      if (correctedBy != null) {
        UserAuditDto correctedByUserAuditDto = UserAuditDto.builder()
          .id(correctedBy.getIdAsString())
          .employeeId(correctedBy.getEmployeeId())
          .firstName(correctedBy.getFirstName())
          .lastName(correctedBy.getLastName())
          .build();
        taskExecutionDto.setCorrectedBy(correctedByUserAuditDto);
        taskExecutionDto.setCorrectedAt(taskExecution.getCorrectedAt());
      }
    }
    taskDto.setTaskExecution(taskExecutionDto);
  }
}
