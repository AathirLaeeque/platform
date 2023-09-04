package com.leucine.streem.constant;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class Type {

  public static final Set<Parameter> NON_EXECUTABLE_PARAMETER_TYPES = Collections.unmodifiableSet(EnumSet.of(Parameter.MATERIAL, Parameter.INSTRUCTION));

  public static final Set<Collaborator> AUTHOR_TYPES = Collections.unmodifiableSet(EnumSet.of(Collaborator.AUTHOR, Collaborator.PRIMARY_AUTHOR));

  public static final Set<Parameter> ALLOWED_PARAMETER_TYPES_FOR_CALCULATION_PARAMETER = Collections.unmodifiableSet(EnumSet.of(Parameter.CALCULATION, Parameter.NUMBER, Parameter.SHOULD_BE));

  public static final Set<Parameter> ALLOWED_PARAMETER_TYPES_NUMBER_PARAMETER_VALIDATION = Collections.unmodifiableSet(EnumSet.of(Parameter.RESOURCE));

  public static final Set<Parameter> PARAMETER_MEDIA_TYPES = Collections.unmodifiableSet(EnumSet.of(Parameter.MEDIA, Parameter.FILE_UPLOAD, Parameter.SIGNATURE));

  public static final Set<Type.AutomationActionType> ACTION_TYPES_WITH_NO_REFERENCED_PARAMETER_ID = Set.of(Type.AutomationActionType.SET_PROPERTY, Type.AutomationActionType.ARCHIVE_OBJECT, Type.AutomationActionType.CREATE_OBJECT);

  public enum PropertyType {
    CHECKLIST,
    JOB
  }
  public enum VerificationType {
    NONE,
    SELF,
    PEER,
    BOTH,
  }

  public enum Parameter {
    CALCULATION,
    CHECKLIST,
    DATE,
    DATE_TIME,
    INSTRUCTION,
    MATERIAL,
    MEDIA,
    FILE_UPLOAD,
    MULTISELECT,
    NUMBER,
    SHOULD_BE,
    RESOURCE,
    MULTI_RESOURCE,
    SINGLE_SELECT,
    SINGLE_LINE,
    SIGNATURE,
    MULTI_LINE,
    YES_NO
  }

  public enum EntityType {
    ACTIVITY("ACT"),
    CHECKLIST("CHK"),
    SCHEDULER("SCH"),
    JOB("JOB"),
    TASK("TSK"),
    STAGE("STG");

    private final String code;

    EntityType(String code) {
      this.code = code;
    }

    public String getCode() {
      return code;
    }
  }

  public enum Collaborator {
    PRIMARY_AUTHOR,
    AUTHOR,
    REVIEWER,
    SIGN_OFF_USER,
  }

  public enum TaskException {
    COMPLETED_WITH_EXCEPTION,
    ERROR_CORRECTION,
    PARAMETER_DEVIATION,
    SKIPPED,
    DURATION_EXCEPTION,
    YES_NO
  }

  public enum JobLogColumnType {
    DATE("Date"),
    TEXT("Text"),
    FILE("File");

    private final String label;

    JobLogColumnType(String label) {
      this.label = label;
    }

    public String get() {
      return label;
    }
  }

  public enum JobLogTriggerType {
    CHK_ID, // common
    JOB_ID, // common
    JOB_STATE, // common
    CHK_NAME, // common
    PARAMETER_VALUE,
    PROCESS_PARAMETER_VALUE,
    RELATION_VALUE,
    RESOURCE_PARAMETER,
    JOB_PROPERTY_VALUE,
    CHK_PROPERTY_VALUE,
    JOB_START_TIME, // common
    TSK_START_TIME,
    TSK_END_TIME,
    JOB_END_TIME, // common
    JOB_CREATED_AT, // common
    JOB_CREATED_BY, // common
    JOB_MODIFIED_BY, // common
    JOB_STARTED_BY, // common
    JOB_ENDED_BY, // common
    TSK_STARTED_BY, // common
    TSK_ENDED_BY, // common
    RESOURCE

  }

  public enum AutomationType {
    PROCESS_BASED,
    OBJECT_BASED,
  }
  public enum AutomationTriggerType {
    JOB_STARTED,
    JOB_COMPLETED,
    TASK_STARTED,
    TASK_COMPLETED,
  }

  public enum AutomationActionType {
    SET_PROPERTY,
    INCREASE_PROPERTY,
    DECREASE_PROPERTY,
    CLEAR_PROPERTY,
    CREATE_OBJECT,
    ARCHIVE_OBJECT,
    SET_RELATION
  }

  public enum TargetEntityType {
    RESOURCE_PARAMETER,
    OBJECT
  }

  public enum ParameterTargetEntityType {
    TASK,
    PROCESS,
    UNMAPPED
  }

  public enum AutomationDateTimeCaptureType {
    START_TIME,
    END_TIME
  }

  public enum ConfigurableViewTargetType {
    PROCESS,
    JOB
  }

  public enum CustomViewFilterType {
    CHECKLIST_ID("checklistId"),
    CHECKLIST_NAME("checklistName"),
    CHECKLIST_CODE("checklistCode"),
    JOB_CODE("code"),
    JOB_STATE("state"),
    STARTED_AT("startedAt"),
    ENDED_AT("endedAt"),
    CREATED_AT("createdAt"),
    MODIFIED_AT("modifiedAt"),
    CREATED_BY("createdBy"),
    STARTED_BY("startedBy"),
    MODIFIED_BY("modifiedBy"),
    ENDED_BY("endedBy");
    private final String value;

    CustomViewFilterType(String value) {
      this.value=value;
    }

    public String getValue() {
      return value;
    }

  }

  public enum ScheduledJobType {
    CREATE_JOB
  }

  public enum ScheduledJobGroup {
    JOBS
  }
}
