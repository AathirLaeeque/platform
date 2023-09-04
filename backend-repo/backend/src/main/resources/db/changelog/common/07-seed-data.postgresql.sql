-- liquibase formatted sql

--changeset mokshesh:07-seed-data-1
--comment: updating the fqdn for Leucine
UPDATE organisations
SET fqdn = 'https://app.leucine.tech'
WHERE id = 1;