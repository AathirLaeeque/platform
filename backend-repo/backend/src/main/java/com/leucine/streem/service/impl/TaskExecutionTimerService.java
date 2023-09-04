package com.leucine.streem.service.impl;

import com.leucine.streem.dto.TaskPauseReasonOrComment;
import com.leucine.streem.dto.request.TaskPauseOrResumeRequest;
import com.leucine.streem.model.TaskExecution;
import com.leucine.streem.model.TaskExecutionTimer;
import com.leucine.streem.model.User;
import com.leucine.streem.repository.ITaskExecutionTimerRepository;
import com.leucine.streem.service.ITaskExecutionTimerService;
import com.leucine.streem.util.DateTimeUtils;
import com.leucine.streem.util.Utility;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class TaskExecutionTimerService implements ITaskExecutionTimerService {
  private final ITaskExecutionTimerRepository taskExecutionTimerRepository;

  @Override
  public void saveTaskPauseTimer(TaskPauseOrResumeRequest taskPauseOrResumeRequest, TaskExecution taskExecution, User principalUserEntity) {
    long now = DateTimeUtils.now();
    TaskExecutionTimer pauseTimer = TaskExecutionTimer.builder()
      .taskExecutionId(taskExecution.getId())
      .pausedAt(now)
      .reason(taskPauseOrResumeRequest.reason())
      .comment(taskPauseOrResumeRequest.comment())
      .build();

    pauseTimer.setCreatedBy(principalUserEntity);
    pauseTimer.setCreatedAt(now);
    pauseTimer.setModifiedAt(now);
    pauseTimer.setModifiedBy(principalUserEntity);
    taskExecutionTimerRepository.save(pauseTimer);
  }

  @Override
  public Map<Long, List<TaskPauseReasonOrComment>> calculateDurationAndReturnReasonsOrComments(List<TaskExecution> taskExecutionList) {
    Map<Long, List<TaskPauseReasonOrComment>> reasonOrCommentsMap = new HashMap<>();
    for (TaskExecution taskExecution : taskExecutionList) {
      Long duration = taskExecution.getDuration();
      List<TaskExecutionTimer> totalTaskDurationList;
      List<TaskPauseReasonOrComment> taskPauseReasonOrCommentList = new ArrayList<>();
      long now = DateTimeUtils.now();
      if (taskExecution.getStartedAt() != null) {
        if (Utility.isEmpty(duration)) {
          duration = now - taskExecution.getStartedAt();
        } else {
          int totalPauseResumeCount = 0;
          //TODO: Fetch in batches
          totalTaskDurationList = taskExecutionTimerRepository.findAllByTaskExecutionIdOrderByIdDesc(taskExecution.getId());
          long pauseDuration = 0L, resumeDuration = 0L;

          for (TaskExecutionTimer timer : totalTaskDurationList) {
            if (!Utility.isEmpty(timer.getPausedAt())) {
              pauseDuration = pauseDuration + timer.getPausedAt();
              totalPauseResumeCount += 1;
            }
            if (!Utility.isEmpty(timer.getResumedAt())) {
              resumeDuration = resumeDuration + timer.getResumedAt();
              totalPauseResumeCount += 1;
            }
            if (!Utility.isEmpty(timer.getReason()) || !Utility.isEmpty(timer.getComment())) {
              taskPauseReasonOrCommentList.add(new TaskPauseReasonOrComment(timer.getReason(), timer.getComment()));
            }
          }

          pauseDuration = pauseDuration - taskExecution.getStartedAt();
          if (totalPauseResumeCount % 2 == 0) {
            pauseDuration = pauseDuration + now;
          }

          duration = Math.abs(resumeDuration - pauseDuration);
        }

        taskExecution.setDuration(duration);
      }
      reasonOrCommentsMap.put(taskExecution.getId(), taskPauseReasonOrCommentList);
    }
    return reasonOrCommentsMap;
  }
}
