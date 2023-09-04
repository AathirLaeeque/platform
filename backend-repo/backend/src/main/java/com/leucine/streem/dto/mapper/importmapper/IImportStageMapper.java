package com.leucine.streem.dto.mapper.importmapper;

import com.leucine.streem.dto.StageDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.dto.request.ImportStageRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {IImportTaskMapper.class})
public interface IImportStageMapper extends IBaseMapper<ImportStageRequest, StageDto> {

  @Mapping(source = "tasks", target = "taskRequests")
  ImportStageRequest toDto(StageDto e);
}
