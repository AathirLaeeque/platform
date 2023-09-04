package com.leucine.streem.controller;

import com.leucine.streem.dto.AutomationDto;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.MediaDto;
import com.leucine.streem.dto.TaskDto;
import com.leucine.streem.dto.request.*;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public interface ITaskController {

  @PostMapping("/v1/checklists/{checklistId}/stages/{stageId}/tasks")
  Response<TaskDto> createTask(@PathVariable Long checklistId, @PathVariable Long stageId, @RequestBody TaskRequest taskRequest) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/v1/tasks/{taskId}")
  Response<TaskDto> updateTask(@PathVariable Long taskId, @RequestBody TaskRequest taskRequest) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/v1/tasks/reorder")
  Response<BasicDto> reorderTasks(@RequestBody TaskReorderRequest taskReorderRequest) throws StreemException, ResourceNotFoundException;

  @PatchMapping("/v1/tasks/{taskId}/timer/set")
  Response<TaskDto> setTimer(@PathVariable Long taskId, @RequestBody TimerRequest timerRequest) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/v1/tasks/{taskId}/timer/unset")
  Response<TaskDto> unsetTimer(@PathVariable Long taskId) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/v1/tasks/{taskId}/stop/add")
  Response<TaskDto> addStop(@PathVariable Long taskId) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/v1/tasks/{taskId}/stop/remove")
  Response<TaskDto> removeStop(@PathVariable Long taskId) throws ResourceNotFoundException, StreemException;

  @PostMapping("/v1/tasks/{taskId}/medias")
  Response<TaskDto> addMedia(@PathVariable Long taskId, @RequestBody MediaRequest mediaRequest) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/v1/tasks/{taskId}/medias/{mediaId}")
  Response<MediaDto> updateMedia(@PathVariable Long taskId, @PathVariable Long mediaId, @RequestBody MediaRequest mediaRequest) throws ResourceNotFoundException, StreemException;

  @DeleteMapping("/v1/tasks/{taskId}/medias/{mediaId}")
  Response<TaskDto> deleteMedia(@PathVariable Long taskId, @PathVariable Long mediaId);

  @PatchMapping("/v1/tasks/{taskId}/archive")
  Response<TaskDto> archiveTask(@PathVariable Long taskId) throws ResourceNotFoundException, StreemException;

  @PostMapping("/v1/tasks/{taskId}/automations")
  Response<TaskDto> addAutomations(@PathVariable Long taskId, @RequestBody List<AutomationRequest> automationRequests) throws StreemException, ResourceNotFoundException;

  @DeleteMapping("/v1/tasks/{taskId}/automations/{automationId}")
  Response<TaskDto> deleteAutomation(@PathVariable Long taskId, @PathVariable Long automationId) throws StreemException, ResourceNotFoundException;

  @PatchMapping("/v1/tasks/{taskId}/automations/{automationId}")
  Response<AutomationDto> updateAutomation(@PathVariable Long taskId, @PathVariable Long automationId, @RequestBody AutomationRequest automationRequest) throws StreemException, ResourceNotFoundException;


}

