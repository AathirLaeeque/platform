package com.leucine.streem.migration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leucine.streem.collections.JobLog;
import com.leucine.streem.collections.JobLogData;
import com.leucine.streem.collections.JobLogMediaData;
import com.leucine.streem.collections.JobLogResource;
import com.leucine.streem.config.MediaConfig;
import com.leucine.streem.constant.State;
import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.ResourceParameterChoiceDto;
import com.leucine.streem.dto.mapper.IUserMapper;
import com.leucine.streem.dto.projection.JobLogMigrationChecklistView;
import com.leucine.streem.dto.projection.JobLogMigrationParameterValueMediaMapping;
import com.leucine.streem.dto.projection.JobLogTaskExecutionView;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.model.Job;
import com.leucine.streem.model.Parameter;
import com.leucine.streem.model.ParameterValue;
import com.leucine.streem.model.helper.parameter.CalculationParameter;
import com.leucine.streem.model.helper.parameter.ChoiceParameterBase;
import com.leucine.streem.model.helper.parameter.ResourceParameter;
import com.leucine.streem.repository.*;
import com.leucine.streem.service.IChecklistService;
import com.leucine.streem.util.DateTimeUtils;
import com.leucine.streem.util.JsonUtils;
import com.leucine.streem.util.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.leucine.streem.constant.CollectionName.JOB_LOGS;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobLogMigration202212161515 {
  private final IJobRepository jobRepository;
  private final IParameterValueRepository parameterValueRepository;

  private final IParameterRepository parameterRepository;
  private final IChecklistService checklistService;
  private final ObjectMapper objectMapper;
  private final MediaConfig mediaConfig;
  private final ITaskExecutionRepository taskExecutionRepository;
  private final MongoTemplate mongoTemplate;
  private final IUserMapper userMapper;
  private final IChecklistRepository checklistRepository;
  private final IParameterValueMediaRepository parameterValueMediaRepository;


  private void updateJobLogs(List<Job> jobList, Long checklistId) throws IOException {
    List<JobLog> jobLogs = new ArrayList<>();
    Set<Long> jobIds = jobList.stream().map(Job::getId).collect(Collectors.toSet());
    Map<Long, Parameter> parameterMap = parameterRepository.findByChecklistIdAndArchived(checklistId, false).stream().collect(Collectors.toMap(Parameter::getId, parameter -> parameter));

    Map<Long, List<JobLogTaskExecutionView>> taskExecutionMap = taskExecutionRepository.findTaskExecutionDetailsByJobId(jobIds).stream().collect(Collectors.groupingBy(JobLogTaskExecutionView::getJobId));


    JobLogMigrationChecklistView jobLogMigrationChecklistView = checklistRepository.findChecklistInfoById(checklistId);
    for (Job job : jobList) {
      var jobLogRecord = new JobLog();
      List<JobLogData> logs = new ArrayList<>();
      jobLogRecord.setLogs(logs);
      jobLogRecord.setId(job.getIdAsString());
      jobLogRecord.setFacilityId(job.getFacilityId().toString());
      jobLogRecord.setChecklistId(jobLogMigrationChecklistView.getId().toString());
      jobLogRecord.setChecklistName(jobLogMigrationChecklistView.getName());
      jobLogRecord.setChecklistCode(jobLogMigrationChecklistView.getCode());
      jobLogRecord.setCode(job.getCode());
      jobLogRecord.setState(job.getState());
      jobLogRecord.setStartedAt(job.getStartedAt() == null ? null : job.getStartedAt());
      jobLogRecord.setEndedAt(job.getEndedAt() == null ? null : job.getEndedAt());
      jobLogRecord.setCreatedBy(userMapper.toUserAuditDto(job.getCreatedBy()));
      jobLogRecord.setStartedBy(job.getStartedBy() == null ? null : userMapper.toUserAuditDto(job.getStartedBy()));
      jobLogRecord.setEndedBy(job.getEndedBy() == null ? null : userMapper.toUserAuditDto(job.getEndedBy()));
      jobLogRecord.setCreatedAt(job.getCreatedAt());
      jobLogRecord.setModifiedAt(job.getModifiedAt());

      var jobIdLog = new JobLogData(JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.JOB_ID, "Job Id", job.getCode(), job.getCode(), null, null);
      var jobStateLog = new JobLogData(JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.JOB_STATE, "Job State", job.getState().name(), job.getState().name(), null, null);

      var chkNameLog = new JobLogData(JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.CHK_NAME, "Checklist Name", jobLogMigrationChecklistView.getName(), jobLogMigrationChecklistView.getName(), null, null);
      var chkIdLog = new JobLogData(JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.CHK_ID, "Checklist Id", jobLogMigrationChecklistView.getCode(), jobLogMigrationChecklistView.getCode(), null, null);
      var jobStartedByUser = job.getStartedBy();
      String fullName;
      if (jobStartedByUser != null) {
        fullName = Utility.getFullName(jobStartedByUser.getFirstName(), jobStartedByUser.getLastName());
        var jobStartedByLog = new JobLogData(JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.JOB_STARTED_BY, "Job Started By", fullName, fullName, null, null);
        logs.add(jobStartedByLog);
      }
      if (job.getStartedAt() != null) {
        var jobStartedTimeLog = new JobLogData(JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.JOB_START_TIME, "Job Start", job.getStartedAt().toString(), job.getStartedAt().toString(), null, null);
        logs.add(jobStartedTimeLog);
      }

      var jobCreatedAtTimeLog = new JobLogData(JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.JOB_CREATED_AT, "Job Created At", job.getCreatedAt().toString(), job.getCreatedAt().toString(), null, null);
      logs.add(jobCreatedAtTimeLog);
      fullName = Utility.getFullName(job.getCreatedBy().getFirstName(), job.getCreatedBy().getLastName());
      var jobStartedByLog = new JobLogData(JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.JOB_CREATED_BY, "Job Created By", fullName, fullName, null, null);
      logs.add(jobStartedByLog);

      fullName = Utility.getFullName(job.getModifiedBy().getFirstName(), job.getModifiedBy().getLastName());
      var jobModifiedByLog = new JobLogData(JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.JOB_MODIFIED_BY, "Job Modified By", fullName, fullName, null, null);
      logs.add(jobModifiedByLog);

      if (job.getEndedAt() != null && job.getEndedBy() != null) {
        var jobEndedTimeLog = new JobLogData(JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.JOB_END_TIME, "Job End", job.getEndedAt().toString(), job.getEndedAt().toString(), null, null);
        logs.add(jobEndedTimeLog);
        var jobEndedByUserFullName = Utility.getFullName(job.getEndedBy().getFirstName(), job.getEndedBy().getLastName());
        var jobEndedByLog = new JobLogData(JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.JOB_ENDED_BY, "Job Ended By", jobEndedByUserFullName, jobEndedByUserFullName, null, null);
        logs.add(jobEndedByLog);
      }
      logs.add(jobIdLog);
      logs.add(jobStateLog);
      logs.add(chkNameLog);
      logs.add(chkIdLog);

      List<ParameterValue> parameterValueList = parameterValueRepository.findAllByJobId(job.getId());

      Map<String, Object> parameterValues = new HashMap<>();
      for (var parameterValue : parameterValueList) {
        var parameter = parameterMap.get(parameterValue.getParameterId());
        var triggerType = parameter.getTargetEntityType().equals(Type.ParameterTargetEntityType.PROCESS) ? Type.JobLogTriggerType.PROCESS_PARAMETER_VALUE : Type.JobLogTriggerType.PARAMETER_VALUE;
        switch (parameter.getType()) {
          case MULTI_LINE, SINGLE_LINE, SHOULD_BE, NUMBER -> {
            String value;
            if (parameterValue.getValue() != null) {
              value = parameterValue.getValue();
              logs.add(new JobLogData(parameter.getId().toString(), triggerType, parameter.getLabel(), value, value, null, null));
              parameterValues.put(parameter.getIdAsString(), parameterValue.getValue());
            }
          }
          case DATE, DATE_TIME -> {
            String formattedDate = "";
            if (parameterValue.getValue() != null) {
              formattedDate = DateTimeUtils.getFormattedDate(Long.parseLong(parameterValue.getValue()));
              parameterValues.put(parameter.getIdAsString(), parameterValue.getValue());
            }
            logs.add(new JobLogData(parameter.getIdAsString(), triggerType, parameter.getLabel(), formattedDate, formattedDate, null, null));
          }
          case CALCULATION -> {
            CalculationParameter calculationParameter = JsonUtils.readValue(parameter.getData().toString(), CalculationParameter.class);
            String valueWithUOM = "";
            if (parameterValue.getValue() != null) {
              valueWithUOM = parameterValue.getValue() + Utility.SPACE + calculationParameter.getUom();
              parameterValues.put(parameter.getIdAsString(), valueWithUOM);
            }
            logs.add(new JobLogData(parameter.getIdAsString(), triggerType, parameter.getLabel(), valueWithUOM, valueWithUOM, null, null));
          }
          case CHECKLIST, MULTISELECT, SINGLE_SELECT, YES_NO -> {
            List<ChoiceParameterBase> activities = JsonUtils.jsonToCollectionType(parameter.getData().toString(), List.class, ChoiceParameterBase.class);
            Map<String, String> optionsNameMap = activities.stream().collect(
              Collectors.toMap(ChoiceParameterBase::getId, ChoiceParameterBase::getName));
            JsonNode oldChoices = parameterValue.getChoices();
            if (oldChoices != null) {
              List<String> selectedItems = new ArrayList<>();
              List<String> selectedIdentifierItems = new ArrayList<>();
              Map<String, String> result = objectMapper.convertValue(oldChoices, new TypeReference<>() {
              });
              for (ChoiceParameterBase choiceParameter : activities) {
                String state = result.get(choiceParameter.getId());
                if (State.Selection.SELECTED.name().equals(state)) {
                  selectedItems.add(optionsNameMap.get(choiceParameter.getId()));
                  selectedIdentifierItems.add(choiceParameter.getId());
                }
              }
              String value = String.join(",", selectedItems);
              String identifierValue = String.join(",", selectedIdentifierItems);
              parameterValues.put(parameter.getIdAsString(), identifierValue);
              logs.add(new JobLogData(parameter.getIdAsString(), triggerType, parameter.getLabel(), value, identifierValue, null, null));
            }
          }
          case MEDIA, SIGNATURE, FILE_UPLOAD -> {

            List<JobLogMigrationParameterValueMediaMapping> parameterValueMediaMappings = parameterValueMediaRepository.findMediaByParameterValueId(parameterValue.getId());
            List<JobLogMediaData> jobLogMedias = new ArrayList<>();
            for (JobLogMigrationParameterValueMediaMapping parameterValueMedia : parameterValueMediaMappings) {
              JobLogMediaData jobLogMediaData = new JobLogMediaData();

              jobLogMediaData.setName(Type.Parameter.SIGNATURE.equals(parameter.getType()) ? "Signature" : parameterValueMedia.getName());
              jobLogMediaData.setType(parameterValueMedia.getType());
              jobLogMediaData.setDescription(parameterValueMedia.getDescription());
              String link = mediaConfig.getCdn() + java.io.File.separator + parameterValueMedia.getRelativePath() + java.io.File.separator + parameterValueMedia.getFilename();
              jobLogMediaData.setLink(link);
              jobLogMedias.add(jobLogMediaData);
            }
            parameterValues.put(parameter.getIdAsString(), null);
            logs.add(new JobLogData(parameter.getIdAsString(), triggerType, parameter.getLabel(), null, null, jobLogMedias, null));
          }
          case RESOURCE, MULTI_RESOURCE -> {
            ResourceParameter resourceParameter = JsonUtils.readValue(parameter.getData().toString(), ResourceParameter.class);
            List<ResourceParameterChoiceDto> choices = JsonUtils.jsonToCollectionType(parameterValue.getChoices(), List.class, ResourceParameterChoiceDto.class);
            List<String> selectedItems = new ArrayList<>();
            List<String> selectedIdentifierItems = new ArrayList<>();
            if (choices != null) {
              for (ResourceParameterChoiceDto choice : choices) {
                selectedIdentifierItems.add(choice.getObjectId());
                selectedItems.add(choice.getObjectExternalId());
              }
              String value = String.join(",", selectedItems);
              String identifierValue = String.join(",", selectedIdentifierItems);
              parameterValues.put(parameter.getIdAsString(), identifierValue);
              logs.add(new JobLogData(parameter.getIdAsString(), Type.JobLogTriggerType.RESOURCE_PARAMETER, parameter.getLabel(), value, identifierValue, null, null));

              // JOB LOG CUSTOM VIEW - ENTITY TYPE - RESOURCE
              var jobLogDataToCreateOrUpdate = new JobLogData();
              boolean jobLogRecordFound = false;
              if (!Utility.isEmpty(jobLogRecord) && !Utility.isEmpty(jobLogRecord.getLogs())) {
                for (JobLogData jobLogData : jobLogRecord.getLogs()) {
                  if (jobLogData.getEntityId().equals(resourceParameter.getObjectTypeId().toString()) && jobLogData.getTriggerType().equals(Type.JobLogTriggerType.RESOURCE)) {
                    jobLogRecordFound = true;
                    jobLogDataToCreateOrUpdate = jobLogData;
                    jobLogData.setMedias(null);
                    jobLogData.setValue(null);
                    jobLogData.setIdentifierValue(null);
                  }
                }
              }

              jobLogDataToCreateOrUpdate.setEntityId(resourceParameter.getObjectTypeId().toString());
              jobLogDataToCreateOrUpdate.setTriggerType(Type.JobLogTriggerType.RESOURCE);
              jobLogDataToCreateOrUpdate.setDisplayName(resourceParameter.getObjectTypeDisplayName());

              var iterator = jobLogDataToCreateOrUpdate.getResourceParameters().entrySet().iterator();
              List<String> objectIds = new ArrayList<>();
              while (iterator.hasNext()) {
                Map.Entry<String, JobLogResource> mapElement = iterator.next();
                var jobLogResource = mapElement.getValue();
                for (ResourceParameterChoiceDto resourceParameterChoiceDto : jobLogResource.getChoices()) {
                  objectIds.add(resourceParameterChoiceDto.getObjectId());
                }
              }
              parameterValues.put(resourceParameter.getObjectTypeId().toString(), objectIds);

              JobLogResource jobLogResource = new JobLogResource();
              jobLogResource.setDisplayName(parameter.getLabel());
              jobLogResource.setChoices(choices);
              jobLogDataToCreateOrUpdate.getResourceParameters().put(parameter.getIdAsString(), jobLogResource);

              if (!jobLogRecordFound) {
                jobLogRecord.getLogs().add(jobLogDataToCreateOrUpdate);
              }
            }
          }
        }
      }
      jobLogRecord.setParameterValues(parameterValues);


      List<JobLogTaskExecutionView> taskExecutions = taskExecutionMap.get(job.getId());

      for (var taskExecution : taskExecutions) {

        if (taskExecution.getStartedAt() != null && taskExecution.getTaskStartedByFirstName() != null) {
          var taskStartedAt = new JobLogData(taskExecution.getTaskId().toString(), Type.JobLogTriggerType.TSK_START_TIME, taskExecution.getName() + " " + "Start", taskExecution.getStartedAt().toString(), taskExecution.getStartedAt().toString(), null, null);
          var fullNameTaskStartedBy = Utility.getFullName(taskExecution.getTaskStartedByLastName(), taskExecution.getTaskStartedByLastName());
          var taskStartedBy = new JobLogData(taskExecution.getTaskId().toString(), Type.JobLogTriggerType.TSK_STARTED_BY, taskExecution.getName() + " " + "Started By", fullNameTaskStartedBy, fullNameTaskStartedBy, null, null);
          logs.add(taskStartedAt);
          logs.add(taskStartedBy);
        }
        if (taskExecution.getEndedAt() != null && taskExecution.getTaskModifiedByFirstName() != null) {
          var taskEndedAt = new JobLogData(taskExecution.getTaskId().toString(), Type.JobLogTriggerType.TSK_END_TIME, taskExecution.getName() + " " + "End", taskExecution.getEndedAt().toString(), taskExecution.getEndedAt().toString(), null, null);
          var fullNameTaskEndedBy = Utility.getFullName(taskExecution.getTaskModifiedByFirstName(), taskExecution.getTaskModifiedByLastName());
          var taskEndedBy = new JobLogData(taskExecution.getTaskId().toString(), Type.JobLogTriggerType.TSK_ENDED_BY, taskExecution.getName() + " " + "Ended By", fullNameTaskEndedBy, fullNameTaskEndedBy, null, null);
          logs.add(taskEndedAt);
          logs.add(taskEndedBy);
        }
      }
      jobLogs.add(jobLogRecord);
    }
    bulkSave(jobLogs);
  }

  private void bulkSave(List<JobLog> jobLogs) {
    if (!jobLogs.isEmpty()) {
      mongoTemplate.insertAll(jobLogs);
    }
  }


  public BasicDto execute() {
    mongoTemplate.remove(new Query(), JOB_LOGS);
    Set<Long> checklistIds = checklistRepository.findByStateIn(Set.of(State.Checklist.PUBLISHED, State.Checklist.DEPRECATED));
    for (Long checklistId : checklistIds) {
      try {
        checklistService.reconfigureJobLogColumns(checklistId);

        List<Job> jobList = jobRepository.findAllByChecklistId(checklistId);
        updateJobLogs(jobList, checklistId);
      } catch (ResourceNotFoundException | IOException e) {
        log.error("Error while migrating job logs for checklist id: " + checklistId, e);
      }
    }

    return (new BasicDto()).setMessage("Migration Successful");
  }
}
