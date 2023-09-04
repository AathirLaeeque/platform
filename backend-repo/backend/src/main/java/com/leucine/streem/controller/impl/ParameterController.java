package com.leucine.streem.controller.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leucine.streem.controller.IParameterController;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.ParameterDto;
import com.leucine.streem.dto.ParameterInfoDto;
import com.leucine.streem.dto.request.*;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.service.IParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ParameterController implements IParameterController {

  private final IParameterService parameterService;

  @Autowired
  public ParameterController(IParameterService parameterService) {
    this.parameterService = parameterService;
  }

  @Override
  public Response<ParameterDto> addParameterToTask(Long checklistId, Long stageId, Long taskId, ParameterCreateRequest parameterCreateRequest) throws ResourceNotFoundException, StreemException, IOException {
    return Response.builder().data(parameterService.addParameterToTask(checklistId, stageId, taskId, parameterCreateRequest)).build();
  }

  @Override
  public Response<BasicDto> reorderParameters(Long checklistId, Long stageId, Long taskId, ParameterReorderRequest parameterReorderRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(parameterService.reorderParameters(checklistId, stageId, taskId, parameterReorderRequest)).build();
  }

  @Override
  public Response<ParameterDto> createParameter(Long checklistId, ParameterCreateRequest parameterCreateRequest) throws ResourceNotFoundException, StreemException, IOException {
    return Response.builder().data(parameterService.createParameter(checklistId, parameterCreateRequest)).build();
  }

  @Override
  public Response<ParameterDto> getParameter(Long parameterId) throws StreemException, ResourceNotFoundException {
    return Response.builder().data(parameterService.getParameter(parameterId)).build();
  }

  @Override
  public Response<ParameterDto> mapParameterToTask(Long checklistId, Long taskId, MapParameterToTaskRequest mapParameterToTaskRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(parameterService.mapParameterToTask(checklistId, taskId, mapParameterToTaskRequest)).build();
  }

  @Override
  public Response<ParameterInfoDto> unmapParameter(Long parameterId) throws StreemException, ResourceNotFoundException {
    return Response.builder().data(parameterService.unmapParameter(parameterId)).build();
  }

  @Override
  public Response<ParameterDto> updateParameter(Long parameterId, ParameterUpdateRequest parameterUpdateRequest) throws ResourceNotFoundException, StreemException, IOException {
    return Response.builder().data(parameterService.updateParameter(parameterId, parameterUpdateRequest)).build();
  }

  @Override
  public Response<ParameterInfoDto> archiveParameter(Long parameterId) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(parameterService.archiveParameter(parameterId)).build();
  }

  @Override
  public Response<BasicDto> updateParameterVisibility(ParameterVisibilityRequest parameterVisibilityRequest) throws StreemException {
    return Response.builder().data(parameterService.updateParameterVisibility(parameterVisibilityRequest)).build();
  }
}
