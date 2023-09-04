-- liquibase formatted sql

-- changeset siba:202301171240-seed-schema-1
-- comment drop default value for choices field

ALTER TABLE parameter_values
  ALTER COLUMN choices DROP DEFAULT;
