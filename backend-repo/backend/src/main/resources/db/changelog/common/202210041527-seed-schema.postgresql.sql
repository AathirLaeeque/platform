-- liquibase formatted sql

-- changeset sathyam:202210041527-seed-schema-1
-- comment drop existing and add foreign key constraint to jobs_id column of relation_values table
ALTER TABLE relation_values DROP CONSTRAINT IF EXISTS fk88e5jno0mr3sxaj3cfui;
ALTER TABLE relation_values ADD CONSTRAINT fk88e5jno0mr3sxaj3cfui FOREIGN KEY (jobs_id) REFERENCES jobs (id) ON UPDATE NO ACTION ON DELETE NO ACTION;
