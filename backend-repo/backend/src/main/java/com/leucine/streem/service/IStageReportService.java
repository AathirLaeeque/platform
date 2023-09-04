package com.leucine.streem.service;

import com.leucine.streem.dto.StageExecutionReportDto;

import java.util.List;

public interface IStageReportService {
  List<StageExecutionReportDto> getStageExecutionInfo(Long jobId);

  void incrementTaskCompleteCount(Long jobId, Long taskId);

  void registerStagesForJob(Long checklistId, Long jobId);

  void unregisterStagesForJob(Long jobId);

  void setStageToInProgress(Long jobId, Long taskId);
}
