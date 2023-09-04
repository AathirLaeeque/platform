package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.SchedulerDto;
import com.leucine.streem.dto.SchedulerInfoDto;
import com.leucine.streem.dto.SchedulerPartialDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.dto.request.CreateProcessSchedulerRequest;
import com.leucine.streem.dto.request.UpdateSchedulerRequest;
import com.leucine.streem.model.Scheduler;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface ISchedulerMapper extends IBaseMapper<SchedulerDto, Scheduler> {
  @Mapping(source = "modifiedAt", target = "audit.modifiedAt")
  @Mapping(source = "modifiedBy", target = "audit.modifiedBy")
  @Mapping(source = "createdAt", target = "audit.createdAt")
  @Mapping(source = "createdBy", target = "audit.createdBy")
  @Mapping(source = "version.version", target = "versionNumber")
  SchedulerDto toDto(Scheduler scheduler);

  @Mapping(source = "modifiedAt", target = "audit.modifiedAt")
  @Mapping(source = "modifiedBy", target = "audit.modifiedBy")
  @Mapping(source = "createdAt", target = "audit.createdAt")
  @Mapping(source = "createdBy", target = "audit.createdBy")
  SchedulerPartialDto toPartialDto(Scheduler scheduler);

  List<SchedulerPartialDto> toPartialDto(List<Scheduler> schedulers);

  @Mapping(source = "modifiedAt", target = "audit.modifiedAt")
  @Mapping(source = "modifiedBy", target = "audit.modifiedBy")
  @Mapping(source = "createdAt", target = "audit.createdAt")
  @Mapping(source = "createdBy", target = "audit.createdBy")
  SchedulerInfoDto toInfoDto(Scheduler scheduler);

  CreateProcessSchedulerRequest toCreateRequest(UpdateSchedulerRequest updateSchedulerRequest);
}
