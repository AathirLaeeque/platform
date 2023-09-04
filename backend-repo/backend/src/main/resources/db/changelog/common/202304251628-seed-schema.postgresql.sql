-- liquibase formatted sql

--changeset siba:202304251628-seed-schema-1
--comment: creating task execution timer entity
CREATE TABLE IF NOT EXISTS task_execution_timers
(
  id                 BIGINT NOT NULL
    PRIMARY KEY,
  created_at         BIGINT NOT NULL,
  modified_at        BIGINT NOT NULL,
  comment            text,
  paused_at          BIGINT NOT NULL,
  reason             text,
  resumed_at         BIGINT,
  task_executions_id BIGINT NOT NULL,
  created_by         BIGINT,
  modified_by        BIGINT
);

ALTER TABLE task_execution_timers
  ADD CONSTRAINT fknit8olo6meif8twgan7r98y51 FOREIGN KEY (task_executions_id) REFERENCES task_executions (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE task_execution_timers
  ADD CONSTRAINT fkn9nnd8jsj7ydvsiiw9c8tb0he FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE task_execution_timers
  ADD CONSTRAINT fk64a4xcqdqa6h9hsfbs6lsw2a4 FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

--changeset siba:202304251628-seed-schema-2
--comment: adding column duration in task_executions

ALTER TABLE task_executions
  ADD COLUMN IF NOT EXISTS duration BIGINT;
