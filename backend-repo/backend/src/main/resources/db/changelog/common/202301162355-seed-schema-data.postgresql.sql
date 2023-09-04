-- liquibase formatted sql

--changeset sathyam:202301162355-seed-schema-data-1
ALTER TABLE parameters
ADD COLUMN rules jsonb;
ALTER TABLE parameter_values
ADD COLUMN hidden boolean default false;
ALTER TABLE temp_parameter_values
ADD COLUMN hidden boolean default false;
