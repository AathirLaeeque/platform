-- liquibase formatted sql

--changeset sathyam:202301162353-seed-schema-data-1
ALTER TABLE parameters
ADD COLUMN is_auto_initialized boolean default false;
ALTER TABLE parameters
ADD COLUMN auto_initialize jsonb;
