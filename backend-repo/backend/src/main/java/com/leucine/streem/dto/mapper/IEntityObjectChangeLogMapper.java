package com.leucine.streem.dto.mapper;

import com.leucine.streem.collections.changelogs.ChangeLogDataDto;
import com.leucine.streem.collections.changelogs.ChangeLogInputData;
import com.leucine.streem.collections.changelogs.EntityDataDto;
import com.leucine.streem.collections.changelogs.EntityObjectChangeLog;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface IEntityObjectChangeLogMapper extends IBaseMapper<ChangeLogDataDto, EntityObjectChangeLog> {
  @Mapping(source = "currentFacilityId", target = "facilityId")
  @Mapping(source = "changeLogDataDto.oldUsageStatus", target = "usageStatus.oldStatus")
  @Mapping(source = "changeLogDataDto.newUsageStatus", target = "usageStatus.newStatus")
  EntityObjectChangeLog toEntityObjectChangeLog(ChangeLogDataDto changeLogDataDto, String entityId, Long currentFacilityId);

  static List<ChangeLogInputData> toChangeLogInputData(List<EntityDataDto> entityDataDtoList) {
    return entityDataDtoList.stream()
      .map(entityData -> new ChangeLogInputData(entityData.getEntityId(), entityData.getInput()))
      .collect(Collectors.toList());

  }
}
