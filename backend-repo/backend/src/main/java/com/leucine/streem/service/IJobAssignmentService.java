package com.leucine.streem.service;

import com.leucine.streem.dto.request.TaskExecutionAssignmentRequest;
import com.leucine.streem.exception.MultiStatusException;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.helper.PrincipalUser;

public interface IJobAssignmentService {
  void assignUsers(Long jobId, TaskExecutionAssignmentRequest taskExecutionAssignmentRequest, boolean notify, PrincipalUser principalUser) throws ResourceNotFoundException, StreemException, MultiStatusException;
}
