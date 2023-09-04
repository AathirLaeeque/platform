package com.leucine.streem.controller.impl;

import com.leucine.streem.controller.ITaskExecutionController;
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
import com.leucine.streem.service.ITaskExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
public class TaskExecutionController implements ITaskExecutionController {
  private final ITaskExecutionService taskExecutionService;

  @Autowired
  public TaskExecutionController(ITaskExecutionService taskExecutionService) {
    this.taskExecutionService = taskExecutionService;
  }

  @Override
  public Response<TaskDto> getTask(Long taskId, Long jobId) throws ResourceNotFoundException {
    return Response.builder().data(taskExecutionService.getTask(taskId, jobId)).build();
  }

  @Override
  public Response<TaskDto> validateTask(Long taskId, Long jobId) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(taskExecutionService.validateTask(taskId, jobId)).build();
  }

  @Override
  public Response<TaskExecutionDto> startTask(Long taskId, TaskExecutionRequest taskExecutionRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(taskExecutionService.startTask(taskId, taskExecutionRequest)).build();
  }

  @Override
  public Response<TaskExecutionDto> completeTask(Long taskId, TaskCompletionRequest taskCompletionRequest) throws ResourceNotFoundException, StreemException, IOException {
    return Response.builder().data(taskExecutionService.completeTask(taskId, taskCompletionRequest)).build();
  }

  @Override
  public Response<TaskExecutionDto> completeTaskWithException(Long taskId, TaskCompletionRequest taskCompletionRequest) throws ResourceNotFoundException, StreemException, IOException {
    return Response.builder().data(taskExecutionService.completeWithException(taskId, taskCompletionRequest)).build();
  }

  @Override
  public Response<BasicDto> signOffTask(TaskSignOffRequest taskSignOffRequest) throws StreemException {
    return Response.builder().data(taskExecutionService.signOff(taskSignOffRequest)).build();
  }

  @Override
  public Response<List<TaskExecutionAssigneeView>> getTaskExecutionAssignees(Set<Long> taskExecutionIds) {
    return Response.builder().data(taskExecutionService.getTaskExecutionAssignees(taskExecutionIds)).build();
  }

  @Override
  public Response<TaskExecutionDto> skipTask(Long taskId, TaskExecutionRequest taskExecutionRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(taskExecutionService.skipTask(taskId, taskExecutionRequest)).build();
  }

  @Override
  public Response<TaskExecutionDto> enableCorrection(Long taskId, TaskExecutionRequest taskExecutionRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(taskExecutionService.enableCorrection(taskId, taskExecutionRequest)).build();
  }

  @Override
  public Response<TaskExecutionDto> cancelCorrection(Long taskId, TaskExecutionRequest taskExecutionRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(taskExecutionService.cancelCorrection(taskId, taskExecutionRequest)).build();
  }

  @Override
  public Response<TaskExecutionDto> completeCorrection(Long taskId, TaskExecutionRequest taskExecutionRequest) throws ResourceNotFoundException, StreemException, IOException {
    return Response.builder().data(taskExecutionService.completeCorrection(taskId, taskExecutionRequest)).build();
  }

  @Override
  public Response<TaskExecutionDto> pauseTask(Long taskId, TaskPauseOrResumeRequest taskPauseOrResumeRequest) throws StreemException {
    return Response.builder().data(taskExecutionService.pauseTask(taskId, taskPauseOrResumeRequest)).build();
  }

  @Override
  public Response<TaskExecutionDto> resumeTask(Long taskId, TaskPauseOrResumeRequest taskPauseOrResumeRequest) throws StreemException {
    return Response.builder().data(taskExecutionService.resumeTask(taskId, taskPauseOrResumeRequest)).build();
  }
}
