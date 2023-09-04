package com.leucine.streem.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leucine.streem.collections.*;
import com.leucine.streem.collections.helper.MongoFilter;
import com.leucine.streem.constant.JobLogMisc;
import com.leucine.streem.constant.State;
import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.ResourceParameterChoiceDto;
import com.leucine.streem.dto.UserAuditDto;
import com.leucine.streem.dto.mapper.ISearchFilterMapper;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.model.Relation;
import com.leucine.streem.model.*;
import com.leucine.streem.model.helper.JobLogColumn;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.model.helper.search.SearchOperator;
import com.leucine.streem.repository.IJobLogRepository;
import com.leucine.streem.repository.IParameterRepository;
import com.leucine.streem.service.ICustomViewService;
import com.leucine.streem.service.IJobLogService;
import com.leucine.streem.util.DateTimeUtils;
import com.leucine.streem.util.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.leucine.streem.constant.JobLogMisc.*;
import static com.leucine.streem.constant.Type.CustomViewFilterType.values;
import static com.leucine.streem.util.WorkbookUtils.getXSSFFont;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobLogService implements IJobLogService {
  private final IJobLogRepository logItemRepository;
  private final IParameterRepository parameterRepository;
  private final MongoTemplate mongoTemplate;
  private final ICustomViewService customViewService;
  private final ObjectMapper objectMapper;

  @Override
  public Page<JobLog> findAllJobLogs(String filters, Pageable pageable) {
    Query query = MongoFilter.buildQuery(filters);
    long count = mongoTemplate.count(query, JobLog.class);
    query.with(pageable);
    List<JobLog> jobLogList = mongoTemplate.find(query, JobLog.class);
    return PageableExecutionUtils.getPage(jobLogList, pageable, () -> count);
  }

  @Override
  public JobLog findByJobId(String jobId) {
    return logItemRepository.findById(jobId).get();
  }

  @Override
  public void createJobLog(String jobId, String jobCode, State.Job jobState, Long jobCreatedAt, UserAuditDto jobCreatedBy, String checklistId, String checklistName,
                           String checklistCode, String facilityId, PrincipalUser principalUser) {
    log.info("[createJobLog] Request to create a new job log record, jobId: {}, jobCode: {}, checklistId: {}, facilityId: {}", jobId, jobCode, checklistId, facilityId);
    JobLog logItem = new JobLog();
    logItem.setId(jobId);
    logItem.setCreatedBy(jobCreatedBy);
    logItem.setModifiedBy(jobCreatedBy);
    logItem.setCode(jobCode);
    logItem.setState(jobState);
    logItem.setChecklistCode(checklistCode);
    logItem.setChecklistName(checklistName);
    logItem.setFacilityId(facilityId);
    logItem.setChecklistId(checklistId);
    logItem.setCreatedAt(jobCreatedAt);
    logItem.setModifiedAt(jobCreatedAt);
    var jobLogColumns = new ArrayList<JobLogData>();

    String fullName = Utility.getFullNameFromPrincipalUser(principalUser);
    String createdAt = String.valueOf(jobCreatedAt);
    JobLogData jobIdLog = getJobLogData(JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.JOB_ID, JobLogMisc.JOB, null, jobCode, jobCode);
    JobLogData checklistIdLog = getJobLogData(JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.CHK_ID, JobLogMisc.PROCESS, null, checklistCode, checklistCode);
    JobLogData checklistNameLog = getJobLogData(JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.CHK_NAME, JobLogMisc.PROCESS, null, checklistName, checklistName);
    JobLogData jobStateLog = getJobLogData(JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.JOB_STATE, JobLogMisc.JOB, null, jobState.getDisplayName(), jobState.name());
    JobLogData jobCreatedAtLog = getJobLogData(JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.JOB_CREATED_AT, JobLogMisc.JOB, null, createdAt, createdAt);
    JobLogData jobCreatedByLog = getJobLogData(JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.JOB_CREATED_BY, JobLogMisc.JOB, null, fullName, fullName);
    JobLogData jobModifiedByLog = getJobLogData(JobLog.COMMON_COLUMN_ID, Type.JobLogTriggerType.JOB_MODIFIED_BY, JobLogMisc.JOB, null, fullName, fullName);

    jobLogColumns.add(jobIdLog);
    jobLogColumns.add(checklistIdLog);
    jobLogColumns.add(checklistNameLog);
    jobLogColumns.add(jobStateLog);
    jobLogColumns.add(jobCreatedAtLog);
    jobLogColumns.add(jobCreatedByLog);
    jobLogColumns.add(jobModifiedByLog);

    try {
      logItem.setLogs(jobLogColumns);
      logItemRepository.save(logItem);
    } catch (Exception ex) {
      log.error("[createJobLog] error creating job log record", ex);
    }
  }

  @Override
  public List<JobLogColumn> getJobLogColumnForChecklist(Checklist checklist) {
    List<JobLogColumn> jobLogColumns = new ArrayList<>();

    jobLogColumns.add(getJobLogColumn(JobLog.COMMON_COLUMN_ID, Type.JobLogColumnType.DATE, Type.JobLogTriggerType.JOB_START_TIME, JobLogMisc.JOB));
    jobLogColumns.add(getJobLogColumn(JobLog.COMMON_COLUMN_ID, Type.JobLogColumnType.DATE, Type.JobLogTriggerType.JOB_CREATED_AT, JobLogMisc.JOB));
    jobLogColumns.add(getJobLogColumn(JobLog.COMMON_COLUMN_ID, Type.JobLogColumnType.TEXT, Type.JobLogTriggerType.JOB_CREATED_BY, JobLogMisc.JOB));
    jobLogColumns.add(getJobLogColumn(JobLog.COMMON_COLUMN_ID, Type.JobLogColumnType.TEXT, Type.JobLogTriggerType.JOB_MODIFIED_BY, JobLogMisc.JOB));
    jobLogColumns.add(getJobLogColumn(JobLog.COMMON_COLUMN_ID, Type.JobLogColumnType.TEXT, Type.JobLogTriggerType.JOB_ID, JobLogMisc.JOB));
    jobLogColumns.add(getJobLogColumn(JobLog.COMMON_COLUMN_ID, Type.JobLogColumnType.TEXT, Type.JobLogTriggerType.JOB_STATE, JobLogMisc.JOB));
    jobLogColumns.add(getJobLogColumn(JobLog.COMMON_COLUMN_ID, Type.JobLogColumnType.TEXT, Type.JobLogTriggerType.CHK_ID, JobLogMisc.PROCESS));
    jobLogColumns.add(getJobLogColumn(JobLog.COMMON_COLUMN_ID, Type.JobLogColumnType.TEXT, Type.JobLogTriggerType.CHK_NAME, JobLogMisc.PROCESS));

    jobLogColumns.add(getJobLogColumn(JobLog.COMMON_COLUMN_ID, Type.JobLogColumnType.TEXT, Type.JobLogTriggerType.JOB_STARTED_BY, JobLogMisc.JOB));

    var processParameters = parameterRepository.getParametersByChecklistIdAndTargetEntityType(checklist.getId(), Type.ParameterTargetEntityType.PROCESS);
    for (Parameter parameter : processParameters) {
      switch (parameter.getType()) {
        case YES_NO, MULTISELECT, SINGLE_SELECT, MULTI_LINE, SINGLE_LINE, NUMBER, DATE, DATE_TIME ->
          jobLogColumns.add(getJobLogColumn(parameter.getIdAsString(), Type.JobLogColumnType.TEXT, Type.JobLogTriggerType.PROCESS_PARAMETER_VALUE, parameter.getLabel()));
        case MEDIA, FILE_UPLOAD, SIGNATURE ->
          jobLogColumns.add(getJobLogColumn(parameter.getIdAsString(), Type.JobLogColumnType.FILE, Type.JobLogTriggerType.PROCESS_PARAMETER_VALUE, parameter.getLabel()));
        case RESOURCE,MULTI_RESOURCE ->
          jobLogColumns.add(getJobLogColumn(parameter.getIdAsString(), Type.JobLogColumnType.TEXT, Type.JobLogTriggerType.RESOURCE_PARAMETER, parameter.getLabel()));
      }
    }

    for (Stage stage : checklist.getStages()) {
      for (Task task : stage.getTasks()) {
        jobLogColumns.add(getJobLogColumn(task.getIdAsString(), Type.JobLogColumnType.DATE, Type.JobLogTriggerType.TSK_START_TIME, task.getName()));
        jobLogColumns.add(getJobLogColumn(task.getIdAsString(), Type.JobLogColumnType.TEXT, Type.JobLogTriggerType.TSK_STARTED_BY, task.getName()));

        for (Parameter parameter : task.getParameters()) {
          switch (parameter.getType()) {
            case YES_NO, MULTISELECT, SINGLE_SELECT, SINGLE_LINE, MULTI_LINE, SHOULD_BE, NUMBER, CALCULATION, DATE, DATE_TIME ->
              jobLogColumns.add(getJobLogColumn(parameter.getIdAsString(), Type.JobLogColumnType.TEXT, Type.JobLogTriggerType.PARAMETER_VALUE, parameter.getLabel()));
            case MEDIA, FILE_UPLOAD, SIGNATURE ->
              jobLogColumns.add(getJobLogColumn(parameter.getIdAsString(), Type.JobLogColumnType.FILE, Type.JobLogTriggerType.PARAMETER_VALUE, parameter.getLabel()));
            case RESOURCE,MULTI_RESOURCE ->
              jobLogColumns.add(getJobLogColumn(parameter.getIdAsString(), Type.JobLogColumnType.TEXT, Type.JobLogTriggerType.RESOURCE_PARAMETER, parameter.getLabel()));
          }
        }
        jobLogColumns.add(getJobLogColumn(task.getIdAsString(), Type.JobLogColumnType.DATE, Type.JobLogTriggerType.TSK_END_TIME, task.getName()));
        jobLogColumns.add(getJobLogColumn(task.getIdAsString(), Type.JobLogColumnType.TEXT, Type.JobLogTriggerType.TSK_ENDED_BY, task.getName()));
      }
    }

    for (Relation relation : checklist.getRelations()) {
      jobLogColumns.add(getJobLogColumn(relation.getIdAsString(), Type.JobLogColumnType.TEXT, Type.JobLogTriggerType.RELATION_VALUE, relation.getDisplayName()));
    }

    jobLogColumns.add(getJobLogColumn(JobLog.COMMON_COLUMN_ID, Type.JobLogColumnType.DATE, Type.JobLogTriggerType.JOB_END_TIME, JobLogMisc.JOB));
    jobLogColumns.add(getJobLogColumn(JobLog.COMMON_COLUMN_ID, Type.JobLogColumnType.TEXT, Type.JobLogTriggerType.JOB_ENDED_BY, JobLogMisc.JOB));

    return jobLogColumns;
  }

  @Override
  //TODO: Removed @Async annotation move this to single mongo query upsert
  public void recordJobLogTrigger(String jobId, String entityId, Type.JobLogTriggerType jobLogTriggerType, String label, List<JobLogMediaData> medias,
                                  String value, String identifierValue, UserAuditDto modifiedBy) {
    String displayName = getDisplayNameByLabelAndTriggerType(label, jobLogTriggerType);

    JobLogData jobLogData = new JobLogData();
    jobLogData.setMedias(medias);
    jobLogData.setValue(value);
    jobLogData.setIdentifierValue(identifierValue);
    jobLogData.setEntityId(entityId);
    jobLogData.setTriggerType(jobLogTriggerType);
    jobLogData.setDisplayName(displayName);

    setOrUpdateLogItem(jobId, jobLogTriggerType, jobLogData, modifiedBy);
  }

  @Override
  public void recordJobLogResource(String jobId, String parameterId, String parameterLabel, String objectTypeId, String objectTypeDisplayName, List<ResourceParameterChoiceDto> choices) {
    var jobLog = logItemRepository.findById(jobId).get();

    var jobLogDataToCreateOrUpdate = new JobLogData();
    boolean jobLogRecordFound = false;
    for (JobLogData jobLogData : jobLog.getLogs()) {
      if (jobLogData.getEntityId().equals(objectTypeId) && jobLogData.getTriggerType().equals(Type.JobLogTriggerType.RESOURCE)) {
        jobLogRecordFound = true;
        jobLogDataToCreateOrUpdate = jobLogData;
        jobLogData.setMedias(null);
        jobLogData.setValue(null);
        jobLogData.setIdentifierValue(null);
      }
    }

    jobLogDataToCreateOrUpdate.setEntityId(objectTypeId);
    jobLogDataToCreateOrUpdate.setTriggerType(Type.JobLogTriggerType.RESOURCE);
    jobLogDataToCreateOrUpdate.setDisplayName(objectTypeDisplayName);

    var parameterValues = jobLog.getParameterValues();
    var iterator = jobLogDataToCreateOrUpdate.getResourceParameters().entrySet().iterator();
    List<String> objectIds = new ArrayList<>();
    while (iterator.hasNext()) {
      Map.Entry<String, JobLogResource> mapElement = iterator.next();
      var jobLogResource = mapElement.getValue();
      for (ResourceParameterChoiceDto resourceParameterChoiceDto : jobLogResource.getChoices()) {
        objectIds.add(resourceParameterChoiceDto.getObjectId());
      }
    }
    parameterValues.put(objectTypeId, objectIds);
    jobLog.setParameterValues(parameterValues);

    JobLogResource jobLogResource = new JobLogResource();
    jobLogResource.setDisplayName(parameterLabel);
    jobLogResource.setChoices(choices);
    jobLogDataToCreateOrUpdate.getResourceParameters().put(parameterId, jobLogResource);

    if (!jobLogRecordFound) {
      jobLog.getLogs().add(jobLogDataToCreateOrUpdate);
    }
    logItemRepository.save(jobLog);
  }

  @Override
  public void updateJobState(String jobId, PrincipalUser principalUser, State.Job state) {
    var jobLog = logItemRepository.findById(jobId).get();
    for (JobLogData jobLogData : jobLog.getLogs()) {
      if (jobLogData.getTriggerType().equals(Type.JobLogTriggerType.JOB_STATE)) {
        jobLogData.setValue(state.name());
        jobLogData.setIdentifierValue(state.name());
      }
      if (jobLogData.getTriggerType().equals(Type.JobLogTriggerType.JOB_MODIFIED_BY)) {
        String fullName = Utility.getFullNameFromPrincipalUser(principalUser);
        jobLogData.setValue(fullName);
        jobLogData.setIdentifierValue(fullName);
      }
    }
    jobLog.setState(state);
    logItemRepository.save(jobLog);
  }

  private JobLogColumn getJobLogColumn(String entityId, Type.JobLogColumnType jobLogColumnType, Type.JobLogTriggerType triggerType, String label) {
    var jobLogColumn = new JobLogColumn();
    jobLogColumn.setId(entityId);
    jobLogColumn.setType(jobLogColumnType);
    jobLogColumn.setTriggerType(triggerType);
    jobLogColumn.setDisplayName(getDisplayNameByLabelAndTriggerType(label, triggerType));
    return jobLogColumn;
  }

  private JobLogData getJobLogData(String entityId, Type.JobLogTriggerType jobLogTriggerType, String label, List<JobLogMediaData> medias,
                                   String value, String identifierValue) {
    String displayName = getDisplayNameByLabelAndTriggerType(label, jobLogTriggerType);

    JobLogData jobLogData = new JobLogData();
    jobLogData.setMedias(medias);
    jobLogData.setValue(value);
    jobLogData.setIdentifierValue(identifierValue);
    jobLogData.setEntityId(entityId);
    jobLogData.setTriggerType(jobLogTriggerType);
    jobLogData.setDisplayName(displayName);
    return jobLogData;
  }

  private String getDisplayNameByLabelAndTriggerType(String label, Type.JobLogTriggerType jobLogTriggerType) {
    StringBuilder builder = new StringBuilder(label);
    switch (jobLogTriggerType) {
      case CHK_ID, JOB_ID -> builder.append(Utility.SPACE).append(JobLogMisc.ID_SUFFIX);
      case JOB_STATE -> builder.append(Utility.SPACE).append(JobLogMisc.STATE_SUFFIX);
      case CHK_NAME -> builder.append(Utility.SPACE).append(JobLogMisc.NAME_SUFFIX);
      case JOB_CREATED_AT -> builder.append(Utility.SPACE).append(JobLogMisc.CREATED_AT_SUFFIX);
      case JOB_CREATED_BY -> builder.append(Utility.SPACE).append(JobLogMisc.CREATED_BY_SUFFIX);
      case JOB_MODIFIED_BY -> builder.append(Utility.SPACE).append(JobLogMisc.MODIFIED_BY_SUFFIX);
      case JOB_START_TIME, TSK_START_TIME -> builder.append(Utility.SPACE).append(JobLogMisc.START_TIME_SUFFIX);
      case JOB_END_TIME, TSK_END_TIME -> builder.append(Utility.SPACE).append(JobLogMisc.END_TIME_SUFFIX);
      case JOB_STARTED_BY, TSK_STARTED_BY -> builder.append(Utility.SPACE).append(JobLogMisc.STARTED_BY_SUFFIX);
      case JOB_ENDED_BY, TSK_ENDED_BY -> builder.append(Utility.SPACE).append(JobLogMisc.ENDED_BY_SUFFIX);
    }
    return builder.toString();
  }

  private void setOrUpdateLogItem(String jobId, Type.JobLogTriggerType jobLogTriggerType, JobLogData jobLogDataToCreateOrUpdate, UserAuditDto modifiedBy) {
    var jobLog = logItemRepository.findById(jobId).get();
    Long time = DateTimeUtils.now();
    jobLog.setModifiedAt(time);
    jobLog.setModifiedBy(modifiedBy);
    if (Type.JobLogTriggerType.JOB_ENDED_BY.equals(jobLogTriggerType)) {
      jobLog.setEndedBy(modifiedBy);
      jobLog.setEndedAt(time);
    }
    if (Type.JobLogTriggerType.JOB_STARTED_BY.equals(jobLogTriggerType)) {
      jobLog.setStartedBy(modifiedBy);
      jobLog.setStartedAt(time);
    }
    var logEntryExists = false;

    for (JobLogData jobLogData : jobLog.getLogs()) {
      if (jobLogDataToCreateOrUpdate.getEntityId().equals(jobLogData.getEntityId()) && jobLogDataToCreateOrUpdate.getTriggerType().equals(jobLogData.getTriggerType())) {
        logEntryExists = true;

        jobLogData.setValue(jobLogDataToCreateOrUpdate.getValue());
        jobLogData.setIdentifierValue(jobLogDataToCreateOrUpdate.getIdentifierValue());
        jobLogData.setEntityId(jobLogDataToCreateOrUpdate.getEntityId());
        jobLogData.setDisplayName(jobLogDataToCreateOrUpdate.getDisplayName());
        jobLogData.setTriggerType(jobLogDataToCreateOrUpdate.getTriggerType());
        jobLogData.setMedias(jobLogDataToCreateOrUpdate.getMedias());
      }
    }

    if (!logEntryExists) {
      jobLog.getLogs().add(jobLogDataToCreateOrUpdate);
    }

    Map<String, Object> parameterValues = new HashMap<>();
    for (JobLogData jobLogData : jobLog.getLogs()) {
      Set<Type.JobLogTriggerType> types = new HashSet<>();
      types.add(Type.JobLogTriggerType.PROCESS_PARAMETER_VALUE);
      types.add(Type.JobLogTriggerType.PARAMETER_VALUE);
      types.add(Type.JobLogTriggerType.RESOURCE_PARAMETER);

      if (types.contains(jobLogData.getTriggerType())) {
        parameterValues.put(jobLogData.getEntityId(), jobLogData.getIdentifierValue());
      }
    }

    jobLog.setParameterValues(parameterValues);
    logItemRepository.save(jobLog);
  }

  /* TODO
  Check if we can use FileOutputStream as using ByteArrayOutputStream the whole file will be created in memory causing performance issue for large data.
  Also, need to add data in chunks (X records) and in parallel stream the file
   */
  @Override
  public ByteArrayInputStream createJobLogExcel(String customViewId, String filters) throws ResourceNotFoundException, IOException {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         Workbook wb = new XSSFWorkbook()
    ) {
      CustomView customView = customViewService.getCustomViewById(customViewId);
      var searchFilter = ISearchFilterMapper.toSearchCriteria(customView.getFilters());
      List<JobLog> jobLogs = findAllJobLogs(objectMapper.writeValueAsString(searchFilter), Pageable.unpaged()).getContent();
      Map<String, String> checklistNameAndCodeMapping = new HashMap<>();
      createDataSheet(wb, customView, jobLogs, checklistNameAndCodeMapping);
      createDetailsSheet(wb, customView.getFilters(), checklistNameAndCodeMapping);
      wb.write(outputStream);
      return new ByteArrayInputStream(outputStream.toByteArray());
    }
  }

  public void createDataSheet(Workbook wb, CustomView customView, List<JobLog> jobLogs, Map<String, String> checklistNameAndCodeMapping) throws ResourceNotFoundException {
    wb.createSheet(DATA_SHEET);

    CreationHelper createHelper = wb.getCreationHelper();
    Sheet dataSheet = wb.getSheet(DATA_SHEET);
    CellStyle headerStyle = wb.createCellStyle();
    XSSFFont font = getXSSFFont((XSSFWorkbook) wb, ARIAL_FONT_NAME, FONT_HEIGHT);
    headerStyle.setFont(font);

    if (Utility.isEmpty(jobLogs)) {
      return;
    }

    List<CustomViewColumn> customViewColumns = customView.getColumns().stream()
      .sorted(Comparator.comparing(CustomViewColumn::getOrderTree))
      .toList();

    final Map<String, CustomViewColumn> jobLogViewColumnMap = customView.getColumns().stream()
      .collect(Collectors.toMap(jobLogViewColumn -> jobLogViewColumn.getId() + jobLogViewColumn.getTriggerType(), Function.identity()));

    Row headerDetailsRow = dataSheet.createRow(0);
    final Set<String> uniqueLogs = new HashSet<>();
    for (int i = 0; i < customViewColumns.size(); i++) {
      Cell jobLogColumn = headerDetailsRow.createCell(i);
      var triggerType = Type.JobLogTriggerType.valueOf(customViewColumns.get(i).getTriggerType());
      jobLogColumn.setCellValue(customViewColumns.get(i).getDisplayName());
      jobLogColumn.setCellStyle(headerStyle);
      uniqueLogs.add(customViewColumns.get(i).getId() + triggerType);
    }
    for (int i = 0; i < jobLogs.size(); i++) {
      JobLog jobLog = jobLogs.get(i);
      Map<String, JobLogData> requiredJobLogData = jobLog.getLogs().stream()
        .filter(jobLogData -> uniqueLogs.contains(jobLogData.getEntityId() + jobLogData.getTriggerType()))
        .collect(Collectors.toMap(jobLogData -> jobLogData.getEntityId() + jobLogData.getTriggerType(), Function.identity()));

      checklistNameAndCodeMapping.put(jobLog.getChecklistCode(), jobLog.getChecklistName());

      Row logsRow = dataSheet.createRow(i + 1);
      for (String logKey : jobLogViewColumnMap.keySet()) {
        CustomViewColumn mappedCustomViewColumn = jobLogViewColumnMap.get(logKey);
        JobLogData mappedJobLogData = requiredJobLogData.get(logKey);
        Cell logsCell = logsRow.createCell(mappedCustomViewColumn.getOrderTree() - 1);

        if (mappedJobLogData != null) {
          setCellValue(logsCell, mappedCustomViewColumn.getType(), mappedJobLogData, createHelper, wb);
        }
      }
    }
  }

  private static void setCellValue(Cell logsCell, String type, JobLogData mappedJobLogData, CreationHelper createHelper, Workbook wb) {
    var jobLogColumnType = Type.JobLogColumnType.valueOf(type);
    switch (jobLogColumnType) {
      case TEXT -> {
        if (Objects.requireNonNull(mappedJobLogData.getTriggerType()) == Type.JobLogTriggerType.RESOURCE) {
          String resourceValue = mappedJobLogData.getResourceParameters().values().stream()
            .map(jobLogResource -> jobLogResource.getDisplayName() + ":" + jobLogResource.getChoices()
              .stream()
              .map(ResourceParameterChoiceDto::getObjectDisplayName)
              .collect(Collectors.joining(","))
            ).collect(Collectors.joining());
          logsCell.setCellValue(resourceValue);
        } else {
          logsCell.setCellValue(mappedJobLogData.getValue());
        }
      }
      case DATE -> {
        LocalDateTime localDateTime = DateTimeUtils.getLocalDateTime(Long.parseLong(mappedJobLogData.getValue()));
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(DateTimeUtils.getWorkbookDateFormat()));
        logsCell.setCellValue(localDateTime);
        logsCell.setCellStyle(cellStyle);
      }
    }
  }

  public void createDetailsSheet(Workbook wb, List<CustomViewFilter> filters, Map<String, String> checklistNameAndCodeMapping) {
    wb.createSheet(DETAILS_SHEET);
    Sheet detailsSheet = wb.getSheet(DETAILS_SHEET);

    CellStyle headerStyle = wb.createCellStyle();
    XSSFFont font = getXSSFFont((XSSFWorkbook) wb, ARIAL_FONT_NAME, FONT_HEIGHT);
    headerStyle.setFont(font);

    Row processDetailsRow = detailsSheet.createRow(0);
    Cell processCellHeader = processDetailsRow.createCell(0);
    processCellHeader.setCellValue(PROCESS_CELL);
    processCellHeader.setCellStyle(headerStyle);

    int cellCounter = 1;
    for (Map.Entry<String, String> entry : checklistNameAndCodeMapping.entrySet()) {
      Cell processCellValue = processDetailsRow.createCell(cellCounter);
      var processValue = String.format("%s (ID: %s), ", entry.getValue(), entry.getKey());
      processCellValue.setCellValue(processValue);
      cellCounter++;
    }
    Row filterHeaderRow = detailsSheet.createRow(1);
    Cell filterHeaderCell = filterHeaderRow.createCell(0);
    filterHeaderCell.setCellValue(FILTERS_CELL);
    filterHeaderCell.setCellStyle(headerStyle);


    if (!Utility.isEmpty(filters)) {
      for (int i = 0; i < filters.size(); i++) {
        Row filterIndexRow = detailsSheet.createRow(checklistNameAndCodeMapping.size() + i + 2);
        Cell filterIndexCell = filterIndexRow.createCell(0);
        filterIndexCell.setCellValue("Filter " + (i + 1));
        filterIndexCell.setCellStyle(headerStyle);
        var filter = filters.get(i);
        String filterValue = filter.getValue(), filterDisplayName = filter.getDisplayName(), comparisonOperator = getComparisonOperatorValue(filter.getConstraint());
        String filterKeyValue = filter.getKey();
        if (!filterKeyValue.contains("parameterValues")) {
          var filterKey= Arrays.stream(values()).filter(f -> Objects.equals(f.getValue(), filter.getKey())).findFirst().get();
          filterValue = switch (filterKey) {
            case ENDED_AT, CREATED_AT, MODIFIED_AT -> DateTimeUtils.getFormattedDateTime(Long.valueOf(filterValue));
            default -> filterValue;
          };
        }
        var filterFormat = String.format("Where %s %s %s", filterDisplayName, comparisonOperator, filterValue);
        Cell filterValueCell = filterIndexRow.createCell(1);
        filterValueCell.setCellValue(filterFormat);
      }
    }
  }

  private String getComparisonOperatorValue(String operator) {
    return SearchOperator.valueOf(operator).getOperator();
  }
}
