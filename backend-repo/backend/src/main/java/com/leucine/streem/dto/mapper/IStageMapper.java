package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.StageDto;
import com.leucine.streem.dto.StageReportDto;
import com.leucine.streem.dto.TaskPauseReasonOrComment;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.*;
import org.mapstruct.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {ITaskMapper.class})
public interface IStageMapper extends IBaseMapper<StageDto, Stage> {
  @Named(value = "toStageDto")
  @Mapping(target = "tasks", qualifiedByName = "toTaskDtoList")
  StageDto toDto(Stage stage, @Context Map<Long, ParameterValue> parameterValueMap,
                 @Context Map<Long, TaskExecution> taskExecutionMap,
                 @Context Map<Long, TempParameterValue> tempParameterValueMap,
                 @Context Map<Long, List<TaskPauseReasonOrComment>> pauseReasonOrCommentMap,
                 @Context Map<Long, List<ParameterVerification>> parameterVerificationMapPeerAndSelf
  );

  @Named(value = "toStageDtoList")
  @IterableMapping(qualifiedByName = "toStageDto")
  List<StageDto> toDto(Set<Stage> stage, @Context Map<Long, ParameterValue> parameterValueMap,
                       @Context Map<Long, TaskExecution> taskExecutionMap,
                       @Context Map<Long, TempParameterValue> tempParameterValueMap,
                       @Context Map<Long, List<TaskPauseReasonOrComment>> pauseReasonOrCommentMap,
                       @Context Map<Long, List<ParameterVerification>> parameterVerificationMapPeerAndSelf
  );

  @Mapping(target = "tasks", ignore = true)
  StageReportDto toStageReportDto(Stage stage);
}
