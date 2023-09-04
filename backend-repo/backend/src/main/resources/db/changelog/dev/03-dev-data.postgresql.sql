-- liquibase formatted sql

--changeset mokshesh:03-dev-data-1
--comment: updating the fqdn for Leucine I
UPDATE organisations
SET fqdn = 'https://FQDN'
WHERE id = 1617625401;

--changeset mokshesh:03-dev-data-2
--comment: updating the fqdn for Leucine II
UPDATE organisations
SET fqdn = 'https://FQDN'
WHERE id = 1643103402;
