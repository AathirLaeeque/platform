package com.leucine.streem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leucine.streem.dto.*;
import com.leucine.streem.dto.projection.ShouldBeParameterStatusView;
import com.leucine.streem.dto.projection.TaskExecutionAssigneeDetailsView;
import com.leucine.streem.dto.request.CreateJobRequest;
import com.leucine.streem.dto.request.JobCweDetailRequest;
import com.leucine.streem.dto.request.TaskExecutionAssignmentRequest;
import com.leucine.streem.dto.request.UpdateJobRequest;
import com.leucine.streem.exception.MultiStatusException;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface IJobService {
  JobDto getJobById(Long jobId) throws ResourceNotFoundException;

  Page<JobPartialDto> getAllJobs(String objectId, String filters, Pageable pageable);

  Page<JobPartialDto> getAllJobsCount(String objectId, String filters, Pageable pageable);

  Page<JobPartialDto> getJobsAssignedToMe(String objectId, String filters, Pageable pageable) throws StreemException;

  CountDto getJobsAssignedToMeCount(String objectId, String filters) throws StreemException;

  JobDto createJob(CreateJobRequest createJobRequest) throws StreemException, ResourceNotFoundException, MultiStatusException, IOException;

  BasicDto updateJob(Long jobId, UpdateJobRequest updateJobRequest) throws ResourceNotFoundException, StreemException;

  JobInfoDto startJob(Long jobId) throws ResourceNotFoundException, StreemException;

  JobInfoDto completeJob(Long jobId) throws ResourceNotFoundException, StreemException;

  JobInfoDto completeJobWithException(Long jobId, JobCweDetailRequest jobCweDetailRequest) throws ResourceNotFoundException, StreemException;

  BasicDto bulkAssign(Long jobId, TaskExecutionAssignmentRequest taskExecutionAssignmentRequest, boolean notify) throws ResourceNotFoundException, StreemException, MultiStatusException;

  List<TaskExecutionAssigneeDetailsView> getAssignees(Long jobId);

  JobStateDto getJobState(Long jobId) throws ResourceNotFoundException;

  Page<JobPartialDto> getAllByResource(String objectId, String filters, Pageable pageable);

  StageDetailsDto getStageData(Long jobId, Long stageId) throws ResourceNotFoundException;

  JobReportDto getJobReport(Long jobId) throws ResourceNotFoundException, JsonProcessingException;

  JobInformationDto getJobInformation(Long jobId) throws ResourceNotFoundException;

  JobReportDto printJobReport(Long jobId) throws ResourceNotFoundException, JsonProcessingException;

  JobPrintDto printJob(Long jobId) throws ResourceNotFoundException, JsonProcessingException;

  boolean isJobExistsBySchedulerIdAndDateGreaterThanOrEqualToExpectedStartDate(Long schedulerId, Long epochDateTime);

  Page<ShouldBeParameterStatusView> getShouldBeParameterStatus(String processName, String parameterName, Pageable pageable);
}
