package com.leucine.streem.service;

import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.TaskDto;
import com.leucine.streem.dto.TaskExecutionDto;
import com.leucine.streem.dto.projection.TaskExecutionAssigneeView;
import com.leucine.streem.dto.request.TaskCompletionRequest;
import com.leucine.streem.dto.request.TaskExecutionRequest;
import com.leucine.streem.dto.request.TaskPauseOrResumeRequest;
import com.leucine.streem.dto.request.TaskSignOffRequest;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.Task;
import com.leucine.streem.model.TaskExecution;
import com.leucine.streem.model.TaskExecutionUserMapping;
import com.leucine.streem.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface ITaskExecutionService {
  TaskDto getTask(Long taskId, Long jobId) throws ResourceNotFoundException;

  TaskExecutionDto startTask(Long taskId, TaskExecutionRequest taskExecutionRequest) throws ResourceNotFoundException, StreemException;

  BasicDto validateTask(Long taskId, Long jobId) throws StreemException, ResourceNotFoundException;

  TaskExecutionDto completeTask(Long taskId, TaskCompletionRequest taskCompletionRequest) throws ResourceNotFoundException, StreemException, IOException;

  TaskExecutionDto skipTask(Long taskId, TaskExecutionRequest taskExecutionRequest) throws ResourceNotFoundException, StreemException;

  TaskExecutionDto completeWithException(Long taskId, TaskCompletionRequest taskCompletionRequest) throws ResourceNotFoundException, StreemException, IOException;

  TaskExecutionDto enableCorrection(Long taskId, TaskExecutionRequest taskExecutionRequest) throws ResourceNotFoundException, StreemException;

  TaskExecutionDto completeCorrection(Long taskId, TaskExecutionRequest taskExecutionRequest) throws ResourceNotFoundException, StreemException, IOException;

  TaskExecutionDto cancelCorrection(Long taskId, TaskExecutionRequest taskExecutionRequest) throws ResourceNotFoundException, StreemException;

  List<TaskExecutionAssigneeView> getTaskExecutionAssignees(Set<Long> taskExecutionIds);

  BasicDto signOff(TaskSignOffRequest taskSignOffRequest) throws StreemException;

  TaskExecution getTaskExecutionByJobAndTaskId(Long id, Long jobId);

  TaskExecutionUserMapping validateAndGetAssignedUser(Long taskId, TaskExecution taskExecution, User user) throws ResourceNotFoundException;

  void updateUserAction(TaskExecutionUserMapping taskExecutionUserMapping);

  boolean isInvalidTimedTaskCompletedState(Task task, Long startedAt, Long endedAt);

  TaskExecutionDto pauseTask(Long taskId, TaskPauseOrResumeRequest taskPauseOrResumeRequest) throws StreemException;

  TaskExecutionDto resumeTask(Long taskId, TaskPauseOrResumeRequest taskPauseOrResumeRequest) throws StreemException;
}
