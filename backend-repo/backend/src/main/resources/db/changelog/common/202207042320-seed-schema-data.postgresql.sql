-- liquibase formatted sql

--changeset siba:202207042320-seed-schema-data-1
CREATE TABLE checklist_default_users
(
    id           BIGINT NOT NULL,
    checklists_id BIGINT NOT NULL,
    users_id      BIGINT NOT NULL,
    tasks_id     BIGINT NOT NULL,
    CONSTRAINT checklist_reviewer_mapping_pkey PRIMARY KEY (id),
    CONSTRAINT uniquedefaultuserconstraint UNIQUE (checklists_id, users_id, tasks_id)
);

ALTER TABLE checklist_default_users
    ADD CONSTRAINT fkn1ae4i3odmi0j3uhveq6c7cdk FOREIGN KEY (checklists_id) REFERENCES checklists (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE checklist_default_users
    ADD CONSTRAINT fkm1ae4i3odmi0j3uhveq6c7cdk FOREIGN KEY (users_id) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE checklist_default_users
    ADD CONSTRAINT fko1ae4i3odmi0j3uhveq6c7cdk FOREIGN KEY (tasks_id) REFERENCES tasks (id) ON UPDATE NO ACTION ON DELETE NO ACTION;
