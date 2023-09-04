package com.leucine.streem.controller.impl;

import com.leucine.streem.controller.IParameterExecutionController;
import com.leucine.streem.dto.ParameterDto;
import com.leucine.streem.dto.RuleHideShowDto;
import com.leucine.streem.dto.TempParameterDto;
import com.leucine.streem.dto.request.ParameterExecuteRequest;
import com.leucine.streem.dto.request.ParameterStateChangeRequest;
import com.leucine.streem.dto.request.ParameterTemporaryExecuteRequest;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.service.IParameterExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class ParameterExecutionController implements IParameterExecutionController {
  private final IParameterExecutionService parameterService;

  @Autowired
  public ParameterExecutionController(IParameterExecutionService parameterService) {
    this.parameterService = parameterService;
  }

  @Override
  public Response<ParameterDto> executeParameter(ParameterExecuteRequest parameterExecuteRequest) throws IOException, StreemException, ResourceNotFoundException {
    return Response.builder().data(parameterService.executeParameter(parameterExecuteRequest.getJobId(), parameterExecuteRequest, false)).build();
  }

  @Override
  public Response<TempParameterDto> fixError(ParameterExecuteRequest parameterExecuteRequest) throws IOException, StreemException, ResourceNotFoundException {
    return Response.builder().data(parameterService.executeParameterForError(parameterExecuteRequest)).build();
  }

  @Override
  public Response<ParameterDto> rejectParameter(ParameterStateChangeRequest parameterStateChangeRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(parameterService.rejectParameter(parameterStateChangeRequest)).build();
  }

  @Override
  public Response<ParameterDto> approveParameter(ParameterStateChangeRequest parameterStateChangeRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(parameterService.approveParameter(parameterStateChangeRequest)).build();
  }

  @Override
  public Response<RuleHideShowDto> executeTemporary(ParameterTemporaryExecuteRequest parameterTemporaryExecuteRequest) throws IOException {
    return  Response.builder().data(parameterService.tempExecuteRules(parameterTemporaryExecuteRequest.parameterValues(), parameterTemporaryExecuteRequest.checklistId())).build();
  }

}
