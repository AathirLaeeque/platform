-- liquibase formatted sql

-- changeset mokshesh:04-seed-schema-1
ALTER TABLE properties
  ALTER COLUMN organisations_id SET NOT NULL;

-- changeset mokshesh:04-seed-schema-2
DROP TABLE addresses;

-- changeset mokshesh:04-seed-schema-3
DROP TABLE user_facilities_mapping;

-- changeset mokshesh:04-seed-schema-4
ALTER TABLE users
  ALTER COLUMN email DROP NOT NULL;

-- changeset sathyam:04-seed-schema-5
CREATE TABLE activity_media_mapping (
    medias_id BIGINT NOT NULL,
    activities_id BIGINT NOT NULL,
    archived BOOLEAN DEFAULT FALSE,
    created_at BIGINT NOT NULL,
    modified_at BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    modified_by BIGINT NOT NULL,
    PRIMARY KEY (medias_id, activities_id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (modified_by) REFERENCES users(id),
    FOREIGN KEY (medias_id) REFERENCES medias(id),
    FOREIGN KEY (activities_id) REFERENCES activities(id)
);
