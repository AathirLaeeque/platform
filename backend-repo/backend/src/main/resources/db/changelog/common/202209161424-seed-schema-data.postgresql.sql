-- liquibase formatted sql

--changeset sathyam:202209161424-seed-schema-data-1
UPDATE activities SET validations = '{}' where validations is NULL;
ALTER TABLE activities ALTER COLUMN validations SET NOT NULL;

