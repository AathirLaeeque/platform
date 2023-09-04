-- liquibase formatted sql

--changeset mayank:202209281148-seed-schema-1
--comment added table checklist facility mapping
CREATE TABLE checklist_facility_mapping
(
  facilities_id BIGINT NOT NULL,
  checklists_id BIGINT NOT NULL,
  created_at    BIGINT NOT NULL,
  modified_at   BIGINT NOT NULL,
  created_by    BIGINT NOT NULL,
  modified_by   BIGINT NOT NULL,
  PRIMARY KEY (facilities_id, checklists_id),
  FOREIGN KEY (checklists_id) REFERENCES checklists (id),
  FOREIGN KEY (facilities_id) REFERENCES facilities (id)
);

--changeset mayank:202209281148-seed-schema-2
--comment added entries in checklist facility mapping
INSERT INTO checklist_facility_mapping (checklists_id, created_at, created_by, modified_at, modified_by,
                                        facilities_id) (SELECT id,
                                                               created_at,
                                                               created_by,
                                                               modified_at,
                                                               modified_by,
                                                               facilities_id from checklists);

--changeset mayank:202209281148-seed-schema-3
--comment dropped facility column from checklist
ALTER TABLE checklists DROP COLUMN facilities_id;

