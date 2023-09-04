package com.leucine.streem.controller.impl;

import com.leucine.streem.controller.IStageController;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.StageDto;
import com.leucine.streem.dto.request.StageReorderRequest;
import com.leucine.streem.dto.request.StageRequest;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.service.IStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StageController implements IStageController {
  private final IStageService stageService;

  @Autowired
  public StageController(IStageService stageService) {
    this.stageService = stageService;
  }

  @Override
  public Response<StageDto> createStage(Long checklistId, StageRequest stageRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(stageService.createStage(checklistId, stageRequest)).build();
  }

  @Override
  public Response<BasicDto> reorderStages(StageReorderRequest stageReorderRequest) throws StreemException {
    return Response.builder().data(stageService.reorderStages(stageReorderRequest)).build();
  }

  @Override
  public Response<StageDto> updateStage(Long stageId, StageRequest stageRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(stageService.updateStage(stageId, stageRequest)).build();
  }

  @Override
  public Response<StageDto> archiveStage(Long stageId) throws StreemException, ResourceNotFoundException {
    return Response.builder().data(stageService.archiveStage(stageId)).build();
  }
}
