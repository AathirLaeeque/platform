package com.leucine.streem.service;

import com.leucine.streem.collections.JobLog;
import com.leucine.streem.collections.JobLogMediaData;
import com.leucine.streem.constant.State;
import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.ResourceParameterChoiceDto;
import com.leucine.streem.dto.UserAuditDto;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.Checklist;
import com.leucine.streem.model.User;
import com.leucine.streem.model.helper.JobLogColumn;
import com.leucine.streem.model.helper.PrincipalUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface IJobLogService {
  Page<JobLog> findAllJobLogs(String filters, Pageable pageable);

  JobLog findByJobId(String jobId);

  void createJobLog(String jobId, String jobCode, State.Job jobState, Long jobCreatedAt, UserAuditDto jobCreatedBy, String checklistId,
                    String checklistName, String checklistCode, String facilityId, PrincipalUser principalUser);

  List<JobLogColumn> getJobLogColumnForChecklist(Checklist checklist);

  void recordJobLogTrigger(String jobId, String entityId, Type.JobLogTriggerType jobLogTriggerType, String label, List<JobLogMediaData> medias, String value, String identifierValue, UserAuditDto userAuditDto);

  void recordJobLogResource(String jobId, String parameterId, String label, String objectTypeId, String objectTypeDisplayName, List<ResourceParameterChoiceDto> choices);

  void updateJobState(String jobId, PrincipalUser principalUser, State.Job state);

  ByteArrayInputStream createJobLogExcel(String customViewId, String filters) throws IOException, ResourceNotFoundException, StreemException;
}
