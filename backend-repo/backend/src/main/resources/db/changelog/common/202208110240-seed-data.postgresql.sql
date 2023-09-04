-- liquibase formatted sql

--changeset mokshesh:202208110240-seed-data-1
INSERT INTO users (id, created_at, modified_at, organisations_id, employee_id, email, first_name, archived, last_name)
VALUES (2, 0, 0, 1, '2', 'bot@leucinetech.com', 'Leucine Bot', true, null);

-- changeset mokshesh:202208110240-seed-schema-2
-- comment migrate all the existing checklist to cleaning use case
UPDATE checklists
SET use_cases_id = (SELECT id from use_cases u WHERE u.name = 'cleaning')
WHERE id > -1;

-- changeset mokshesh:202202221615-seed-schema-3
-- comment migrate all the existing jobs to cleaning use case
UPDATE jobs
SET use_cases_id = (SELECT id from use_cases u WHERE u.name = 'cleaning')
WHERE id > -1;

-- changeset mokshesh:202202221615-seed-schema-4
-- comment add index on use_cases_id column to jobs table and set it to NOT NULL
CREATE INDEX idx3de8f27a751f40bc9191a509 ON jobs (use_cases_id);
ALTER TABLE jobs
  ALTER COLUMN use_cases_id SET NOT NULL;
