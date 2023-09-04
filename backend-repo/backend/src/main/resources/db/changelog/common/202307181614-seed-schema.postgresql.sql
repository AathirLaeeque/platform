-- liquibase formatted sql

--changeset Sathyam:202307181614-seed-schema-1
ALTER TABLE job_audits
  ADD COLUMN parameters JSONB NOT NULL DEFAULT '{}'::JSONB;
