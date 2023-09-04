-- liquibase formatted sql

-- changeset sathyam:05-seed-schema-1
ALTER TABLE medias RENAME COLUMN link TO relative_path;

-- changeset sathyam:05-seed-schema-2
ALTER TABLE medias ADD COLUMN original_filename VARCHAR(255);

-- changeset sathyam:05-seed-schema-3
ALTER TABLE medias ADD COLUMN organisations_id BIGINT REFERENCES organisations (id);

-- changeset sathyam:05-seed-schema-4
ALTER TABLE job_audits DROP COLUMN facilities_id;
ALTER TABLE job_audits DROP COLUMN event;
ALTER TABLE job_audits DROP COLUMN severity;
ALTER TABLE job_audits DROP COLUMN diff_data;
ALTER TABLE job_audits DROP COLUMN old_data;
ALTER TABLE job_audits DROP COLUMN new_data;

-- changeset mokshesh:05-seed-schema-5
ALTER TABLE checklists
  ALTER COLUMN review_cycle SET DEFAULT 1;
UPDATE checklists
SET review_cycle = 1
WHERE review_cycle IS NULL;
ALTER TABLE checklists
  ALTER COLUMN review_cycle SET NOT NULL;