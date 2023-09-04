-- liquibase formatted sql

--changeset Anjali:202306061105-seed-schema-1
ALTER TABLE parameter_values
  ADD COLUMN IF NOT EXISTS verified BOOLEAN DEFAULT FALSE;

--changeset Anjali:202306061105-seed-schema-2
ALTER TABLE temp_parameter_values
  ADD COLUMN IF NOT EXISTS verified BOOLEAN DEFAULT FALSE;

--changeset Anjali:202306061105-seed-schema-3
ALTER TABLE parameters
  ADD COLUMN IF NOT EXISTS verification_type VARCHAR(50) DEFAULT 'NONE' NOT NULL;

--changeset Anjali:202306061105-seed-schema-4
UPDATE parameter_values
  SET state='APPROVAL_PENDING' WHERE state='PENDING_FOR_APPROVAL';
