-- liquibase formatted sql

--changeset sathyam:202304241211-seed-schema-1
-- TODO verify
CREATE TABLE IF NOT EXISTS auto_initialized_parameters
(
  id                             BIGINT NOT NULL,
  auto_initialized_parameters_id BIGINT NOT NULL,
  referenced_parameters_id       BIGINT NOT NULL,
  checklists_id                  BIGINT NOT NULL,
  created_at                     BIGINT NOT NULL,
  modified_at                    BIGINT NOT NULL,
  created_by                     BIGINT NOT NULL,
  modified_by                    BIGINT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (checklists_id) REFERENCES checklists (id),
  FOREIGN KEY (auto_initialized_parameters_id) REFERENCES parameters (id),
  FOREIGN KEY (referenced_parameters_id) REFERENCES parameters (id)
);
