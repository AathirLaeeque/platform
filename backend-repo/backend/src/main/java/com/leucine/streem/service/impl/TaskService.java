package com.leucine.streem.service.impl;

import com.leucine.streem.constant.State;
import com.leucine.streem.dto.AutomationDto;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.MediaDto;
import com.leucine.streem.dto.TaskDto;
import com.leucine.streem.dto.mapper.IMediaMapper;
import com.leucine.streem.dto.mapper.ITaskMapper;
import com.leucine.streem.dto.request.*;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.exception.ExceptionType;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.*;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.repository.*;
import com.leucine.streem.service.ITaskAutomationService;
import com.leucine.streem.service.IChecklistService;
import com.leucine.streem.service.ITaskService;
import com.leucine.streem.util.DateTimeUtils;
import com.leucine.streem.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService {
  private final IChecklistService checklistService;
  private final IMediaMapper mediaMapper;
  private final IMediaRepository mediaRepository;
  private final IStageRepository stageRepository;
  private final ITaskMapper taskMapper;
  private final ITaskMediaMappingRepository taskMediaMappingRepository;
  private final ITaskRepository taskRepository;
  private final IUserRepository userRepository;
  private final ITaskAutomationService automationService;

  @Override
  public TaskDto createTask(Long checklistId, Long stageId, TaskRequest taskRequest) throws ResourceNotFoundException, StreemException {
    log.info("[createTask] Request to create task, checklistId: {}, stageId: {}, taskRequest: {}", checklistId, stageId, taskRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Stage stage = stageRepository.findById(stageId).orElseThrow(() -> new ResourceNotFoundException(stageId, ErrorCode.STAGE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Checklist checklist = stage.getChecklist();
    checklistService.validateChecklistModificationState(checklist.getId(), checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());

    Task task = new Task();
    task.setName(taskRequest.getName());
    task.setOrderTree(taskRequest.getOrderTree());
    task.setCreatedBy(principalUserEntity);
    task.setModifiedBy(principalUserEntity);
    task.setStage(stage);

    return taskMapper.toDto(taskRepository.save(task));
  }

  @Override
  public TaskDto updateTask(Long taskId, TaskRequest taskRequest) throws ResourceNotFoundException, StreemException {
    log.info("[updateTask] Request to update task, taskId: {}, taskRequest: {}", taskId, taskRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Checklist checklist = task.getStage().getChecklist();
    checklistService.validateChecklistModificationState(checklist.getId(), checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());

    task.setName(taskRequest.getName());
    task.setModifiedBy(principalUserEntity);

    return taskMapper.toDto(taskRepository.save(task));
  }

  @Override
  public TaskDto setTimer(Long taskId, TimerRequest timerRequest) throws StreemException, ResourceNotFoundException {
    log.info("[setTimer] Request to set timer to task, taskId: {}, timerRequest: {}", taskId, timerRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    validateTimerRequest(timerRequest, taskId);
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Checklist checklist = task.getStage().getChecklist();
    checklistService.validateChecklistModificationState(checklist.getId(), checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());

    task.setTimed(true);
    task.setModifiedBy(principalUserEntity);
    task.setTimerOperator(timerRequest.getTimerOperator().name());
    task.setMinPeriod(timerRequest.getMinPeriod());
    task.setMaxPeriod(timerRequest.getMaxPeriod());

    return taskMapper.toDto(taskRepository.save(task));
  }

  @Override
  public TaskDto unsetTimer(Long taskId) throws StreemException, ResourceNotFoundException {
    log.info("[unsetTimer] Request unset timer from task, taskId: {}", taskId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Checklist checklist = task.getStage().getChecklist();
    checklistService.validateChecklistModificationState(checklist.getId(), checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());

    task.setTimed(false);
    task.setModifiedBy(principalUserEntity);
    task.setTimerOperator(null);
    task.setMinPeriod(null);
    task.setMaxPeriod(null);

    return taskMapper.toDto(taskRepository.save(task));
  }

  @Override
  public TaskDto addStop(Long taskId) throws ResourceNotFoundException, StreemException {
    log.info("[addStop] Request to add stop to task, taskId: {}", taskId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Checklist checklist = task.getStage().getChecklist();
    checklistService.validateChecklistModificationState(checklist.getId(), checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());

    task.setHasStop(true);
    task.setModifiedBy(principalUserEntity);

    return taskMapper.toDto(taskRepository.save(task));
  }

  @Override
  public TaskDto removeStop(Long taskId) throws ResourceNotFoundException, StreemException {
    log.info("[removeStop] Request to remove stop from task, taskId: {}", taskId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Checklist checklist = task.getStage().getChecklist();
    checklistService.validateChecklistModificationState(checklist.getId(), checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());

    task.setHasStop(false);
    task.setModifiedBy(principalUserEntity);

    return taskMapper.toDto(taskRepository.save(task));
  }

  @Override
  public TaskDto addMedia(Long taskId, MediaRequest mediaRequest) throws ResourceNotFoundException, StreemException {
    log.info("[addMedia] Request to add media to task, taskId: {}, mediaRequest: {}", taskId, mediaRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Checklist checklist = task.getStage().getChecklist();
    checklistService.validateChecklistModificationState(checklist.getId(), checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());

    Media media = mediaRepository.findById(mediaRequest.getMediaId())
            .orElseThrow(() -> new ResourceNotFoundException(mediaRequest.getMediaId(), ErrorCode.MEDIA_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    media.setName(mediaRequest.getName());
    media.setDescription(mediaRequest.getDescription());

    task.addMedia(mediaRepository.save(media), principalUserEntity);
    return taskMapper.toDto(taskRepository.save(task));
  }

  @Override
  public MediaDto updateMedia(Long taskId, Long mediaId, MediaRequest mediaRequest) throws ResourceNotFoundException, StreemException {
    log.info("[updateMedia] Request to update media of a task, taskId: {}, mediaId: {}, mediaRequest: {}", taskId, mediaRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    TaskMediaMapping taskMediaMapping = taskMediaMappingRepository.getByTaskIdAndMediaId(taskId, mediaId)
            .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_MEDIA_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Checklist checklist = checklistService.findByTaskId(taskId);
    checklistService.validateChecklistModificationState(checklist.getId(), checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());

    Media media = taskMediaMapping.getMedia();

    mediaMapper.update(mediaRequest, media);
    media.setModifiedBy(principalUserEntity);

    return mediaMapper.toDto(mediaRepository.save(media));
  }

  //TODO Work with UI to remove unnecessary returns, UI knows the action done
  // need not return whole object, applies at every other place
  @Override
  public TaskDto deleteMedia(Long taskId, Long mediaId) {
    log.info("[deleteMedia] Request to delete media from task, taskId: {}, mediaId: {}", taskId, mediaId);
    taskMediaMappingRepository.deleteByTaskIdAndMediaId(taskId, mediaId);
    return taskMapper.toDto(taskRepository.findById(taskId).get());
  }

  @Override
  public TaskDto archiveTask(Long taskId) throws StreemException, ResourceNotFoundException {
    log.info("[archiveTask] Request to archive task, taskId: {}", taskId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Checklist checklist = task.getStage().getChecklist();
    checklistService.validateChecklistModificationState(checklist.getId(), checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());

    for (Parameter parameter : task.getParameters()) {
      parameter.setArchived(true);
    }

    task.setArchived(true);
    task.setModifiedBy(principalUserEntity);

    return taskMapper.toDto(taskRepository.save(task));
  }

  @Override
  public BasicDto reorderTasks(TaskReorderRequest taskReorderRequest) throws ResourceNotFoundException, StreemException {
    log.info("[reorderTasks] Request to reorder tasks, taskReorderRequest: {}", taskReorderRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var state = checklistService.findById(taskReorderRequest.getChecklistId()).getState();
    if (!State.CHECKLIST_EDIT_STATES.contains(state)) {
      ValidationUtils.invalidate(taskReorderRequest.getChecklistId(), ErrorCode.PROCESS_CANNOT_BE_MODFIFIED);
    }
    taskReorderRequest.getTasksOrder().forEach((taskId, order) -> taskRepository.reorderTask(taskId, order, principalUser.getId(), DateTimeUtils.now()));

    BasicDto basicDto = new BasicDto();
    basicDto.setMessage("success");
    return basicDto;
  }

  @Override
  public TaskDto addAutomations(Long taskId, List<AutomationRequest> automationRequests) throws ResourceNotFoundException, StreemException {
    log.info("[addAutomations] Request to add task automations, taskId: {}, automationRequests: {}", taskId, automationRequests);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Checklist checklist = task.getStage().getChecklist();
    checklistService.validateChecklistModificationState(checklist.getId(), checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());

    return automationService.addTaskAutomations(taskId, automationRequests);
  }

  @Override
  public TaskDto deleteAutomation(Long taskId, Long automationId) throws ResourceNotFoundException, StreemException {
    log.info("[addAutomation] Request to delete task automation, taskId: {}, automationId: {}", taskId, automationId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Checklist checklist = task.getStage().getChecklist();
    checklistService.validateChecklistModificationState(checklist.getId(), checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());

    return automationService.deleteTaskAutomation(taskId, automationId);
  }

  @Override
  public AutomationDto updateAutomation(Long taskId, Long automationId, AutomationRequest automationRequest) throws ResourceNotFoundException, StreemException {
    log.info("[addAutomation] Request to update task automation, taskId: {}, automationId: {}, automationRequest: {}", taskId, automationId, automationRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Checklist checklist = task.getStage().getChecklist();
    checklistService.validateChecklistModificationState(checklist.getId(), checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());

    return automationService.updateAutomation(automationId, automationRequest);
  }

  private void validateTimerRequest(TimerRequest timerRequest, Long taskId) throws StreemException {
    Long minPeriod = timerRequest.getMinPeriod();
    Long maxPeriod = timerRequest.getMaxPeriod();

    switch (timerRequest.getTimerOperator()) {
      case LESS_THAN:
        if (maxPeriod <= 0) {
          ValidationUtils.invalidate(taskId, ErrorCode.TIMED_TASK_LT_TIMER_CANNOT_BE_ZERO);
        }
        break;
      case NOT_LESS_THAN:
        if(minPeriod <= 0)  {
          ValidationUtils.invalidate(taskId, ErrorCode.TIMED_TASK_NLT_MIN_PERIOD_CANNOT_BE_ZERO);
        }
        if (minPeriod >= maxPeriod) {
          ValidationUtils.invalidate(taskId, ErrorCode.TIMED_TASK_NLT_MAX_PERIOD_SHOULD_BE_GT_MIN_PERIOD);
        }
    }
  }
}
