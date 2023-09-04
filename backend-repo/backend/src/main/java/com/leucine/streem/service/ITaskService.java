package com.leucine.streem.service;

import com.leucine.streem.dto.AutomationDto;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.MediaDto;
import com.leucine.streem.dto.TaskDto;
import com.leucine.streem.dto.request.*;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;

import java.util.List;

public interface ITaskService {
  TaskDto createTask(Long checklistId, Long stageId, TaskRequest taskRequest) throws ResourceNotFoundException, StreemException;

  TaskDto updateTask(Long taskId, TaskRequest taskRequest) throws ResourceNotFoundException, StreemException;

  TaskDto addStop(Long taskId) throws ResourceNotFoundException, StreemException;

  TaskDto removeStop(Long taskId) throws ResourceNotFoundException, StreemException;

  TaskDto addMedia(Long taskId, MediaRequest mediaRequest) throws ResourceNotFoundException, StreemException;

  MediaDto updateMedia(Long taskId, Long mediaId, MediaRequest mediaRequest) throws ResourceNotFoundException, StreemException;

  TaskDto deleteMedia(Long taskId, Long mediaId);

  TaskDto setTimer(Long taskId, TimerRequest timerRequest) throws StreemException, ResourceNotFoundException;

  TaskDto unsetTimer(Long taskId) throws StreemException, ResourceNotFoundException;

  TaskDto archiveTask(Long taskId) throws StreemException, ResourceNotFoundException;

  BasicDto reorderTasks(TaskReorderRequest taskReorderRequest) throws ResourceNotFoundException, StreemException;

  TaskDto addAutomations(Long taskId, List<AutomationRequest> automationRequests) throws ResourceNotFoundException, StreemException;

  TaskDto deleteAutomation(Long taskId, Long automationId) throws ResourceNotFoundException, StreemException;

  AutomationDto updateAutomation(Long taskId, Long automationId, AutomationRequest automationRequest) throws ResourceNotFoundException, StreemException;

}
