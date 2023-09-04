package com.leucine.streem.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.leucine.streem.model.ParameterValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/jobs")
public interface IJobController {
  @GetMapping
  @ResponseBody
  Response<Page<JobPartialDto>> getAll(@RequestParam(name = "objectId", defaultValue = "") String objectId, @RequestParam(name = "filters", defaultValue = "") String filters, Pageable pageable);

  @GetMapping("/count")
  @ResponseBody
  Response<Page<JobPartialDto>> getAllCount(@RequestParam(name = "objectId", defaultValue = "") String objectId, @RequestParam(name = "filters", defaultValue = "") String filters, Pageable pageable);

  @GetMapping("/assignee/me")
  @ResponseBody
  Response<Page<JobPartialDto>> getJobsAssignedToMe(@RequestParam(name = "objectId", defaultValue = "") String objectId, @RequestParam(name = "filters", defaultValue = "") String filters, Pageable pageable) throws StreemException;

  @GetMapping("/assignee/me/count")
  @ResponseBody
  Response<CountDto> getJobsAssignedToMeCount(@RequestParam(name = "objectId", defaultValue = "") String objectId, @RequestParam(name = "filters", defaultValue = "") String filters) throws StreemException;

  @GetMapping("/{jobId}")
  @ResponseBody
  Response<JobDto> getJob(@PathVariable Long jobId) throws ResourceNotFoundException;

  @GetMapping("/{jobId}/cwe-details")
  @ResponseBody
  Response<JobCweDto> getJobCweDetail(@PathVariable Long jobId) throws ResourceNotFoundException;

  @PostMapping
  @ResponseBody
  Response<JobDto> createJob(@RequestBody CreateJobRequest createJobRequest) throws ResourceNotFoundException, StreemException, MultiStatusException, IOException;

  @PatchMapping("/{jobId}")
  @ResponseBody
  Response<BasicDto> updateJob(@PathVariable Long jobId, @RequestBody UpdateJobRequest updateJobRequest) throws StreemException, ResourceNotFoundException;

  @PatchMapping("/{jobId}/start")
  Response<JobInfoDto> startJob(@PathVariable("jobId") Long jobId) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/{jobId}/complete")
  Response<JobInfoDto> completeJob(@PathVariable("jobId") Long jobId) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/{jobId}/complete-with-exception")
  Response<JobInfoDto> completeJobWithException(@PathVariable("jobId") Long jobId, @Valid @RequestBody JobCweDetailRequest jobCweDetailRequest) throws ResourceNotFoundException, StreemException;

  @GetMapping("/{jobId}/state")
  Response<JobStateDto> getJobState(@PathVariable("jobId") Long jobId) throws ResourceNotFoundException;

  @GetMapping("/{jobId}/stages/state")
  Response<StageDetailsDto> pollStageData(@PathVariable("jobId") Long jobId, @RequestParam(name = "stageId") Long stageId) throws ResourceNotFoundException;

  @PatchMapping("/{jobId}/assignments")
  Response<BasicDto> bulkAssign(@PathVariable(name = "jobId") Long jobId, @RequestBody TaskExecutionAssignmentRequest taskExecutionAssignmentRequest,
                                @RequestParam(required = false, defaultValue = "false") boolean notify) throws ResourceNotFoundException, StreemException, MultiStatusException;

  @GetMapping("/{jobId}/assignments")
  Response<List<TaskExecutionAssigneeDetailsView>> getAssignees(@PathVariable(name = "jobId") Long jobId);

  @GetMapping("/{jobId}/reports")
  Response<JobReportDto> getJobReport(@PathVariable("jobId") Long jobId) throws ResourceNotFoundException, JsonProcessingException;

  @GetMapping("/{jobId}/reports/print")
  Response<JobReportDto> printJobReport(@PathVariable("jobId") Long jobId) throws ResourceNotFoundException, JsonProcessingException;

  @GetMapping("/{jobId}/print")
  Response<JobPrintDto> printJob(@PathVariable("jobId") Long jobId) throws ResourceNotFoundException, JsonProcessingException;

  @GetMapping("/by/resource/{objectId}")
  Response<Page<JobPartialDto>> getAllByResource(@PathVariable("objectId") String objectId, @RequestParam("filters") String filters, Pageable pageable);

  @GetMapping("/{jobId}/info")
  Response<JobInformationDto> getJobInformation(@PathVariable("jobId") Long jobId) throws ResourceNotFoundException;

  // TODO better we could have /v1/jobs/parameter-executions?status=PENDING_FOR_APPROVAL
  // make this change in repeat task
  @GetMapping("/should-be-parameters")
  Response<Page<ShouldBeParameterStatusView>> getPendingForApprovalShouldBeParameters(@RequestParam (value = "processName", required = false, defaultValue = "") String processName, @RequestParam (value = "parameterName", required = false, defaultValue = "") String parameterName, @SortDefault(sort = ParameterValue.DEFAULT_SORT, direction = Sort.Direction.DESC) Pageable pageable);

}
