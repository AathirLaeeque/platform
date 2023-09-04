-- liquibase formatted sql

-- changeset mokshesh:08-seed-schema-1
--comment: adding fqdn column to organisations
ALTER TABLE organisations
  ALTER COLUMN fqdn SET NOT NULL;
