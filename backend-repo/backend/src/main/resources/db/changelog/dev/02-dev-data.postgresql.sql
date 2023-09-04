-- liquibase formatted sql

--changeset mokshesh:02-dev-data-1
--comment: adding organisation: Leucine II
INSERT INTO organisations (id, created_at, modified_at, archived, name)
VALUES (1643103402, 1643103402, 1643103402, false, 'Leucine II');

--changeset mokshesh:02-dev-data-2
--comment: adding facility: New York, London & Sydney for organisation: Leucine II
INSERT INTO facilities (id, archived, name, organisations_id, created_at, modified_at)
VALUES (1643103501, false, 'New York', 1643103402, 1643103501, 1643103501),
       (1643103502, false, 'London', 1643103402, 1643103502, 1643103502),
       (1643103503, false, 'Sydney', 1643103402, 1643103503, 1643103503);

--changeset mokshesh:02-dev-data-3
--comment: adding users for organisation: Leucine II
INSERT INTO users (id, created_at, modified_at, organisations_id, employee_id, email, first_name, last_name, archived)
VALUES (1643103300, 1643103300, 1643103300, 1643103402, 'L-account.owner.02', 'a0.02@mailinator.com', 'Mustaq', 'L', false);

