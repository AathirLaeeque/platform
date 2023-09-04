-- liquibase formatted sql

-- changeset mokshesh:06-seed-schema-1
ALTER TABLE medias
  ALTER COLUMN organisations_id SET NOT NULL;

-- changeset mokshesh:06-seed-schema-2
ALTER TABLE checklists
  ADD COLUMN organisations_id BIGINT REFERENCES organisations (id);

UPDATE checklists c
SET organisations_id = f.organisations_id
FROM facilities f
where f.id = c.facilities_id;

ALTER TABLE checklists
  ALTER COLUMN organisations_id SET NOT NULL;

-- changeset mokshesh:06-seed-schema-3
ALTER TABLE jobs
  ADD COLUMN facilities_id BIGINT REFERENCES facilities (id);

UPDATE jobs j
SET facilities_id = c.facilities_id
FROM checklists c
where j.checklists_id = c.id;

ALTER TABLE jobs
  ALTER COLUMN facilities_id SET NOT NULL;

-- changeset mokshesh:06-seed-schema-4
ALTER TABLE jobs
  ADD COLUMN organisations_id BIGINT REFERENCES organisations (id);

UPDATE jobs j
SET organisations_id = c.organisations_id
FROM checklists c
where j.checklists_id = c.id;

ALTER TABLE jobs
  ALTER COLUMN organisations_id SET NOT NULL;

-- changeset mokshesh:06-seed-schema-5
--comment: adding fqdn column to organisations
ALTER TABLE organisations
  ADD COLUMN fqdn TEXT;

