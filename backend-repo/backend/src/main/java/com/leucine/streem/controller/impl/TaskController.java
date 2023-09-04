package com.leucine.streem.controller.impl;

import com.leucine.streem.controller.ITaskController;
import com.leucine.streem.dto.AutomationDto;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.MediaDto;
import com.leucine.streem.dto.TaskDto;
import com.leucine.streem.dto.request.*;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.service.ITaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskController implements ITaskController {
  private final ITaskService taskService;

  @Autowired
  public TaskController(ITaskService taskService) {
    this.taskService = taskService;
  }

  @Override
  public Response<TaskDto> createTask(Long checklistId, Long stageId, TaskRequest taskRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(taskService.createTask(checklistId, stageId, taskRequest)).build();
  }

  @Override
  public Response<TaskDto> updateTask(Long taskId, TaskRequest taskRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(taskService.updateTask(taskId, taskRequest)).build();
  }

  @Override
  public Response<BasicDto> reorderTasks(TaskReorderRequest taskReorderRequest) throws StreemException, ResourceNotFoundException {
    return Response.builder().data(taskService.reorderTasks(taskReorderRequest)).build();
  }

  @Override
  public Response<TaskDto> setTimer(Long taskId, TimerRequest timerRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(taskService.setTimer(taskId, timerRequest)).build();
  }

  @Override
  public Response<TaskDto> unsetTimer(Long taskId) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(taskService.unsetTimer(taskId)).build();
  }

  @Override
  public Response<TaskDto> addStop(Long taskId) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(taskService.addStop(taskId)).build();
  }

  @Override
  public Response<TaskDto> removeStop(Long taskId) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(taskService.removeStop(taskId)).build();
  }

  @Override
  public Response<TaskDto> addMedia(Long taskId, MediaRequest mediaRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(taskService.addMedia(taskId, mediaRequest)).build();
  }

  @Override
  public Response<MediaDto> updateMedia(Long taskId, Long mediaId, MediaRequest mediaRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(taskService.updateMedia(taskId, mediaId, mediaRequest)).build();
  }

  @Override
  public Response<TaskDto> deleteMedia(Long taskId, Long mediaId) {
    return Response.builder().data(taskService.deleteMedia(taskId, mediaId)).build();
  }

  @Override
  public Response<TaskDto> archiveTask(Long taskId) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(taskService.archiveTask(taskId)).build();
  }

  @Override
  public Response<TaskDto> addAutomations(Long taskId, List<AutomationRequest> automationRequests) throws StreemException, ResourceNotFoundException {
    return Response.builder().data(taskService.addAutomations(taskId, automationRequests)).build();
  }

  @Override
  public Response<TaskDto> deleteAutomation(Long taskId, Long automationId) throws StreemException, ResourceNotFoundException {
    return Response.builder().data(taskService.deleteAutomation(taskId, automationId)).build();
  }

  @Override
  public Response<AutomationDto> updateAutomation(Long taskId, Long automationId, AutomationRequest automationRequest) throws StreemException, ResourceNotFoundException {
    return Response.builder().data(taskService.updateAutomation(taskId, automationId, automationRequest)).build();
  }
}
