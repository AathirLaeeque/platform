package com.leucine.streem.dto.mapper.importmapper;

import com.leucine.streem.dto.TaskDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.dto.request.ImportTaskRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = IImportMediaMapper.class)
public interface IImportTaskMapper extends IBaseMapper<ImportTaskRequest, TaskDto> {

  @Mapping(source = "parameters", target = "parameterRequests")
  @Mapping(source = "automations", target = "automationRequests")
  @Mapping(source = "medias", target = "mediaRequests")
  ImportTaskRequest toDto(TaskDto e);
}
