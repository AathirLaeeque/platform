-- liquibase formatted sql

--changeset sathyam:202208110023-seed-schema-1
CREATE TABLE automations
(
id              BIGINT             NOT NULL,
type		  VARCHAR(50)        NOT NULL,
action_type	  VARCHAR(50)        NOT NULL,
target_entity_type VARCHAR(50)   NOT NULL,
trigger_type    VARCHAR(50)       NOT NULL,
action_details  JSONB	DEFAULT '{}' NOT NULL,
trigger_details JSONB	DEFAULT '{}' NOT NULL,
created_at      BIGINT             NOT NULL,
modified_at     BIGINT             NOT NULL,
created_by      BIGINT             NOT NULL,
modified_by     BIGINT             NOT NULL,
CONSTRAINT pk_automations PRIMARY KEY (id)
);

--changeset sathyam:202208110023-seed-schema-2
CREATE TABLE task_automation_mapping
(
id             BIGINT             NOT NULL,
tasks_id	 BIGINT             NOT NULL,
automations_id BIGINT             NOT NULL,
display_name   VARCHAR(255)       NOT NULL,
order_tree     INTEGER	    NOT NULL,
created_at     BIGINT             NOT NULL,
modified_at    BIGINT             NOT NULL,
created_by     BIGINT             NOT NULL,
modified_by    BIGINT             NOT NULL,
CONSTRAINT pk_task_automation_mapping PRIMARY KEY (id)
);

--changeset sathyam:202208110023-seed-schema-3
ALTER TABLE task_automation_mapping
ADD CONSTRAINT fk_task_automation_mapping_tasks_id FOREIGN KEY (tasks_id) REFERENCES tasks (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

--changeset sathyam:202208110023-seed-schema-4
ALTER TABLE task_automation_mapping
ADD CONSTRAINT fk_checklist_automation_mapping_automations_id FOREIGN KEY (automations_id) REFERENCES automations (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

