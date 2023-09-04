package com.leucine.streem.dto.response;

public enum ErrorCode {
  // Checklist
  PROCESS_NOT_FOUND("E101", "Process not Found"),
  PROCESS_NOT_PUBLISHED("E102", "Process not published"),
  PROCESS_CANNOT_BE_MODFIFIED("E103", "Process cannot be modified"),
  USER_NOT_ALLOWED_TO_MODIFY_PROCESS("E103", "User not allowed to modify process"),
  CANNOT_ASSIGN_COLLABORATOR("E106", "User cannot be assigned as he/she has already assiged or has not been part of review."),
  CANNOT_UNASSIGN_COLLABORATOR("E104", "User cannot be unassigned as he/she has already started or has been part of review."),
  ONLY_PRIMARY_AUTHORS_ALLOWED("E105", "Only prototype owner is allowed to perform the operation."),
  //  CANNOT_UNASSIGN_REVIEWER("E106", "User cannot be unassigned as he/she has already started or has been part of review."),
  //  ALREADY_ASSIGNED_FOR_REVIEWING("E106", "Already assigned for reviewing."),
//  CHECKLIST_NOT_IN_REVIEW_STATE("E108", "Checklist not in being reviewed or change request in progress state."),
//  CANNOT_START_AN_ALREADY_STARTED_REVIEW("E109", "Cannot start an already started review"),
//  CANNOT_COMPLETE_AN_REVIEW_THAT_IS_NOT_STARTED("E110", "Cannot complete an review which is not started"),
//  CANNOT_CONTINUE_A_REVIEW_THAT_IS_NOT_DONE("E111", "Cannot continue reviewing that is not completed"),
//  CANNOT_SEND_TO_AUTHOR("E112", "Cannot send to author as you have not completed/done with review"),
  PROCESS_INVALID_STATE_ACTION("E113", "Action performed on the process is invalid on current state"),
  //  CHECKLIST_ALREADY_BEING_REVIEWED("E114", "Checklist is already being reviewed"),
//  CHECKLIST_ALREADY_READY_FOR_RELEASE("E115", "Checklist is already ready for release"),
//  CHECKLIST_ALREADY_PUBLISHED("E116", "Checklist has already being published"),
  PROCESS_AUTHOR_ALREADY_ASSIGNED("E117", "Author is already assigned to process"),
  PROCESS_AUTHOR_NOT_ASSIGNED("E118", "Author is not assigned to process"),
  CANNOT_UNASSIGN_PRIMARY_AUTHOR_FROM_PROCESS("E119", "Cannot unassign primary author from process"),
  CANNOT_ARCHIVE_PROCESS_WITH_ACTIVE_JOBS("E120", "Process can't be archived as there are active Jobs for it"),
  CANNOT_START_REVISION_FROM_NON_PUBLISHED_PROCESS("E121", "Cannot start a revision from a non-published process"),
  REVISION_ALREADY_BEING_BUILT("E122", "This process is already being Revised. You Cannot revise it again."),
  PROCESS_BY_TASK_NOT_FOUND("E123", "No process found for given Task Id"),
  PROCESS_REVIEWER_CANNOT_BE_AUTHOR("E124", "Some of the selected Authors have already reviewed this prototype. Reviewers cannot be Authors."),

  /*--Collaborator--*/
  COLLABORATION_INVALID_STATE_ACTION("E120", "Action performed on the collaboration is invalid on current state"),

  /*--SignOff users--*/
  PROCESS_USER_SIGNOFF_ERROR("E125", "User cannot signoff the process as previous users didn't sign off the process."),
  PROCESS_CAN_ONLY_BE_ARCHIVED_BY("E126", "Only account owner, facility admin and author can archive it"),
  PROCESS_EMPTY_STAGE_VALIDATION("E127", "Process must contain at least one stage"),
  PROCESS_EMPTY_TASK_VALIDATION("E128", "Stage must contain at least one task"),


  PROCESS_CANNOT_BE_CREATED_FROM_ALL_FACILITY("E129", "Process cannot be created from All Facility"),
  JOB_CANNOT_BE_CREATED_FROM_ALL_FACILITY("E130", "Job cannot be created from Global Portal"),
  ARCHIVE_REASON_CANNOT_BE_EMPTY("E131", "Archive reason cannot be empty"),
  UNARCHIVE_REASON_CANNOT_BE_EMPTY("E132", "Unarchive reason cannot be empty"),
  PROCESS_RELATION_STATUS_VALIDATION_ERROR("E133", "Process relation status validation error"),
  PROCESS_RELATION_PROPERTY_VALIDATION_ERROR("E134", "Process relation property validation error"),
  PROCESS_RELATION_PROPERTY_VALIDATION_COULD_NOT_RUN_MISSING_DATA("E135", "Could not perform relation property validation due to missing data"),
  PROCESS_RELATION_AUTOMATION_ACTION_ERROR("E136", "Could not perform actions related to process relation"),
  CANNOT_ASSIGN_TRAINING_USER_IN_ALL_FACILITY("E137", "Cannot assign training users from Global Portal"),
  PROCESS_CAN_ONLY_BE_UNARCHIVED_BY("E138", "Only Owner, Facility Admin, Author can unarchive it"),

  // Task
  TASK_INCOMPLETE("E201", "Task Incomplete"),
  TASK_NOT_FOUND("E202", "Task not Found"),
  TIMED_TASK_REASON_CANNOT_BE_EMPTY("E203", "Timed Task reason cannot be empty"),
  PROVIDE_REASON_TO_SKIP_TASK("E204", "Provide reason to skip Task"),
  USER_NOT_ASSIGNED_TO_EXECUTE_TASK("E206", "You are not assigned to this Task"),
  PROVIDE_REASON_TO_FORCE_CLOSE_TASK("E207", "Provide reason to force close Task"),
  OPERATION_CANNOT_BE_PERFORMED_ON_A_COMPLETED_TASK("E208", "Operation cannot be performed on a completed Task"),
  TIMED_TASK_INVALID_OPERATION("E209", "Timed task invalid operation"),
  TASK_NAME_CANNOT_BE_EMPTY("E210", "Task name cannot be empty"),
  TASK_SHOULD_HAVE_ATLEAST_ONE_EXECUTABLE_PARAMETER("E211", "Task should have at least one executable parameter"),
  TASK_NOT_IN_PROGRESS("E212", "Task not in progress"),
  TASK_ALREADY_COMPLETED("E213", "Task already completed"),
  TASK_NOT_ENABLED_FOR_CORRECTION("E214", "Task not enabled for correction"),
  TASK_NOT_SIGNED_OFF("E215", "Task not signed off"),
  TASK_MEDIA_NOT_FOUND("E216", "Task media not found"),
  TIMED_TASK_LT_TIMER_CANNOT_BE_ZERO("E217", "Timer cannot be set to zero"),
  TIMED_TASK_NLT_MIN_PERIOD_CANNOT_BE_ZERO("E218", "Minimum time cannot be zero"),
  TIMED_TASK_NLT_MAX_PERIOD_SHOULD_BE_GT_MIN_PERIOD("E219", "Maximum time should be more than Minimum time"),
  TASK_COMPLETED_ASSIGNMENT_FAILED("E220", "Failed to perform assignment operation on a completed task"),
  FAILED_TO_UNASSIGN_SINCE_USER_PERFORMED_ACTIONS_ON_TASK("E221", "Failed to unassign user since user performed some action on task"),
  FAILED_TO_UNASSIGN_SINCE_USER_SIGNED_OFF_TASK("E222", "Failed to unassign user since user signed off the task"),
  TASK_ALREADY_ENABLED_FOR_CORRECTION("E223", "Task already enabled for correction"),
  TASK_ENABLED_FOR_CORRECTION("E223", "Some of the tasks are enabled for correction. Please confirm/cancel them before completing Job with Exception"),
  TASK_ALREADY_IN_PROGRESS("E224", "Task already in progress"),
  TASK_IN_PROGRESS("E224", "Some of the tasks are in progress. Please complete them before completing Job with Exception"),
  TASK_AUTOMATION_INVALID_MAPPED_PARAMETERS("E225", "Task automation refers to parameters that no longer exist or have been archived"),
  TASK_IS_IN_NON_RESUMABLE_STATE("E226", "Task is in non resumable state"),
  TASK_IS_IN_NON_PAUSED_STATE("E227", "Task is in paused state"),
  DEPENDENT_TASK_NOT_ENABLED_FOR_CORRECTION("E214", "Dependent task is not enabled for correction"),
  AUTOMATION_WILL_NOT_RUN_DUE_TO_MISSING_RESOURCES("E215", "Automation not run due to missing resources"),


  //Stage
  STAGE_INCOMPLETE("E301", "Stage Incomplete"),
  STAGE_NOT_FOUND("E302", "Stage Not Found"),
  STAGE_NAME_CANNOT_BE_EMPTY("E303", "Stage name cannot be empty"),
  //Parameter
  PARAMETER_INCOMPLETE("E401", "Parameter Incomplete"),
  PARAMETER_NOT_FOUND("E402", "Parameter not found"),
  CANNOT_EXECUTE_PARAMETER_ON_A_COMPLETED_TASK("E403", "Cannot execute parameter on a completed Task"),
  CANNOT_EXECUTE_PARAMETER_ON_A_NONSTARTED_TASK("E404", "Cannot execute parameter on a non started Task"),
  PROVIDE_REASON_FOR_YES_NO_PARAMETER("E405", "Provide reason for yes/no parameter"),
  PROVIDE_REASON_FOR_PARAMETER_PARAMETER_OFF_LIMITS("E406", "Provide reason for parameter parameter off limits"),
  YES_NO_PARAMETER_OPTIONS_NAME_CANNOT_BE_EMPTY("E407", "yes/no parameter option's name cannot be empty"),
  YES_NO_PARAMETER_SHOULD_HAVE_EXACTLY_TWO_OPTIONS("E408", "yes/no parameter should have exactly two options"),
  YES_NO_PARAMETER_TITLE_CANNOT_BE_EMPTY("E409", "yes/no parameter title cannot be empty"),
  MULTISELECT_PARAMETER_OPTIONS_NAME_CANNOT_BE_EMPTY("E410", "Multiselect parameter option's name cannot be empty"),
  MULTISELECT_PARAMETER_OPTIONS_CANNOT_BE_EMPTY("E411", "Multiselect parameter options cannot be empty"),
  SINGLE_SELECT_PARAMETER_OPTIONS_NAME_CANNOT_BE_EMPTY("E412", "Single select parameter option's name cannot be empty"),
  SINGLE_SELECT_PARAMETER_OPTIONS_CANNOT_BE_EMPTY("E413", "Single select parameter options cannot be empty"),
  PROCESS_PARAMETER_OPTIONS_NAME_CANNOT_BE_EMPTY("E414", "Process parameter option's name cannot be empty"),
  PROCESS_PARAMETER_OPTIONS_CANNOT_BE_EMPTY("E415", "Process parameter options cannot be empty"),
  SHOULD_BE_PARAMETER_VALUE_INVALID("E417", "Invalid should-be parameter value(s)"),
  SHOULD_BE_PARAMETER_OPERATOR_CANNOT_BE_EMPTY("E418", "Should-be parameter operator cannot be empty"),
  MATERIAL_PARAMETER_NAME_CANNOT_BE_EMPTY("E419", "Material parameter name cannot be empty"),
  MATERIAL_PARAMETER_LIST_CANNOT_BE_EMPTY("E420", "Material parameter list cannot be empty"),
  MATERIAL_PARAMETER_CANNOT_BE_MANDATORY("E421", "Material parameter cannot be mandatory"),
  INSTRUCTION_PARAMETER_TEXT_CANNOT_BE_EMPTY("E422", "Instruction parameter text cannot be empty"),
  INSTRUCTION_PARAMETER_CANNOT_BE_MANDATORY("E423", "Instruction parameter cannot be mandatory"),
  PARAMETER_EXECUTION_NOT_FOUND("E425", "Parameter execution record not found"),
  PARAMETER_VALUE_NOT_FOUND("E426", "Parameter value record not found"),
  TEMP_PARAMETER_VALUE_NOT_FOUND("E427", "Temporary Parameter value record not found"),
  PARAMETER_EXECUTION_MEDIA_CANNOT_BE_EMPTY("E428", "Media cannot be empty"),
  ONLY_SUPERVISOR_CAN_APPROVE_OR_REJECT_PARAMETER("E429", "Only supervisor can approve or reject parameter"),
  SHOULD_BE_PARAMETER_UOM_CANNOT_BE_EMPTY("E430", "Parameter parameter unit of measurement cannot be empty"),
  SHOULD_BE_PARAMETER_NAME_CANNOT_BE_EMPTY("E431", "Parameter parameter name cannot be empty"),
  SHOULD_BE_PARAMETER_LOWER_VALUE_CANNOT_BE_MORE_THAN_UPPER_VALUE("E432", "Lower value cannot be more than Upper value"),
  CALCULATION_PARAMETER_EXPRESSION_CANNOT_BE_EMPTY("E433", "Calculation parameter expression cannot be empty"),
  CALCULATION_PARAMETER_INVALID_EXPRESSION("E434", "Calculation parameter invalid expression"),
  CALCULATION_PARAMETER_UOM_CANNOT_BE_EMPTY("E435", "Calculation parameter unit of measurement cannot be empty"),
  CALCULATION_PARAMETER_DEPENDENT_PARAMETER_VALUES_NOT_SET("E436", "Some of the parameter values are not set to perform this calculation"),

  CALCULATION_PARAMETER_DEPENDENT_PARAMETERS_NOT_FOUND("E437", "Some of the parameters required to perform this calculation does not exist"),
  CALCULATION_PARAMETER_LABEL_CANNOT_BE_EMPTY("E438", "Calculation parameter label cannot be empty"),
  PARAMETER_LABEL_CANNOT_BE_EMPTY("E439", "Parameter label cannot be empty"),

  NUMBER_PARAMETER_INVALID_VALUE("E440", "Number parameter invalid value"),
  NUMBER_PARAMETER_CUSTOM_VALIDATION_ERROR("E441", "Number parameter custom validation error"),
  NUMBER_PARAMETER_RELATION_PROPERTY_VALIDATION_ERROR("E442", "Number parameter relation property validation error"),
  NUMBER_PARAMETER_RELATION_PROPERTY_VALIDATION_COULD_NOT_RUN_MISSING_DATA("E443", "Could not perform relation property validation due to missing data"),
  PARAMETER_ALREADY_MAPPED("E444", "Parameter has already been mapped"),
  ERROR_UNMAPPING_PARAMETER("E445", "Error unmapping parameters"),
  ERROR_MAPPING_PARAMETER("E446", "Error mapping parameters"),
  ERROR_REORDERING_PARAMETER("E447", "Error reordering parameters"),
  MANDATORY_PARAMETER_VALUES_NOT_PROVIDED("E448", "Mandatory parameter values not provided"),
  UNMAPPED_PARAMETERS_EXISTS("E449", "Process contains parameters that are not mapped"),
  PARAMETER_DATA_INCONSISTENCY("E450", "Parameter Data Inconsistent"),
  PARAMETER_VALIDATIONS_INCONSISTENT_DATA("E451", "Some of the parameters required to perform validations on this parameter does not exist"),
  PARAMETER_AUTO_INITIALIZE_INVALID_DATE("E452", "Parameter is set to auto initialize but data is not found"),

  DATE_PARAMETER_INVALID_VALUE("E453", "Date parameter invalid value"),
  DETECTED_A_CYCLE_IN_CALCULATION_PARAMETER("E454", "Detected a cycle in calculation parameter"),
  CALCULATION_PARAMETER_INVALID_VARIABLE_NAME("E455", "Calculation activity invalid variable name"),
  PARAMETER_CANNOT_BE_ASSIGNED_FOR_CREATE_JOB_FORM("E456", "parameter cannot be assigned for create job form due to verification enabled"),
  USER_NOT_ALLOWED_TO_SELF_VERIFIY_PARAMETER("E458", "User not allowed to self verify parameter"),
  PARAMETER_VERIFICATION_NOT_FOUND("E459", "Parameter verification details not found"),
  PARAMETER_VERIFICATION_INITIATION_FAILED("E460", "Parameter verification initiation failed"),
  SELF_VERIFICATION_NOT_ALLOWED("E461", "Self verification not allowed"),
  PEER_VERIFICATION_NOT_ALLOWED("E462", "Peer verification not allowed"),
  PARAMETER_VERIFICATION_COMPLETION_FAILED("E462", "Parameter verification completion failed"),
  PARAMETER_VERIFICATION_CANCELLATION_FAILED("E463", "Parameter verification cancellation failed"),
  PARAMETER_VERIFICATION_RECALL_FAILED("E464", "parameter verification recall failed"),
  PARAMETER_VERIFICATION_NOT_ALLOWED("E465", "Parameter verification not allowed"),
  CANNOT_EXECUTE_VERIFICATION_PENDING_PARAMETER("E466", "paramter cannot be execcuted due to pending verification approval"),
  CANNOT_SEND_PARAMETER_FOR_PEER_VERIFICATION("E467", "parameter cannot be sent for peer verification"),

  RESOURCE_PARAMETER_OBJECT_TYPE_CANNOT_BE_EMPTY("E468", "Resource Parameter Object Type cannot be empty"),
  INVALID_FILTER_CONFIGURATIONS("E457", "Invalid filter configurations"),
  VERIFICATION_CANNOT_BE_ENABLED_FOR_CREATE_JOB_FORM_PARAMETER("E469", "verification cannot be enabled for create job form type of parameter"),
  PROCESS_PARAMETER_LINKED_TO_ANOTHER_PROCESS_PARAMETER("E458", "Process parameter is linked to another process parameter, please de-link that process parameter from all other process parameters before deleting it"),
  PARAMETER_EXECUTION_MEDIA_ARCHIVED_REASON_CANNOT_BE_EMPTY("E459", "Reason for archiving media cannot be empty"),
  PARAMETER_EXECUTION_MEDIA_NAME_CANNOT_BE_EMPTY("E460", "Media name cannot be empty"),
  CALCULATION_PARAMETER_VARIABLE_SET_CANNOT_BE_EMPTY("E461", "Calculation parameter variable set cannot be empty"),
  CJF_PARAMETER_CANNOT_BE_AUTOINITIALIAZED_BY_TASK_PARAMETER("E470", "Process parameter cannot be auto linked by a task parameter"),
  UNCHECKED_PARAMETER_CHOICES_EXITS("E471", "There are unchecked items in parameter, please check all items before verification"),
  CALCULATION_PARAMETER_VARIABLE_CONTAINS_ARCHIVED_PARAMETER("E472", "Calculation parameter variable contains archived parameter"),
  MANDATORY_ACTIVITY_PENDING("E473", "Mandatory activity pending"),

  //Media
  MEDIA_NOT_FOUND("E501", "Media Not found"),
  MATERIAL_PARAMETER_INVALID_FILE_EXTENSION("E502", "Invalid file uploaded for material parameter"),
  //User
  USER_NOT_FOUND("E601", "User Not found"),
  CANNOT_ARCHIVE_USER("E602", "User cannot be archived since he/she is assigned to active jobs"),
  //Job
  JOB_NOT_FOUND("E701", "Job Not found"),
  JOB_IS_NOT_IN_PROGRESS("E702", "Job is not in progress"),
  JOB_ALREADY_COMPLETED("E703", "Job already completed"),
  JOB_IS_BLOCKED("E704", "Job is in blocked state"),
  JOB_IS_NOT_IN_BLOCKED("E705", "Job is not in blocked state"),
  UNASSIGNED_JOB_CANNOT_BE_STARTED("E706", "Unassigned job cannot be started"),
  JOB_ALREADY_STARTED("E707", "Job already started"),
  UNSUPPORTED_JOB_STATE("E708", "Unsupported job state"),
  ARCHIVED_USER_CANNOT_BE_ASSIGNED_TO_JOB("E709", "Archived User can't be assigned to a Job"),
  JOB_NO_STARTED("E710", "Job not started"),
  USER_ALREADY_ASSIGNED_TO_JOB("E711", "User is already assigned to this Job"),
  JOB_CWE_DETAIL_NOT_FOUND("E712", "Job cwe detail not found for this job"),
  COMMENT_TO_COMPLETE_JOB_WITH_EXCEPTION_CANNOT_BE_EMPTY("E713", "Comment to complete job with exception cannot be empty"),
  JOB_RELATION_MANDATORY("E714", "Relation in this job is mandatory"),
  USER_NOT_ASSIGNED_TO_EXECUTE_JOB("E801", "You are not assigned to this Job"),
  RESOURCE_ALREADY_EXIST("E901", "Resource already Exists"),
  NOT_AUTHORIZED("E1001", "Unauthorized"),
  ACCESS_DENIED("E1002", "Access Denied"),
  FILE_UPLOAD_LIMIT_EXCEEDED("E1101", "File upload size exceeded"),
  FILE_EXTENSION_INVALID("E1102", "File extension invalid"),
  JAAS_SERVICE_ERROR("E1201", "Jaas service error"),
  MANDATORY_PROCESS_PROPERTY_NOT_SET("E1301", "Mandatory process property not set"),
  MANDATORY_JOB_PROPERTY_NOT_SET("E1302", "Mandatory job property not set"),
  JOB_EXPECTED_START_DATE_CANNOT_BE_AFTER_EXPECTED_END_DATE("E1303", "Job expected start date cannot be after expected end date"),
  JOB_EXPECTED_START_DATE_CANNOT_BE_A_PAST_DATE("E1304", "Job expected start date cannot be a past date"),
  JOB_EXPECTED_END_DATE_CANNOT_BE_A_PAST_DATE("E1305", "Job expected end date cannot be a past date"),


  //Facility
  FACILITY_NOT_FOUND("E1401", "Facility not Found"),

  // Usecase
  USE_CASE_NOT_FOUND("E1501", "Use Case not Found"),

  // Object Type
  OBJECT_TYPE_NOT_FOUND("E1601", "Object Type not Found"),
  OBJECT_TYPE_MANDATORY_PROPERTIES_NOT_SET("E1602", "Object Type mandatory properties not set"),
  OBJECT_TYPE_PROPERTY_INPUT_TYPE_CANNOT_BE_CHANGED("E1603", "property input type cannot be changed"),
  OBJECT_TYPE_RELATION_TYPE_CANNOT_BE_CHANGED("E1604", "relation type cannot be changed"),
  OBJECT_TYPE_PROPERTY_CANNOT_BE_ARCHIVED("E1605", "object type property cannot be archived"),
  EXTERNAL_ID_CANNOT_BE_CHANGED("E1606", "external Id cannot be changed"),
  RELATION_TARGET_CARDINALITY_CANNOT_BE_CHANGED("E1607", "relation target cardinality cannot be changed"),
  FIXED_PROPERTY_CANNOT_BE_CHANGED("E1608", "object type fixed property cannot be changed"),
  OBJECT_TYPE_MANDATORY_RELATIONS_NOT_SET("E1609", "Object Type mandatory relations not set"),
  ENTITY_OBJECT_NOT_FOUND("E1701", "Entity Object not Found"),
  ENTITY_OBJECT_PROPERTIES_VALIDATION("E1702", "Entity objects properties validation error"),
  ENTITY_OBJECT_PROPERTY_INVALID_INPUT("E1703", "Entity objects property invalid input"),
  COULD_NOT_CREATE_ENTITY_OBJECT_IN_ALL_FACILITY("E1704", "Entity objects cannot be created from Global Portal"),
  COULD_NOT_UPDATE_ENTITY_OBJECT_IN_ALL_FACILITY("E1705", "Entity objects cannot be updated from Global Portal"),
  RESOURCE_PARAMETER_RELATION_PROPERTY_VALIDATION_ERROR("E1801", "Resource parameter relation property validation error"),
  RESOURCE_PARAMETER_RELATION_PROPERTY_VALIDATION_COULD_NOT_RUN_MISSING_DATA("E1802", "Could not perform relation property validation due to missing data"),
  RESOURCE_PARAMETER_AUTOMATION_ACTION_ERROR("E1803", "Could not perform actions related to resource parameter"),
  OBJECT_ALREADY_ARCHIVED("E1804", "Object is  already archived"),
  OBJECT_ALREADY_UNARCHIVED("E1805", "Object is already unarchived"),
  OBJECT_TYPE_DISPLAY_NAME_ALREADY_EXISTS("E1806", "This name already exists, Please set a unique name for this object type"),
  OBJECT_TYPE_DISPLAY_NAME_INVALID("E1807", "object type display name invalid"),
  OBJECT_TYPE_PROPERTY_DISPLAY_NAME_INVALID("E1810", "property display name invalid"),
  OBJECT_TYPE_PROPERTY_DISPLAY_NAME_ALREADY_EXISTS("E1811", "This name already exists, Please set a unique name for this property"),
  OBJECT_TYPE_RELATION_DISPLAY_NAME_ALREADY_EXISTS("E1812", "This name already exists, Please set a unique name for this relation"),
  OBJECT_TYPE_RELATION_DISPLAY_NAME_INVALID("E1813", "relation display name invalid"),
  OBJECT_TYPE_ARCHIVED_PROPERTY_CANNOT_BE_CHANGED("E1815", "archive property cannot be changed"),
  OBJECT_TYPE_ARCHIVED_RELATION_CANNOT_BE_CHANGED("E1816", "archive property cannot be changed"),
  OBJECT_TYPE_PROPERTY_DROPDOWN_OPTION_DISPLAY_NAME_INVALID("E1817", "option display name invalid"),
  OBJECT_TYPE_PROPERTY_DROPDOWN_OPTION_DISPLAY_NAME_EXISTS("E1818", "option display name already exists"),
  CANNOT_PERFORM_AUTOMATION_ACTION_ON_ARCHIVED_PROPERTY("E1820", "cannot perform automation action on archive property"),

  // Automation
  AUTOMATION_NOT_FOUND("E1901", "Automation not Found"),
  // CUSTOM VIEW
  CUSTOM_VIEW_NOT_FOUND("E2001", "Custom View Not Found"),
  CUSTOM_VIEW_LABEL_INVALID("E2002", "The custom view name is invalid"),

  //SHORT CODE
  NO_SUCH_SHORT_DATA_FOUND("E3001", "No such short data mapped to given facility id"),
  EMPTY_SHORT_CODE_DATA("E3002", "Empty Short Code data"),
  SHORT_CODE_ALREADY_MAPPED_TO_OBJECT("E3003", "This short code is already mapped to an object"),
  OBJECT_IS_NOT_MAPPED_TO_THIS_FACILITY("E3005", "Object is not mapped to the facility"),
  INVALID_OBJECT_TYPE_SCANNED("E3006", "Object doesn't belong to required object type"),
  ERROR_CREATING_A_SCHEDULER("E2105", "Error creating a scheduler"),

  // General
  PARAMETER_VERIFICATION_INCOMPLETE("E9002", "parameter verification incomplete"),

  // Sheduler
  SCHEDULER_NOT_FOUND("E2101", "Scheduler not Found"),
  INVALID_SCHEDULER_START_DATE("E2102", "Scheduler Start Date cannot be a past date"),
  INVALID_SCHEDULER_DUE_AFTER_DATE("E2103", "Scheduler invalid Due After date"),
  SCHEDULER_WITH_SAME_NAME_EXIST("E2104", "Scheduler with same name already exist"),

  //Branching rules
  RULE_ID_CANNOT_BE_EMPTY("E2201", "Rule Id cannot be empty"),
  RULE_CONSTRAINT_CANNOT_BE_EMPTY("E2202", "Rule Constraint cannot be empty"),
  RULE_INPUT_CANNOT_BE_EMPTY("E2203", "Rule input cannot be empty"),
  RULE_HIDE_OR_SHOW_BOTH_CANNOT_BE_EMPTY("E2204", "Both rule hide or show cannot be empty"),
  INCORRECT_RULE_CONFIGURED("E2205", "Incorrect rule is configured"),

  // General
  DUPLICATE_RECORD_ERROR("E9001", "A resource with the same unique identifier already exists"),
  REASON_CANNOT_BE_EMPTY("E9003", "Reason cannot be empty");

  private final String code;
  private final String description;

  ErrorCode(String code, String description) {
    this.code = code;
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public String getCode() {
    return code;
  }

  @Override
  public String toString() {
    return code + ": " + description;
  }
}

