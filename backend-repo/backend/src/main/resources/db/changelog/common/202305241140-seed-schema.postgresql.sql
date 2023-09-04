-- liquibase formatted sql

--changeset Anjali:202305241140-seed-schema-1

CREATE TABLE IF NOT EXISTS parameter_verifications
(
  id                    BIGINT        NOT NULL,
  comments              TEXT,
  jobs_id               BIGINT        NOT NULL,
  parameter_values_id   BIGINT        NOT NULL,
  verification_status   VARCHAR(255)  NOT NULL,
  verification_type     VARCHAR(255)  NOT NULL,
  created_at            BIGINT        NOT NULL,
  created_by            BIGINT        NOT NULL,
  modified_by           BIGINT        NOT NULL,
  modified_at           BIGINT        NOT NULL,
  users_id              BIGINT        NOT NULL,
  CONSTRAINT parameter_verifications_pkey PRIMARY KEY (id),
  FOREIGN KEY (parameter_values_id) REFERENCES parameter_values (id),
  FOREIGN KEY (jobs_id) REFERENCES jobs (id)
)
