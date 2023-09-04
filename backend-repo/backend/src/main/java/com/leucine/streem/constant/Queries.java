package com.leucine.streem.constant;

public final class Queries {
  public static final String GET_PARAMTER_VALUE_BY_PARAMETER_ID_AND_JOB_ID = "select av from #{#entityName} av inner join fetch av.job j inner join fetch av.parameter act where act" +
    ".id = :parameterId and j.id = :jobId";
  public static final String CREATE_OR_UPDATE_CODE = "INSERT INTO codes (counter, type, clause, organisations_id) VALUES (1, ?, ?, ?) "
    + "ON CONFLICT (organisations_id, type, clause) DO UPDATE "
    + "SET counter = codes.counter + 1 returning *";
  //TODO remove hardcoded states in both the below queries so query can be used in other places
  public static final String GET_ALL_INCOMPLETE_PARAMETERS_BY_JOB_ID = """
    select distinct p.id as parameterId, t.id as taskId
    from jobs j
             inner join task_executions te on j.id = te.jobs_id
             inner join tasks t on te.tasks_id = t.id
             inner join parameters p on t.id = p.tasks_id
             inner join parameter_values pv on p.id = pv.parameters_id and pv.jobs_id = j.id
        
    where te.state not in ('COMPLETED',
                           'COMPLETED_WITH_EXCEPTION', 'SKIPPED',
                           'COMPLETED_WITH_CORRECTION')
      and p.is_mandatory = true
      and p.archived = false
      and pv.state <> 'EXECUTED'
      and pv.hidden <> true
      and j.id = :jobId
    """;
  public static final String GET_INCOMPLETE_PARAMETER_IDS_BY_JOB_ID_AND_TASK_ID = "select a.id from parameters a left outer join parameter_values av on a.id = av.parameters_id " +
    "and av.jobs_id = :jobId where a.id in (select act.id from tasks tsk inner join parameters act on act" +
    ".tasks_id = tsk.id where act.is_mandatory = true and act.archived = false and tsk.id = :taskId) and av.state <> 'EXECUTED' and av.hidden <> true";
  public static final String GET_EXECUTABLE_PARAMETER_IDS_BY_TASK_ID = "select act.id from Parameter act where act.task.id = :taskId and act.type not in('INSTRUCTION', 'MATERIAL')";
  public static final String UPDATE_PARAMETER_VALUE_STATE = "update parameter_values set state = :state where parameters_id in :parameterIds and jobs_id = :jobId";
  public static final String UPDATE_PARAMETER_VALUES = "update parameter_values set value = :value, choices = :choices, state=:state, reason = :reason , modified_by = :modifiedBy, modified_at = :modifiedAt where parameters_id = :parameterId and jobs_id = :jobId";
  public static final String UPDATE_TASK_EXECUTION_STATE = "update task_executions set state = :state where id = :id";
  public static final String UPDATE_TASK_EXECUTION_STATE_AND_CORRECTION_REASON = "update task_executions set state=:state, correction_reason=:correctionReason where id = :id";
  public static final String UPDATE_TASK_EXECUTION_ENABLE_CORRECTION = "update task_executions set correction_enabled='true', correction_reason=:correctionReason where id = :id";
  public static final String UPDATE_TASK_EXECUTION_COMPLETE_CORRECTION = "update task_executions set correction_enabled='false' where id = :id";
  public static final String UPDATE_TASK_EXECUTION_CANCEL_CORRECTION = "update task_executions set correction_enabled='false',correction_reason=null where id = :id";
  public static final String UPDATE_PARAMETER_CHOICES_REASON_AND_STATE = "update temp_parameter_values set choices = :choices, reason = :reason, state = :state, modified_at = :modifiedAt, modified_by = :modifiedBy where parameters_id=:parameterId and jobs_id = :jobId";
  public static final String UPDATE_TEMP_PARAMETER_CHOICES_AND_STATE_BY_PARAMETER_AND_JOB_ID = "update temp_parameter_values set choices = :choices, state = :state, modified_at = :modifiedAt, modified_by = :modifiedBy where parameters_id = :parameterId and " +
    "jobs_id = :jobId";
  public static final String UPDATE_TEMP_PARAMETER_VALUE_AND_STATE_BY_PARAMETER_AND_JOB_ID = "update temp_parameter_values set value=:value, state=:state, modified_at = :modifiedAt, modified_by = :modifiedBy where parameters_id=:parameterId and " +
    "jobs_id = :jobId";
  public static final String UPDATE_TEMP_PARAMETER_VALUE_AND_REASON_BY_PARAMETER_AND_JOB_ID = "update temp_parameter_values set value=:value, state = 'EXECUTED', reason = :reason, modified_at = :modifiedAt, modified_by = :modifiedBy where " +
    "parameters_id = :parameterId and jobs_id = :jobId";
  public static final String GET_ALL_TASK_ASSIGNEES_DETAILS_BY_JOB_ID = "WITH taskExecutionAssigneeDetail AS (SELECT teum.users_id AS users_id, COUNT(*) AS assigned_tasks, COUNT(CASE WHEN teum.state = 'SIGNED_OFF' THEN 1 ELSE NULL END) AS signed_off_tasks, " +
    "COUNT( CASE WHEN ( teum.state != 'SIGNED_OFF' AND tex.state IN ( 'COMPLETED', 'SKIPPED', 'COMPLETED_WITH_EXCEPTION', 'COMPLETED_WITH_CORRECTION')) THEN 1 ELSE NULL END) AS pending_sign_offs, " +
    "CASE WHEN (count(teum.task_executions_id)) = :totalExecutionIds THEN TRUE ELSE FALSE END AS completely_assigned FROM task_execution_user_mapping teum " +
    "inner join task_executions tex on tex.id = teum.task_executions_id WHERE teum.task_executions_id IN (SELECT te.id FROM task_executions te WHERE jobs_id = :jobId) GROUP BY teum.users_id) " +
    "SELECT tad.users_id as id, tad.assigned_tasks as assignedTasks, tad.pending_sign_offs as pendingSignOffs, tad.completely_assigned as completelyAssigned, tad.signed_off_tasks as signedOffTasks, u.first_name AS firstName, " +
    "u.last_name AS lastName, u.employee_id AS employeeId FROM taskExecutionAssigneeDetail tad inner join users u on tad.users_id = u.id";
  public static final String GET_ALL_JOB_ASSIGNEES = "select u.id as id, tex.jobs_id as jobId, u.first_name as firstName, u.last_name lastName, u.employee_id as employeeId from " +
    "task_execution_user_mapping teum inner join users u on teum.users_id =  u.id inner join task_executions tex on tex.id = teum.task_executions_id where tex.jobs_id in :jobIds group by u.id, tex.jobs_id";
  public static final String GET_ALL_JOB_ASSIGNEES_COUNT = "select count(distinct u.id) from task_execution_user_mapping teum inner join users u on teum.users_id =  u.id inner join task_executions tex on tex.id = teum.task_executions_id where tex.jobs_id = :jobId";
  public static final String IS_ALL_TASK_UNASSIGNED = "select CASE WHEN count(te)>0 THEN true ELSE false END from task_execution_user_mapping te where task_executions_id in " +
    "(select tex.id from task_executions tex inner join jobs j on j.id = tex.jobs_id and j.id = :jobId)";
  public static final String IS_USER_ASSIGNED_TO_ANY_TASK = "select CASE WHEN count(te)>0 THEN true ELSE false END from task_execution_user_mapping te where task_executions_id " +
    "in (select tex.id from task_executions tex inner join jobs j on j.id = tex.jobs_id and j.id = :jobId) " +
    "and users_id = :userId";
  public static final String IS_COLLABORATOR_MAPPING_EXISTS_BY_CHECKLIST_AND_USER_ID_AND_COLLBORATOR_TYPE = "select CASE WHEN count(c)>0 THEN true ELSE false END from ChecklistCollaboratorMapping c where " +
    "c.checklist.id = :checklistId and c.user.id = :userId and type in :types";
  public static final String UPDATE_STAGE_ORDER_BY_STAGE_ID = "update stages set order_tree = :order, modified_by = :userId, modified_at = :modifiedAt where id = :stageId";
  public static final String UNASSIGN_REVIEWERS_FROM_CHECKLIST = "delete from checklist_collaborator_mapping where checklists_id = :checklistId and phase = :phase and " +
    "users_id in :userIds";
  public static final String GET_ALL_COLLABORATORS_BY_CHECKLIST_ID_AND_PHAST_TYPE = "select u.id, u.first_name as firstName, u.last_name as lastName, u.employee_id as employeeId, crm.state, crm.order_tree as orderTree, crm.type, crm.modified_at as modifiedAt from " +
    "checklist_collaborator_mapping crm join users u on crm.users_id = u.id where crm.checklists_id = :checklistId and crm.phase_type = :phaseType and crm" +
    ".phase = (select max(phase) from checklists where id = :checklistId)";
  public static final String GET_ALL_COLLABORATORS_BY_CHECKLIST_ID_AND_TYPE = "select u.id, u.first_name as firstName, u.last_name as lastName, u.email as email, u.employee_id as employeeId, crm.state, crm.order_tree as orderTree, crm.type , crm.modified_at as modifiedAt " +
    "from checklist_collaborator_mapping crm join users u on crm.users_id = u.id where crm.checklists_id = :checklistId and type = :type and " +
    "crm.phase = (select max(phase) from checklist_collaborator_mapping where checklists_id = :checklistId and type = :type)";
  public static final String GET_ALL_COLLABORATORS_BY_CHECKLIST_ID_AND_TYPE_ORDER_BY_AND_MODIFIED_AT_ORDER_TREE = "select u.id, u.first_name as firstName, u.last_name as lastName, u.email as email, u.employee_id as employeeId, crm.state, crm.order_tree as orderTree, crm.type , crm.modified_at as modifiedAt " +
    "from checklist_collaborator_mapping crm join users u on crm.users_id = u.id where crm.checklists_id = :checklistId and type =:type and " +
    "crm.phase = (select max(phase) from checklist_collaborator_mapping where checklists_id = :checklistId and type = :type) order by crm.order_tree, crm.modified_at desc";
  public static final String GET_ALL_COLLABORATORS_BY_CHECKLIST_ID_AND_TYPE_IN = "select u.id, u.first_name as firstName, u.last_name as lastName, u.email as email, u.employee_id as employeeId, crm.state, crm.order_tree as orderTree, crm.type from " +
    "checklist_collaborator_mapping crm join users u on crm.users_id = u.id where crm.checklists_id = :checklistId and type in :types and " +
    "crm.phase = (select max(phase) from checklist_collaborator_mapping where checklists_id = :checklistId and type in :types)";
  public static final String DELETE_AUTHOR_FROM_CHECKLIST = "delete from checklist_collaborator_mapping where checklists_id = :checklistId and " +
    " users_id in :userIds and type in ('AUTHOR','PRIMARY_AUTHOR')";
  public static final String GET_CHECKLIST_BY_TASK_ID = "select c from Checklist c inner join c.stages s inner join s.tasks t where t.id = :taskId";
  public static final String UNASSIGN_USERS_FROM_NON_STARTED_TASKS = "delete from task_execution_user_mapping teum where teum.task_executions_id in " +
    "(select te.id from task_executions te where te.jobs_id in (select id from jobs j where j.state in ('IN_PROGRESS', 'ASSIGNED')) and te.state = 'NOT_STARTED') and teum.users_id = :userId";
  public static final String SET_JOB_TO_UNASSIGNED_IF_NO_USER_IS_ASSIGNED = "update jobs job set state = 'UNASSIGNED' where job.id in (select te.jobs_id from task_execution_user_mapping teum right outer join task_executions te on teum.task_executions_id = te.id where te.id in (select tex.id from task_executions " +
    "tex inner join jobs j on j.id = tex.jobs_id and j.state = 'ASSIGNED') group by te.jobs_id having count(teum.task_executions_id) = 0)";
  public static final String IS_USER_ASSIGNED_TO_IN_PROGRESS_TASKS = "select case when count(teum.users_id) > 0 THEN true else false end from task_execution_user_mapping teum where teum.task_executions_id in " +
    "(select te.id from task_executions te where te.jobs_id in (select id from jobs j where j.state = 'IN_PROGRESS') and te.state = 'IN_PROGRESS') and teum.users_id = :userId";
  public static final String GET_NON_SIGNED_OFF_TASKS_BY_JOB_ID = "select te.tasks_id from task_executions te inner join task_execution_user_mapping teum on teum.task_executions_id = te.id and jobs_id =:jobId " +
    "group by te.tasks_id, teum.task_executions_id having count(*) != (select count(*) from task_execution_user_mapping mapping where mapping.task_executions_id = teum.task_executions_id " +
    "and mapping.state = 'SIGNED_OFF')";
  public static final String GET_NON_COMPLETED_TASKS_BY_JOB_ID = "select te.tasks_id from task_executions te where te.jobs_id = :jobId and te.state not in ('NOT_STARTED', 'COMPLETED', 'SKIPPED', 'COMPLETED_WITH_EXCEPTION')";
  public static final String GET_ENABLED_FOR_CORRECTION_TASKS_BY_JOB_ID = "select te.tasks_id from task_executions te where te.jobs_id = :jobId and te.correction_enabled = true";
  public static final String UPDATE_TASK_ASSIGNEE_STATE = "update task_execution_user_mapping set state = :state, modified_by = :modifiedBy, modified_at = :modifiedAt where users_id = :userId and task_executions_id in :taskExecutionIds";
  public static final String UNASSIGN_USERS_FROM_TASK_EXECUTIONS = "delete from task_execution_user_mapping where task_executions_id in :taskExecutionIds and users_id in :userIds and state <> 'SIGNED_OFF' and action_performed = false";
  //TODO resuse states as set of constants applies everywhere
  public static final String GET_NON_SIGNED_OFF_TASKS_BY_JOB_AND_USER_ID = "select te.tasks_id from task_executions te inner join task_execution_user_mapping teum on teum.task_executions_id = te.id " +
    "and teum.users_id = :userId and jobs_id = :jobId where teum.state != 'SIGNED_OFF' and te.state in ('COMPLETED', 'SKIPPED', 'COMPLETED_WITH_EXCEPTION', 'COMPLETED_WITH_CORRECTION')";
  public static final String GET_RECENT_VERSION_BY_ANCESTOR = "SELECT MAX(version) from Version v where v.ancestor = :ancestor";
  public static final String GET_PROTOTYPE_CHECKLIST_ID_BY_ANCESTOR = "SELECT self from Version v where v.ancestor = :ancestor and v.parent IS NOT NULL and v.version IS NULL ORDER BY v.createdAt DESC";
  public static final String GET_CHECKLIST_CODE = "SELECT code from Checklist c where c.id = :checklistId";

  public static final String UPDATE_CHECKLIST_STATE = "UPDATE Checklist c SET c.state = :state where c.id = :checklistId";
  public static final String UPDATE_DEPRECATE_VERSION_BY_PARENT = "UPDATE Version v SET v.deprecatedAt = :deprecatedAt where v.self = :parent and v.deprecatedAt IS NULL";
  public static final String DELETE_TASK_MEDIA_MAPPING = "delete from task_media_mapping where tasks_id = :taskId and medias_id =:mediaId";
  public static final String GET_TASK_EXECUTION_STATUS_COUNT_BY_JOB_IDS = "select t.jobs_id as jobId, count(CASE WHEN t.state in ('COMPLETED', 'COMPLETED_WITH_EXCEPTION', 'SKIPPED', 'COMPLETED_WITH_CORRECTION') then 1 else NULL end) completedTasks, " +
    "count(*) totalTasks from task_executions t where t.jobs_id in :jobIds group by t.jobs_id";
  public static final String IS_ACTIVE_JOB_EXIST_FOR_GIVEN_CHECKLIST = "select CASE WHEN count(j.id)>0 THEN true ELSE false END from Job j where j.checklist.id = :checklistId and state not in :jobStates";
  public static final String READ_TEMP_PARAMETER_VALUE_BY_JOB_AND_STAGE_ID = "select av from TempParameterValue av where av.job.id = :jobId and av.parameter.id in " +
    "(select a.id from Parameter a where a.task.id in (select t.id from Task t where t.stage.id = :stageId))";
  public static final String READ_PARAMETER_VALUE_BY_JOB_ID_AND_STAGE_ID = "select av from ParameterValue av where av.job.id = :jobId and av.parameter.id in " +
    "(select a.id from Parameter a where a.task.id in (select t.id from Task t where t.stage.id = :stageId))";
  public static final String READ_TASK_EXECUTION_BY_JOB_AND_STAGE_ID = "select te from TaskExecution te where te.job.id = :jobId and te.task.id in (select t.id from Task t where t.stage.id = :stageId)";
  public static final String INCREMENT_TASK_COMPLETE_COUNT_BY_JOB_ID_AND_STAGE_ID = "update r_stage_execution set completed_tasks = completed_tasks + 1 where jobs_id = :jobId and stages_id = :stageId";
  public static final String GET_STAGE_EXECUTION_REPORT_BY_JOB_ID = "select s from StageExecutionReport s where jobId = :jobId";
  public static final String GET_STAGE_BY_TASK_ID = "select s from Stage s inner join s.tasks t where t.id = :taskId";
  public static final String GET_STAGE_ID_BY_TASK_ID = "select s.id from Stage s inner join s.tasks t where t.id = :taskId";

  public static final String GET_TOTAL_TASKS_VIEW_BY_CHECKLIST_ID = "select s.id as stageId, s.name as stageName, count(t.id) as totalTasks from stages s inner join tasks t on t.stages_id = s.id where s.checklists_id = :checklistId group by s.id";
  public static final String UPDATE_STATE_TO_IN_PROGRESS_IN_STAGE_REPORT_BY_JOB_ID_AND_STAGE_ID = "update r_stage_execution set tasks_in_progress = true where jobs_id = :jobId and stages_id = :stageId";
  public static final String DELETE_STAGE_EXECUTION_BY_JOB_ID = "DELETE FROM r_stage_execution where jobs_id = :jobId";
  public static final String UPDATE_TASK_ORDER = "update tasks set order_tree = :order, modified_by = :userId, modified_at = :modifiedAt where id = :taskId";
  public static final String DELETE_COLLABORATOR_COMMENTS_BY_CHECKLIST_COLLABORATOR_MAPPING = "delete from checklist_collaborator_comments where checklist_collaborator_mappings_id = :checklistCollaboratorMappingId";
  public static final String GET_TASK_BY_PARMETER_ID = "select t from Task t inner join t.parameters a where a.id = :parameterId";
  public static final String GET_TASK_EXECUTION_USER_MAPPING_BY_TASK_EXECUTION_AND_USER_ID_IN = "select state as assigneeState, action_performed as isActionPerformed, users_id as userId, task_executions_id as taskExecutionId " +
    "from task_execution_user_mapping teum where teum.task_executions_id = :taskExecutionId and teum.users_id in :userIds";
  public static final String GET_TASK_EXECUTION_USER_MAPPING_BY_TASK_EXECUTION_IN_AND_USER_ID_IN = "select state as assigneeState, action_performed as isActionPerformed, users_id as userId, task_executions_id as taskExecutionId " +
    "from task_execution_user_mapping teum where teum.task_executions_id in :taskExecutionIds and teum.users_id in :userIds";
  public static final String GET_TASK_EXECUTION_USER_MAPPING_BY_TASK_EXECUTION_IN = "WITH taskExecutionAssigneeDetail AS (SELECT m.users_id AS users_id, count(m.task_executions_id) as assigned_tasks, case when (count(m.task_executions_id) <> :totalExecutionIds) then false else true end as completely_assigned " +
    "FROM task_execution_user_mapping m where m.task_executions_id in :taskExecutionIds group by m.users_id) " +
    "SELECT tad.users_id as id, tad.assigned_tasks as assignedTasks, tad.completely_assigned as completelyAssigned, u.first_name AS firstName, u.last_name AS lastName, u.employee_id AS employeeId " +
    "FROM taskExecutionAssigneeDetail tad inner join users u on tad.users_id = u.id";
  public static final String GET_TASK_USER_MAPPING_BY_TASK_IN = " WITH taskAssigneeDetail AS (SELECT cdu.users_id AS users_id, count(cdu.tasks_id) as assigned_tasks, case when (count(cdu.tasks_id) <> :totalTaskIds) then false else true end as completely_assigned " +
    "FROM checklist_default_users cdu where cdu.tasks_id in :taskIds and checklists_id = :checklistId and facilities_id= :facilityId group by cdu.users_id)" +
    "SELECT tad.users_id as id, tad.assigned_tasks as assignedTasks, tad.completely_assigned as completelyAssigned, u.first_name AS firstName, u.last_name AS lastName, u.employee_id AS employeeId FROM taskAssigneeDetail tad inner join users u on tad.users_id = u.id";
  public static final String GET_TASK_EXECUTION_COUNT_BY_JOB_ID = "select count(id) from task_executions where jobs_id=:jobId";
  public static final String GET_PARAMETER_VALUES_BY_JOB_ID_AND_TASK_ID_AND_PARAMETER_TYPE_IN = "select av from ParameterValue av inner join av.job j inner join av.parameter a inner join a.task t where a.type in :parameterTypes and j.id = :jobId and t.id in :taskIds";
  public static final String GET_STAGES_BY_JOB_ID_WHERE_ALL_TASK_EXECUTION_STATE_IN = "select distinct st from TaskExecution tex inner join tex.job js inner join tex.task ts inner join " +
    "ts.stage st where js.id = :jobId group by st having count(case when tex.state in :taskExecutionStates then 1 else null end) = count(tex.task.id) order by st.orderTree";
  public static final String GET_TASK_EXECUTIONS_BY_JOB_ID_AND_STAGE_ID_IN = "select te from TaskExecution te inner join te.task t inner join t.stage s inner join te.job j where j.id=:jobId and s.id in :stageIds order by s.orderTree, t.orderTree";
  public static final String GET_ALL_MEDIAS_WHERE_ID_IN = "select m from Media m where id in :mediaIds";
  public static final String GET_STAGES_BY_CHECKLIST_ID_AND_ORDER_BY_ORDER_TREE = "select s from Stage s where s.checklistId=:checklistId and s.archived=false order by orderTree";
  public static final String GET_TASKS_BY_STAGE_ID_IN_AND_ORDER_BY_ORDER_TREE = "select t from Task t inner join t.stage s where t.stageId in :stageIds and s.archived=false and t.archived=false order by s.orderTree, t.orderTree";
  public static final String GET_PARAMETERS_BY_TASK_ID_IN_AND_ORDER_BY_ORDER_TREE = "select a from Parameter a inner join a.task t inner join t.stage s where a.taskId in :taskIds and s.archived=false and t.archived=false and a.archived=false order by s.orderTree, t.orderTree, a.orderTree";
  public static final String GET_CHECKLIST_DEFAULT_USER_IDS_BY_CHECKLIST_ID_TASK_ID = "select c.user.id from ChecklistDefaultUsers c where c.checklist.id = :checklistId and c.task.id = :taskId";
  public static final String UNASSIGN_DEFAULT_USERS_BY_CHECKLISTID_AND_TASKID = "delete from checklist_default_users where users_id in :userIds and checklists_id = :checklistId and tasks_id in :taskIds";
  public static final String GET_CHECKLIST_DEFAULT_USER_IDS_BY_CHECKLIST_ID = "select c.user.id from ChecklistDefaultUsers c where c.checklist.id = :checklistId and c.facilityId= :facilityId";
  public static final String GET_DEFAULT_USERS_TASK_BY_CHECKLIST_ID = "select c.task.id from ChecklistDefaultUsers c where c.checklist.id = :checklistId";
  public static final String GET_TASK_IDS_BY_CHECKLIST_ID_AND_USER_ID = "select c.task.id from ChecklistDefaultUsers c where c.checklist.id = :checklistId and c.user.id = :userId and c.facility.id =:facilityId";
  public static final String GET_ENABLED_PARAMETERS_COUNT_BY_PARAMETER_TYPE_IN_AND_ID_IN = "select count(a.id) from Parameter a where a.id in :parameterIds and a.type in :types and a.archived=false";
  public static final String GET_PARAMETER_VALUES_BY_JOB_ID_AND_PARAMETER_ID_IN = "select av from ParameterValue av where av.jobId = :jobId and av.parameterId in :parameterIds";
  public static final String GET_TEMP_PARAMETER_VALUES_BY_JOB_ID_AND_PARAMETER_ID_IN = "select av from TempParameterValue av where av.jobId = :jobId and av.parameterId in :parameterIds";
  public static final String GET_PARAMETER_VALUES_BY_JOB_ID_AND_PARAMETER_TARGET_ENTITY_TYPE_IN = "select av from ParameterValue av inner join av.job j inner join av.parameter a where a.targetEntityType in :targetEntityTypes and j.id = :jobId";

  public static final String GET_PARAMETER_VALUE_BY_JOB_ID_AND_PARAMETER_ID = "select av from ParameterValue av where av.jobId = :jobId and av.parameterId = :parameterId";
  public static final String GET_ALL_NON_COMPLETED_TASKS_OF_JOB = "select tasks_id from task_executions where state not in ('COMPLETED', 'COMPLETED_WITH_EXCEPTION', 'SKIPPED') and jobs_id = :jobId and tasks_id in (" +
    "select t.id from tasks t inner join parameters a on t.id=a.tasks_id and a.id in (select parameters_id from parameter_values av where av.jobs_id = :jobId and av.hidden <> true) and a.is_mandatory = true and a.archived = false" +
    ")";
  public static final String DELETE_TASK_AUTOMATION_MAPPING = "delete from task_automation_mapping where tasks_id = :taskId and automations_id =:automationsId";
  public static final String GET_ALL_AUTOMATIONS_IN_TASK_AUTOMATION_MAPPING_BY_TASK_ID = """
    select a
    from Automation a
    where a.id in
          (select tam.automationId from TaskAutomationMapping tam where tam.taskId = :taskId)
    """;
  public static final String DELETE_CHECKLIST_FACILITY_MAPPING = "delete from checklist_facility_mapping where checklists_id= :checklistId and facilities_id in :facilityIds";
  public static final String GET_PARAMETERS_COUNT_BY_CHECKLIST_ID_AND_PARAMETER_ID_IN_AND_TARGET_ENTITY_TYPE = "select count(id) from Parameter a where a.archived = false and a.checklistId=:checklistId and a.id in :parameterIds and a.targetEntityType=:targetEntityType";
  public static final String UPDATE_PARAMETERS_TARGET_ENTITY_TYPE = "UPDATE Parameter a SET a.targetEntityType = :targetEntityType where a.id in :parameterIds";

  public static final String GET_PARAMETERS_BY_CHECKLIST_ID_AND_TARGET_ENTITY_TYPE = "select a from Parameter a where a.archived = false and a.checklistId=:checklistId and a.targetEntityType=:targetEntityType";
  public static final String GET_ARCHIVED_PARAMETERS_BY_REFERENCED_PARAMETER_ID = "select * from parameters where archived = true and id in ( :referencedParameterIds )";
  public static final String UPDATE_PARAMETER_TARGET_ENTITY_TYPE_BY_CHECKLIST_ID_AND_TARGET_ENTITY_TYPE = "update Parameter a set a.targetEntityType=:updatedTargetEntityType where a.checklistId=:checklistId and a.targetEntityType=:targetEntityType";
  public static final String UPDATE_PARAMETER_ORDER = "update parameters set order_tree = :order, modified_by = :userId, modified_at = :modifiedAt where id = :parameterId";

  public static final String UPDATE_PARAMETER_AUTO_INITIALIZE_BY_PARAMETER_ID = "update parameters set auto_initialize = :autoInitialize where id = :parameterId";
  public static final String UPDATE_PARAMETER_VALIDATION_BY_PARAMETER_ID = "update parameters set validations = :validations where id = :parameterId";

  public static final String UPDATE_PARAMETER_VALUE_VISIBILITY = """
    UPDATE parameter_values pv
    SET hidden = :visibility
    WHERE pv.parameters_id IN(:parameterIds) AND jobs_id = :jobId
    """;

  public static final String UPDATE_PARAMETER_RULES_BY_PARAMETER_ID = "update parameters set rules = :rules where id = :parameterId";
  public static final String UPDATE_PARAMETER_DATA_BY_PARAMETER_ID = "update parameters set data = :data where id = :parameterId";
  public static final String GET_CHECKLIST_STATE_BY_STAGE_ID = "SELECT c.state FROM Checklist c inner join c.stages s where s.id=:stageId";
  public static final String FIND_JOB_PROCESS_INFO = """
    select j.id as jobId, j.code as jobCode, c.name as processName, c.id as processId, c.code as processCode
    from jobs j
             inner join checklists c on j.checklists_id = c.id
    where j.id = :jobId
    """;
  public static final String FIND_TASK_EXECUTION_TIMER_AT_PAUSE = "SELECT tm FROM TaskExecutionTimer tm WHERE tm.taskExecutionId = :taskExecutionId and tm.resumedAt IS NULL";

  public static final String UPDATE_VISIBILITY_OF_PARAMETERS = """
    UPDATE Parameter p
    SET p.hidden =
        CASE
            WHEN p.id IN :hiddenParameterIds THEN true
            WHEN p.id IN :visibleParameterIds THEN false
        END
    WHERE p.id IN (:hiddenParameterIds, :visibleParameterIds)
    """;

  public static final String IS_LINKED_PARAMETER_EXISTS_BY_PARAMETER_ID = """
    select exists(select id
                  from parameters
                  where checklists_id = :checklistId
                    and auto_initialize ->> 'parameterId' = :parameterId
                    and archived = false);
                    """;

  public static final String FIND_BY_JOB_ID_AND_PARAMETER_VALUES_ID_AND_VERIFICATION_TYPE = "SELECT * from parameter_verifications where jobs_id = :jobId and parameter_values_id = :parameterValueId and verification_type = :verificationType order by modified_at desc limit 1";
  public static final String FIND_BY_JOB_ID_AND_PARAMETER_ID_AND_PARAMETER_VERIFICATION_TYPE = "SELECT * from parameter_verifications pvf join parameter_values pv on pvf.parameter_values_id = pv.id join parameters p on pv.parameters_id = p.id where pvf.jobs_id = :jobId and p.id = :parameterId and pvf.verification_type = :verificationType order by pvf.modified_at desc limit 1";

  public static final String GET_ALL_CHECKLIST_IDS_BY_TARGET_ENTITY_TYPE_AND_OBJECT_TYPE_IN_DATA = """
    select p.checklists_id
    from parameters p where p.target_entity_type = :targetEntityType and p.data->>'objectTypeId'= :objectTypeId
    """;

  public static final String GET_ALL_JOB_IDS_BY_TARGET_ENTITY_TYPE_AND_OBJECT_TYPE_IN_DATA = """
      SELECT pv.jobs_id FROM parameter_values pv JOIN parameters p ON p.id = pv.parameters_id WHERE p.target_entity_type = :targetEntityType AND choices @> :objectId
    """;

  public static final String GET_VERIFICATION_INCOMPLETE_PARAMETER_IDS_BY_JOB_ID_AND_TASK_ID =  "select a.id from parameters a left outer join parameter_values av on a.id = av.parameters_id " +
    "and av.jobs_id = :jobId where a.id in (select act.id from tasks tsk inner join parameters act on act" +
    ".tasks_id = tsk.id where act.archived = false and tsk.id = :taskId) and av.state <> 'EXECUTED' and a.verification_type != 'NONE' and av.hidden <> true and av.value is not null";


  public static final String IS_JOB_EXISTS_BY_SCHEDULER_ID_AND_DATE_GREATER_THAN_EXPECTED_START_DATE = "select CASE WHEN count(j.id)>0 THEN true ELSE false END from Job j where j.schedulerId = :schedulerId and j.expectedStartDate >= :date";
  public static final String GET_CHECKLIST_BY_STATE = "SELECT c.id FROM Checklist c WHERE c.state in :state";
  public static final String GET_CHECKLIST_BY_STATE_NOT = "SELECT c.id FROM Checklist c WHERE c.state != :state";
  public static final String GET_ALL_SHOULD_BE_PARAMETER_STATUS =
    ("""
      select ps.id as parameterId,
      pvs.id as parameterValueId,
      pvs.jobs_id as jobId,
      ps.label as parameterName,
      ts.name as taskName,
      cs.name as processName,
      pvs.modified_at as modifiedAt,
      js.code as jobCode,
      ss.id as stageId,
      ts.id as taskId,
      pvs.created_at as createdAt
      from parameter_values pvs
      join parameters ps on pvs.parameters_id = ps.id
      join jobs js on js.id = pvs.jobs_id
      join checklists cs on cs.id = js.checklists_id
      join stages ss on ss.checklists_id = cs.id
      join tasks ts on ts.stages_id = ss.id
      where js.facilities_id = :facilityId
      and pvs.state = 'PENDING_FOR_APPROVAL'
      and ps.tasks_id = ts.id
      and (ps.label LIKE :parameterName or cs.name LIKE :processName)
      """);

  private Queries() {
  }

}
