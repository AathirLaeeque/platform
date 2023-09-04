package com.leucine.streem.dto.mapper.importmapper;

import com.leucine.streem.dto.ChecklistDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.dto.request.ImportChecklistRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {IImportStageMapper.class})
public interface IImportChecklistMapper extends IBaseMapper<ImportChecklistRequest, ChecklistDto> {

  @Mapping(source = "parameters", target = "parameterRequests")
  @Mapping(source = "stages", target = "stageRequests")
  ImportChecklistRequest toDto(ChecklistDto e);
}
