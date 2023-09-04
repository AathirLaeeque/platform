-- liquibase formatted sql

--changeset siba:202208241256-seed-schema-1
ALTER TABLE relations
ADD COLUMN is_mandatory BOOLEAN DEFAULT FALSE;
