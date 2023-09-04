-- liquibase formatted sql

-- changeset sathyam:202208031003-seed-schema-1
-- comment add description field to activities entity
ALTER TABLE activities
  ADD COLUMN description text;

-- changeset mokshesh:202208031003-seed-schema-2
-- comment add validations column to activities entity
ALTER TABLE activities
  ADD COLUMN validations jsonb default '{}';

-- changeset mokshesh:202208031003-seed-schema-3
-- comment add validations column to activities entity
ALTER TABLE checklists
  ADD COLUMN job_log_columns jsonb default '{}';

