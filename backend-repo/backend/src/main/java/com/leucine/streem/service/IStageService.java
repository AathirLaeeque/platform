package com.leucine.streem.service;

import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.StageDto;
import com.leucine.streem.dto.request.StageReorderRequest;
import com.leucine.streem.dto.request.StageRequest;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;

public interface IStageService {
  StageDto createStage(Long checklistId, StageRequest stageRequest) throws ResourceNotFoundException, StreemException;

  StageDto updateStage(Long stageId, StageRequest stageRequest) throws ResourceNotFoundException, StreemException;

  StageDto archiveStage(Long stageId) throws StreemException, ResourceNotFoundException;

  BasicDto reorderStages(StageReorderRequest stageReorderRequest) throws StreemException;
}
