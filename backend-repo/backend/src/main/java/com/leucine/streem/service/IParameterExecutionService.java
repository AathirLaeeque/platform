package com.leucine.streem.service;

import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.ParameterDto;
import com.leucine.streem.dto.RuleHideShowDto;
import com.leucine.streem.dto.TempParameterDto;
import com.leucine.streem.dto.UserAuditDto;
import com.leucine.streem.dto.request.ParameterExecuteRequest;
import com.leucine.streem.dto.request.ParameterStateChangeRequest;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.helper.PrincipalUser;

import java.io.IOException;
import java.util.Map;

public interface IParameterExecutionService {
  ParameterDto executeParameter(Long jobId, ParameterExecuteRequest parameterExecuteRequest, boolean isAutoInitialized) throws IOException, StreemException, ResourceNotFoundException;

  ParameterDto executeParameter(Long jobId, ParameterExecuteRequest parameterExecuteRequest, boolean isAutoInitialized, Type.JobLogTriggerType jobLogTriggerType, PrincipalUser principalUser) throws IOException, StreemException, ResourceNotFoundException;

  TempParameterDto executeParameterForError(ParameterExecuteRequest parameterExecuteRequest) throws IOException, StreemException, ResourceNotFoundException;

  ParameterDto rejectParameter(ParameterStateChangeRequest parameterStateChangeRequest) throws ResourceNotFoundException, StreemException;

  ParameterDto approveParameter(ParameterStateChangeRequest parameterStateChangeRequest) throws ResourceNotFoundException, StreemException;

  void updateJobLog(Long jobId, Long parameterId, Type.Parameter parameterType, String parameterValueReason, String label, Type.JobLogTriggerType triggerType, UserAuditDto userAuditDto);

  RuleHideShowDto tempExecuteRules(Map<Long, ParameterExecuteRequest> parameterExecuteRequestMap, Long checklistId) throws IOException;
}
