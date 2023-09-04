-- liquibase formatted sql

--changeset mayank:202209261018-seed-schema-1
--comment added facility id in default checklist user table
ALTER TABLE checklist_default_users
  ADD COLUMN facilities_id BIGINT REFERENCES facilities (id);

--changeset mayank:202209261018-seed-schema-2
--comment copied facility id from checklist to default user table
UPDATE checklist_default_users cdu
SET facilities_id=cl.facilities_id FROM (SELECT facilities_id, id FROM checklists) as cl
WHERE cdu.checklists_id=cl.id
