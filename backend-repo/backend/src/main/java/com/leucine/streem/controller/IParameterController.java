package com.leucine.streem.controller;

import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.ParameterDto;
import com.leucine.streem.dto.ParameterInfoDto;
import com.leucine.streem.dto.request.*;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public interface IParameterController {
  @PostMapping("/v1/checklists/{checklistId}/stages/{stageId}/tasks/{taskId}/parameters")
  Response<ParameterDto> addParameterToTask(@PathVariable Long checklistId, @PathVariable Long stageId, @PathVariable Long taskId,
                                    @RequestBody ParameterCreateRequest parameterCreateRequest) throws ResourceNotFoundException, StreemException, IOException;

  @PatchMapping("/v1/checklists/{checklistId}/tasks/{taskId}/parameters/map")
  Response<ParameterDto> mapParameterToTask(@PathVariable Long checklistId, @PathVariable Long taskId, @RequestBody MapParameterToTaskRequest mapParameterToTaskRequest) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/v1/checklists/{checklistId}/stages/{stageId}/tasks/{taskId}/parameters/reorder")
  Response<BasicDto> reorderParameters(@PathVariable Long checklistId, @PathVariable Long stageId, @PathVariable Long taskId,
                                      @RequestBody ParameterReorderRequest parameterReorderRequest) throws ResourceNotFoundException, StreemException;

  @PostMapping("/v1/checklists/{checklistId}/parameters")
  Response<ParameterDto> createParameter(@PathVariable Long checklistId, @RequestBody ParameterCreateRequest parameterCreateRequest) throws ResourceNotFoundException, StreemException, IOException;

  @GetMapping("/v1/parameters/{parameterId}")
  Response<ParameterDto> getParameter(@PathVariable Long parameterId) throws StreemException, ResourceNotFoundException;

  @PatchMapping("/v1/parameters/{parameterId}")
  Response<ParameterDto> updateParameter(@PathVariable Long parameterId, @RequestBody ParameterUpdateRequest parameterUpdateRequest) throws ResourceNotFoundException, StreemException, IOException;

  @PatchMapping("/v1/parameters/{parameterId}/unmap")
  Response<ParameterInfoDto> unmapParameter(@PathVariable Long parameterId) throws StreemException, ResourceNotFoundException;

  @PatchMapping("/v1/parameters/{parameterId}/archive")
  Response<ParameterInfoDto> archiveParameter(@PathVariable Long parameterId) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/v1/parameters/visibility")
  Response<BasicDto> updateParameterVisibility(@RequestBody ParameterVisibilityRequest parameterVisibilityRequest) throws StreemException;

}
