package com.leucine.streem.service;

import com.leucine.streem.dto.TaskPauseReasonOrComment;
import com.leucine.streem.dto.request.TaskPauseOrResumeRequest;
import com.leucine.streem.model.TaskExecution;
import com.leucine.streem.model.User;

import java.util.List;
import java.util.Map;

public interface ITaskExecutionTimerService {

  Map<Long, List<TaskPauseReasonOrComment>> calculateDurationAndReturnReasonsOrComments(List<TaskExecution> taskExecutionList);

  void saveTaskPauseTimer(TaskPauseOrResumeRequest taskPauseOrResumeRequest, TaskExecution taskExecution, User principalUserEntity);

}
