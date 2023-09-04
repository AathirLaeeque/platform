-- liquibase formatted sql

-- changeset mokshesh:01-seed-schema-1
CREATE TABLE activities
(
  id           BIGINT                NOT NULL,
  archived     BOOLEAN DEFAULT FALSE NOT NULL,
  order_tree   INTEGER               NOT NULL,
  data         JSONB   DEFAULT '{}'  NOT NULL,
  label        VARCHAR(255),
  is_mandatory BOOLEAN DEFAULT FALSE NOT NULL,
  type         VARCHAR(255)          NOT NULL,
  created_at   BIGINT                NOT NULL,
  modified_at  BIGINT                NOT NULL,
  created_by   BIGINT                NOT NULL,
  modified_by  BIGINT                NOT NULL,
  tasks_id     BIGINT                NOT NULL,
  CONSTRAINT activities_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-2
CREATE TABLE users
(
  id               BIGINT       NOT NULL,
  created_at       BIGINT       NOT NULL,
  modified_at      BIGINT       NOT NULL,
  organisations_id BIGINT,
  employee_id      VARCHAR(255) NOT NULL,
  email            VARCHAR(255) NOT NULL,
  first_name       VARCHAR(255) NOT NULL,
  is_verified      BOOLEAN      NOT NULL,
  is_archived      BOOLEAN      NOT NULL,
  last_name        VARCHAR(255),
  username         VARCHAR(255),
  CONSTRAINT users_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-3
CREATE TABLE tasks
(
  id             BIGINT                NOT NULL,
  name           VARCHAR(255)          NOT NULL,
  archived       BOOLEAN DEFAULT FALSE NOT NULL,
  order_tree     INTEGER               NOT NULL,
  has_stop       BOOLEAN DEFAULT FALSE NOT NULL,
  max_period     BIGINT,
  min_period     BIGINT,
  timer_operator VARCHAR(255),
  is_timed       BOOLEAN DEFAULT FALSE NOT NULL,
  is_mandatory   BOOLEAN DEFAULT FALSE NOT NULL,
  stages_id      BIGINT                NOT NULL,
  created_at     BIGINT                NOT NULL,
  modified_at    BIGINT                NOT NULL,
  created_by     BIGINT                NOT NULL,
  modified_by    BIGINT                NOT NULL,
  CONSTRAINT tasks_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-4
CREATE TABLE activity_value_approvals
(
  id         BIGINT NOT NULL,
  created_at BIGINT,
  state      VARCHAR(255),
  users_id   BIGINT,
  CONSTRAINT activity_value_approval_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-5
CREATE TABLE activity_value_media_mapping
(
  medias_id          BIGINT NOT NULL,
  activity_values_id BIGINT NOT NULL,
  archived           BOOLEAN DEFAULT FALSE,
  created_at         BIGINT NOT NULL,
  modified_at        BIGINT NOT NULL,
  created_by         BIGINT NOT NULL,
  modified_by        BIGINT NOT NULL,
  CONSTRAINT activity_value_media_mapping_pkey PRIMARY KEY (medias_id, activity_values_id)
);

-- changeset mokshesh:01-seed-schema-6
CREATE TABLE activity_values
(
  id                         BIGINT NOT NULL,
  value                      TEXT,
  reason                     TEXT,
  state                      VARCHAR(255),
  choices                    JSONB DEFAULT '{}',
  jobs_id                    BIGINT NOT NULL,
  activities_id              BIGINT NOT NULL,
  created_at                 BIGINT NOT NULL,
  modified_at                BIGINT NOT NULL,
  created_by                 BIGINT NOT NULL,
  modified_by                BIGINT NOT NULL,
  activity_value_approval_id BIGINT,
  CONSTRAINT activity_values_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-7
CREATE TABLE medias
(
  id          BIGINT                NOT NULL,
  name        VARCHAR(255),
  filename    VARCHAR(255)          NOT NULL,
  description TEXT,
  type        VARCHAR(255)          NOT NULL,
  link        VARCHAR(255)          NOT NULL,
  archived    BOOLEAN DEFAULT FALSE NOT NULL,
  created_at  BIGINT                NOT NULL,
  modified_at BIGINT                NOT NULL,
  created_by  BIGINT                NOT NULL,
  modified_by BIGINT                NOT NULL,
  CONSTRAINT medias_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-8
CREATE TABLE jobs
(
  id            BIGINT       NOT NULL,
  code          VARCHAR(255) NOT NULL,
  state         VARCHAR(255) NOT NULL,
  checklists_id BIGINT       NOT NULL,
  created_at    BIGINT       NOT NULL,
  modified_at   BIGINT       NOT NULL,
  created_by    BIGINT       NOT NULL,
  modified_by   BIGINT       NOT NULL,
  CONSTRAINT jobs_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-9
CREATE TABLE addresses
(
  id          BIGINT       NOT NULL,
  created_at  BIGINT       NOT NULL,
  modified_at BIGINT       NOT NULL,
  city        VARCHAR(255) NOT NULL,
  country     VARCHAR(255) NOT NULL,
  line1       VARCHAR(255) NOT NULL,
  line2       VARCHAR(255) NOT NULL,
  state       VARCHAR(255) NOT NULL,
  pincode     INTEGER      NOT NULL,
  CONSTRAINT addresses_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-10
CREATE TABLE checklists
(
  id            BIGINT                NOT NULL,
  name          VARCHAR(255)          NOT NULL,
  code          VARCHAR(255)          NOT NULL,
  state         VARCHAR(255)          NOT NULL,
  archived      BOOLEAN DEFAULT FALSE NOT NULL,
  versions_id   BIGINT,
  facilities_id BIGINT                NOT NULL,
  created_at    BIGINT                NOT NULL,
  modified_at   BIGINT                NOT NULL,
  created_by    BIGINT                NOT NULL,
  modified_by   BIGINT                NOT NULL,
  review_cycle  INTEGER DEFAULT 1,
  released_at   BIGINT,
  released_by   BIGINT,
  description   TEXT,
  CONSTRAINT checklists_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-11
CREATE TABLE stages
(
  id            BIGINT                NOT NULL,
  name          VARCHAR(255)          NOT NULL,
  archived      BOOLEAN DEFAULT FALSE NOT NULL,
  order_tree    INTEGER               NOT NULL,
  checklists_id BIGINT                NOT NULL,
  created_at    BIGINT                NOT NULL,
  modified_at   BIGINT                NOT NULL,
  created_by    BIGINT                NOT NULL,
  modified_by   BIGINT                NOT NULL,
  CONSTRAINT stages_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-12
CREATE TABLE temp_activity_value_media_mapping
(
  medias_id               BIGINT NOT NULL,
  temp_activity_values_id BIGINT NOT NULL,
  archived                BOOLEAN DEFAULT FALSE,
  created_at              BIGINT NOT NULL,
  modified_at             BIGINT NOT NULL,
  created_by              BIGINT NOT NULL,
  modified_by             BIGINT NOT NULL,
  CONSTRAINT temp_activity_value_media_mapping_pkey PRIMARY KEY (medias_id, temp_activity_values_id)
);

-- changeset mokshesh:01-seed-schema-13
CREATE TABLE job_audits
(
  id               BIGINT NOT NULL,
  jobs_id          BIGINT NOT NULL,
  stages_id        BIGINT,
  tasks_id         BIGINT,
  action           VARCHAR(255),
  details          TEXT,
  diff_data        JSONB,
  event            VARCHAR(255),
  facilities_id    BIGINT NOT NULL,
  new_data         JSONB,
  old_data         JSONB,
  organisations_id BIGINT NOT NULL,
  severity         VARCHAR(255),
  triggered_at     BIGINT NOT NULL,
  triggered_by     BIGINT NOT NULL,
  CONSTRAINT audits_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-14
CREATE TABLE checklist_author_mapping
(
  checklists_id BIGINT                NOT NULL,
  users_id      BIGINT                NOT NULL,
  is_primary    BOOLEAN DEFAULT FALSE NOT NULL,
  created_at    BIGINT                NOT NULL,
  modified_at   BIGINT                NOT NULL,
  created_by    BIGINT                NOT NULL,
  modified_by   BIGINT                NOT NULL,
  state         VARCHAR               NOT NULL,
  CONSTRAINT checklist_author_mapping_pkey PRIMARY KEY (checklists_id, users_id)
);

-- changeset mokshesh:01-seed-schema-15
CREATE TABLE checklist_collaborator_comments
(
  id                                 BIGINT NOT NULL,
  created_at                         BIGINT,
  modified_at                        BIGINT,
  comments                           TEXT   NOT NULL,
  review_state                       VARCHAR(255),
  created_by                         BIGINT,
  modified_by                        BIGINT,
  checklists_id                      BIGINT NOT NULL,
  checklist_collaborator_mappings_id BIGINT NOT NULL,
  CONSTRAINT checklist_collaborator_comments_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-16
CREATE TABLE checklist_collaborator_mapping
(
  id            BIGINT       NOT NULL,
  created_at    BIGINT,
  modified_at   BIGINT,
  order_tree    INTEGER      NOT NULL,
  review_cycle  INTEGER      NOT NULL,
  state         VARCHAR(255),
  type          VARCHAR(255) NOT NULL,
  created_by    BIGINT,
  modified_by   BIGINT,
  checklists_id BIGINT       NOT NULL,
  users_id      BIGINT       NOT NULL,
  CONSTRAINT checklist_collaborator_mapping_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-17
CREATE TABLE checklist_property_values
(
  value         VARCHAR(255),
  properties_id BIGINT NOT NULL,
  checklists_id BIGINT NOT NULL,
  created_at    BIGINT NOT NULL,
  modified_at   BIGINT NOT NULL,
  created_by    BIGINT NOT NULL,
  modified_by   BIGINT NOT NULL,
  CONSTRAINT checklist_property_values_pkey PRIMARY KEY (properties_id, checklists_id)
);

-- changeset mokshesh:01-seed-schema-18
CREATE TABLE properties
(
  id           BIGINT       NOT NULL,
  name         VARCHAR(255) NOT NULL,
  place_holder VARCHAR(255),
  order_tree   INTEGER      NOT NULL,
  is_mandatory BOOLEAN      NOT NULL,
  type         VARCHAR(255) NOT NULL,
  created_at   BIGINT       NOT NULL,
  modified_at  BIGINT       NOT NULL,
  created_by   BIGINT       NOT NULL,
  modified_by  BIGINT       NOT NULL,
  CONSTRAINT properties_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-19
CREATE TABLE checklist_reviewer_comments
(
  id                             BIGINT  NOT NULL,
  checklist_reviewer_mappings_id BIGINT  NOT NULL,
  checklists_id                  BIGINT  NOT NULL,
  comments                       TEXT,
  created_at                     BIGINT  NOT NULL,
  modified_at                    BIGINT  NOT NULL,
  created_by                     BIGINT  NOT NULL,
  modified_by                    BIGINT  NOT NULL,
  review_state                   VARCHAR NOT NULL,
  CONSTRAINT checklist_reviewer_comments_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-20
CREATE TABLE checklist_reviewer_mapping
(
  id            BIGINT       NOT NULL,
  checklists_id BIGINT       NOT NULL,
  users_id      BIGINT       NOT NULL,
  review_cycle  INTEGER      NOT NULL,
  state         VARCHAR(255) NOT NULL,
  created_at    BIGINT       NOT NULL,
  modified_at   BIGINT       NOT NULL,
  created_by    BIGINT       NOT NULL,
  modified_by   BIGINT       NOT NULL,
  CONSTRAINT checklist_reviewer_mapping_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-21
CREATE TABLE facilities
(
  id               BIGINT       NOT NULL,
  name             VARCHAR(255),
  code             VARCHAR(255) NOT NULL,
  organisations_id BIGINT       NOT NULL,
  addresses_id     BIGINT       NOT NULL,
  created_at       BIGINT       NOT NULL,
  modified_at      BIGINT       NOT NULL,
  created_by       BIGINT       NOT NULL,
  modified_by      BIGINT       NOT NULL,
  CONSTRAINT facilities_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-22
CREATE TABLE versions
(
  id            BIGINT NOT NULL,
  created_at    BIGINT,
  modified_at   BIGINT,
  deprecated_at BIGINT,
  parent        BIGINT,
  self          BIGINT,
  type          VARCHAR(255),
  version       INTEGER,
  versioned_at  BIGINT,
  created_by    BIGINT,
  modified_by   BIGINT,
  ancestor      BIGINT,
  CONSTRAINT versions_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-23
CREATE TABLE temp_activity_values
(
  id                         BIGINT NOT NULL,
  value                      TEXT,
  reason                     TEXT,
  state                      VARCHAR(255),
  choices                    JSONB DEFAULT '{}',
  jobs_id                    BIGINT NOT NULL,
  activities_id              BIGINT NOT NULL,
  created_at                 BIGINT NOT NULL,
  modified_at                BIGINT NOT NULL,
  created_by                 BIGINT NOT NULL,
  modified_by                BIGINT NOT NULL,
  activity_value_approval_id BIGINT,
  CONSTRAINT temp_activity_values_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-24
CREATE TABLE codes
(
  type             VARCHAR(255) NOT NULL,
  clause           SMALLINT     NOT NULL,
  counter          INTEGER      NOT NULL,
  organisations_id BIGINT       NOT NULL
);

-- changeset mokshesh:01-seed-schema-25
CREATE TABLE organisations
(
  id             BIGINT       NOT NULL,
  addresses_id   BIGINT       NOT NULL,
  contact_number VARCHAR(255) NOT NULL,
  deleted_status BOOLEAN      NOT NULL,
  created_at     BIGINT       NOT NULL,
  modified_at    BIGINT       NOT NULL,
  CONSTRAINT organisations_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-26
CREATE TABLE email_audits
(
  id             BIGINT NOT NULL,
  from_address   TEXT   NOT NULL,
  to_addresses   TEXT[],
  body           TEXT,
  subject        TEXT,
  cc             TEXT[],
  bcc            TEXT[],
  retry_attempts SMALLINT,
  max_attempts   SMALLINT,
  created_on     BIGINT NOT NULL,
  CONSTRAINT email_audits_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-27
CREATE TABLE email_templates
(
  id      BIGINT       NOT NULL,
  name    VARCHAR(255) NOT NULL,
  content TEXT         NOT NULL
);

-- changeset mokshesh:01-seed-schema-28
CREATE TABLE job_cwe_detail_media_mapping
(
  medias_id          BIGINT NOT NULL,
  job_cwe_details_id BIGINT NOT NULL,
  created_at         BIGINT NOT NULL,
  modified_at        BIGINT NOT NULL,
  created_by         BIGINT NOT NULL,
  modified_by        BIGINT NOT NULL,
  CONSTRAINT job_exception_media_mapping_pkey PRIMARY KEY (medias_id, job_cwe_details_id)
);

-- changeset mokshesh:01-seed-schema-29
CREATE TABLE job_cwe_details
(
  id          BIGINT       NOT NULL,
  reason      TEXT         NOT NULL,
  comment     VARCHAR(255) NOT NULL,
  jobs_id     BIGINT       NOT NULL,
  created_at  BIGINT       NOT NULL,
  modified_at BIGINT       NOT NULL,
  created_by  BIGINT       NOT NULL,
  modified_by BIGINT       NOT NULL,
  CONSTRAINT job_exceptions_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-30
CREATE TABLE job_property_values
(
  value         VARCHAR(255),
  properties_id BIGINT NOT NULL,
  jobs_id       BIGINT NOT NULL,
  created_at    BIGINT NOT NULL,
  modified_at   BIGINT NOT NULL,
  created_by    BIGINT NOT NULL,
  modified_by   BIGINT NOT NULL,
  CONSTRAINT job_property_values_pkey PRIMARY KEY (properties_id, jobs_id)
);

-- changeset mokshesh:01-seed-schema-31
CREATE TABLE task_executions
(
  id                BIGINT       NOT NULL,
  reason            TEXT,
  correction_reason TEXT,
  state             VARCHAR(255) NOT NULL,
  started_at        BIGINT,
  ended_at          BIGINT,
  tasks_id          BIGINT       NOT NULL,
  jobs_id           BIGINT       NOT NULL,
  created_at        BIGINT       NOT NULL,
  modified_at       BIGINT       NOT NULL,
  created_by        BIGINT       NOT NULL,
  modified_by       BIGINT       NOT NULL,
  started_by        BIGINT,
  CONSTRAINT task_executions_pkey PRIMARY KEY (id)
);

-- changeset mokshesh:01-seed-schema-32
CREATE TABLE task_execution_user_mapping
(
  users_id           BIGINT NOT NULL,
  task_executions_id BIGINT NOT NULL,
  created_at         BIGINT NOT NULL,
  modified_at        BIGINT NOT NULL,
  created_by         BIGINT NOT NULL,
  modified_by        BIGINT NOT NULL,
  action_performed   BOOLEAN,
  state              VARCHAR(255),
  CONSTRAINT task_execution_user_mapping_pkey PRIMARY KEY (users_id, task_executions_id)
);

-- changeset mokshesh:01-seed-schema-33
CREATE TABLE task_media_mapping
(
  medias_id   BIGINT NOT NULL,
  tasks_id    BIGINT NOT NULL,
  created_at  BIGINT NOT NULL,
  modified_at BIGINT NOT NULL,
  created_by  BIGINT NOT NULL,
  modified_by BIGINT NOT NULL,
  CONSTRAINT task_media_mapping_pkey PRIMARY KEY (medias_id, tasks_id)
);

-- changeset mokshesh:01-seed-schema-34
CREATE TABLE user_facilities_mapping
(
  users_id      BIGINT NOT NULL,
  facilities_id BIGINT NOT NULL,
  created_at    BIGINT NOT NULL,
  modified_at   BIGINT NOT NULL,
  created_by    BIGINT NOT NULL,
  modified_by   BIGINT NOT NULL,
  CONSTRAINT user_facilities_mapping_pkey PRIMARY KEY (users_id, facilities_id)
);

-- changeset mokshesh:01-seed-schema-35
ALTER TABLE activity_values
  ADD CONSTRAINT activity_values_activities_id_fkey FOREIGN KEY (activities_id) REFERENCES activities (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-36
ALTER TABLE temp_activity_values
  ADD CONSTRAINT temp_activity_values_activities_id_fkey FOREIGN KEY (activities_id) REFERENCES activities (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-37
ALTER TABLE tasks
  ADD CONSTRAINT tasks_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-38
ALTER TABLE tasks
  ADD CONSTRAINT tasks_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-39
ALTER TABLE activity_value_approvals
  ADD CONSTRAINT fk7s9hvd96kckj0h52uemh3w8ha FOREIGN KEY (users_id) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-40
ALTER TABLE activity_values
  ADD CONSTRAINT fkifr04h97993nvxrrvaf9exxt6 FOREIGN KEY (activity_value_approval_id) REFERENCES activity_value_approvals (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-41
ALTER TABLE temp_activity_values
  ADD CONSTRAINT fkn4c88lxdxyu0sy94dbsxalo4f FOREIGN KEY (activity_value_approval_id) REFERENCES activity_value_approvals (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-42
ALTER TABLE activity_value_media_mapping
  ADD CONSTRAINT activity_value_media_mapping_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-43
ALTER TABLE activity_value_media_mapping
  ADD CONSTRAINT activity_value_media_mapping_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-44
ALTER TABLE activity_values
  ADD CONSTRAINT activity_values_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-45
ALTER TABLE activity_values
  ADD CONSTRAINT activity_values_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-46
ALTER TABLE medias
  ADD CONSTRAINT medias_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-47
ALTER TABLE medias
  ADD CONSTRAINT medias_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-48
ALTER TABLE task_media_mapping
  ADD CONSTRAINT task_media_mapping_medias_id_fkey FOREIGN KEY (medias_id) REFERENCES medias (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-49
ALTER TABLE temp_activity_value_media_mapping
  ADD CONSTRAINT temp_activity_value_media_mapping_medias_id_fkey FOREIGN KEY (medias_id) REFERENCES medias (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-50
ALTER TABLE jobs
  ADD CONSTRAINT jobs_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-51
ALTER TABLE jobs
  ADD CONSTRAINT jobs_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-52
ALTER TABLE task_executions
  ADD CONSTRAINT task_executions_jobs_id_fkey FOREIGN KEY (jobs_id) REFERENCES jobs (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-53
ALTER TABLE temp_activity_values
  ADD CONSTRAINT temp_activity_values_jobs_id_fkey FOREIGN KEY (jobs_id) REFERENCES jobs (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-54
ALTER TABLE facilities
  ADD CONSTRAINT facilities_addresses_id_fkey FOREIGN KEY (addresses_id) REFERENCES addresses (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-55
ALTER TABLE organisations
  ADD CONSTRAINT organisations_addresses_id_fkey FOREIGN KEY (addresses_id) REFERENCES addresses (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-56
ALTER TABLE checklists
  ADD CONSTRAINT checklists_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-57
ALTER TABLE checklists
  ADD CONSTRAINT checklists_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-58
ALTER TABLE checklist_collaborator_comments
  ADD CONSTRAINT fk78rs9mepch8svaervq6fq59mv FOREIGN KEY (checklists_id) REFERENCES checklists (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-59
ALTER TABLE checklists
  ADD CONSTRAINT fk9mlm68r9moyp31nb1qu9f8q7p FOREIGN KEY (released_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-60
ALTER TABLE checklist_collaborator_mapping
  ADD CONSTRAINT fkhqu8ipfw8ymu4osou7s5j48ru FOREIGN KEY (checklists_id) REFERENCES checklists (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-61
ALTER TABLE jobs
  ADD CONSTRAINT jobs_checklists_id_fkey FOREIGN KEY (checklists_id) REFERENCES checklists (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-62
ALTER TABLE stages
  ADD CONSTRAINT stages_checklists_id_fkey FOREIGN KEY (checklists_id) REFERENCES checklists (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-63
ALTER TABLE stages
  ADD CONSTRAINT stages_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-64
ALTER TABLE stages
  ADD CONSTRAINT stages_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-65
ALTER TABLE tasks
  ADD CONSTRAINT tasks_stages_id_fkey FOREIGN KEY (stages_id) REFERENCES stages (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-66
ALTER TABLE temp_activity_value_media_mapping
  ADD CONSTRAINT temp_activity_value_media_mapping_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-67
ALTER TABLE temp_activity_value_media_mapping
  ADD CONSTRAINT temp_activity_value_media_mapping_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-68
ALTER TABLE checklist_author_mapping
  ADD CONSTRAINT checklist_author_mapping_checklists_id_fkey FOREIGN KEY (checklists_id) REFERENCES checklists (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-69
ALTER TABLE checklist_author_mapping
  ADD CONSTRAINT checklist_author_mapping_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-70
ALTER TABLE checklist_author_mapping
  ADD CONSTRAINT checklist_author_mapping_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-71
ALTER TABLE checklist_author_mapping
  ADD CONSTRAINT checklist_author_mapping_users_id_fkey FOREIGN KEY (users_id) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-72
ALTER TABLE checklist_collaborator_comments
  ADD CONSTRAINT fk2k9oe3mttovwwstbempeb3vl5 FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-73
ALTER TABLE checklist_collaborator_comments
  ADD CONSTRAINT fkn2ocbnmcq3bxste1880tdob6y FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-74
ALTER TABLE checklist_collaborator_mapping
  ADD CONSTRAINT fkekbi4kf1ol6i69ab906mr0qsn FOREIGN KEY (users_id) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-75
ALTER TABLE checklist_collaborator_mapping
  ADD CONSTRAINT fkg19djcsqxth1fvy58sgd6343k FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-76
ALTER TABLE checklist_collaborator_mapping
  ADD CONSTRAINT fkhq54xyr8haas6ygvea1qnsq5t FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-77
ALTER TABLE checklist_collaborator_comments
  ADD CONSTRAINT fkk9y45yfs7plpk9q3e7ohgoo7a FOREIGN KEY (checklist_collaborator_mappings_id) REFERENCES checklist_collaborator_mapping (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-78
ALTER TABLE checklist_property_values
  ADD CONSTRAINT checklist_property_values_checklists_id_fkey FOREIGN KEY (checklists_id) REFERENCES checklists (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-79
ALTER TABLE checklist_property_values
  ADD CONSTRAINT checklist_property_values_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-80
ALTER TABLE checklist_property_values
  ADD CONSTRAINT checklist_property_values_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-81
ALTER TABLE properties
  ADD CONSTRAINT properties_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-82
ALTER TABLE properties
  ADD CONSTRAINT properties_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-83
ALTER TABLE checklist_reviewer_comments
  ADD CONSTRAINT checklist_reviewer_comments_checklists_id_fkey FOREIGN KEY (checklists_id) REFERENCES checklists (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-84
ALTER TABLE checklist_reviewer_comments
  ADD CONSTRAINT checklist_reviewer_comments_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-85
ALTER TABLE checklist_reviewer_comments
  ADD CONSTRAINT checklist_reviewer_comments_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-86
ALTER TABLE checklist_reviewer_mapping
  ADD CONSTRAINT checklist_reviewer_mapping_checklists_id_fkey FOREIGN KEY (checklists_id) REFERENCES checklists (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-87
ALTER TABLE checklist_reviewer_mapping
  ADD CONSTRAINT checklist_reviewer_mapping_checklists_id_users_id_review_cy_key UNIQUE (checklists_id, users_id, review_cycle);

-- changeset mokshesh:01-seed-schema-88
ALTER TABLE checklist_reviewer_mapping
  ADD CONSTRAINT checklist_reviewer_mapping_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-89
ALTER TABLE checklist_reviewer_mapping
  ADD CONSTRAINT checklist_reviewer_mapping_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-90
ALTER TABLE checklist_reviewer_mapping
  ADD CONSTRAINT checklist_reviewer_mapping_users_id_fkey FOREIGN KEY (users_id) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-91
ALTER TABLE facilities
  ADD CONSTRAINT facilities_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-92
ALTER TABLE facilities
  ADD CONSTRAINT facilities_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-93
ALTER TABLE user_facilities_mapping
  ADD CONSTRAINT user_facilities_mapping_facilities_id_fkey FOREIGN KEY (facilities_id) REFERENCES facilities (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-94
ALTER TABLE versions
  ADD CONSTRAINT fkdbb5h6chr3a6bvfclwusbbvb0 FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-95
ALTER TABLE versions
  ADD CONSTRAINT fkqhwg81jr2y4pi7jrh82q3dvth FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-96
ALTER TABLE temp_activity_values
  ADD CONSTRAINT temp_activity_values_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-97
ALTER TABLE temp_activity_values
  ADD CONSTRAINT temp_activity_values_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-98
ALTER TABLE codes
  ADD CONSTRAINT codes_organisations_id_type_clause_pk PRIMARY KEY (organisations_id, type, clause);

-- changeset mokshesh:01-seed-schema-99
ALTER TABLE users
  ADD CONSTRAINT users_organisations_id_fkey FOREIGN KEY (organisations_id) REFERENCES organisations (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-100
ALTER TABLE email_templates
  ADD CONSTRAINT email_templates_name_key UNIQUE (name);

-- changeset mokshesh:01-seed-schema-101
ALTER TABLE job_cwe_detail_media_mapping
  ADD CONSTRAINT job_exception_media_mapping_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-102
ALTER TABLE job_cwe_detail_media_mapping
  ADD CONSTRAINT job_exception_media_mapping_medias_id_fkey FOREIGN KEY (medias_id) REFERENCES medias (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-103
ALTER TABLE job_cwe_detail_media_mapping
  ADD CONSTRAINT job_exception_media_mapping_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-104
ALTER TABLE job_cwe_details
  ADD CONSTRAINT job_exceptions_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-105
ALTER TABLE job_cwe_details
  ADD CONSTRAINT job_exceptions_jobs_id_fkey FOREIGN KEY (jobs_id) REFERENCES jobs (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-106
ALTER TABLE job_cwe_details
  ADD CONSTRAINT job_exceptions_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-107
ALTER TABLE job_property_values
  ADD CONSTRAINT job_property_values_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-108
ALTER TABLE job_property_values
  ADD CONSTRAINT job_property_values_jobs_id_fkey FOREIGN KEY (jobs_id) REFERENCES jobs (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-109
ALTER TABLE job_property_values
  ADD CONSTRAINT job_property_values_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-110
ALTER TABLE job_property_values
  ADD CONSTRAINT job_property_values_properties_id_fkey FOREIGN KEY (properties_id) REFERENCES properties (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-111
ALTER TABLE task_executions
  ADD CONSTRAINT task_executions_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-112
ALTER TABLE task_executions
  ADD CONSTRAINT task_executions_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-113
ALTER TABLE task_executions
  ADD CONSTRAINT task_executions_started_by_fkey FOREIGN KEY (started_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-114
ALTER TABLE task_executions
  ADD CONSTRAINT task_executions_tasks_id_fkey FOREIGN KEY (tasks_id) REFERENCES tasks (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-115
ALTER TABLE task_execution_user_mapping
  ADD CONSTRAINT task_execution_user_mapping_task_executions_id_fkey FOREIGN KEY (task_executions_id) REFERENCES task_executions (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-116
ALTER TABLE task_execution_user_mapping
  ADD CONSTRAINT task_execution_user_mapping_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-117
ALTER TABLE task_execution_user_mapping
  ADD CONSTRAINT task_execution_user_mapping_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-118
ALTER TABLE task_execution_user_mapping
  ADD CONSTRAINT task_execution_user_mapping_users_id_fkey FOREIGN KEY (users_id) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-119
ALTER TABLE task_media_mapping
  ADD CONSTRAINT task_media_mapping_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-120
ALTER TABLE task_media_mapping
  ADD CONSTRAINT task_media_mapping_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-121
ALTER TABLE task_media_mapping
  ADD CONSTRAINT task_media_mapping_tasks_id_fkey FOREIGN KEY (tasks_id) REFERENCES tasks (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-122
ALTER TABLE user_facilities_mapping
  ADD CONSTRAINT user_facilities_mapping_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-123
ALTER TABLE user_facilities_mapping
  ADD CONSTRAINT user_facilities_mapping_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-124
ALTER TABLE user_facilities_mapping
  ADD CONSTRAINT user_facilities_mapping_users_id_fkey FOREIGN KEY (users_id) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-125
ALTER TABLE activities
  ADD CONSTRAINT activities_created_by_fkey FOREIGN KEY (created_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-126
ALTER TABLE activities
  ADD CONSTRAINT activities_modified_by_fkey FOREIGN KEY (modified_by) REFERENCES users (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-127
ALTER TABLE activities
  ADD CONSTRAINT activities_tasks_id_fkey FOREIGN KEY (tasks_id) REFERENCES tasks (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-128
ALTER TABLE activity_value_media_mapping
  ADD CONSTRAINT activity_value_media_mapping_activity_values_id_fkey FOREIGN KEY (activity_values_id) REFERENCES activity_values (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-129
ALTER TABLE activity_value_media_mapping
  ADD CONSTRAINT activity_value_media_mapping_medias_id_fkey FOREIGN KEY (medias_id) REFERENCES medias (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-130
ALTER TABLE activity_values
  ADD CONSTRAINT activity_values_jobs_id_fkey FOREIGN KEY (jobs_id) REFERENCES jobs (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-131
ALTER TABLE checklists
  ADD CONSTRAINT checklists_facilities_id_fkey FOREIGN KEY (facilities_id) REFERENCES facilities (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-132
ALTER TABLE checklists
  ADD CONSTRAINT checklists_versions_id_fkey FOREIGN KEY (versions_id) REFERENCES versions (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-133
ALTER TABLE temp_activity_value_media_mapping
  ADD CONSTRAINT temp_activity_value_media_mapping_temp_activity_values_id_fkey FOREIGN KEY (temp_activity_values_id) REFERENCES temp_activity_values (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-134
ALTER TABLE checklist_property_values
  ADD CONSTRAINT checklist_property_values_properties_id_fkey FOREIGN KEY (properties_id) REFERENCES properties (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-135
ALTER TABLE checklist_reviewer_comments
  ADD CONSTRAINT checklist_reviewer_comments_checklist_reviewer_mappings_id_fkey FOREIGN KEY (checklist_reviewer_mappings_id) REFERENCES checklist_reviewer_mapping (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-136
ALTER TABLE facilities
  ADD CONSTRAINT facilities_organisations_id_fkey FOREIGN KEY (organisations_id) REFERENCES organisations (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-137
ALTER TABLE codes
  ADD CONSTRAINT codes_organisations_id_fkey FOREIGN KEY (organisations_id) REFERENCES organisations (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset mokshesh:01-seed-schema-138
ALTER TABLE job_cwe_detail_media_mapping
  ADD CONSTRAINT job_exception_media_mapping_job_exceptions_id_fkey FOREIGN KEY (job_cwe_details_id) REFERENCES job_cwe_details (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

