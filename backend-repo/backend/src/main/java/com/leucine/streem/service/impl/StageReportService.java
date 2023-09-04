package com.leucine.streem.service.impl;

import com.leucine.streem.dto.StageExecutionReportDto;
import com.leucine.streem.dto.mapper.IStageExecutionReportMapper;
import com.leucine.streem.dto.projection.StageTotalTasksView;
import com.leucine.streem.model.Stage;
import com.leucine.streem.model.StageExecutionReport;
import com.leucine.streem.repository.IStageReportRepository;
import com.leucine.streem.repository.IStageRepository;
import com.leucine.streem.service.IStageReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StageReportService implements IStageReportService {

  private final IStageReportRepository stageExecutionRepository;
  private final IStageExecutionReportMapper stageExecutionReportMapper;
  private final IStageRepository stageRepository;

  @Override
  public List<StageExecutionReportDto> getStageExecutionInfo(Long jobId) {
    List<StageExecutionReport> stageExecutionReports = stageExecutionRepository.findByJobId(jobId);
    return stageExecutionReportMapper.toDto(stageExecutionReports);
  }

  @Override
  public void incrementTaskCompleteCount(Long jobId, Long taskId) {
    Stage stage = stageRepository.findByTaskId(taskId);
    stageExecutionRepository.incrementTaskCompleteCount(jobId, stage.getId());
  }

  @Override
  public void registerStagesForJob(Long checklistId, Long jobId) {
    List<StageTotalTasksView> stageList = stageRepository.findByChecklistId(checklistId);

    List<StageExecutionReport> stageExecutionReports = new ArrayList<>();
    for (StageTotalTasksView stageTotalTasksView : stageList) {
      StageExecutionReport stageExecutionReport = new StageExecutionReport()
          .setTotalTasks(stageTotalTasksView.getTotalTasks())
          .setCompletedTasks(0)
          .setStageName(stageTotalTasksView.getStageName())
          .setJobId(jobId)
          .setStageId(stageTotalTasksView.getStageId());
      stageExecutionReports.add(stageExecutionReport);
    }

    stageExecutionRepository.saveAll(stageExecutionReports);
  }

  @Override
  public void setStageToInProgress(Long jobId, Long taskId) {
    Stage stage = stageRepository.findByTaskId(taskId);
    stageExecutionRepository.updateStageToInProgress(jobId, stage.getId());
  }

  @Override
  public void unregisterStagesForJob(Long jobId) {
    stageExecutionRepository.deleteStagesForJob(jobId);
  }
}
