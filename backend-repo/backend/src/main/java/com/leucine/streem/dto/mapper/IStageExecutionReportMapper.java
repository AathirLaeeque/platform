package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.StageExecutionReportDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.StageExecutionReport;
import org.mapstruct.Mapper;

@Mapper
public interface IStageExecutionReportMapper extends IBaseMapper<StageExecutionReportDto, StageExecutionReport> {
}

