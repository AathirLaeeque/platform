-- liquibase formatted sql

--changeset sathyam:202209192136-seed-schema-1
ALTER TABLE automations ADD COLUMN archived BOOLEAN DEFAULT FALSE;
