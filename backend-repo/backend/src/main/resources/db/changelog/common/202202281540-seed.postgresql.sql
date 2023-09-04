-- liquibase formatted sql

-- changeset mokshesh:202202281540-seed-1
-- comment adding foreign key constraint to facility_use_case_property_mapping_id column of checklist_property_values table
ALTER TABLE checklist_property_values
  ADD CONSTRAINT fk1a43308796a14eb383956d2e FOREIGN KEY (facility_use_case_property_mapping_id) REFERENCES facility_use_case_property_mapping (id);

-- changeset mokshesh:202202281540-seed-2
-- comment adding foreign key constraint to facility_use_case_property_mapping_id column of job_property_values table
ALTER TABLE job_property_values
  ADD CONSTRAINT fk268e25ce0cba47dbafb4e6be FOREIGN KEY (facility_use_case_property_mapping_id) REFERENCES facility_use_case_property_mapping (id);

-- changeset mokshesh:202202281540-seed-3
-- comment adding checklist_audits table
CREATE TABLE checklist_audits
(
  id               BIGINT      NOT NULL,
  checklists_id    BIGINT      NOT NULL,
  "action"         varchar(50) NULL,
  details          text        NULL,
  triggered_at     int8        NOT NULL,
  triggered_by     int8        NOT NULL,
  organisations_id int8        NOT NULL,
  CONSTRAINT idx215d20d60f004df08ae5c3e5 PRIMARY KEY (id)
);
CREATE INDEX idxb8b9efc9305b4c659da3787a ON checklist_audits (checklists_id);
CREATE INDEX idxd747438b94e0469eb7d70aad ON checklist_audits (organisations_id);

-- changeset mokshesh:202202281540-seed-4
-- comment adding started by columns in jobs table
ALTER TABLE jobs
  ADD COLUMN started_by BIGINT REFERENCES users (id);
UPDATE jobs
SET started_by = ja.triggered_by
FROM job_audits ja
WHERE jobs.id = ja.jobs_id
  AND ja.details LIKE '%started the Job%';

-- changeset mokshesh:202202281540-seed-5
-- comment adding ended by columns in jobs table
ALTER TABLE jobs
  ADD COLUMN ended_by BIGINT REFERENCES users (id);
UPDATE jobs
SET ended_by = ja.triggered_by
FROM job_audits ja
WHERE jobs.id = ja.jobs_id
  AND ja.details LIKE '%completed the _ob%';

-- changeset mokshesh:202202281540-seed-6
--comment: adding index to job_audits tables on column jobs_id
CREATE INDEX idx1edc854fe2bc4a3698c0131c ON job_audits (jobs_id);

-- changeset mokshesh:202202281540-seed-7
--comment: adding index to task_executions tables on column correction_enabled
ALTER TABLE task_executions
  ADD COLUMN correction_enabled BOOLEAN DEFAULT false;

-- changeset mokshesh:202202281540-seed-8
--comment: migration for removing COMPLETED_WITH_CORRECTION state in task_executions
UPDATE task_executions
SET state = 'COMPLETED'
WHERE state = 'COMPLETED_WITH_CORRECTION';

-- changeset mokshesh:202202281540-seed-9
--comment: migration for removing ENABLED_FOR_CORRECTION state in task_executions
UPDATE task_executions
SET correction_enabled = true
WHERE state = 'ENABLED_FOR_CORRECTION';

-- changeset mokshesh:202202281540-seed-10
--comment: update the column comment to text of table job_cwe_details
ALTER TABLE job_cwe_details
  ALTER COLUMN comment TYPE TEXT;

-- changeset mokshesh:202202281540-seed-11
--comment: update the column reason to varchar of table job_cwe_details
ALTER TABLE job_cwe_details
  ALTER COLUMN reason TYPE VARCHAR(45);