package com.leucine.streem.service;

import com.leucine.streem.dto.AutomationDto;
import com.leucine.streem.dto.TaskDto;
import com.leucine.streem.dto.request.AutomationRequest;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.helper.PrincipalUser;

import java.io.IOException;
import java.util.List;

public interface ITaskAutomationService {
  TaskDto addTaskAutomations(Long taskId, List<AutomationRequest> automationRequests) throws ResourceNotFoundException;

  AutomationDto updateAutomation(Long automationId, AutomationRequest automationRequest) throws ResourceNotFoundException;

  TaskDto deleteTaskAutomation(Long taskId, Long automationId) throws ResourceNotFoundException;

  void completeTaskAutomations(Long taskId, Long jobId, String automationReason, PrincipalUser principalUser) throws IOException, ResourceNotFoundException, StreemException;
}
