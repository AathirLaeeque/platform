package com.leucine.streem.controller.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leucine.streem.controller.IJobController;
import com.leucine.streem.dto.*;
import com.leucine.streem.dto.projection.ShouldBeParameterStatusView;
import com.leucine.streem.dto.projection.TaskExecutionAssigneeDetailsView;
import com.leucine.streem.dto.request.CreateJobRequest;
import com.leucine.streem.dto.request.JobCweDetailRequest;
import com.leucine.streem.dto.request.TaskExecutionAssignmentRequest;
import com.leucine.streem.dto.request.UpdateJobRequest;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.MultiStatusException;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.service.IJobCweService;
import com.leucine.streem.service.IJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class JobController implements IJobController {
  private final IJobService jobService;
  private final IJobCweService jobCweService;

  @Autowired
  public JobController(IJobService jobService, IJobCweService jobCweService) {
    this.jobService = jobService;
    this.jobCweService = jobCweService;
  }

  @Override
  public Response<Page<JobPartialDto>> getAll(String objectId, String filters, Pageable pageable) {
    return Response.builder().data(jobService.getAllJobs(objectId, filters, pageable)).build();
  }

  @Override
  public Response<Page<JobPartialDto>> getAllCount(String objectId, String filters, Pageable pageable) {
    return Response.builder().data(jobService.getAllJobsCount(objectId, filters, pageable)).build();
  }

  @Override
  public Response<Page<JobPartialDto>> getJobsAssignedToMe(String objectId, String filters, Pageable pageable) throws StreemException {
    return Response.builder().data(jobService.getJobsAssignedToMe(objectId, filters, pageable)).build();
  }

  @Override
  public Response<CountDto> getJobsAssignedToMeCount(String objectId, String filters) throws StreemException {
    return Response.builder().data(jobService.getJobsAssignedToMeCount(objectId, filters)).build();
  }


  @Override
  public Response<JobDto> getJob(Long jobId) throws ResourceNotFoundException {
    return Response.builder().data(jobService.getJobById(jobId)).build();
  }

  @Override
  public Response<JobCweDto> getJobCweDetail(Long jobId) throws ResourceNotFoundException {
    return Response.builder().data(jobCweService.getJobCweDetail(jobId)).build();
  }

  @Override
  public Response<JobDto> createJob(CreateJobRequest createJobRequest) throws ResourceNotFoundException, StreemException, MultiStatusException, IOException {
    return Response.builder().data(jobService.createJob(createJobRequest)).build();
  }

  @Override
  public Response<BasicDto> updateJob(Long jobId, UpdateJobRequest updateJobRequest) throws StreemException, ResourceNotFoundException {
    return Response.builder().data(jobService.updateJob(jobId, updateJobRequest)).build();
  }

  @Override
  public Response<JobInfoDto> startJob(Long jobId) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(jobService.startJob(jobId)).build();
  }

  @Override
  public Response<JobInfoDto> completeJob(Long jobId) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(jobService.completeJob(jobId)).build();
  }

  @Override
  public Response<JobInfoDto> completeJobWithException(Long jobId, JobCweDetailRequest jobCweDetailRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(jobService.completeJobWithException(jobId, jobCweDetailRequest)).build();
  }

  @Override
  public Response<JobStateDto> getJobState(Long jobId) throws ResourceNotFoundException {
    return Response.builder().data(jobService.getJobState(jobId)).build();
  }

  @Override
  public Response<StageDetailsDto> pollStageData(Long jobId, Long stageId) throws ResourceNotFoundException {
    return Response.builder().data(jobService.getStageData(jobId, stageId)).build();
  }

  @Override
  public Response<BasicDto> bulkAssign(Long jobId, TaskExecutionAssignmentRequest taskExecutionAssignmentRequest, boolean notify) throws ResourceNotFoundException, StreemException, MultiStatusException {
    return Response.builder().data(jobService.bulkAssign(jobId, taskExecutionAssignmentRequest, notify)).build();
  }

  @Override
  public Response<List<TaskExecutionAssigneeDetailsView>> getAssignees(Long jobId) {
    return Response.builder().data(jobService.getAssignees(jobId)).build();
  }

  @Override
  public Response<JobReportDto> getJobReport(Long jobId) throws ResourceNotFoundException, JsonProcessingException {
    return Response.builder().data(jobService.getJobReport(jobId)).build();
  }

  @Override
  public Response<JobReportDto> printJobReport(Long jobId) throws ResourceNotFoundException, JsonProcessingException {
    return Response.builder().data(jobService.printJobReport(jobId)).build();
  }

  @Override
  public Response<JobPrintDto> printJob(Long jobId) throws ResourceNotFoundException, JsonProcessingException {
    return Response.builder().data(jobService.printJob(jobId)).build();
  }

  @Override
  public Response<Page<JobPartialDto>> getAllByResource(String objectId, String filters, Pageable pageable) {
    return Response.builder().data(jobService.getAllByResource(objectId, filters, pageable)).build();
  }

  @Override
  public Response<JobInformationDto> getJobInformation(Long jobId) throws ResourceNotFoundException {
    return Response.builder().data(jobService.getJobInformation(jobId)).build();
  }

  @Override
  public Response<Page<ShouldBeParameterStatusView>> getPendingForApprovalShouldBeParameters(String processName, String parameterName, Pageable pageable) {
    return Response.builder().data(jobService.getShouldBeParameterStatus(processName, parameterName, pageable)).build();
  }
}
