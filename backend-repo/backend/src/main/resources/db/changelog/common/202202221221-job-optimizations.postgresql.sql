-- liquibase formatted sql

-- changeset mokshesh:1645512243-optimizations-1
--comment: adding index to jobs tables on column organisations_id, facilities_id
CREATE INDEX idxf8b4d5f7c5df4069b2b73652 ON jobs (organisations_id, facilities_id);

-- changeset mokshesh:1645512243-optimizations-2
--comment: adding index to jobs tables on column state
CREATE INDEX idxab176c90496d41cbad3767ae ON jobs (state);
