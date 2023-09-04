package com.leucine.streem.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leucine.streem.constant.Action;
import com.leucine.streem.constant.Operator;
import com.leucine.streem.constant.State;
import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.*;
import com.leucine.streem.dto.mapper.IJobAuditMapper;
import com.leucine.streem.dto.request.*;
import com.leucine.streem.model.*;
import com.leucine.streem.model.helper.JobAuditParameter;
import com.leucine.streem.model.helper.JobAuditParameterValue;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.model.helper.SpecificationBuilder;
import com.leucine.streem.model.helper.parameter.ChoiceParameterBase;
import com.leucine.streem.model.helper.parameter.YesNoParameter;
import com.leucine.streem.model.helper.search.SearchCriteria;
import com.leucine.streem.repository.*;
import com.leucine.streem.service.IJobAuditService;
import com.leucine.streem.util.DateTimeUtils;
import com.leucine.streem.util.JsonUtils;
import com.leucine.streem.util.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.SetUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobAuditService implements IJobAuditService {
  public static final String CREATE_JOB = "{0} {1} (ID:{2}) created the Job";
  public static final String START_JOB = "{0} {1} (ID:{2}) started the Job";
  public static final String COMPLETED_JOB = "{0} {1} (ID:{2}) completed the Job";
  public static final String COMPLETED_JOB_WITH_EXCEPTION = "{0} {1} (ID:{2}) completed the job with exception stating reason \"{3}\"";
  public static final String PRINT_JOB = "{0} {1} (ID:{2}) downloaded a PDF of the Job";

  public static final String PRINT_JOB_REPORT = "{0} {1} (ID:{2}) downloaded a PDF of the Job Summary Report";

  public static final String START_TASK = "{0} {1} (ID:{2}) started the Task \"{3}\" of the Stage \"{4}\"";
  public static final String COMPLETE_TASK = "{0} {1} (ID:{2}) completed the Task \"{3}\" of the Stage \"{4}\"";
  public static final String COMPLETE_TASK_WITH_REASON = "{0} {1} (ID:{2}) completed the Task \"{3}\" stating reason \"{4}\" of the Stage \"{5}\"";
  public static final String SKIP_TASK = "{0} {1} (ID:{2}) skipped the Task \"{3}\" stating reason \"{4}\" of the Stage \"{5}\"";
  public static final String COMPLETED_TASK_WITH_EXCEPTION = "{0} {1} (ID:{2}) completed the Task \"{3}\" with exception stating reason \"{4}\" of the Stage \"{5}\"";
  public static final String ENABLED_TASK_FOR_CORRECTION = "{0} {1} (ID:{2}) enabled the Task \"{3}\" for correction stating reason \"{4}\" of the Stage \"{5}\"";
  public static final String CANCEL_CORRECTION = "{0} {1} (ID:{2}) cancelled error correction on Task \"{3}\" of the Stage \"{4}\"";
  public static final String COMPLETE_CORRECTION = "{0} {1} (ID:{2}) completed error correction for Task \"{3}\" of the Stage \"{4}\"";
  public static final String ASSIGNED_USERS_TO_TASKS = "{0} {1} (ID:{2}) Assigned User(s) to Task(s) in the Job";
  public static final String UNASSIGNED_USERS_FROM_TASKS = "{0} {1} (ID:{2}) Unassigned User(s) from Task(s) in the Job";
  public static final String SIGNED_OFF_TASKS = "{0} {1} (ID:{2}) signed off on their completed Tasks";

  public static final String CHOICE_PARAMETER_CHECKED = "{0} {1} (ID:{2}) checked \"{3}\" for Task \"{4}\" of the Stage \"{5}\"";
  public static final String CHOICE_PARAMETER_CORRECTION_CHECKED = "{0} {1} (ID:{2}) checked \"{3}\" when correcting the Task \"{4}\" of the Stage \"{5}\"";
  public static final String CHOICE_PARAMETER_UNCHECKED = "{0} {1} (ID:{2}) unchecked \"{3}\" for Task \"{4}\" of the Stage \"{5}\"";
  public static final String CHOICE_PARAMETER_CORRECTION_UNCHECKED = "{0} {1} (ID:{2}) unchecked \"{3}\" when correcting the Task \"{4}\" of the Stage \"{5}\"";

  public static final String CHOICE_PARAMETER_SELECTED = "{0} {1} (ID:{2}) selected \"{3}\" for Task \"{4}\" of the Stage \"{5}\"";
  public static final String CHOICE_PARAMETER_CORRECTION_SELECTED = "{0} {1} (ID:{2}) selected \"{3}\" when correcting the Task \"{4}\" of the Stage \"{5}\"";
  public static final String CHOICE_PARAMETER_DESELECTED = "{0} {1} (ID:{2}) deselected \"{3}\" for Task \"{4}\" of the Stage \"{5}\"";
  public static final String CHOICE_PARAMETER_CORRECTION_DESELECTED = "{0} {1} (ID:{2}) deselected \"{3}\" when correcting the Task \"{4}\" of the Stage \"{5}\"";

  public static final String YES_NO_PARAMETER = "{0} {1} (ID:{2}) selected \"{3}\" for Task \"{4}\" of the Stage \"{5}\"";
  public static final String YES_NO_PARAMETER_WITH_REASON = "{0} {1} (ID:{2}) selected \"{3}\" stating reason \"{4}\" for Task \"{5}\" of the Stage \"{6}\"";
  public static final String YES_NO_PARAMETER_CORRECTION = "{0} {1} (ID:{2}) selected \"{3}\" on correcting the Task \"{4}\" of the Stage \"{5}\"";
  public static final String YES_NO_PARAMETER_CORRECTION_WITH_REASON = "{0} {1} (ID:{2}) selected \"{3}\" stating reason \"{4}\" on correcting the Task \"{5}\" of the Stage \"{6}\"";
  public static final String TEXT_BOX_PARAMETER = "{0} {1} (ID:{2}) updated text input to \"{3}\" in task \"{4}\" of the Stage \"{5}\"";
  public static final String TEXT_BOX_PARAMETER_ON_CORRECTION = "{0} {1} (ID:{2}) updated text input to \"{3}\" on correcting the task \"{4}\" of the Stage \"{5}\"";

  public static final String SHOULD_BE_PARAMETER = "{0} {1} (ID:{2}) updated \"{3}\" from \"{4}\" to \"{5}\" in task \"{6}\" of the Stage \"{7}\"";
  public static final String SHOULD_BE_PARAMETER_INITIAL = "{0} {1} (ID:{2}) provided \"{3}\" for \"{4}\" in task \"{5}\" of the Stage \"{6}\"";
  public static final String SHOULD_BE_PARAMETER_WITH_REASON = "{0} {1} (ID:{2}) updated \"{3}\" from \"{4}\" to \"{5}\" stating reason \"{6}\" in task \"{7}\" of the Stage \"{8}\"";
  public static final String SHOULD_BE_PARAMETER_INITIAL_WITH_REASON = "{0} {1} (ID:{2}) provided \"{3}\" for \"{4}\" stating reason \"{5}\" in task \"{6}\" of the Stage \"{7}\"";

  public static final String SHOULD_BE_PARAMETER_CORRECTION = "{0} {1} (ID:{2}) updated \"{3}\" from \"{4}\" to \"{5}\" when correcting the task \"{6}\" of the Stage \"{7}\"";
  public static final String SHOULD_BE_PARAMETER_CORRECTION_INITIAL = "{0} {1} (ID:{2}) provided \"{3}\" for \"{4}\" when correcting the task \"{5}\" of the Stage \"{6}\"";
  public static final String SHOULD_BE_PARAMETER_CORRECTION_WITH_REASON = "{0} {1} (ID:{2}) updated \"{3}\" from \"{4}\" to \"{5}\" stating reason \"{6}\" when correcting the task \"{7}\" of the Stage \"{8}\"";
  public static final String SHOULD_BE_PARAMETER_CORRECTION_INITIAL_WITH_REASON = "{0} {1} (ID:{2}) provided \"{3}\" for \"{4}\" stating reason \"{5}\" when correcting the task \"{6}\" of the Stage \"{7}\"";

  public static final String NUMBER_PARAMETER = "{0} {1} (ID:{2}) updated the value of \"{3}\" to \"{4}\" in task \"{5}\" of the Stage \"{6}\"";
  public static final String NUMBER_PARAMETER_INITIAL = "{0} {1} (ID:{2}) provided a value of \"{3}\" for \"{4}\" for Task \"{5}\" of the Stage \"{6}\"";
  public static final String NUMBER_PARAMETER_CORRECTION = "{0} {1} (ID:{2}) updated the value of \"{3}\" to \"{4}\" when correcting the task \"{5}\" of the Stage \"{6}\"";
  public static final String NUMBER_PARAMETER_CORRECTION_INITIAL = "{0} {1} (ID:{2}) provided a value of \"{3}\" for \"{4}\" when correcting the task \"{5}\" of the Stage \"{6}\"";

  // TODO hack to get the date parameter to work, "ABC" is replaced with 0 this needs to be changed, below 4 audits
  public static final String DATE_PARAMETER = "{0} {1} (ID:{2}) updated \"{{{ABC}}}\" for \"{3}\" for the Task \"{4}\" of the Stage \"{5}\"";
  public static final String DATE_PARAMETER_INITIAL = "{0} {1} (ID:{2}) set \"{{{ABC}}}\" for \"{3}\" for the task \"{4}\" of the Stage \"{5}\"";
  public static final String DATE_PARAMETER_CORRECTION = "{0} {1} (ID:{2}) updated \"{{{ABC}}}\" for \"{3}\" when correcting the task \"{4}\" of the Stage \"{5}\"";
  public static final String DATE_PARAMETER_CORRECTION_INITIAL = "{0} {1} (ID:{2}) set \"{{{ABC}}}\" for \"{3}\" when correcting the task \"{4}\" of the Stage \"{5}\"";

  public static final String CALCULATION_PARAMETER = "{0} {1} (ID:{2}) updated the calculated value of \"{3}\" to \"{4}\" for Task \"{5}\" of the Stage \"{6}\"";
  public static final String CALCULATION_PARAMETER_INITIAL = "{0} {1} (ID:{2}) calculated the value for \"{3}\" as \"{4}\" for Task \"{5}\" of the Stage \"{6}\"";

  public static final String CALCULATION_PARAMETER_CORRECTION = "{0} {1} (ID:{2}) updated the calculated value of \"{3}\" to \"{4}\" when correcting the Task \"{5}\" of the Stage \"{6}\"";
  public static final String CALCULATION_PARAMETER_CORRECTION_INITIAL = "{0} {1} (ID:{2}) calculated the value for \"{3}\" as \"{4}\" when correcting the Task \"{5}\" of the Stage \"{6}\"";

  public static final String RESOURCE_PARAMETER = "{0} {1} (ID:{2}) updated selection for \"{3}\" to \"{4}\" for Task \"{5}\" of the Stage \"{6}\"";
  public static final String RESOURCE_PARAMETER_INITIAL = "{0} {1} (ID:{2}) selected \"{3}\" for \"{4}\" for Task \"{5}\" of the Stage \"{6}\"";
  public static final String RESOURCE_PARAMETER_DESELECTION = "{0} {1} (ID:{2}) deselected \"{3}\" for \"{4}\" for Task \"{5}\" of the Stage \"{6}\"";

  public static final String RESOURCE_PARAMETER_CORRECTION = "{0} {1} (ID:{2}) updated selection for \"{3}\" to \"{4}\" when correcting the Task \"{5}\" of the Stage \"{6}\"";
  public static final String RESOURCE_PARAMETER_CORRECTION_INITIAL = "{0} {1} (ID:{2}) selected \"{3}\" for \"{4}\" when correcting the Task \"{5}\" of the Stage \"{6}\"";
  public static final String RESOURCE_PARAMETER_CORRECTION_INITIAL_DESELECTED = "{0} {1} (ID:{2}) deselected \"{3}\" for \"{4}\" when correcting the Task \"{5}\" of the Stage \"{6}\"";

  public static final String APPROVE_PARAMETER = "{0} {1} (ID:{2}) approved the provided values for \"{3}\"  in task \"{4}\" of the Stage \"{5}";
  public static final String REJECT_PARAMETER = "{0} {1} (ID:{2}) rejected the provided values for \"{3}\"  in task \"{4}\" of the Stage \"{5}";

  public static final String SIGNATURE_PARAMETER = "{0} {1} (ID:{2}) updated the signature in Task \"{3}\" of the Stage \"{4}\"";
  public static final String SIGNATURE_PARAMETER_CORRECTION = "{0} {1} (ID:{2}) updated the signature on correcting the Task \"{3}\" of the Stage \"{4}\"";
  public static final String MEDIA_PARAMETER = "{0} {1} (ID:{2}) uploaded file {3} to Task \"{4}\" of the Stage \"{5}\"";
  public static final String MEDIA_PARAMETER_CORRECTION = "{0} {1} (ID:{2}) uploaded file {3} on correcting the Task \"{4}\" of the Stage \"{5}\"";
  public static final String MEDIA_PARAMETER_ARCHIVED = "{0} {1} (ID:{2}) archived file {3} to Task \"{4}\" of the Stage \"{5}\"";
  public static final String MEDIA_PARAMETER_ARCHIVED_CORRECTION = "{0} {1} (ID:{2}) archived file {3} on correcting the Task \"{4}\" of the Stage \"{5}\"";
  public static final String INITIATE_SELF_VERIFICATION = "{0} {1} (ID:{2}) initiated self verification for the Parameter \"{3}\" in Task \"{4}\" of the Stage \"{5}\"";
  public static final String APPROVE_SELF_VERIFICATION = "{0} {1} (ID:{2}) approved self verification for the Parameter \"{3}\" in Task \"{4}\" of the Stage \"{5}\"";
  public static final String RECALL_SELF_VERIFICATION = "{0} {1} (ID:{2}) cancel self verification for the Parameter \"{3}\" in Task \"{4}\" of the Stage \"{5}\"";
  public static final String SUBMITTED_FOR_PEER_VERIFICATION = "{0} {1} (ID:{2}) requested  peer verification for the Parameter \"{3}\" in Task \"{4}\" of the Stage \"{5}\"";
  public static final String RECALL_PEER_VERIFICATION = "{0} {1} (ID:{2}) requesting recall peer verification for the Parameter \"{3}\" in Task \"{4}\" of the Stage \"{5}\"";
  public static final String ACCEPT_PEER_VERIFICATION = "{0} {1} (ID:{2}) approved peer verification for the Parameter \"{3}\" in Task \"{4}\" of the Stage \"{5}\"";
  public static final String REJECT_PEER_VERIFICATION = "{0} {1} (ID:{2}) rejected peer verification for the Parameter \"{3}\" in Task \"{4}\" of the Stage \"{5}\"";
  private static final String PAUSE_TASK = "{0} {1} (ID:{2}) stated reason \"{3}\" to pause the Task \"{4}\" of the Stage \"{5}\"";
  private static final String RESUME_TASK = "{0} {1} (ID:{2}) resumed the Task \"{3}\" of the Stage \"{4}\"";

  private final IJobAuditRepository jobAuditRepository;
  private final IJobAuditMapper jobAuditMapper;
  private final IStageRepository stageRepository;
  private final ITaskRepository taskRepository;
  private final ObjectMapper objectMapper;
  private final IParameterRepository parameterRepository;
  private final IParameterValueRepository parameterValueRepository;
  private final ITempParameterValueRepository tempParameterValueRepository;

  @Override
  public Page<JobAuditDto> getAuditsByJobId(Long jobId, String filters, Pageable pageable) {
    List<Object> values = new ArrayList<>();
    values.add(jobId);
    SearchCriteria mandatorySearchCriteria = new SearchCriteria()
      .setField("jobId")
      .setOp(Operator.Search.EQ.toString())
      .setValues(values);
    Specification<JobAudit> specification = SpecificationBuilder.createSpecification(filters, Collections.singletonList(mandatorySearchCriteria));

    Page<JobAudit> jobAudits = jobAuditRepository.findAll(specification, pageable);
    List<JobAuditDto> jobAuditDtoList = jobAuditMapper.toDto(jobAudits.getContent());
    return new PageImpl<>(jobAuditDtoList, pageable, jobAudits.getTotalElements());
  }

  @Override
  public void createJob(String jobId, PrincipalUser principalUser) {
    String details = formatMessage(CREATE_JOB, principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId());
    jobAuditRepository.save(getInfoAudit(details, null, Long.parseLong(jobId), null, null, principalUser));
  }

  @Async
  @Override
  public void startJob(JobInfoDto jobDto, PrincipalUser principalUser) {
    String details = formatMessage(START_JOB, principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId());
    jobAuditRepository.save(getInfoAudit(details, null, Long.parseLong(jobDto.getId()), null, null, principalUser));
  }

  @Async
  @Override
  public void completeJob(JobInfoDto jobDto, PrincipalUser principalUser) {
    String details = formatMessage(COMPLETED_JOB, principalUser.getFirstName(),
      principalUser.getLastName(), principalUser.getEmployeeId());
    jobAuditRepository.save(getInfoAudit(details, null, Long.parseLong(jobDto.getId()), null, null, principalUser));
  }

  @Override
  public void completeJobWithException(Long jobId, JobCweDetailRequest jobCweDetailRequest, PrincipalUser principalUser) {
    String details = formatMessage(COMPLETED_JOB_WITH_EXCEPTION, principalUser.getFirstName(),
      principalUser.getLastName(), principalUser.getEmployeeId(), jobCweDetailRequest.getReason().get());
    jobAuditRepository.save(getInfoAudit(details, null, jobId, null, null, principalUser));
  }

  @Async
  @Override
  public void printJob(JobPrintDto jobPrintDto, PrincipalUser principalUser) {
    String details = formatMessage(PRINT_JOB, principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId());
    jobAuditRepository.save(getInfoAudit(details, null, Long.parseLong(jobPrintDto.getId()), null, null, principalUser));
  }

  @Async
  @Override
  public void printJobReport(JobReportDto jobReportDto, PrincipalUser principalUser) {
    String details = formatMessage(PRINT_JOB_REPORT, principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId());
    jobAuditRepository.save(getInfoAudit(details, null, Long.parseLong(jobReportDto.getId()), null, null, principalUser));
  }

  @Async
  @Override
  public void startTask(Long taskId, TaskExecutionRequest taskExecutionRequest, PrincipalUser principalUser) {
    Task task = taskRepository.findById(taskId).get();
    Stage stage = stageRepository.findByTaskId(taskId);
    String details = formatMessage(START_TASK, principalUser.getFirstName(),
      principalUser.getLastName(), principalUser.getEmployeeId(), task.getName(), stage.getName());
    jobAuditRepository.save(getInfoAudit(details, null, taskExecutionRequest.getJobId(), stage.getId(), taskId, principalUser));
  }

  @Override
  public void initiateSelfVerification(Long jobId, ParameterVerification parameterVerification, PrincipalUser principalUser) {
    Task task = parameterVerification.getParameterValue().getParameter().getTask();
    Stage stage = stageRepository.findByTaskId(task.getId());
    String details = formatMessage(INITIATE_SELF_VERIFICATION, parameterVerification.getCreatedBy().getFirstName(), parameterVerification.getCreatedBy().getLastName(), parameterVerification.getCreatedBy().getEmployeeId(),
      parameterVerification.getParameterValue().getParameter().getLabel(), task.getName(), stage.getName());

    jobAuditRepository.save(getInfoAudit(details, null, jobId, stage.getId(), task.getId(), principalUser));

  }

  @Override
  public void completeSelfVerification(Long jobId, ParameterVerification parameterVerification, PrincipalUser principalUser) {
    Task task = parameterVerification.getParameterValue().getParameter().getTask();
    Stage stage = stageRepository.findByTaskId(task.getId());
    String details = formatMessage(APPROVE_SELF_VERIFICATION, parameterVerification.getModifiedBy().getFirstName(), parameterVerification.getModifiedBy().getLastName(), parameterVerification.getModifiedBy().getEmployeeId(),
      parameterVerification.getParameterValue().getParameter().getLabel(), task.getName(), stage.getName());
    jobAuditRepository.save(getInfoAudit(details, null, jobId, stage.getId(), task.getId(), principalUser));
  }

  @Override
  public void recallVerification(Long jobId, ParameterVerification parameterVerification, PrincipalUser principalUser) {
    Task task = parameterVerification.getParameterValue().getParameter().getTask();
    Stage stage = stageRepository.findByTaskId(task.getId());
    if (parameterVerification.getVerificationType().equals(Type.VerificationType.PEER)) {
      String details = formatMessage(RECALL_PEER_VERIFICATION, parameterVerification.getModifiedBy().getFirstName(), parameterVerification.getModifiedBy().getLastName(), parameterVerification.getModifiedBy().getEmployeeId(),
        parameterVerification.getParameterValue().getParameter().getLabel(), task.getName(), stage.getName());
      jobAuditRepository.save(getInfoAudit(details, null, jobId, stage.getId(), task.getId(), principalUser));
    } else {
      String details = formatMessage(RECALL_SELF_VERIFICATION, parameterVerification.getModifiedBy().getFirstName(), parameterVerification.getModifiedBy().getLastName(), parameterVerification.getModifiedBy().getEmployeeId(),
        parameterVerification.getParameterValue().getParameter().getLabel(), task.getName(), stage.getName());
      jobAuditRepository.save(getInfoAudit(details, null, jobId, stage.getId(), task.getId(), principalUser));
    }
  }

  @Override
  public void sendForPeerVerification(Long jobId, ParameterVerification parameterVerification, PrincipalUser principalUser) {
    Task task = parameterVerification.getParameterValue().getParameter().getTask();
    Stage stage = stageRepository.findByTaskId(task.getId());
    String details = formatMessage(SUBMITTED_FOR_PEER_VERIFICATION, parameterVerification.getModifiedBy().getFirstName(), parameterVerification.getModifiedBy().getLastName(), parameterVerification.getModifiedBy().getEmployeeId(),
      parameterVerification.getParameterValue().getParameter().getLabel(), task.getName(), stage.getName());
    jobAuditRepository.save(getInfoAudit(details, null, jobId, stage.getId(), task.getId(), principalUser));
  }

  @Override
  public void acceptPeerVerification(Long jobId, ParameterVerification parameterVerification, PrincipalUser principalUser) {
    Task task = parameterVerification.getParameterValue().getParameter().getTask();
    Stage stage = stageRepository.findByTaskId(task.getId());
    String details = formatMessage(ACCEPT_PEER_VERIFICATION, parameterVerification.getModifiedBy().getFirstName(), parameterVerification.getModifiedBy().getLastName(), parameterVerification.getModifiedBy().getEmployeeId(),
      parameterVerification.getParameterValue().getParameter().getLabel(), task.getName(), stage.getName());
    jobAuditRepository.save(getInfoAudit(details, null, jobId, stage.getId(), task.getId(), principalUser));
  }


  @Override
  public void resumeTask(Long taskId, TaskPauseOrResumeRequest taskPauseOrResumeRequest, PrincipalUser principalUser) {
    Task task = taskRepository.findById(taskId).get();
    Stage stage = stageRepository.findByTaskId(taskId);
    String details = formatMessage(RESUME_TASK, principalUser.getFirstName(),
      principalUser.getLastName(), principalUser.getEmployeeId(), task.getName(), stage.getName());
    jobAuditRepository.save(getInfoAudit(details, null, taskPauseOrResumeRequest.jobId(), stage.getId(), taskId, principalUser));
  }

  @Override
  public void pauseTask(Long taskId, TaskPauseOrResumeRequest taskPauseOrResumeRequest, PrincipalUser principalUser) {
    Task task = taskRepository.findById(taskId).get();
    Stage stage = stageRepository.findByTaskId(taskId);
    String details = formatMessage(PAUSE_TASK, principalUser.getFirstName(),
      principalUser.getLastName(), principalUser.getEmployeeId(), taskPauseOrResumeRequest.reason().getText(), task.getName(), stage.getName());
    jobAuditRepository.save(getInfoAudit(details, null, taskPauseOrResumeRequest.jobId(), stage.getId(), taskId, principalUser));
  }

  @Override
  public void rejectPeerVerification(Long jobId, ParameterVerification parameterVerification, PrincipalUser principalUser) {
    Task task = parameterVerification.getParameterValue().getParameter().getTask();
    Stage stage = stageRepository.findByTaskId(task.getId());
    String details = formatMessage(REJECT_PEER_VERIFICATION, parameterVerification.getModifiedBy().getFirstName(), parameterVerification.getModifiedBy().getLastName(), parameterVerification.getModifiedBy().getEmployeeId(),
      parameterVerification.getParameterValue().getParameter().getLabel(), task.getName(), stage.getName());
    jobAuditRepository.save(getInfoAudit(details, null, jobId, stage.getId(), task.getId(), principalUser));
  }

  @Async
  @Override
  public void completeTask(Long taskId, TaskCompletionRequest taskCompletionRequest, PrincipalUser principalUser) {
    Task task = taskRepository.findById(taskId).get();
    Stage stage = stageRepository.findByTaskId(taskId);

    String details = "";

    if (Utility.isEmpty(taskCompletionRequest.getReason())) {
      details = formatMessage(COMPLETE_TASK, principalUser.getFirstName(),
        principalUser.getLastName(), principalUser.getEmployeeId(), task.getName(), stage.getName());
    } else {
      details = formatMessage(COMPLETE_TASK_WITH_REASON, principalUser.getFirstName(),
        principalUser.getLastName(), principalUser.getEmployeeId(), task.getName(), taskCompletionRequest.getReason(),
        stage.getName());
    }

    JobAudit jobAudit = getInfoAudit(details, null, taskCompletionRequest.getJobId(), stage.getId(), taskId, principalUser);
    jobAuditRepository.save(jobAudit);
  }

  @Async
  @Override
  public void completeTaskWithException(Long taskId, TaskCompletionRequest taskCompletionRequest, PrincipalUser principalUser) {
    Task task = taskRepository.findById(taskId).get();
    Stage stage = stageRepository.findByTaskId(taskId);
    String details = formatMessage(COMPLETED_TASK_WITH_EXCEPTION, principalUser.getFirstName(),
      principalUser.getLastName(), principalUser.getEmployeeId(), task.getName(), taskCompletionRequest.getReason(), stage.getName());
    JobAudit jobAudit = getInfoAudit(details, null, taskCompletionRequest.getJobId(), stage.getId(), taskId, principalUser);
    jobAuditRepository.save(jobAudit);
  }

  @Async
  @Override
  public void skipTask(Long taskId, TaskExecutionRequest taskExecutionRequest, PrincipalUser principalUser) {
    Task task = taskRepository.findById(taskId).get();
    Stage stage = stageRepository.findByTaskId(taskId);
    String details = formatMessage(SKIP_TASK, principalUser.getFirstName(),
      principalUser.getLastName(), principalUser.getEmployeeId(), task.getName(), taskExecutionRequest.getReason(), stage.getName());
    JobAudit jobAudit = getInfoAudit(details, null, taskExecutionRequest.getJobId(), stage.getId(), taskId, principalUser);
    jobAuditRepository.save(jobAudit);
  }

  @Async
  @Override
  public void enableTaskForCorrection(Long taskId, TaskExecutionRequest taskExecutionRequest, PrincipalUser principalUser) {
    Task task = taskRepository.findById(taskId).get();
    Stage stage = stageRepository.findByTaskId(taskId);
    String details = formatMessage(ENABLED_TASK_FOR_CORRECTION, principalUser.getFirstName(),
      principalUser.getLastName(), principalUser.getEmployeeId(), task.getName(), taskExecutionRequest.getCorrectionReason(), stage.getName());
    JobAudit jobAudit = getInfoAudit(details, null, taskExecutionRequest.getJobId(), stage.getId(), taskId, principalUser);
    jobAuditRepository.save(jobAudit);
  }

  @Async
  @Override
  public void cancelCorrection(Long taskId, TaskExecutionRequest taskExecutionRequest, PrincipalUser principalUser) {
    Task task = taskRepository.findById(taskId).get();
    Stage stage = stageRepository.findByTaskId(taskId);
    String details = formatMessage(CANCEL_CORRECTION, principalUser.getFirstName(),
      principalUser.getLastName(), principalUser.getEmployeeId(), task.getName(), stage.getName());
    JobAudit jobAudit = getInfoAudit(details, null, taskExecutionRequest.getJobId(), stage.getId(), taskId, principalUser);
    jobAuditRepository.save(jobAudit);
  }

  @Async
  @Override
  public void completeCorrection(Long taskId, TaskExecutionRequest taskExecutionRequest, PrincipalUser principalUser) {
    Task task = taskRepository.findById(taskId).get();
    Stage stage = stageRepository.findByTaskId(taskId);
    String details = formatMessage(COMPLETE_CORRECTION, principalUser.getFirstName(),
      principalUser.getLastName(), principalUser.getEmployeeId(), task.getName(), stage.getName());
    JobAudit jobAudit = getInfoAudit(details, null, taskExecutionRequest.getJobId(), stage.getId(), taskId, principalUser);
    jobAuditRepository.save(jobAudit);
  }

  @Async
  @Override
  public void bulkAssignUsersToJob(Long jobId, boolean areUsersAssigned, boolean areUsersUnassigned, PrincipalUser principalUser) {
    String details;
    JobAudit jobAudit;

    if (areUsersAssigned) {
      details = formatMessage(ASSIGNED_USERS_TO_TASKS, principalUser.getFirstName(),
        principalUser.getLastName(), principalUser.getEmployeeId());
      jobAudit = getInfoAudit(details, null, jobId, null, null, principalUser);
      jobAuditRepository.save(jobAudit);
    }

    if (areUsersUnassigned) {
      details = formatMessage(UNASSIGNED_USERS_FROM_TASKS, principalUser.getFirstName(),
        principalUser.getLastName(), principalUser.getEmployeeId());
      jobAudit = getInfoAudit(details, null, jobId, null, null, principalUser);
      jobAuditRepository.save(jobAudit);
    }
  }

  //  @Async //TODO Removing temporarily because this needs to be call after the transaction is completed
  @Override
  public <T extends BaseParameterValueDto> void executedParameter(Long jobId, Long parameterId, @Nullable T oldValue, List<MediaDto> oldMedias, Type.Parameter parameterType,
                                                                  boolean isExecutedForCorrection, String reason, PrincipalUser principalUser) throws IOException {
    Task task = taskRepository.findByParameterId(parameterId);
    Stage stage = stageRepository.findByTaskId(task.getId());
    Parameter parameter = parameterRepository.findById(parameterId).get();
    List<TempParameterValueMediaMapping> tempParameterValueMedias = new ArrayList<>();
    List<ParameterValueMediaMapping> parameterValueMedias = new ArrayList<>();

    ParameterValueBase parameterValue;

    if (isExecutedForCorrection) {
      TempParameterValue tempParameterValueNew = tempParameterValueRepository.findByParameterIdAndJobId(parameterId, jobId).get();
      parameterValue = tempParameterValueNew;
      tempParameterValueMedias = tempParameterValueNew.getMedias();
    } else {
      ParameterValue parameterValueNew = parameterValueRepository.findByJobIdAndParameterId(jobId, parameterId);
      parameterValue = parameterValueNew;
      parameterValueMedias = parameterValueNew.getMedias();
    }


    switch (parameterType) {
      case CHECKLIST:
        saveChecklistAudit(jobId, stage, task, parameter, parameterValue.getChoices(), oldValue, isExecutedForCorrection, principalUser);
        break;
      case SINGLE_SELECT:
        saveSingleSelectAudit(jobId, stage, task, parameter, parameterValue.getChoices(), oldValue, isExecutedForCorrection, principalUser);
        break;
      case MULTISELECT:
        saveMultiSelectAudit(jobId, stage, task, parameter, parameterValue.getChoices(), oldValue, isExecutedForCorrection, principalUser);
        break;
      case MULTI_LINE:
      case SINGLE_LINE:
        saveTextParameterAudit(jobId, stage, task, parameterValue.getValue(), isExecutedForCorrection, principalUser);
        break;
      case SHOULD_BE:
        saveShouldBeParameter(jobId, stage, task, parameter, parameterValue.getValue(), oldValue, isExecutedForCorrection, reason, principalUser);
        break;
      case SIGNATURE:
        saveSignatureAudit(jobId, stage, task, isExecutedForCorrection, principalUser);
        break;
      case YES_NO:
        saveYesNoAudit(jobId, stage, task, parameter, parameterValue.getChoices(), isExecutedForCorrection, reason, principalUser);
        break;
      case MEDIA, FILE_UPLOAD:
        saveMediaAudit(jobId, stage, task, parameterValueMedias, tempParameterValueMedias, oldMedias, isExecutedForCorrection, principalUser);
        break;
      case NUMBER:
        saveNumberParameter(jobId, stage, task, parameter, parameterValue.getValue(), oldValue, isExecutedForCorrection, principalUser);
        break;
      case DATE:
        saveDateParameter(jobId, stage, task, parameter, parameterValue.getValue(), oldValue, isExecutedForCorrection, principalUser);
        break;
      case DATE_TIME:
        saveDateTimeParameter(jobId, stage, task, parameter, parameterValue.getValue(), oldValue, isExecutedForCorrection, principalUser);
        break;
      case CALCULATION:
        saveCalculationParameter(jobId, stage, task, parameter, parameterValue.getValue(), oldValue, isExecutedForCorrection, principalUser);
        break;
      case RESOURCE:
        saveResourceParameter(jobId, stage, task, parameter, parameterValue.getChoices().toString(), oldValue, isExecutedForCorrection, principalUser);
        break;
      case MULTI_RESOURCE:
        saveMultiResourceParameter(jobId, stage, task, parameter, parameterValue.getChoices().toString(), oldValue, isExecutedForCorrection, principalUser);
        break;
    }
  }

  @Async
  @Override
  public void signedOffTasks(TaskSignOffRequest taskSignOffRequest, PrincipalUser principalUser) {
    String details = formatMessage(SIGNED_OFF_TASKS, principalUser.getFirstName(),
      principalUser.getLastName(), principalUser.getEmployeeId());
    JobAudit jobAudit = getInfoAudit(details, null, taskSignOffRequest.getJobId(), null, null, principalUser);
    jobAuditRepository.save(jobAudit);
  }

  @Async
  @Override
  public void approveParameter(Long jobId, ParameterDto parameterDto, Long parameterId, PrincipalUser principalUser) {
    Task task = taskRepository.findByParameterId(parameterId);
    Stage stage = stageRepository.findByTaskId(task.getId());

    try {
      String parameter = getAuditParameter(parameterDto);
      String details = formatMessage(APPROVE_PARAMETER, principalUser.getFirstName(),
        principalUser.getLastName(), principalUser.getEmployeeId(), parameter, task.getName(), stage.getName());
      JobAudit jobAudit = getInfoAudit(details, null, jobId, stage.getId(), task.getId(), principalUser);
      jobAuditRepository.save(jobAudit);
    } catch (Exception ex) {
      log.error("[approveParameter] error saving audit", ex);
    }
  }

  @Async
  @Override
  public void rejectParameter(Long jobId, ParameterDto parameterDto, Long parameterId, PrincipalUser principalUser) {
    Task task = taskRepository.findByParameterId(parameterId);
    Stage stage = stageRepository.findByTaskId(task.getId());

    try {
      String parameter = getAuditParameter(parameterDto);
      String details = formatMessage(REJECT_PARAMETER, principalUser.getFirstName(),
        principalUser.getLastName(), principalUser.getEmployeeId(), parameter, task.getName(), stage.getName());
      JobAudit jobAudit = getInfoAudit(details, null, jobId, stage.getId(), task.getId(), principalUser);
      jobAuditRepository.save(jobAudit);
    } catch (Exception exception) {
      log.error("[rejectParameter] error saving audit", exception);
    }
  }

  private void saveAudit(String details, JobAuditParameter jobAuditParameter, Long jobId, Long stageId, Long taskId, PrincipalUser principalUser) {
    if (!details.isEmpty()) {
      JobAudit jobAudit = getInfoAudit(details, jobAuditParameter, jobId, stageId, taskId, principalUser);
      jobAuditRepository.save(jobAudit);
    }
  }

  private String getAuditParameter(ParameterDto parameterDto) throws JsonProcessingException {
    switch (Type.Parameter.valueOf(parameterDto.getType())) {
      case SHOULD_BE:
        return parameterDto.getLabel();
      case YES_NO:
        YesNoParameter yesNoParameter = JsonUtils.readValue(parameterDto.getData().toString(), YesNoParameter.class);
        return yesNoParameter.getName();
      default:
        throw new IllegalArgumentException("Incorrect parameter type");
    }
  }

  //TODO facility id probably needs to be the selected facility or
  // do we need facility id to be saved ?
  private JobAudit getInfoAudit(String details, JobAuditParameter jobAuditParameter, Long jobId, Long stageId, Long taskId, PrincipalUser principalUser) {
    if (jobAuditParameter == null) {
      jobAuditParameter = new JobAuditParameter();
    }
    // TODO Workaround need to fix this
    JsonNode jobParameters = JsonUtils.valueToNode(jobAuditParameter.getParameters());
    return new JobAudit()
      .setDetails(details)
      .setParameters(jobParameters)
      .setTriggeredAt(DateTimeUtils.now())
      .setTriggeredAt(DateTimeUtils.now())
      .setTriggeredBy(principalUser.getId())
      .setJobId(jobId)
      .setStageId(stageId)
      .setTaskId(taskId)
      .setAction(Action.Audit.EXECUTE_JOB)
      .setOrganisationsId(principalUser.getOrganisationId());
  }


  //TODO facility id probably needs to be the selected facility or
  // do we need facility id to be saved ?
  private JobAudit getInfoAudit(String details, Long jobId, Long stageId, Long taskId, User principalUser) {
    return new JobAudit()
      .setDetails(details)
      .setTriggeredAt(DateTimeUtils.now())
      .setTriggeredAt(DateTimeUtils.now())
      .setTriggeredBy(principalUser.getId())
      .setJobId(jobId)
      .setStageId(stageId)
      .setTaskId(taskId)
      .setAction(Action.Audit.EXECUTE_JOB)
      .setOrganisationsId(principalUser.getOrganisationId());
  }

  private <T extends BaseParameterValueDto> void saveChecklistAudit(Long jobId, Stage stage, Task task, Parameter parameter, JsonNode newData,
                                                                    @Nullable T oldValue, boolean isExecutedForCorrection,
                                                                    PrincipalUser principalUser) throws IOException {
    String details = "";
    List<ChoiceParameterBase> parameters = JsonUtils.jsonToCollectionType(parameter.getData(), List.class, ChoiceParameterBase.class);
    Map<String, ChoiceParameterBase> choicesDetailsMap =
      parameters.stream().collect(Collectors.toMap(ChoiceParameterBase::getId, p -> p));
    String selectedItemDetails = "";
    if (null != oldValue) {
      JsonNode oldChoices = oldValue.getChoices();
      if (null != oldChoices) {
        Map<String, String> result = objectMapper.convertValue(oldChoices, new TypeReference<>() {
        });
        Map<String, String> newChoicesMap = objectMapper.convertValue(newData, new TypeReference<>() {
        });
        for (Map.Entry<String, String> newChoice : newChoicesMap.entrySet()) {
          String state = result.get(newChoice.getKey());
          if (!state.equals(newChoice.getValue())) {
            if (!State.Selection.SELECTED.name().equals(state)) {
              details = formatMessage(isExecutedForCorrection ? CHOICE_PARAMETER_CORRECTION_CHECKED : CHOICE_PARAMETER_CHECKED,
                principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
                choicesDetailsMap.get(newChoice.getKey()).getName(), task.getName(), stage.getName());
            } else {
              details = formatMessage(isExecutedForCorrection ? CHOICE_PARAMETER_CORRECTION_UNCHECKED : CHOICE_PARAMETER_UNCHECKED,
                principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
                choicesDetailsMap.get(newChoice.getKey()).getName(), task.getName(), stage.getName());
            }
            break;
          }
        }
      }
    } else {
      Map<String, String> newChoicesMap = objectMapper.convertValue(newData, new TypeReference<>() {
      });
      for (Map.Entry<String, String> newChoice : newChoicesMap.entrySet()) {
        if (State.Selection.SELECTED.name().equals(newChoice.getKey())) {
          details = formatMessage(isExecutedForCorrection ? CHOICE_PARAMETER_CORRECTION_CHECKED : CHOICE_PARAMETER_CHECKED,
            principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
            choicesDetailsMap.get(newChoice.getKey()).getName(), task.getName(), stage.getName());
          break;
        }
      }
    }
    saveAudit(details, null, jobId, stage.getId(), task.getId(), principalUser);
  }

  private <T extends BaseParameterValueDto> void saveSingleSelectAudit(Long jobId, Stage stage, Task task, Parameter parameter, JsonNode newData,
                                                                       @Nullable T oldValue, boolean isExecutedForCorrection,
                                                                       PrincipalUser principalUser) throws IOException {
    List<ChoiceParameterBase> parameters = JsonUtils.jsonToCollectionType(parameter.getData(), List.class, ChoiceParameterBase.class);
    Map<String, ChoiceParameterBase> choicesDetailsMap =
      parameters.stream().collect(Collectors.toMap(ChoiceParameterBase::getId, p -> p));
    String selectedItemDetails = "";
    String deselectedItemDetails = "";

    if (!Utility.isEmpty(oldValue) && !Utility.isEmpty(oldValue.getChoices())) {
      JsonNode oldChoices = oldValue.getChoices();
      Map<String, String> result = objectMapper.convertValue(oldChoices, new TypeReference<>() {
      });
      Map<String, String> newChoicesMap = objectMapper.convertValue(newData, new TypeReference<>() {
      });
      for (Map.Entry<String, String> newChoice : newChoicesMap.entrySet()) {
        String state = result.get(newChoice.getKey());
        if (!state.equals(newChoice.getValue())) {
          if (!State.Selection.SELECTED.name().equals(state)) {
            selectedItemDetails = formatMessage(isExecutedForCorrection ? CHOICE_PARAMETER_CORRECTION_SELECTED : CHOICE_PARAMETER_SELECTED,
              principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
              choicesDetailsMap.get(newChoice.getKey()).getName(), task.getName(), stage.getName());
          } else {
            deselectedItemDetails = formatMessage(isExecutedForCorrection ? CHOICE_PARAMETER_CORRECTION_DESELECTED : CHOICE_PARAMETER_DESELECTED,
              principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
              choicesDetailsMap.get(newChoice.getKey()).getName(), task.getName(), stage.getName());
          }
        }
      }
      saveAudit(selectedItemDetails, null, jobId, stage.getId(), task.getId(), principalUser);
      saveAudit(deselectedItemDetails, null, jobId, stage.getId(), task.getId(), principalUser);
    } else {
      Map<String, String> newChoicesMap = objectMapper.convertValue(newData, new TypeReference<>() {
      });
      for (Map.Entry<String, String> newChoice : newChoicesMap.entrySet()) {
        if (State.Selection.SELECTED.name().equals(newChoice.getValue())) {
          selectedItemDetails = formatMessage(isExecutedForCorrection ? CHOICE_PARAMETER_CORRECTION_SELECTED : CHOICE_PARAMETER_SELECTED,
            principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
            choicesDetailsMap.get(newChoice.getKey()).getName(), task.getName(), stage.getName());
          saveAudit(selectedItemDetails, null, jobId, stage.getId(), task.getId(), principalUser);
        }
      }
    }
  }

  private <T extends BaseParameterValueDto> void saveMultiSelectAudit(Long jobId, Stage stage, Task task, Parameter parameter, JsonNode newData,
                                                                      @Nullable T oldValue, boolean isExecutedForCorrection,
                                                                      PrincipalUser principalUser) throws IOException {
    List<ChoiceParameterBase> parameters = JsonUtils.jsonToCollectionType(parameter.getData(), List.class, ChoiceParameterBase.class);
    Map<String, ChoiceParameterBase> choicesDetailsMap =
      parameters.stream().collect(Collectors.toMap(ChoiceParameterBase::getId, p -> p));
    String details = "";

    if (!Utility.isEmpty(oldValue) && !Utility.isEmpty(oldValue.getChoices())) {
      JsonNode oldChoices = oldValue.getChoices();
      Map<String, String> oldChoicesMap = objectMapper.convertValue(oldChoices, new TypeReference<>() {
      });
      Map<String, String> newChoicesMap = objectMapper.convertValue(newData, new TypeReference<>() {
      });
      for (Map.Entry<String, String> newChoice : newChoicesMap.entrySet()) {
        String state = oldChoicesMap.get(newChoice.getKey());
        if (!state.equals(newChoice.getValue())) {
          if (!State.Selection.SELECTED.name().equals(state)) {
            details = formatMessage(isExecutedForCorrection ? CHOICE_PARAMETER_CORRECTION_SELECTED : CHOICE_PARAMETER_SELECTED,
              principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
              choicesDetailsMap.get(newChoice.getKey()).getName(), task.getName(), stage.getName());
            saveAudit(details, null, jobId, stage.getId(), task.getId(), principalUser);
          } else {
            details = formatMessage(isExecutedForCorrection ? CHOICE_PARAMETER_CORRECTION_DESELECTED : CHOICE_PARAMETER_DESELECTED, principalUser.getFirstName(),
              principalUser.getLastName(), principalUser.getEmployeeId(), choicesDetailsMap.get(newChoice.getKey()).getName(),
              task.getName(), stage.getName());
            saveAudit(details, null, jobId, stage.getId(), task.getId(), principalUser);
          }
        }
      }

    } else {
      Map<String, String> newChoicesMap = objectMapper.convertValue(newData, new TypeReference<>() {
      });
      for (Map.Entry<String, String> newChoice : newChoicesMap.entrySet()) {
        if (State.Selection.SELECTED.name().equals(newChoice.getValue())) {
          details = formatMessage(isExecutedForCorrection ? CHOICE_PARAMETER_CORRECTION_SELECTED : CHOICE_PARAMETER_SELECTED,
            principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
            choicesDetailsMap.get(newChoice.getKey()).getName(), task.getName(), stage.getName());
          saveAudit(details, null, jobId, stage.getId(), task.getId(), principalUser);
        }
      }
    }
  }

  private void saveTextParameterAudit(Long jobId, Stage stage, Task task, String newValue,
                                      boolean isExecutedForCorrection, PrincipalUser principalUser) {
    String details = formatMessage(isExecutedForCorrection ? TEXT_BOX_PARAMETER_ON_CORRECTION : TEXT_BOX_PARAMETER,
      principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
      newValue, task.getName(), stage.getName());
    saveAudit(details, null, jobId, stage.getId(), task.getId(), principalUser);
  }

  private <T extends BaseParameterValueDto> void saveShouldBeParameter(Long jobId, Stage stage, Task task, Parameter parameter, String newValue,
                                                                       @Nullable T oldValue, boolean isExecutedForCorrection, String reason,
                                                                       PrincipalUser principalUser) throws JsonProcessingException {
    String details = "";
    if (Utility.isEmpty(reason)) {
      if (null != oldValue && null != oldValue.getValue()) {
        details = formatMessage(isExecutedForCorrection ? SHOULD_BE_PARAMETER_CORRECTION : SHOULD_BE_PARAMETER,
          principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
          parameter.getLabel(), oldValue.getValue(), newValue, task.getName(), stage.getName());
      } else {
        details = formatMessage(isExecutedForCorrection ? SHOULD_BE_PARAMETER_CORRECTION_INITIAL : SHOULD_BE_PARAMETER_INITIAL,
          principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
          newValue, parameter.getLabel(), task.getName(), stage.getName());
      }
    } else {
      if (null != oldValue && null != oldValue.getValue()) {
        details = formatMessage(isExecutedForCorrection ? SHOULD_BE_PARAMETER_CORRECTION_WITH_REASON : SHOULD_BE_PARAMETER_WITH_REASON,
          principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
          parameter.getLabel(), oldValue.getValue(), newValue, reason, task.getName(), stage.getName());
      } else {
        details = formatMessage(isExecutedForCorrection ? SHOULD_BE_PARAMETER_CORRECTION_INITIAL_WITH_REASON : SHOULD_BE_PARAMETER_INITIAL_WITH_REASON,
          principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
          newValue, parameter.getLabel(), reason, task.getName(), stage.getName());
      }
    }
    saveAudit(details, null, jobId, stage.getId(), task.getId(), principalUser);
  }

  private void saveSignatureAudit(Long jobId, Stage stage, Task task, boolean isExecutedForCorrection, PrincipalUser principalUser) {
    String details = formatMessage(isExecutedForCorrection ? SIGNATURE_PARAMETER_CORRECTION : SIGNATURE_PARAMETER,
      principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
      task.getName(), stage.getName());
    saveAudit(details, null, jobId, stage.getId(), task.getId(), principalUser);
  }

  private void saveYesNoAudit(Long jobId, Stage stage, Task task, Parameter parameter, JsonNode newData,
                              boolean isExecutedForCorrection, String reason, PrincipalUser principalUser) throws IOException {
    String details = "";
    List<ChoiceParameterBase> parameters = JsonUtils.jsonToCollectionType(parameter.getData(), List.class, ChoiceParameterBase.class);
    Map<String, ChoiceParameterBase> choicesDetailsMap =
      parameters.stream().collect(Collectors.toMap(ChoiceParameterBase::getId, p -> p));
    Map<String, String> newChoicesMap = objectMapper.convertValue(newData, new TypeReference<>() {
    });
    for (Map.Entry<String, String> newChoice : newChoicesMap.entrySet()) {
      if (State.Selection.SELECTED.name().equals(newChoice.getValue())) {
        if (Utility.isEmpty(reason)) {
          details = formatMessage(isExecutedForCorrection ? YES_NO_PARAMETER_CORRECTION : YES_NO_PARAMETER,
            principalUser.getFirstName(), principalUser.getLastName(),
            principalUser.getEmployeeId(), choicesDetailsMap.get(newChoice.getKey()).getName(), task.getName(), stage.getName());
        } else {
          details = formatMessage(isExecutedForCorrection ? YES_NO_PARAMETER_CORRECTION_WITH_REASON : YES_NO_PARAMETER_WITH_REASON,
            principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
            choicesDetailsMap.get(newChoice.getKey()).getName(), reason, task.getName(), stage.getName());
        }
        break;
      }
    }
    saveAudit(details, null, jobId, stage.getId(), task.getId(), principalUser);
  }

  private void saveMediaAudit(Long jobId, Stage stage, Task task, List<ParameterValueMediaMapping> mediaValues,
                              List<TempParameterValueMediaMapping> tempMediaValues, List<MediaDto> oldMedias, boolean isExecutedForCorrection, PrincipalUser principalUser) {
    StringBuilder medias = new StringBuilder();
    Map<Long, MediaDto> mediaMap = oldMedias.stream()
      .collect(Collectors.toMap(media -> Long.valueOf(media.getId()), media -> media));

    List<Media> newMedias = new ArrayList<>();
    Set<Long> archivedMediaIds = new HashSet<>();
    if (isExecutedForCorrection) {
      for (TempParameterValueMediaMapping mediaMapping : tempMediaValues) {
        newMedias.add(mediaMapping.getMedia());
        if (mediaMapping.isArchived()) {
          archivedMediaIds.add(mediaMapping.getMedia().getId());
        }
      }
    } else {
      for (ParameterValueMediaMapping mediaMapping : mediaValues) {
        newMedias.add(mediaMapping.getMedia());
        if (mediaMapping.isArchived()) {
          archivedMediaIds.add(mediaMapping.getMedia().getId());
        }
      }
    }

    for (Media media : newMedias) {
      if (!mediaMap.containsKey(media.getId())) {
        medias.append("Name: ").append(media.getName());
        if (!Utility.isEmpty(media.getDescription())) {
          medias.append(" ").append("(Description: ").append(media.getDescription()).append(")");
        }
        String details = formatMessage(isExecutedForCorrection ? MEDIA_PARAMETER_CORRECTION : MEDIA_PARAMETER,
          principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
          medias.toString(), task.getName(), stage.getName());
        saveAudit(details, null, jobId, stage.getId(), task.getId(), principalUser);
      } else if (archivedMediaIds.contains(media.getId())) {
        medias.append("Name: ").append(media.getName());
        if (!Utility.isEmpty(media.getDescription())) {
          medias.append(" ").append("(Description: ").append(media.getDescription()).append(")");
        }
        String details = formatMessage(isExecutedForCorrection ? MEDIA_PARAMETER_ARCHIVED_CORRECTION : MEDIA_PARAMETER_ARCHIVED,
          principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
          medias.toString(), task.getName(), stage.getName());
        saveAudit(details, null, jobId, stage.getId(), task.getId(), principalUser);
      }

    }
  }

  private String getUploadedMediaAuditMessage(Media media) {
    StringBuilder mediaMessage = new StringBuilder();
    mediaMessage.append("Name: ").append(media.getName());
    if (!Utility.isEmpty(media.getDescription())) {
      mediaMessage.append(" ").append("(Description: ").append(media.getDescription()).append(")");
    }
    mediaMessage.append(",");
    return mediaMessage.toString();
  }

  private String formatMessage(String pattern, String... replacements) {
    for (int i = 0; i < replacements.length; i++) {
      if (replacements[i] != null) {
        pattern = pattern.replace("{" + i + "}", replacements[i]);
      } else {
        pattern = pattern.replace("{" + i + "}", "");
      }
    }

    return pattern;
  }

  private <T extends BaseParameterValueDto> void saveDateParameter(Long jobId, Stage stage, Task task, Parameter parameter, String newValue,
                                                                   @Nullable T oldValue, boolean isExecutedForCorrection,
                                                                   PrincipalUser principalUser) {
    saveDateOrDateTimeAudit(jobId, stage, task, parameter, oldValue, isExecutedForCorrection, principalUser, Long.parseLong(newValue));
  }

  private <T extends BaseParameterValueDto> void saveDateOrDateTimeAudit(Long jobId, Stage stage, Task task, Parameter parameter, T oldValue, boolean isExecutedForCorrection, PrincipalUser principalUser, Long date) {
    String details;
    JobAuditParameter jobAuditParameter = new JobAuditParameter();
    Map<Integer, JobAuditParameterValue> jobAuditParameterValueMap = new HashMap<>();
    JobAuditParameterValue jobAuditParameterValue = new JobAuditParameterValue();
    jobAuditParameterValue.setType(parameter.getType());
    jobAuditParameterValue.setValue(date);
    jobAuditParameterValueMap.put(0, jobAuditParameterValue);
    jobAuditParameter.setParameters(jobAuditParameterValueMap);
    if (null != oldValue && null != oldValue.getValue()) {
      details = formatMessage(isExecutedForCorrection ? DATE_PARAMETER_CORRECTION : DATE_PARAMETER,
        principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(), parameter.getLabel(), task.getName(), stage.getName());
      details = details.replace("{{{ABC}}}", "{{{0}}}");
    } else {
      details = formatMessage(isExecutedForCorrection ? DATE_PARAMETER_CORRECTION_INITIAL : DATE_PARAMETER_INITIAL,
        principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(), parameter.getLabel(), task.getName(), stage.getName());
      details = details.replace("{{{ABC}}}", "{{{0}}}");
    }
    saveAudit(details, jobAuditParameter, jobId, stage.getId(), task.getId(), principalUser);
  }

  private <T extends BaseParameterValueDto> void saveDateTimeParameter(Long jobId, Stage stage, Task task, Parameter parameter, String newValue,
                                                                       @Nullable T oldValue, boolean isExecutedForCorrection,
                                                                       PrincipalUser principalUser) {
    saveDateOrDateTimeAudit(jobId, stage, task, parameter, oldValue, isExecutedForCorrection, principalUser, Long.parseLong(newValue));
  }

  private <T extends BaseParameterValueDto> void saveNumberParameter(Long jobId, Stage stage, Task task, Parameter parameter, String newValue,
                                                                     @Nullable T oldValue, boolean isExecutedForCorrection,
                                                                     PrincipalUser principalUser) {
    String details = "";
    if (null != oldValue && null != oldValue.getValue()) {
      details = formatMessage(isExecutedForCorrection ? NUMBER_PARAMETER_CORRECTION : NUMBER_PARAMETER,
        principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
        parameter.getLabel(), newValue, task.getName(), stage.getName());
    } else {
      details = formatMessage(isExecutedForCorrection ? NUMBER_PARAMETER_CORRECTION_INITIAL : NUMBER_PARAMETER_INITIAL,
        principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
        newValue, parameter.getLabel(), task.getName(), stage.getName());
    }
    saveAudit(details, null, jobId, stage.getId(), task.getId(), principalUser);
  }

  private <T extends BaseParameterValueDto> void saveCalculationParameter(Long jobId, Stage stage, Task task, Parameter parameter, String updatedValue,
                                                                          @Nullable T oldValue, boolean isExecutedForCorrection, PrincipalUser principalUser) {
    String details = "";
    if (null != oldValue && null != oldValue.getValue()) {
      details = formatMessage(isExecutedForCorrection ? CALCULATION_PARAMETER_CORRECTION : CALCULATION_PARAMETER,
        principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
        parameter.getLabel(), updatedValue, task.getName(), stage.getName());
    } else {
      details = formatMessage(isExecutedForCorrection ? CALCULATION_PARAMETER_CORRECTION_INITIAL : CALCULATION_PARAMETER_INITIAL,
        principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
        parameter.getLabel(), updatedValue, task.getName(), stage.getName());
    }
    saveAudit(details, null, jobId, stage.getId(), task.getId(), principalUser);
  }

  private <T extends BaseParameterValueDto> void saveResourceParameter(Long jobId, Stage stage, Task task, Parameter parameter, String newData,
                                                                       @Nullable T oldValue, boolean isExecutedForCorrection,
                                                                       PrincipalUser principalUser) throws JsonProcessingException {
    String details = "";
    List<ResourceParameterChoiceDto> choices = JsonUtils.readValue(newData, new TypeReference<>() {
    });
    var choice = choices.get(0);
    String value = choice.getObjectDisplayName() + "(ID: " + choice.getObjectExternalId() + ")";

    if (null != oldValue && null != oldValue.getValue()) {
      details = formatMessage(isExecutedForCorrection ? RESOURCE_PARAMETER_CORRECTION : RESOURCE_PARAMETER,
        principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
        parameter.getLabel(), value, task.getName(), stage.getName());
    } else {
      details = formatMessage(isExecutedForCorrection ? RESOURCE_PARAMETER_CORRECTION_INITIAL : RESOURCE_PARAMETER_INITIAL,
        principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
        value, parameter.getLabel(), task.getName(), stage.getName());
    }
    saveAudit(details, null, jobId, stage.getId(), task.getId(), principalUser);
  }


  private <T extends BaseParameterValueDto> void saveMultiResourceParameter(Long jobId, Stage stage, Task task, Parameter parameter, String newData,
                                                                            @Nullable T oldValue, boolean isExecutedForCorrection,
                                                                            PrincipalUser principalUser) throws IOException {
    String details = "";
    List<ResourceParameterChoiceDto> choices = JsonUtils.readValue(newData, new TypeReference<>() {
    });
    List<ResourceParameterChoiceDto> oldChoices = new ArrayList<>();
    if (null != oldValue && !Utility.isEmpty(oldValue.getChoices())) {
      oldChoices = JsonUtils.readValue(oldValue.getChoices().toString(), new TypeReference<>() {
      });
    }

    Map<String, ResourceParameterChoiceDto> newChoicesMap = choices.stream().collect(Collectors.toMap(ResourceParameterChoiceDto::getObjectId, Function.identity()));
    Map<String, ResourceParameterChoiceDto> oldChoicesMap = oldChoices.stream().collect(Collectors.toMap(ResourceParameterChoiceDto::getObjectId, Function.identity()));

    Set<String> oldChoicesIds = oldChoices.stream().map(ResourceParameterChoiceDto::getObjectId).collect(Collectors.toSet());
    Set<String> newChoiceIds = choices.stream().map(ResourceParameterChoiceDto::getObjectId).collect(Collectors.toSet());

    Set<String> newSelections = SetUtils.difference(newChoiceIds, oldChoicesIds);
    Set<String> deselections = SetUtils.difference(oldChoicesIds, newChoiceIds);

    for (String id : newSelections) {
      ResourceParameterChoiceDto choice = newChoicesMap.get(id);
      String value = choice.getObjectDisplayName() + "(ID: " + choice.getObjectExternalId() + ")";
      details = formatMessage(isExecutedForCorrection ? RESOURCE_PARAMETER_CORRECTION_INITIAL : RESOURCE_PARAMETER_INITIAL,
        principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
        value, parameter.getLabel(), task.getName(), stage.getName());
      saveAudit(details, null, jobId, stage.getId(), task.getId(), principalUser);
    }

    for (String id : deselections) {
      ResourceParameterChoiceDto choice = oldChoicesMap.get(id);
      String value = choice.getObjectDisplayName() + "(ID: " + choice.getObjectExternalId() + ")";
      details = formatMessage(isExecutedForCorrection ? RESOURCE_PARAMETER_CORRECTION_INITIAL_DESELECTED : RESOURCE_PARAMETER_DESELECTION,
        principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(),
        value, parameter.getLabel(), task.getName(), stage.getName());
      saveAudit(details, null, jobId, stage.getId(), task.getId(), principalUser);
    }

  }
}
