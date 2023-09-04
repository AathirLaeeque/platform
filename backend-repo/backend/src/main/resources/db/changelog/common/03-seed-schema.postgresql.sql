-- liquibase formatted sql

-- changeset sathyam:03-seed-schema-1
CREATE TABLE r_stage_execution
(
  id                BIGINT  NOT NULL,
  stages_id         BIGINT  NOT NULL,
  jobs_id           BIGINT  NOT NULL,
  stage_name        VARCHAR(255),
  total_tasks       INTEGER NOT NULL,
  completed_tasks   INTEGER NOT NULL DEFAULT 0,
  tasks_in_progress boolean NOT NULL DEFAULT FALSE
);

-- changeset sathyam:03-seed-schema-2
ALTER TABLE activities
  ALTER COLUMN label TYPE text;

-- changeset sathyam:03-seed-schema-3
ALTER TABLE facilities
  DROP COLUMN addresses_id;

-- changeset sathyam:03-seed-schema-4
ALTER TABLE checklist_collaborator_mapping
  RENAME COLUMN review_cycle TO phase;

-- changeset sathyam:03-seed-schema-5
ALTER TABLE checklist_collaborator_mapping
  ADD COLUMN phase_type VARCHAR(50);

-- changeset sathyam:03-seed-schema-6
CREATE SEQUENCE temp_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  NO MAXVALUE
  CACHE 1;

-- changeset sathyam:03-seed-schema-7
INSERT INTO checklist_collaborator_mapping(id, order_tree, phase, state, type, phase_type, created_at, modified_at, created_by, modified_by, checklists_id, users_id)
SELECT cast(round(date_part('epoch', now())) + nextval('temp_seq') AS BIGINT),
       1,
       1,
       'NOT_STARTED',
       CASE WHEN is_primary = TRUE THEN 'PRIMARY_AUTHOR' ELSE 'AUTHOR' END,
       'BUILD',
       created_at,
       modified_at,
       created_by,
       modified_by,
       checklists_id,
       users_id
FROM checklist_author_mapping cam;

-- changeset sathyam:03-seed-schema-8
UPDATE checklist_collaborator_mapping
SET phase_type='SIGN_OFF'
WHERE type = 'SIGN_OFF_USER';

-- changeset sathyam:03-seed-schema-9
UPDATE checklist_collaborator_mapping
SET phase_type='REVIEW'
WHERE type = 'REVIEWER';

-- changeset sathyam:03-seed-schema-10
UPDATE checklist_collaborator_mapping
SET type       = 'SIGN_OFF_USER',
    phase_type = 'SIGN_OFF'
WHERE type = 'APPROVER';

-- changeset sathyam:03-seed-schema-11
ALTER TABLE checklist_collaborator_mapping
  ALTER COLUMN phase_type SET NOT NULL;

-- changeset sathyam:03-seed-schema-12
INSERT INTO r_stage_execution(id, stages_id, jobs_id, stage_name, total_tasks, completed_tasks, tasks_in_progress)
SELECT CAST(round(date_part('epoch', now())) + nextval('temp_seq') AS BIGINT),
       s.id,
       j.id,
       s.name,
       (SELECT COUNT(*) FROM tasks t WHERE t.stages_id = s.id),
       (SELECT COUNT(*)
        FROM task_executions te
               INNER JOIN tasks t ON t.id = te.tasks_id
        WHERE t.stages_id = s.id
          AND te.jobs_id = j.id
          AND te.state = 'COMPLETED'),
       (CASE
          WHEN (SELECT COUNT(*)
                FROM task_executions te
                       INNER JOIN tasks t ON t.id = te.tasks_id
                WHERE t.stages_id = s.id
                  AND te.jobs_id = j.id
                  AND te.state IN ('IN_PROGRESS', 'SKIPPED', 'COMPLETED', 'COMPLETED_WITH_CORRECTION', 'COMPLETED_WITH_EXCEPTION', 'ENABLED_FOR_CORRECTION')) > 0 THEN TRUE
          ELSE FALSE END)
FROM stages s
       INNER JOIN checklists c ON s.checklists_id = c.id
       INNER JOIN jobs j ON j.checklists_id = c.id;

-- changeset sathyam:03-seed-schema-13
DROP TABLE checklist_author_mapping;

-- changeset sathyam:03-seed-schema-14
DROP SEQUENCE temp_seq;

-- changeset sathyam:03-seed-schema-15
ALTER TABLE jobs
  ADD COLUMN started_at BIGINT;
UPDATE jobs
SET started_at = ja.triggered_at
FROM job_audits ja
WHERE jobs.id = ja.jobs_id
  AND ja.details LIKE '%started the Job%';

-- changeset sathyam:03-seed-schema-16
ALTER TABLE jobs
  ADD COLUMN ended_at BIGINT;
UPDATE jobs
SET ended_at = ja.triggered_at
FROM job_audits ja
WHERE jobs.id = ja.jobs_id
  AND ja.details LIKE '%completed the _ob%';

-- changeset sathyam:03-seed-schema-17
ALTER TABLE job_cwe_detail_media_mapping
  ALTER COLUMN created_at SET NOT NULL;

-- changeset sathyam:03-seed-schema-18
ALTER TABLE job_cwe_detail_media_mapping
  ALTER COLUMN modified_at SET NOT NULL;

-- changeset sathyam:03-seed-schema-19
ALTER TABLE job_cwe_detail_media_mapping
  ALTER COLUMN created_by SET NOT NULL;

-- changeset sathyam:03-seed-schema-20
ALTER TABLE job_cwe_detail_media_mapping
  ALTER COLUMN modified_by SET NOT NULL;

-- changeset sathyam:03-seed-schema-21
ALTER TABLE job_cwe_details
  ALTER COLUMN reason TYPE TEXT;

-- changeset sathyam:03-seed-schema-22
ALTER TABLE job_cwe_details
  ALTER COLUMN created_at SET NOT NULL;

-- changeset sathyam:03-seed-schema-23
ALTER TABLE job_cwe_details
  ALTER COLUMN modified_at SET NOT NULL;

-- changeset sathyam:03-seed-schema-24
ALTER TABLE job_cwe_details
  ALTER COLUMN created_by SET NOT NULL;

-- changeset sathyam:03-seed-schema-25
ALTER TABLE job_cwe_details
  ALTER COLUMN modified_by SET NOT NULL;

-- changeset sathyam:03-seed-schema-26
ALTER TABLE job_cwe_details
  ALTER COLUMN jobs_id SET NOT NULL;

-- changeset sathyam:03-seed-schema-27
ALTER TABLE job_cwe_details
  ALTER COLUMN comment SET NOT NULL;

-- changeset sathyam:03-seed-schema-28
ALTER TABLE checklists
  ALTER COLUMN state TYPE VARCHAR(50);

-- changeset sathyam:03-seed-schema-29
ALTER TABLE checklists
  ALTER COLUMN code TYPE VARCHAR(20);

-- changeset sathyam:03-seed-schema-30
ALTER TABLE activities
  ALTER COLUMN type TYPE VARCHAR(20);

-- changeset sathyam:03-seed-schema-31
ALTER TABLE activity_value_approvals
  ALTER COLUMN state TYPE VARCHAR(50);

-- changeset sathyam:03-seed-schema-32
ALTER TABLE activity_values
  ALTER COLUMN state TYPE VARCHAR(50);

-- changeset sathyam:03-seed-schema-33
ALTER TABLE temp_activity_values
  ALTER COLUMN state TYPE VARCHAR(50);

-- changeset sathyam:03-seed-schema-34
ALTER TABLE checklist_collaborator_comments
  ALTER COLUMN review_state TYPE VARCHAR(50);

-- changeset sathyam:03-seed-schema-35
ALTER TABLE checklist_collaborator_mapping
  ALTER COLUMN state TYPE VARCHAR(50);

-- changeset sathyam:03-seed-schema-36
ALTER TABLE checklist_collaborator_mapping
  ALTER COLUMN type TYPE VARCHAR(50);

-- changeset sathyam:03-seed-schema-37
ALTER TABLE checklist_collaborator_mapping
  ALTER COLUMN phase_type TYPE VARCHAR(50);

-- changeset sathyam:03-seed-schema-38
ALTER TABLE jobs
  ALTER COLUMN code TYPE VARCHAR(50);

-- changeset sathyam:03-seed-schema-39
ALTER TABLE jobs
  ALTER COLUMN state TYPE VARCHAR(50);

-- changeset sathyam:03-seed-schema-40
ALTER TABLE job_audits
  ALTER COLUMN action TYPE VARCHAR(50);

-- changeset sathyam:03-seed-schema-41
ALTER TABLE job_audits
  ALTER COLUMN event TYPE VARCHAR(50);

-- changeset sathyam:03-seed-schema-42
ALTER TABLE job_audits
  ALTER COLUMN severity TYPE VARCHAR(50);

-- changeset sathyam:03-seed-schema-43
ALTER TABLE properties
  ALTER COLUMN type TYPE VARCHAR(50);

-- changeset sathyam:03-seed-schema-44
ALTER TABLE tasks
  ALTER COLUMN timer_operator TYPE VARCHAR(50);

-- changeset sathyam:03-seed-schema-45
ALTER TABLE task_executions
  ALTER COLUMN state TYPE VARCHAR(50);

-- changeset sathyam:03-seed-schema-46
ALTER TABLE task_execution_user_mapping
  ALTER COLUMN state TYPE VARCHAR(50);

-- changeset sathyam:03-seed-schema-47
ALTER TABLE versions
  ALTER COLUMN type TYPE VARCHAR(50);

-- changeset sathyam:03-seed-schema-48
DROP TABLE checklist_reviewer_comments;

-- changeset sathyam:03-seed-schema-49
DROP TABLE checklist_reviewer_mapping;

-- changeset mokshesh:03-seed-schema-50
ALTER TABLE facilities
  DROP COLUMN code;

-- changeset mokshesh:03-seed-schema-51
ALTER TABLE organisations
  DROP COLUMN addresses_id;

-- changeset mokshesh:03-seed-schema-52
ALTER TABLE properties
  ADD COLUMN archived BOOLEAN DEFAULT FALSE;

-- changeset mokshesh:03-seed-schema-53
ALTER TABLE properties
  ADD COLUMN organisations_id BIGINT REFERENCES organisations (id);

-- changeset mokshesh:03-seed-schema-54
ALTER TABLE organisations
  RENAME COLUMN contact_number TO name;

-- changeset mokshesh:03-seed-schema-55
ALTER TABLE organisations
  RENAME COLUMN deleted_status TO archived;

-- changeset mokshesh:03-seed-schema-56
ALTER TABLE facilities
  DROP COLUMN created_by;

-- changeset mokshesh:03-seed-schema-57
ALTER TABLE facilities
  DROP COLUMN modified_by;

-- changeset mokshesh:03-seed-schema-58
ALTER TABLE facilities
  ADD COLUMN archived BOOLEAN DEFAULT FALSE;

-- changeset mokshesh:03-seed-schema-59
ALTER TABLE user_facilities_mapping
  DROP COLUMN created_by;

-- changeset mokshesh:03-seed-schema-60
ALTER TABLE user_facilities_mapping
  DROP COLUMN modified_by;

-- changeset mokshesh:03-seed-schema-61
ALTER TABLE users
  DROP COLUMN is_verified;

-- changeset mokshesh:03-seed-schema-62
ALTER TABLE users
  DROP COLUMN username;

-- changeset mokshesh:03-seed-schema-63
ALTER TABLE users
    RENAME COLUMN is_archived TO archived;