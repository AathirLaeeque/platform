-- liquibase formatted sql

-- changeset siba:202302081537-seed-schema-1
-- comment drop existing not null constraint of columns modified_at, modified_by of parameter_values

ALTER TABLE parameter_values
  ALTER COLUMN modified_at DROP NOT NULL;

ALTER TABLE parameter_values
  ALTER COLUMN modified_by DROP NOT NULL;

-- changeset siba:202302081537-seed-schema-2
-- comment drop existing not null constraint of columns modified_at, modified_by of temp_parameter_values
ALTER TABLE temp_parameter_values
  ALTER COLUMN modified_at DROP NOT NULL;

ALTER TABLE temp_parameter_values
  ALTER COLUMN modified_by DROP NOT NULL;


-- changeset siba:202302081537-seed-schema-3
-- comment add column corrected_by in task_executions table and migrate corrected_by user from audit_logs
ALTER TABLE task_executions
  ADD COLUMN IF NOT EXISTS corrected_by BIGINT;

-- changeset siba:202302081537-seed-schema-4
-- comment add column corrected_at in task_executions table and migrate corrected_at user from audit_logs
ALTER TABLE task_executions
  ADD COLUMN IF NOT EXISTS corrected_at BIGINT;

-- changeset siba:202302081537-seed-data-1
-- migrations for old jobs with error correction enabled
WITH ucb AS (SELECT jobs_id, details, tasks_id, u.id as user_id, ja.triggered_at as corrected_at
             FROM job_audits ja
                    INNER JOIN  users u
                                ON ja.details LIKE '%completed error correction for Task%'
                                  AND u.employee_id = substring(ja.details, POSITION('(ID:' IN ja.details) + 4,
                                                                position(')' IN ja.details) -
                                                                POSITION('(ID:' IN ja.details) - 4))
UPDATE task_executions te
SET corrected_by = ucb.user_id,
    corrected_at= ucb.corrected_at
FROM ucb
WHERE te.jobs_id = ucb.jobs_id
  AND te.tasks_id = ucb.tasks_id;


-- changeset siba:202302081537-seed-data-2
-- setting default values for modified_at and modified_by as null
update parameter_values
set modified_at = null,
    modified_by = null
where state = 'NOT_STARTED';
