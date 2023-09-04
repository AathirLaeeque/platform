-- liquibase formatted sql

-- changeset mokshesh:202202221615-seed-schema-1
-- comment rename the existing properties table
ALTER TABLE properties
  RENAME TO _properties;
ALTER INDEX properties_pkey RENAME TO _properties_pkey;
ALTER TABLE checklist_property_values
  DROP CONSTRAINT checklist_property_values_properties_id_fkey;
ALTER TABLE job_property_values
  DROP CONSTRAINT job_property_values_properties_id_fkey;

-- changeset mokshesh:202202221615-seed-schema-2
-- comment adding use_cases table
CREATE TABLE use_cases
(
  id          BIGINT  NOT NULL,
  "name"      VARCHAR NOT NULL,
  "label"     VARCHAR NOT NULL,
  description TEXT    NULL,
  order_tree  INTEGER NOT NULL DEFAULT 1,
  metadata    JSONB            DEFAULT '{}' NOT NULL,
  archived    BOOLEAN NOT NULL DEFAULT false,
  created_by  BIGINT  NOT NULL,
  created_at  BIGINT  NOT NULL,
  modified_by BIGINT  NOT NULL,
  modified_at BIGINT  NOT NULL,
  CONSTRAINT use_cases_pkey PRIMARY KEY (id)
);

ALTER TABLE use_cases
  ADD CONSTRAINT fkifr0gtg19c3bkwu7lghkndv24 FOREIGN KEY (created_by) REFERENCES users (id);
ALTER TABLE use_cases
  ADD CONSTRAINT fkn0xcvq0q66gj74x6ixkrw2tbh FOREIGN KEY (modified_by) REFERENCES users (id);

-- changeset mokshesh:202202221615-seed-schema-3
-- comment adding facility_use_case_mapping table
CREATE TABLE facility_use_case_mapping
(
  facilities_id BIGINT  NOT NULL,
  use_cases_id  BIGINT  NOT NULL,
  quota         INTEGER NOT NULL,
  created_by    BIGINT  NOT NULL,
  created_at    BIGINT  NOT NULL,
  modified_by   BIGINT  NOT NULL,
  modified_at   BIGINT  NOT NULL,
  CONSTRAINT facility_use_case_mapping_pkey PRIMARY KEY (facilities_id, use_cases_id)
);

ALTER TABLE facility_use_case_mapping
  ADD CONSTRAINT fkmdp8bontdxf9nuufyungm1v8i FOREIGN KEY (use_cases_id) REFERENCES use_cases (id);
ALTER TABLE facility_use_case_mapping
  ADD CONSTRAINT fkmhw8nww3l5m0migssf6meqm3g FOREIGN KEY (modified_by) REFERENCES users (id);
ALTER TABLE facility_use_case_mapping
  ADD CONSTRAINT fkmunw7et2lqi97c9abgfjoi55y FOREIGN KEY (created_by) REFERENCES users (id);
ALTER TABLE facility_use_case_mapping
  ADD CONSTRAINT fkr9ppwim7ovgscgsd7jjw9aj79 FOREIGN KEY (facilities_id) REFERENCES facilities (id);
CREATE INDEX idx0f942d5072b145919fe72e2f ON facility_use_case_mapping (facilities_id);

-- changeset mokshesh:202202221615-seed-schema-4
-- comment add new properties table
CREATE TABLE properties
(
  id           BIGINT       NOT NULL,
  use_cases_id BIGINT       NULL,
  name         VARCHAR(255) NOT NULL,
  label        VARCHAR(255) NOT NULL,
  place_holder VARCHAR(255) NOT NULL,
  order_tree   INTEGER      NOT NULL DEFAULT 1,
  is_global    BOOLEAN      NOT NULL DEFAULT false,
  "type"       VARCHAR      NOT NULL,
  archived     BOOLEAN      NOT NULL DEFAULT false,
  created_by   BIGINT       NOT NULL,
  created_at   BIGINT       NOT NULL,
  modified_by  BIGINT       NOT NULL,
  modified_at  BIGINT       NOT NULL,
  CONSTRAINT properties_pkey PRIMARY KEY (id)
);

ALTER TABLE properties
  ADD CONSTRAINT fkd8ome4g3150hd4tgdn025pwwd FOREIGN KEY (modified_by) REFERENCES users (id);
ALTER TABLE properties
  ADD CONSTRAINT fke03f23ddj4konwk1tiggla9df FOREIGN KEY (created_by) REFERENCES users (id);
ALTER TABLE properties
  ADD CONSTRAINT fkfye648rlsr9rrj83enrkq280j FOREIGN KEY (use_cases_id) REFERENCES use_cases (id);

-- changeset mokshesh:202202221615-seed-schema-5
-- comment adding facility_use_case_property_mapping table
CREATE TABLE facility_use_case_property_mapping
(
  id                 BIGINT       NOT NULL,
  facilities_id      BIGINT       NOT NULL,
  use_cases_id       BIGINT       NOT NULL,
  properties_id      BIGINT       NOT NULL,
  label_alias        VARCHAR(255) NOT NULL,
  place_holder_alias VARCHAR(512) NOT NULL,
  order_tree         INTEGER      NOT NULL DEFAULT 1,
  is_mandatory       BOOLEAN      NOT NULL DEFAULT false,
  created_by         BIGINT       NOT NULL,
  created_at         BIGINT       NOT NULL,
  modified_by        BIGINT       NOT NULL,
  modified_at        BIGINT       NOT NULL,
  CONSTRAINT facility_use_case_property_mapping_pkey PRIMARY KEY (id)
);

ALTER TABLE facility_use_case_property_mapping
  ADD CONSTRAINT fk8spfk8n8uad5qpficou8y3ao7 FOREIGN KEY (facilities_id) REFERENCES facilities (id);
ALTER TABLE facility_use_case_property_mapping
  ADD CONSTRAINT fkk1676ah17n165rfssprxc6s84 FOREIGN KEY (use_cases_id) REFERENCES use_cases (id);
ALTER TABLE facility_use_case_property_mapping
  ADD CONSTRAINT fkg630bso374kul0o89qaqwhnpm FOREIGN KEY (properties_id) REFERENCES properties (id);
ALTER TABLE facility_use_case_property_mapping
  ADD CONSTRAINT fkgg8x2xl0m8ftiyhvpftxc2bhx FOREIGN KEY (modified_by) REFERENCES users (id);
ALTER TABLE facility_use_case_property_mapping
  ADD CONSTRAINT fklvour4x03vx0rfepsvacqaf9h FOREIGN KEY (created_by) REFERENCES users (id);
ALTER TABLE facility_use_case_property_mapping
  ADD CONSTRAINT unqb9f98ab5cb984e9d913b10d8 UNIQUE (facilities_id, use_cases_id, properties_id);
CREATE INDEX idxde693d8b33bc41baaac62ded ON facility_use_case_property_mapping (facilities_id, use_cases_id);

-- changeset mokshesh:202202221615-seed-schema-6
-- comment adding use case id column to checklist table
ALTER TABLE checklists
  ADD COLUMN use_cases_id BIGINT REFERENCES use_cases (id);

-- changeset mokshesh:202202221615-seed-schema-7
-- comment adding use case id column to jobs table
ALTER TABLE jobs
  ADD COLUMN use_cases_id BIGINT REFERENCES use_cases (id);

-- changeset mokshesh:202202221615-seed-schema-8
-- comment renaming the column properties_id to facility_use_case_property_mapping_id
ALTER TABLE checklist_property_values
  RENAME properties_id to facility_use_case_property_mapping_id;

-- changeset mokshesh:202202221615-seed-schema-9
-- comment renaming the column properties_id to facility_use_case_property_mapping_id
ALTER TABLE job_property_values
  RENAME properties_id to facility_use_case_property_mapping_id;

-- changeset mokshesh:202202221615-seed-schema-10
-- comment drop table _properties
DROP TABLE _properties;
