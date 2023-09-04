-- liquibase formatted sql

--changeset sathyam:202208091712-seed-schema-1
CREATE TABLE relations
(
  id             BIGINT             NOT NULL,
  order_tree     INTEGER            NOT NULL,
  external_id    VARCHAR(255)       NOT NULL,
  display_name   VARCHAR(255)       NOT NULL,
  validations    JSONB DEFAULT '{}' NOT NULL,
  variables      JSONB DEFAULT '{}' NOT NULL,
  cardinality     VARCHAR(50)        NOT NULL,
  object_type_id VARCHAR(50)        NOT NULL,
  collection     VARCHAR(255)       NOT NULL,
  url_path       TEXT               NOT NULL,
  checklists_id  BIGINT             NOT NULL,
  created_at     BIGINT             NOT NULL,
  modified_at    BIGINT             NOT NULL,
  created_by     BIGINT             NOT NULL,
  modified_by    BIGINT             NOT NULL,
  CONSTRAINT pkjxq7qi13myhmjajlr9rt PRIMARY KEY (id)
);

--changeset sathyam:202208091712-seed-schema-2
ALTER TABLE relations
  ADD CONSTRAINT fkfoen1zul5hpj8tetw1ko FOREIGN KEY (checklists_id) REFERENCES checklists (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

--changeset sathyam:202208091712-seed-schema-3
CREATE TABLE relation_values
(
  id                       BIGINT       NOT NULL,
  jobs_id                  BIGINT       NOT NULL,
  relations_id             BIGINT       NOT NULL,
  object_id                VARCHAR(50)  NOT NULL,
  object_external_id       VARCHAR(255) NOT NULL,
  collection               VARCHAR(255) NOT NULL,
  object_display_name      VARCHAR(255) NOT NULL,
  object_type_external_id  VARCHAR(255) NOT NULL,
  object_type_display_name VARCHAR(255) NOT NULL,
  created_at               BIGINT       NOT NULL,
  modified_at              BIGINT       NOT NULL,
  created_by               BIGINT       NOT NULL,
  modified_by              BIGINT       NOT NULL,
  CONSTRAINT pkp32qjbpeex1d3zpvhx66 PRIMARY KEY (id)
);

--changeset sathyam:202208091712-seed-schema-4
ALTER TABLE relation_values
  ADD CONSTRAINT fkpr19zs0t0j1pgj6r4ums FOREIGN KEY (relations_id) REFERENCES relations (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

--changeset sathyam:202208091712-seed-schema-5
ALTER TABLE relation_values
  ADD CONSTRAINT fk88e5jno0mr3sxaj3cfui FOREIGN KEY (jobs_id) REFERENCES jobs (id) ON UPDATE NO ACTION ON DELETE NO ACTION;
