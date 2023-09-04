package com.leucine.streem.service;

import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.*;
import com.leucine.streem.dto.request.*;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.ParameterVerification;
import com.leucine.streem.model.helper.PrincipalUser;
import com.mongodb.lang.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface IJobAuditService {
  Page<JobAuditDto> getAuditsByJobId(Long jobId, String filters, Pageable pageable) throws StreemException;

  void createJob(String jobId, PrincipalUser principalUser);

  void startJob(JobInfoDto jobDto, PrincipalUser principalUser);

  void completeJob(JobInfoDto jobDto, PrincipalUser principalUser);

  void completeJobWithException(Long jobId, JobCweDetailRequest jobCweDetailRequest, PrincipalUser principalUser);

  void printJob(JobPrintDto jobPrintDto, PrincipalUser principalUser);

  void printJobReport(JobReportDto jobReportDto, PrincipalUser principalUser);

  void startTask(Long taskId, TaskExecutionRequest taskExecutionRequest, PrincipalUser principalUser);

  void completeTask(Long taskId, TaskCompletionRequest taskCompletionRequest, PrincipalUser principalUser);

  void completeTaskWithException(Long taskId, TaskCompletionRequest taskCompletionRequest, PrincipalUser principalUser);

  void skipTask(Long taskId, TaskExecutionRequest taskExecutionRequest, PrincipalUser principalUser);

  void enableTaskForCorrection(Long taskId, TaskExecutionRequest taskExecutionRequest, PrincipalUser principalUser);

  void cancelCorrection(Long taskId, TaskExecutionRequest taskExecutionRequest, PrincipalUser principalUser);

  void completeCorrection(Long taskId, TaskExecutionRequest taskExecutionRequest, PrincipalUser principalUser);

  void bulkAssignUsersToJob(Long jobId, boolean areUsersAssigned, boolean areUsersUnassigned, PrincipalUser principalUser);

  <T extends BaseParameterValueDto> void executedParameter(Long jobId, Long parameterId, @Nullable T oldValue, List<MediaDto> mediaDtoList, Type.Parameter parameterType,
                                                           boolean isExecutedForCorrection, String reason, PrincipalUser principalUser) throws IOException, ResourceNotFoundException;

  void signedOffTasks(TaskSignOffRequest taskSignOffRequest, PrincipalUser principalUser);

  void approveParameter(Long jobId, ParameterDto parameterDto, Long parameterId, PrincipalUser principalUser);

  void rejectParameter(Long jobId, ParameterDto parameterDto, Long parameterId, PrincipalUser principalUser);

  void initiateSelfVerification(Long jobId, ParameterVerification parameterVerification, PrincipalUser principalUser);

  void completeSelfVerification(Long jobId, ParameterVerification parameterVerification, PrincipalUser principalUser);

  void recallVerification(Long jobId, ParameterVerification parameterVerification, PrincipalUser principalUser);

  void sendForPeerVerification(Long jobId, ParameterVerification parameterVerification, PrincipalUser principalUser);

  void acceptPeerVerification(Long jobId, ParameterVerification parameterVerification, PrincipalUser principalUser);

  void rejectPeerVerification(Long jobId, ParameterVerification parameterVerification, PrincipalUser principalUser);

  void pauseTask(Long taskId, TaskPauseOrResumeRequest taskPauseOrResumeRequest, PrincipalUser principalUser);

  void resumeTask(Long taskId, TaskPauseOrResumeRequest taskPauseOrResumeRequest, PrincipalUser principalUser);

}
