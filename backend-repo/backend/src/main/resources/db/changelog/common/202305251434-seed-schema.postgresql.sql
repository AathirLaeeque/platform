-- liquibase formatted sql

--changeset sathyam:202305251434-seed-schema-1
--comment: adding scheduler tables
CREATE TABLE IF NOT EXISTS schedulers
(
  id                     BIGINT       NOT NULL,
  name                   VARCHAR(255) NOT NULL,
  description            TEXT,
  state                  VARCHAR(50)  NOT NULL,
  code                   VARCHAR(20)  NOT NULL,
  facilities_id          BIGINT       NOT NULL,
  versions_id            BIGINT       NOT NULL,
  use_cases_id           BIGINT       NOT NULL,
  checklists_id          BIGINT       NOT NULL,
  checklists_name  VARCHAR(255) NOT NULL,
  expected_start_date BIGINT NOT NULL,
  is_repeated      BOOLEAN DEFAULT FALSE,
  recurrence_rule  TEXT,
  due_date_interval INTEGER,
  due_date_duration JSONB,
  data             JSONB DEFAULT '{}' NOT NULL,
  is_custom_recurrence BOOLEAN DEFAULT FALSE,
  enabled          BOOLEAN DEFAULT TRUE,
  archived         BOOLEAN DEFAULT FALSE,
  created_at       BIGINT NOT NULL,
  modified_at      BIGINT NOT NULL,
  created_by       BIGINT NOT NULL,
  modified_by      BIGINT NOT NULL,
  deprecated_at    BIGINT,
  PRIMARY KEY (id),
  FOREIGN KEY (checklists_id) REFERENCES checklists (id),
  FOREIGN KEY (facilities_id) REFERENCES facilities (id),
  FOREIGN KEY (use_cases_id) REFERENCES use_cases (id),
  FOREIGN KEY (versions_id) REFERENCES versions (id)

);

--changeset sathyam:202305251434-seed-schema-2
--comment: add scheduler related columns to jobs table
ALTER TABLE jobs
  ADD COLUMN IF NOT EXISTS is_scheduled BOOLEAN DEFAULT FALSE;
ALTER TABLE jobs
  ADD COLUMN IF NOT EXISTS schedulers_id BIGINT NULL;
ALTER TABLE jobs
  ADD COLUMN IF NOT EXISTS expected_start_date BIGINT;
ALTER TABLE jobs
  ADD COLUMN IF NOT EXISTS expected_end_date BIGINT;
ALTER TABLE jobs
  ADD CONSTRAINT jobs_schedulers_id_fkey FOREIGN KEY (schedulers_id) REFERENCES schedulers (id) ON UPDATE NO ACTION ON DELETE NO ACTION;
