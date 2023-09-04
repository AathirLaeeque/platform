package com.leucine.streem.controller;

import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.StageDto;
import com.leucine.streem.dto.request.StageReorderRequest;
import com.leucine.streem.dto.request.StageRequest;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import org.springframework.web.bind.annotation.*;

@RestController
public interface IStageController {
  @PostMapping("/v1/checklists/{checklistId}/stages")
  Response<StageDto> createStage(@PathVariable Long checklistId, @RequestBody StageRequest stageRequest) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/v1/stages/{stageId}")
  Response<StageDto> updateStage(@PathVariable Long stageId, @RequestBody StageRequest stageRequest) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/v1/stages/reorder")
  Response<BasicDto> reorderStages(@RequestBody StageReorderRequest stageReorderRequest) throws StreemException;

  @PatchMapping("/v1/stages/{stageId}/archive")
  Response<StageDto> archiveStage(@PathVariable Long stageId) throws StreemException, ResourceNotFoundException;
}
