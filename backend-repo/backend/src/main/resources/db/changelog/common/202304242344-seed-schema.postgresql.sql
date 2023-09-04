-- liquibase formatted sql

-- changeset siba:202304242344-seed-schema-1
-- comment add column hidden in parameter table

ALTER TABLE parameters
  ADD COLUMN IF NOT EXISTS hidden boolean DEFAULT false;

-- changeset siba:202304242344-seed-schema-2
-- comment add column impacted_by in parameter_values table

ALTER TABLE parameter_values
  ADD COLUMN IF NOT EXISTS impacted_by jsonb;

-- changeset siba:202304242344-seed-schema-3
-- comment add column impacted_by in temp_parameter_values table

ALTER TABLE temp_parameter_values
  ADD COLUMN IF NOT EXISTS impacted_by jsonb;
