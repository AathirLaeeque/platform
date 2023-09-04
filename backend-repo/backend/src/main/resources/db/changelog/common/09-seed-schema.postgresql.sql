-- liquibase formatted sql

-- changeset mokshesh:08-seed-schema-1
-- comment increasing the size of tasks' name to 512
ALTER TABLE tasks
  ALTER COLUMN name type varchar(512);

-- changeset mokshesh:08-seed-schema-2
-- comment increasing the size of stages' name to 512
ALTER TABLE stages
  ALTER COLUMN name type varchar(512);

-- changeset mokshesh:08-seed-schema-3
-- comment increasing the size of checklists' name to 512
ALTER TABLE checklists
  ALTER COLUMN name type varchar(512);