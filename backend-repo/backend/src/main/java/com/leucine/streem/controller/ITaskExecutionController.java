package com.leucine.streem.controller;

import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.TaskDto;
import com.leucine.streem.dto.TaskExecutionDto;
import com.leucine.streem.dto.projection.TaskExecutionAssigneeView;
import com.leucine.streem.dto.request.TaskCompletionRequest;
import com.leucine.streem.dto.request.TaskExecutionRequest;
import com.leucine.streem.dto.request.TaskPauseOrResumeRequest;
import com.leucine.streem.dto.request.TaskSignOffRequest;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
public interface ITaskExecutionController {
  @GetMapping("/v1/tasks/{taskId}")
  Response<TaskDto> getTask(@PathVariable Long taskId, @RequestParam(name = "jobId") Long jobId) throws ResourceNotFoundException;

  // TODO fix apis when you move to repeat, dynamic task
  @GetMapping("/v1/tasks/{taskId}/validate")
  Response<TaskDto> validateTask(@PathVariable Long taskId, @RequestParam(name = "jobId") Long jobId) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/v1/tasks/{taskId}/start")
  Response<TaskExecutionDto> startTask(@PathVariable Long taskId, @RequestBody TaskExecutionRequest taskExecutionRequest) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/v1/tasks/{taskId}/complete")
  Response<TaskExecutionDto> completeTask(@PathVariable Long taskId, @RequestBody TaskCompletionRequest taskCompletionRequest) throws ResourceNotFoundException, StreemException, IOException;

  @PatchMapping("/v1/tasks/{taskId}/complete-with-exception")
  Response<TaskExecutionDto> completeTaskWithException(@PathVariable Long taskId, @RequestBody TaskCompletionRequest taskCompletionRequest) throws ResourceNotFoundException, StreemException, IOException;

  @PatchMapping("/v1/tasks/sign-off")
  Response<BasicDto> signOffTask(@RequestBody TaskSignOffRequest taskSignOffRequest) throws StreemException;

  @PostMapping("/v1/tasks/assignments")
  Response<List<TaskExecutionAssigneeView>> getTaskExecutionAssignees(@RequestBody Set<Long> taskExecutionIds);

  @PatchMapping("/v1/tasks/{taskId}/skip")
  Response<TaskExecutionDto> skipTask(@PathVariable Long taskId, @RequestBody TaskExecutionRequest taskExecutionRequest) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/v1/tasks/{taskId}/correction/start")
  Response<TaskExecutionDto> enableCorrection(@PathVariable Long taskId, @RequestBody TaskExecutionRequest taskExecutionRequest) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/v1/tasks/{taskId}/correction/cancel")
  Response<TaskExecutionDto> cancelCorrection(@PathVariable Long taskId, @RequestBody TaskExecutionRequest taskExecutionRequest) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/v1/tasks/{taskId}/correction/complete")
  Response<TaskExecutionDto> completeCorrection(@PathVariable Long taskId, @RequestBody TaskExecutionRequest taskExecutionRequest) throws ResourceNotFoundException, StreemException, IOException;

  @PostMapping("/v1/tasks/{taskId}/pause")
  Response<TaskExecutionDto> pauseTask(@PathVariable Long taskId, @RequestBody TaskPauseOrResumeRequest taskPauseOrResumeRequest) throws StreemException;

  @PatchMapping("/v1/tasks/{taskId}/resume")
  Response<TaskExecutionDto> resumeTask(@PathVariable Long taskId, @RequestBody TaskPauseOrResumeRequest taskPauseOrResumeRequest) throws StreemException;
}
