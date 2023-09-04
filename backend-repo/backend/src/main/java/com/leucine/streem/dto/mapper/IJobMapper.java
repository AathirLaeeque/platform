package com.leucine.streem.dto.mapper;

import com.leucine.streem.constant.State;
import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.*;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.dto.projection.JobAssigneeView;
import com.leucine.streem.dto.projection.TaskExecutionCountView;
import com.leucine.streem.model.*;
import com.leucine.streem.model.helper.PrincipalUser;
import org.mapstruct.*;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Mapper(uses = {IChecklistMapper.class})
public interface IJobMapper extends IBaseMapper<JobDto, Job> {

  @Override
  JobDto toDto(Job job);

  JobDto toDto(Job job, @Context Map<Long, ParameterValue> parameterValueMap, @Context Map<Long, TaskExecution> taskExecutionMap,
               @Context Map<Long, TempParameterValue> tempParameterValueMap, @Context Map<Long, List<TaskPauseReasonOrComment>> pauseReasonOrCommentMap,
               @Context Map<Long, List<ParameterVerification>> parameterVerificationMapPeerAndSelf);

  JobInfoDto toJobInfoDto(Job job, @Context PrincipalUser principalUser);

  @Mapping(source = "parameterValues", target = "parameterValues", qualifiedByName = "toParameterValuesDto")
  JobInformationDto toJobInformationDto(Job job);

  JobStateDto toJobStateDto(Job job);

  @Mapping(source = "relationValues", target = "relations", qualifiedByName = "toRelationValuesDto")
  @Mapping(source = "parameterValues", target = "parameterValues", qualifiedByName = "toParameterValuesDto")
  JobPartialDto jobToJobPartialDto(Job job, @Context Map<String, List<JobAssigneeView>> jobAssignees,
                                   @Context Map<String, TaskExecutionCountView> taskExecutionCountViewMap);

  List<JobPartialDto> jobToJobPartialDto(List<Job> jobs, @Context Map<String, List<JobAssigneeView>> jobAssignees,
                                         @Context Map<String, TaskExecutionCountView> taskExecutionCountViewMap);

  @AfterMapping
  default void setJobAssigneesAndTaskCompletedCount(Job job, @MappingTarget JobPartialDto jobPartialDto, @Context Map<String, List<JobAssigneeView>> jobAssigneeViewMap,
                                                    @Context Map<String, TaskExecutionCountView> taskExecutionCountViewMap) {
    jobPartialDto.setCompletedTasks(taskExecutionCountViewMap.get(job.getIdAsString()).getCompletedTasks());
    jobPartialDto.setTotalTasks(taskExecutionCountViewMap.get(job.getIdAsString()).getTotalTasks());
    jobPartialDto.setAssignees(jobAssigneeViewMap.get(job.getIdAsString()));
  }

  @AfterMapping
  default void setCompletedTasks(Job job, @MappingTarget JobDto jobDto, @Context Map<Long, ParameterValue> parameterValueMap,
                                 @Context Map<Long, TaskExecution> taskExecutionMap, @Context Map<Long, TempParameterValue> tempParameterValueMap) {
    jobDto.setTotalTasks(job.getTaskExecutions().size());
    jobDto.setCompletedTasks(getCompletedTasks(job));
  }

  @AfterMapping
  default void setAudit(Job job, @MappingTarget JobInfoDto jobDto, @Context PrincipalUser principalUser) {
    jobDto.setAudit(IAuditMapper.createAuditDtoFromPrincipalUser(principalUser, job.getModifiedAt()));
  }

  default long getCompletedTasks(Job job) {
    return job.getTaskExecutions().stream()
      .filter(taskExecution -> (taskExecution.getState().equals(State.TaskExecution.COMPLETED)
        || taskExecution.getState().equals(State.TaskExecution.SKIPPED)
        || taskExecution.getState().equals(State.TaskExecution.COMPLETED_WITH_EXCEPTION))).count();
  }

  @Mapping(source = "relationValues", target = "relations", qualifiedByName = "toRelationValuesDto")
  JobReportDto toJobReportDto(Job job);

  @Mapping(source = "relationValues", target = "relations", qualifiedByName = "toRelationValuesDto")
  JobPrintDto toJobPrintDto(Job job, @Context Map<Long, ParameterValue> parameterValueMap, @Context Map<Long, TaskExecution> taskExecutionMap,
                            @Context Map<Long, TempParameterValue> tempParameterValueMap, @Context Map<Long, List<TaskPauseReasonOrComment>> pauseReasonOrCommentMap,
                            @Context Map<Long, List<ParameterVerification>> parameterVerificationMapPeerAndSelf);

  @Named("toRelationValuesDto")
  static List<RelationValueDto> toRelationsDto(Set<RelationValue> relations) {
    //TODO: create a IRelationValueMapper and use toDto method here rather then setting values
    var relationValuesMap = relations.stream()
      .map(r -> RelationValueDto.builder()
        .id(r.getRelationId().toString())
        .externalId(r.getObjectTypeExternalId())
        .displayName(r.getObjectTypeDisplayName())
        .targets(new ArrayList<>(Collections.singletonList(
          RelationValueTargetDto.builder()
            .id(r.getIdAsString())
            .collection(r.getCollection())
            .displayName(r.getDisplayName())
            .externalId(r.getExternalId())
            .build())))
        .build())
      .collect(toMap(RelationValueDto::getExternalId,
        Function.identity(),
        (r1, r2) -> {
          r1.getTargets().addAll(r2.getTargets());
          return r1;
        }
      ));
    return new ArrayList<>(relationValuesMap.values());
  }

  @Named("toParameterValuesDto")
  static List<ParameterDto> toParameterValuesDto(Set<ParameterValue> parameterValues) {
    List<ParameterDto> parameterDtos = new ArrayList<>();

    for (ParameterValue parameterValue : parameterValues) {
      if (Type.ParameterTargetEntityType.PROCESS.equals(parameterValue.getParameter().getTargetEntityType())) {

        Parameter parameter = parameterValue.getParameter();

        ParameterDto parameterDto = new ParameterDto();
        ParameterValueDto parameterValueDto = new ParameterValueDto();
        parameterValueDto.setChoices(parameterValue.getChoices());
        parameterValueDto.setValue(parameterValue.getValue());
        parameterValueDto.setReason(parameterValue.getReason());
        parameterValueDto.setState(parameterValue.getState());
        parameterValueDto.setAudit(IAuditMapper.createAuditDto(parameterValue.getModifiedBy(), parameterValue.getModifiedAt()));

        parameterDto.setId(parameter.getIdAsString());
        parameterDto.setDescription(parameter.getDescription());
        parameterDto.setMandatory(parameter.isMandatory());
        parameterDto.setLabel(parameter.getLabel());
        parameterDto.setType(parameter.getType().toString());
        parameterDto.setTargetEntityType(parameter.getTargetEntityType());
        parameterDto.setOrderTree(parameter.getOrderTree());
        parameterDto.setData(parameter.getData());
        parameterDto.setResponse(parameterValueDto);

        parameterDtos.add(parameterDto);
      }
    }

    return parameterDtos;
  }
}
