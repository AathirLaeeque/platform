-- liquibase formatted sql

-- changeset sathyam:202302081630-seed-schema-data-1
ALTER TABLE task_executions
  ADD COLUMN IF NOT EXISTS ended_by BIGINT;

--changeset sathyam:202302081630-seed-schema-data-2
WITH result AS (SELECT jobs_id, details, tasks_id, u.id as user_id, ja.triggered_at as corrected_at
                FROM job_audits ja
                       INNER JOIN users u
                                  ON ja.details LIKE '%completed the Task%'
                                    AND u.employee_id = substring(ja.details, POSITION('(ID:' IN ja.details) + 4,
                                                                  position(')' IN ja.details) -
                                                                  POSITION('(ID:' IN ja.details) - 4))
UPDATE task_executions te
SET ended_by = result.user_id
  FROM result
WHERE te.jobs_id = result.jobs_id
  AND te.tasks_id = result.tasks_id;

WITH result AS (SELECT jobs_id, details, tasks_id, u.id as user_id
             FROM job_audits ja
                    INNER JOIN  users u
                               ON ja.details LIKE '%skipped the Task%'
                                 AND u.employee_id = substring(ja.details, POSITION('(ID:' IN ja.details) + 4,
                                                               position(')' IN ja.details) -
                                                               POSITION('(ID:' IN ja.details) - 4))
UPDATE task_executions te
SET ended_by = result.user_id
  FROM result
WHERE te.jobs_id = result.jobs_id
  AND te.tasks_id = result.tasks_id;

WITH result AS (SELECT jobs_id, details, tasks_id, u.id as user_id
             FROM job_audits ja
                    INNER JOIN  users u
                               ON ja.details LIKE '%completed the Task % with exception stating reason%'
                                 AND u.employee_id = substring(ja.details, POSITION('(ID:' IN ja.details) + 4,
                                                               position(')' IN ja.details) -
                                                               POSITION('(ID:' IN ja.details) - 4))
UPDATE task_executions te
SET ended_by = result.user_id
  FROM result
WHERE te.jobs_id = result.jobs_id
  AND te.tasks_id = result.tasks_id;
