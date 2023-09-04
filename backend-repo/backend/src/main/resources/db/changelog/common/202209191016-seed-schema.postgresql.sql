-- liquibase formatted sql

--changeset mayank:202209191016-seed-schema-1
--comment add is global flag in checklist
ALTER TABLE checklists
  ADD COLUMN is_global boolean NOT NULL DEFAULT false;

--changeset mayank:202209191016-seed-schema-2
--comment drop not null constraint because of global checklist
ALTER TABLE checklists
  ALTER COLUMN facilities_id DROP NOT NULL;


