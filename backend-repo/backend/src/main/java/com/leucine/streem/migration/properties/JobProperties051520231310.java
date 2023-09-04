package com.leucine.streem.migration.properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leucine.commons.id.IdGenerator;
import com.leucine.streem.migration.properties.config.PropertyLoader;
import com.leucine.streem.migration.properties.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class JobProperties051520231310 {
  private JdbcTemplate jdbcTemplate;
  private DataSourceTransactionManager transactionManager;
  private static final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


  public void execute() throws JsonProcessingException {
    initialiseJdbcTemplate();
    DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
    TransactionStatus status = transactionManager.getTransaction(definition);
    try {

      migrateJobProperties();
      migrateJobPropertyValues();
      resetRules();
      transactionManager.commit(status);
    } catch (Exception e) {
      log.error("Error while migrating job properties", e);
      throw new RuntimeException(e);
    }
  }

  private void initialiseJdbcTemplate() {
    PropertyLoader propertyLoader = new PropertyLoader();
    String driverName = propertyLoader.getProperty("spring.datasource.driver-class-name");
    String database = propertyLoader.getProperty("spring.jpa.database");
    String datasourceHost = propertyLoader.getProperty("datasource.host");
    String datasourcePort = propertyLoader.getProperty("datasource.port");
    String datasourceDatabase = propertyLoader.getProperty("datasource.database");
    String url = "jdbc:%s://%s:%s/%s".formatted(database, datasourceHost, datasourcePort, datasourceDatabase);
    String username = propertyLoader.getProperty("datasource.username");
    String password = propertyLoader.getProperty("datasource.password");

    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(driverName);
    dataSource.setUrl(url);
    dataSource.setUsername(username);
    dataSource.setPassword(password);
    jdbcTemplate = new JdbcTemplate(dataSource);
    transactionManager = new DataSourceTransactionManager(dataSource);
  }

  private void migrateJobProperties() throws JsonProcessingException {
    String getJobPropertySQL = Queries.GET_PROPERTIES_OF_TYPE;

    List<Long> propertyIdSet = jdbcTemplate.queryForList(getJobPropertySQL, Long.class, "JOB");

    String propertyIds = propertyIdSet.stream()
      .map(String::valueOf)
      .collect(Collectors.joining(","));

    String getFacilityUseCasePropertyMappingSQL = Queries.GET_FUPM_BY_PROPERTY_IDS.formatted(propertyIds);

    RowMapper<FacilityUseCasePropertyMappingDto> fupmRowMapper = (rs, rowNum) -> {
      FacilityUseCasePropertyMappingDto dto = new FacilityUseCasePropertyMappingDto();
      dto.setId(rs.getLong("id"));
      dto.setFacilityId(rs.getLong("facilities_id"));
      dto.setUseCaseId(rs.getLong("use_cases_id"));
      dto.setPropertiesId(rs.getLong("properties_id"));
      dto.setLabelAlias(rs.getString("label_alias"));
      dto.setPlaceHolderAlias(rs.getString("place_holder_alias"));
      dto.setOrderTree(rs.getInt("order_tree"));
      dto.setMandatory(rs.getBoolean("is_mandatory"));
      dto.setCreatedAt(rs.getLong("created_at"));
      dto.setCreatedBy(rs.getLong("created_by"));
      dto.setModifiedAt(rs.getLong("modified_at"));
      dto.setModifiedBy(rs.getLong("modified_by"));
      return dto;
    };
    List<FacilityUseCasePropertyMappingDto> fupMapping = new ArrayList<>();
    if (!ObjectUtils.isEmpty(propertyIds)) {
      fupMapping = jdbcTemplate.query(getFacilityUseCasePropertyMappingSQL, fupmRowMapper);
    }

    String checklistFacilityMappingSQL = Queries.GET_ALL_CHECKLIST_FACILITY_MAPPING;

    RowMapper<ChecklistFacilityMappingDto> cfmRowMapper = (rs, rowNum) -> {
      ChecklistFacilityMappingDto dto = new ChecklistFacilityMappingDto();
      dto.setChecklistId(rs.getLong("checklists_id"));
      dto.setFacilityId(rs.getLong("facilities_id"));
      return dto;
    };

    List<ChecklistFacilityMappingDto> checklistFacilityMappingList = jdbcTemplate.query(checklistFacilityMappingSQL, cfmRowMapper);
    Set<Long> checklistIds = checklistFacilityMappingList.stream()
      .map(ChecklistFacilityMappingDto::getChecklistId)
      .collect(Collectors.toSet());

    Map<Long, Long> checklistUseCaseMap = new HashMap<>();
    if (!ObjectUtils.isEmpty(checklistIds)) {
      String getAllUseCasesWithChecklistIdsSQL = "select id, use_cases_id from checklists where id in (%s)".formatted(checklistIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
      checklistUseCaseMap = jdbcTemplate.query(getAllUseCasesWithChecklistIdsSQL, rs -> {
        Map<Long, Long> map = new HashMap<>();
        while (rs.next()) {
          Long id = rs.getLong("id");
          Long useCaseId = rs.getLong("use_cases_id");
          map.put(id, useCaseId);
        }
        return map;
      });
    }

    Map<Long, Set<Long>> checklistFacilityMappingMap = checklistFacilityMappingList.stream()
      .collect(Collectors.groupingBy(ChecklistFacilityMappingDto::getChecklistId, Collectors.mapping(ChecklistFacilityMappingDto::getFacilityId, Collectors.toSet())));
    for (FacilityUseCasePropertyMappingDto fupm : fupMapping) {
      long facilityId = fupm.getFacilityId();
      if (!ObjectUtils.isEmpty(checklistFacilityMappingList)) {
        List<ParameterDto> parameterDtoList = new ArrayList<>();
        for (ChecklistFacilityMappingDto cfm : checklistFacilityMappingList) {
          if (checklistFacilityMappingMap.get(cfm.getChecklistId()).contains(facilityId)) {
            Long fupmId = fupm.getId();
            Long checklistId = cfm.getChecklistId();
            Long checklistUseCaseId = checklistUseCaseMap.get(checklistId);
            if (Objects.equals(checklistUseCaseId, fupm.getUseCaseId())) {
              Long parameterId = IdGenerator.getInstance().nextId();
              ParameterDto parameterDto = new ParameterDto();
              parameterDto.setId(parameterId);
              parameterDto.setArchived(false);
              parameterDto.setOrderTree(fupm.getOrderTree());
              parameterDto.setChecklistId(checklistId);
              parameterDto.setLabel(fupm.getLabelAlias());
              parameterDto.setData("{}");
              parameterDto.setMandatory(fupm.isMandatory());
              parameterDto.setType("SINGLE_LINE");
              parameterDto.setTargetEntityType("PROCESS");
              parameterDto.setCreatedAt(fupm.getCreatedAt());
              parameterDto.setCreatedBy(fupm.getCreatedBy());
              parameterDto.setModifiedAt(fupm.getModifiedAt());
              parameterDto.setModifiedBy(fupm.getModifiedBy());
              parameterDto.setAutoInitialized(false);
              parameterDto.setAutoInitialize("{}");
              parameterDto.setValidations("{}");
              String rule = objectMapper.readTree("""
                {
                  "fupmId": %d,
                  "parameterId": %d,
                  "checklistId": %d
                }
                """.formatted(fupmId, parameterId, checklistId)).toString();
              parameterDto.setRules(rule);

              parameterDtoList.add(parameterDto);
            }
          }
        }
        String insertParameterSQL = Queries.INSERT_INTO_PARAMETERS;

        List<Object[]> batchArgsList = new ArrayList<>();
        for (ParameterDto parameterDto : parameterDtoList) {
          Object[] batchArgs = new Object[]{
            parameterDto.getId(),
            parameterDto.getType(),
            parameterDto.getTargetEntityType(),
            parameterDto.getLabel(),
            parameterDto.getDescription(),
            parameterDto.getOrderTree(),
            parameterDto.isMandatory(),
            parameterDto.isArchived(),
            "{}",
            parameterDto.getChecklistId(),
            parameterDto.getValidations(),
            false,
            "{}",
            parameterDto.getRules(),
            parameterDto.getCreatedAt(),
            parameterDto.getModifiedAt(),
            parameterDto.getCreatedBy(),
            parameterDto.getModifiedBy()
          };
          batchArgsList.add(batchArgs);
        }
        jdbcTemplate.batchUpdate(insertParameterSQL, batchArgsList);

      }
    }
  }

  private void migrateJobPropertyValues() throws JsonProcessingException {
    String getAllParameterOfTargetEntityTypeProcess = Queries.GET_ALL_HACK_NODE_VALUE_STORED_IN_RULES;
    List<String> rules = jdbcTemplate.query(getAllParameterOfTargetEntityTypeProcess, (rs, rowNum) -> rs.getString("rules"));
    Set<JobPropertyValueDto> completedJobAndFupmIds = new HashSet<>();
    long epochId = IdGenerator.getInstance().nextId();

    for (String rule : rules) {
      if (!objectMapper.readTree(rule).isEmpty()) {

        JsonNode hackNode = objectMapper.readTree(rule);
        JsonNode fupmIdNode = hackNode.get("fupmId");
        Long fupmId = fupmIdNode == null ? null : Long.valueOf(fupmIdNode.asText());
        JsonNode parameterIdNode = hackNode.get("parameterId");
        Long parameterId = parameterIdNode == null ? null : Long.valueOf(parameterIdNode.asText());
        JsonNode checklistIdNode = hackNode.get("checklistId");
        Long checklistId = checklistIdNode == null ? null : Long.valueOf(checklistIdNode.asText());

        if (!ObjectUtils.isEmpty(fupmId) && !ObjectUtils.isEmpty(parameterId) && !ObjectUtils.isEmpty(checklistId)) {
          String getJobPropertySQL = Queries.GET_ALL_JOB_PROPERTY_VALUES_WITH_FUPM_ID;

          RowMapper<JobPropertyValueDto> jobPropertyValueDtoRowMapper = (rs, rowNum) -> {
            JobPropertyValueDto dto = new JobPropertyValueDto();
            dto.setJobId(rs.getLong("jobs_id"));
            dto.setFupmId(rs.getLong("facility_use_case_property_mapping_id"));
            dto.setValue(rs.getString("value"));
            dto.setCreatedAt(rs.getLong("created_at"));
            dto.setCreatedBy(rs.getLong("created_by"));
            dto.setModifiedAt(rs.getLong("modified_at"));
            dto.setModifiedBy(rs.getLong("modified_by"));
            return dto;
          };
          List<JobPropertyValueDto> jobPropertyValueDtoList = jdbcTemplate.query(getJobPropertySQL, jobPropertyValueDtoRowMapper, fupmId);

          String jobIds = jobPropertyValueDtoList.stream()
            .map(jobPropertyValueDto -> jobPropertyValueDto.getJobId().toString())
            .collect(Collectors.joining(","));

          String getJobSQL = Queries.GET_ALL_JOB_WITH_IDS.formatted(jobIds);
          RowMapper<JobDto> jobDtoRowMapper = (rs, rowNum) -> {
            JobDto dto = new JobDto();
            dto.setId(rs.getLong("id"));
            dto.setChecklistId(rs.getLong("checklists_id"));
            return dto;
          };
          List<JobDto> jobDtoList = new ArrayList<>();
          if (!ObjectUtils.isEmpty(jobPropertyValueDtoList)) {
            jobDtoList = jdbcTemplate.query(getJobSQL, jobDtoRowMapper);
          }
          Map<Long, Long> jobChecklistMap = jobDtoList.stream()
            .collect(Collectors.toMap(JobDto::getId, JobDto::getChecklistId));

          List<ParameterValueDto> parameterValueDtoList = new ArrayList<>();
          for (JobPropertyValueDto jobPropertyValueDto : jobPropertyValueDtoList) {
            Long checklistIdFromJob = jobChecklistMap.get(jobPropertyValueDto.getJobId());
            if (Objects.equals(checklistIdFromJob, checklistId) && !completedJobAndFupmIds.contains(new JobPropertyValueDto(jobPropertyValueDto.getFupmId(), jobPropertyValueDto.getJobId()))) {
              ParameterValueDto parameterValueDto = new ParameterValueDto();
              parameterValueDto.setId(++epochId);
              parameterValueDto.setValue(jobPropertyValueDto.getValue());
              parameterValueDto.setParameterId(parameterId);
              parameterValueDto.setJobId(jobPropertyValueDto.getJobId());
              parameterValueDto.setCreatedAt(jobPropertyValueDto.getCreatedAt());
              parameterValueDto.setCreatedBy(jobPropertyValueDto.getCreatedBy());
              parameterValueDto.setModifiedAt(jobPropertyValueDto.getModifiedAt());
              parameterValueDto.setModifiedBy(jobPropertyValueDto.getModifiedBy());
              parameterValueDto.setState("EXECUTED");
              parameterValueDto.setHidden(false);
              completedJobAndFupmIds.add(new JobPropertyValueDto(fupmId, jobPropertyValueDto.getJobId()));
              parameterValueDtoList.add(parameterValueDto);
            }
          }
          List<Object[]> parameterValueDtoObjectList = parameterValueDtoList.stream().map(dto -> new Object[]{
            dto.getId(),
            dto.getValue(),
            dto.getReason(),
            dto.getState(),
            dto.getChoices(),
            dto.getJobId(),
            dto.getParameterId(),
            dto.getCreatedAt(),
            dto.getModifiedAt(),
            dto.getCreatedBy(),
            dto.getModifiedBy(),
            dto.isHidden()
          }).collect(Collectors.toList());

          String bulkInsertParameterValues = Queries.INSERT_INTO_PARAMETER_VALUES;

          jdbcTemplate.batchUpdate(bulkInsertParameterValues, parameterValueDtoObjectList);
        }
      }
    }

  }

  private void resetRules() {
    String updateParameterRuleSQL = Queries.RESTORE_ALL_HACK_NODES_STORED_IN_RULES;
    jdbcTemplate.update(updateParameterRuleSQL);
  }

}
