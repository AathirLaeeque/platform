-- liquibase formatted sql

--changeset Siba:202308211838-seed-schema-1

update task_executions te
set modified_at = started_at
where started_at > te.modified_at
  and state = 'IN_PROGRESS';
