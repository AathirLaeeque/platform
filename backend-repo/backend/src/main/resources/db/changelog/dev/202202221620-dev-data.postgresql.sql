-- liquibase formatted sql

-- changeset mokshesh:202202221620-dev-data-1
-- comment adding Cleaning use case
INSERT INTO use_cases (id, "name", "label", description, metadata, order_tree, archived, created_by, created_at, modified_by, modified_at)
VALUES (1644232801, 'cleaning', 'Cleaning', 'Cleaning', '{
  "card-color": "#e0eeff"
}', 1, false, 1, 1644232801, 1, 1644232801);

-- changeset mokshesh:202202221620-dev-data-2
-- comment adding Line Clearance use case
INSERT INTO use_cases (id, "name", "label", description, metadata, order_tree, archived, created_by, created_at, modified_by, modified_at)
VALUES (1644232802, 'line-clearance', 'Line Clearance', 'Line Clearance', '{
  "card-color": "#e0e7ff"
}', 2, false, 1, 1644232802, 1, 1644232802);

-- changeset mokshesh:202202221620-dev-data-3
-- comment adding Area Clearance use case
INSERT INTO use_cases (id, "name", "label", description, metadata, order_tree, archived, created_by, created_at, modified_by, modified_at)
VALUES (1644232803, 'area-clearance', 'Area Clearance', 'Area Clearance', '{
  "card-color": "#e9e0ff"
}', 3, false, 1, 1644232803, 1, 1644232803);

-- changeset mokshesh:202202221620-dev-data-4
-- comment adding Cleaning use case related CHECKLIST properties
INSERT INTO properties (id, use_cases_id, name, label, place_holder, order_tree, is_global, "type", archived, created_by, created_at, modified_by, modified_at)
VALUES (1645580101, 1644232801, 'type-of-equipment', 'Type of Equipment', 'Type of Equipment', 1, true, 'CHECKLIST', false, 1, 1645580101, 1, 1645580101),
       (1645580102, 1644232801, 'change-control-reference', 'Change Control Reference', 'Change Control Reference', 2, false, 'CHECKLIST', false, 1, 1645580102, 1, 1645580102),
       (1645580103, 1644232801, 'document-id', 'Document ID', 'Document ID', 3, false, 'CHECKLIST', false, 1, 1645580103, 1, 1645580103);

-- changeset mokshesh:202202221620-dev-data-5
-- comment adding Cleaning use case related JOB properties
INSERT INTO properties (id, use_cases_id, name, label, place_holder, order_tree, is_global, "type", archived, created_by, created_at, modified_by, modified_at)
VALUES (1645580151, 1644232801, 'equipment-id', 'Equipment ID', 'Equipment ID', 1, true, 'JOB', false, 1, 1645580151, 1, 1645580151),
       (1645580152, 1644232801, 'product-manufactured', 'Product Manufactured', 'Product Manufactured', 2, true, 'JOB', false, 1, 1645580152, 1, 1645580152),
       (1645580153, 1644232801, 'batch-number', 'Batch Number', 'Batch Number', 3, true, 'JOB', false, 1, 1645580153, 1, 1645580153),
       (1645580154, 1644232801, 'room-id', 'Room ID', 'Room ID', 4, false, 'JOB', false, 1, 1645580154, 1, 1645580154),
       (1645580155, 1644232801, 'block-id', 'Block ID', 'Block ID', 5, false, 'JOB', false, 1, 1645580155, 1, 1645580155),
       (1645580156, 1644232801, 'capacity', 'Capacity', 'Capacity', 6, false, 'JOB', false, 1, 1645580156, 1, 1645580156);

-- changeset mokshesh:202202221620-dev-data-6
-- comment adding Line Clearance use case related CHECKLIST properties
INSERT INTO properties (id, use_cases_id, name, label, place_holder, order_tree, is_global, "type", archived, created_by, created_at, modified_by, modified_at)
VALUES (1645580201, 1644232802, 'manufacturing-stage', 'Manufacturing Stage', 'Manufacturing Stage', 1, false, 'CHECKLIST', false, 1, 1645580201, 1, 1645580201),
       (1645580202, 1644232802, 'change-control-reference', 'Change Control Reference', 'Change Control Reference', 2, false, 'CHECKLIST', false, 1, 1645580202, 1, 1645580202),
       (1645580203, 1644232802, 'document-id', 'Document ID', 'Document ID', 3, false, 'CHECKLIST', false, 1, 1645580203, 1, 1645580203);

-- changeset mokshesh:202202221620-dev-data-7
-- comment adding Line Clearance use case related JOB properties
INSERT INTO properties (id, use_cases_id, name, label, place_holder, order_tree, is_global, "type", archived, created_by, created_at, modified_by, modified_at)
VALUES (1645580251, 1644232802, 'product-manufactured', 'Product Manufactured', 'Product Manufactured', 1, true, 'JOB', false, 1, 1645580251, 1, 1645580251),
       (1645580252, 1644232802, 'batch-number', 'Batch Number', 'Batch Number', 2, true, 'JOB', false, 1, 1645580252, 1, 1645580252),
       (1645580253, 1644232802, 'area-id-room-id', 'Area ID / Room ID', 'Area ID / Room ID', 3, false, 'JOB', false, 1, 1645580253, 1, 1645580253);

-- changeset mokshesh:202202221620-dev-data-8
-- comment adding Area Clearance use case related CHECKLIST properties
INSERT INTO properties(id, use_cases_id, name, label, place_holder, order_tree, is_global, "type", archived, created_by, created_at, modified_by, modified_at)
VALUES (1645580301, 1644232803, 'manufacturing-stage', 'Manufacturing Stage', 'Manufacturing Stage', 1, false, 'CHECKLIST', false, 1, 1645580301, 1, 1645580301),
       (1645580302, 1644232803, 'change-control-reference', 'Change Control Reference', 'Change Control Reference', 2, false, 'CHECKLIST', false, 1, 1645580302, 1, 1645580302),
       (1645580303, 1644232803, 'document-id', 'Document ID', 'Document ID', 3, false, 'CHECKLIST', false, 1, 1645580303, 1, 1645580303);

-- changeset mokshesh:202202221620-dev-data-9
-- comment adding Area Clearance use case related JOB properties
INSERT INTO properties (id, use_cases_id, name, label, place_holder, order_tree, is_global, "type", archived, created_by, created_at, modified_by, modified_at)
VALUES (1645580351, 1644232803, 'product-manufactured', 'Product Manufactured', 'Product Manufactured', 1, true, 'JOB', false, 1, 1645580351, 1, 1645580351),
       (1645580352, 1644232803, 'batch-number', 'Batch Number', 'Batch Number', 2, true, 'JOB', false, 1, 1645580352, 1, 1645580352),
       (1645580353, 1644232803, 'area-id-room-id', 'Area ID / Room ID', 'Area ID / Room ID', 3, false, 'JOB', false, 1, 1645580353, 1, 1645580353);

