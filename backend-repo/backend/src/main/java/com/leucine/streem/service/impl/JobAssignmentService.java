package com.leucine.streem.service.impl;

import com.leucine.streem.constant.Action;
import com.leucine.streem.constant.State;
import com.leucine.streem.dto.projection.TaskExecutionAssigneeBasicView;
import com.leucine.streem.dto.request.TaskExecutionAssignmentRequest;
import com.leucine.streem.dto.response.Error;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.exception.ExceptionType;
import com.leucine.streem.exception.MultiStatusException;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.Job;
import com.leucine.streem.model.TaskExecution;
import com.leucine.streem.model.TaskExecutionUserMapping;
import com.leucine.streem.model.User;
import com.leucine.streem.model.helper.BaseEntity;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.repository.IJobRepository;
import com.leucine.streem.repository.ITaskExecutionAssigneeRepository;
import com.leucine.streem.repository.ITaskExecutionRepository;
import com.leucine.streem.repository.IUserRepository;
import com.leucine.streem.service.IJobAssignmentService;
import com.leucine.streem.service.IJobAuditService;
import com.leucine.streem.service.INotificationService;
import com.leucine.streem.util.Utility;
import com.leucine.streem.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobAssignmentService implements IJobAssignmentService {
  private final IJobAuditService jobAuditService;
  private final JobLogService jobLogService;
  private final IJobRepository jobRepository;
  private final INotificationService jobNotificationService;
  private final ITaskExecutionAssigneeRepository taskExecutionAssigneeRepository;
  private final ITaskExecutionRepository taskExecutionRepository;
  private final IUserRepository userRepository;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void assignUsers(Long jobId, TaskExecutionAssignmentRequest taskExecutionAssignmentRequest, boolean notify, PrincipalUser principalUser) throws ResourceNotFoundException, StreemException, MultiStatusException {
    List<Error> errorList = new ArrayList<>();
    Job job = jobRepository.findById(jobId)
      .orElseThrow(() -> new ResourceNotFoundException(jobId, ErrorCode.JOB_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    validateJobState(job.getId(), Action.Job.ASSIGN, job.getState());

    var executionAssignmentRequest = assignments(errorList, taskExecutionAssignmentRequest, principalUser);
    var assignedIds = executionAssignmentRequest.getAssignedUserIds();
    var unassignedIds = executionAssignmentRequest.getUnassignedUserIds();
    /*
      If job is blocked let it remain in blocked state
      do not change states of job
    */
    if (!State.Job.BLOCKED.equals(job.getState())) {
      if (!State.Job.IN_PROGRESS.equals(job.getState())) {
        job.setState(State.Job.ASSIGNED);
      }

      if (!taskExecutionAssigneeRepository.isAllTaskUnassigned(job.getId()) && !State.Job.IN_PROGRESS.equals(job.getState())) {
        job.setState(State.Job.UNASSIGNED);
      }
    }

    jobRepository.save(job);

    //Send email to assigned and unassigned users
    if (notify) {
      jobNotificationService.notifyAssignedUsers(assignedIds, jobId, principalUser.getOrganisationId());
      jobNotificationService.notifyUnassignedUsers(unassignedIds, principalUser.getOrganisationId());
    }

    jobAuditService.bulkAssignUsersToJob(jobId, !Utility.isEmpty(assignedIds), !Utility.isEmpty(unassignedIds), principalUser);

    if (!Utility.isEmpty(errorList)) {
      throw new MultiStatusException("assignments succeeded partially", errorList);
    }

    jobLogService.updateJobState(String.valueOf(jobId), principalUser, job.getState());
  }

  private TaskExecutionAssignmentRequest assignments(List<Error> errorList, TaskExecutionAssignmentRequest taskExecutionAssignmentRequest, PrincipalUser principalUser) {
    User principalUserEntity = userRepository.getById(principalUser.getId());

    Set<Long> assignedIds = new HashSet<>();
    Set<Long> unassignedIds = new HashSet<>();

    var taskExecutionIds = taskExecutionAssignmentRequest.getTaskExecutionIds();

    List<TaskExecution> taskExecutions = taskExecutionRepository.findAllById(taskExecutionIds);
    Map<Long, TaskExecution> taskExecutionMap = taskExecutions.stream()
      .collect(Collectors.toMap(BaseEntity::getId, Function.identity()));

    List<TaskExecutionAssigneeBasicView> taskExecutionUserMappings;
    Map<Long, Set<Long>> userIdTaskExecutionIdsMapping = null;

    if (!Utility.isEmpty(taskExecutionAssignmentRequest.getAssignedUserIds())) {
      /*
        find users from `taskExecutionAssignmentRequest.getAssignedUserIds()`
        and the taskExecutions they are assigned to. This will be used to exclude
        them from adding into `assignees` list (i.e. they are already assigned to the task execution
      */
      taskExecutionUserMappings = taskExecutionAssigneeRepository
        .findByTaskExecutionIdInAndUserIdIn(taskExecutionIds, taskExecutionAssignmentRequest.getAssignedUserIds());
      userIdTaskExecutionIdsMapping = taskExecutionUserMappings.stream()
        .collect(Collectors.groupingBy(te -> Long.valueOf(te.getUserId()), Collectors.mapping(te -> Long.valueOf(te.getTaskExecutionId()), Collectors.toSet())));
    }

    List<TaskExecutionUserMapping> assignees = new ArrayList<>();
    for (Long taskExecutionId : taskExecutionIds) {
      TaskExecution taskExecution = taskExecutionMap.get(taskExecutionId);

      if (!State.TASK_COMPLETED_STATES.contains(taskExecution.getState())) {
        if (!Utility.isEmpty(taskExecutionAssignmentRequest.getAssignedUserIds())) {
          for (Long userId : taskExecutionAssignmentRequest.getAssignedUserIds()) {
            if (!(userIdTaskExecutionIdsMapping.containsKey(userId)
              && userIdTaskExecutionIdsMapping.get(userId).contains(taskExecutionId))) {
              assignedIds.add(userId);
              assignees.add(new TaskExecutionUserMapping(taskExecutionRepository.getById(taskExecutionId), userRepository.getById(userId), principalUserEntity));
            }
          }
        }

        if (!Utility.isEmpty(taskExecutionAssignmentRequest.getUnassignedUserIds())) {
          taskExecutionUserMappings = taskExecutionAssigneeRepository
            .findByTaskExecutionIdAndUserIdIn(taskExecutionId, taskExecutionAssignmentRequest.getUnassignedUserIds());

          for (TaskExecutionAssigneeBasicView taskExecutionAssigneeBasicView : taskExecutionUserMappings) {
            if (taskExecutionAssigneeBasicView.getIsActionPerformed()) {
              ValidationUtils.addError(taskExecutionId, taskExecutionAssigneeBasicView.getUserId(), errorList, ErrorCode.FAILED_TO_UNASSIGN_SINCE_USER_PERFORMED_ACTIONS_ON_TASK);
            } else if (State.TaskExecutionAssignee.SIGNED_OFF.equals(taskExecutionAssigneeBasicView.getAssigneeState())) {
              ValidationUtils.addError(taskExecutionId, taskExecutionAssigneeBasicView.getUserId(), errorList, ErrorCode.FAILED_TO_UNASSIGN_SINCE_USER_SIGNED_OFF_TASK);
            } else {
              unassignedIds.add(Long.valueOf(taskExecutionAssigneeBasicView.getUserId()));
            }
          }

          taskExecutionAssigneeRepository.unassignUsersByTaskExecutions(Collections.singleton(taskExecutionId), unassignedIds);
        }
      } else {
        //TODO: I don't think user id is required here, this was requested from UI
        for (Long userId : taskExecutionAssignmentRequest.getAssignedUserIds()) {
          ValidationUtils.addError(taskExecutionId, String.valueOf(userId), errorList, ErrorCode.TASK_COMPLETED_ASSIGNMENT_FAILED);
        }
        for (Long userId : taskExecutionAssignmentRequest.getUnassignedUserIds()) {
          ValidationUtils.addError(taskExecutionId, String.valueOf(userId), errorList, ErrorCode.TASK_COMPLETED_ASSIGNMENT_FAILED);
        }
      }
    }

    //Bulk save all the assignees
    if (!assignees.isEmpty()) {
      taskExecutionAssigneeRepository.saveAll(assignees);
    }
    return new TaskExecutionAssignmentRequest(taskExecutionIds, assignedIds, unassignedIds);
  }

  //TODO URGENT TODO this is a copy move to a separate class
  private void validateJobState(Long jobId, Action.Job action, State.Job state) throws StreemException {
    switch (action) {
      case START:
        if (State.Job.BLOCKED.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_IS_BLOCKED);
        }
        if (State.Job.UNASSIGNED.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.UNASSIGNED_JOB_CANNOT_BE_STARTED);
        }
        if (State.Job.IN_PROGRESS.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_ALREADY_STARTED);
        }
        if (State.Job.COMPLETED_WITH_EXCEPTION.equals(state) || State.Job.COMPLETED.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_ALREADY_COMPLETED);
        }
        break;
      case COMPLETE:
        if (State.Job.BLOCKED.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_IS_BLOCKED);
        }
        if (State.Job.COMPLETED.equals(state) || State.Job.COMPLETED_WITH_EXCEPTION.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_ALREADY_COMPLETED);
        }
        if (!State.Job.IN_PROGRESS.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_IS_NOT_IN_PROGRESS);
        }
        break;
      case COMPLETE_WITH_EXCEPTION:
        if (State.Job.COMPLETED.equals(state) || State.Job.COMPLETED_WITH_EXCEPTION.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_ALREADY_COMPLETED);
        }
        break;
      case BLOCKED:
        if (State.Job.COMPLETED.equals(state) || State.Job.COMPLETED_WITH_EXCEPTION.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_ALREADY_COMPLETED);
        }

        if (!State.Job.IN_PROGRESS.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_IS_NOT_IN_PROGRESS);
        }

        break;
      case IN_PROGRESS:
        if (State.Job.IN_PROGRESS.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_ALREADY_STARTED);
        }
        if (State.Job.COMPLETED.equals(state) || State.Job.COMPLETED_WITH_EXCEPTION.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_ALREADY_COMPLETED);
        }
        if (!State.Job.BLOCKED.equals(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_IS_NOT_IN_BLOCKED);
        }
        break;
      case ASSIGN, UPDATE:
        if (State.JOB_COMPLETED_STATES.contains(state)) {
          ValidationUtils.invalidate(jobId, ErrorCode.JOB_ALREADY_COMPLETED);
        }
        break;
    }
  }
}
