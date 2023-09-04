-- liquibase formatted sql

-- changeset mokshesh:202203110844-dev-data-1
-- comment enabling Cleaning, Line Clearance and Area Clearance use cases at facility: New York, organisation: Leucine II
INSERT INTO facility_use_case_mapping (facilities_id, use_cases_id, quota, created_by, created_at, modified_by, modified_at)
VALUES (1643103501, 1644232801, 0, 1, 1643103501, 1, 1643103501),
       (1643103501, 1644232802, 0, 1, 1643103501, 1, 1643103501),
       (1643103501, 1644232803, 0, 1, 1643103501, 1, 1643103501);

-- changeset mokshesh:202203110844-dev-data-2
-- comment enabling Cleaning use case related CHECKLIST properties at facility: New York, organisation: Leucine II
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645250102, 1643103501, 1644232801, 1645580102, 'Change Control Reference', 'Change Control Reference', 2, false, 1, 0, 1, 0),
       (1645250103, 1643103501, 1644232801, 1645580103, 'Document ID', 'Document ID', 3, true, 1, 0, 1, 0);

-- changeset mokshesh:202203110844-dev-data-3
-- comment enabling Cleaning use case related JOB properties at facility: New York, organisation: Leucine II
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645250104, 1643103501, 1644232801, 1645580151, 'Equipment ID', 'Equipment ID', 1, true, 1, 0, 1, 0),
       (1645250105, 1643103501, 1644232801, 1645580152, 'Product Manufactured', 'Product Manufactured', 2, true, 1, 0, 1, 0),
       (1645250106, 1643103501, 1644232801, 1645580153, 'Batch No', 'Batch No', 3, true, 1, 0, 1, 0),
       (1645250107, 1643103501, 1644232801, 1645580154, 'Room ID', 'Room ID', 4, false, 1, 0, 1, 0),
       (1645250109, 1643103501, 1644232801, 1645580156, 'Capacity', 'Capacity', 6, true, 1, 0, 1, 0);

-- changeset mokshesh:202203110844-dev-data-4
-- comment enabling Line Clearance use case related CHECKLIST properties at facility: New York, organisation: Leucine II
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645250110, 1643103501, 1644232802, 1645580201, 'Manufacturing Stage', 'Manufacturing Stage', 1, true, 1, 0, 1, 0),
       (1645250111, 1643103501, 1644232802, 1645580202, 'Change Control Reference', 'Change Control Reference', 2, false, 1, 0, 1, 0),
       (1645250112, 1643103501, 1644232802, 1645580203, 'Document ID', 'Document ID', 3, true, 1, 0, 1, 0);

-- changeset mokshesh:202203110844-dev-data-5
-- comment enabling Line Clearance use case related JOB properties at facility: New York, organisation: Leucine II
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645250113, 1643103501, 1644232802, 1645580251, 'Product Manufactured', 'Product Manufactured', 1, true, 1, 0, 1, 0),
       (1645250114, 1643103501, 1644232802, 1645580252, 'Batch Number', 'Batch Number', 2, true, 1, 0, 1, 0),
       (1645250115, 1643103501, 1644232802, 1645580253, 'Area ID / Room ID', 'Area ID / Room ID', 3, true, 1, 0, 1, 0);

-- changeset mokshesh:202203110844-dev-data-6
-- comment enabling Area Clearance use case related CHECKLIST properties at facility: New York, organisation: Leucine II
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645250116, 1643103501, 1644232803, 1645580301, 'Manufacturing Stage', 'Manufacturing Stage', 1, true, 1, 0, 1, 0),
       (1645250117, 1643103501, 1644232803, 1645580302, 'Change Control Reference', 'Change Control Reference', 2, false, 1, 0, 1, 0),
       (1645250118, 1643103501, 1644232803, 1645580303, 'Document ID', 'Document ID', 3, true, 1, 0, 1, 0);

-- changeset mokshesh:202203110844-dev-data-7
-- comment enabling Area Clearance use case related JOB properties at facility: New York, organisation: Leucine II
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645250119, 1643103501, 1644232803, 1645580351, 'Product Manufactured', 'Product Manufactured', 1, true, 1, 0, 1, 0),
       (1645250120, 1643103501, 1644232803, 1645580352, 'Batch Number', 'Batch Number', 2, true, 1, 0, 1, 0);

-- changeset mokshesh:202203110844-dev-data-8
-- comment enabling Cleaning and Line Clearance use cases at facility: London, organisation: Leucine II
INSERT INTO facility_use_case_mapping (facilities_id, use_cases_id, quota, created_by, created_at, modified_by, modified_at)
VALUES (1643103502, 1644232801, 0, 1, 1643103502, 1, 1643103502),
       (1643103502, 1644232802, 0, 1, 1643103502, 1, 1643103502);

-- changeset mokshesh:202203110844-dev-data-9
-- comment enabling Cleaning use case related CHECKLIST properties at facility: London, organisation: Leucine II
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645250221, 1643103502, 1644232801, 1645580101, 'Type of Equipment', 'Type of Equipment. e.g. Blender', 1, true, 1, 0, 1, 0),
       (1645250222, 1643103502, 1644232801, 1645580102, 'Change Control Reference', 'Change Control Reference', 2, false, 1, 0, 1, 0),
       (1645250223, 1643103502, 1644232801, 1645580103, 'Document ID', 'Document ID', 3, true, 1, 0, 1, 0);

-- changeset mokshesh:202203110844-dev-data-10
-- comment enabling Cleaning use case related JOB properties at facility: London, organisation: Leucine II
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645250224, 1643103502, 1644232801, 1645580151, 'Equipment ID', 'Equipment ID', 1, true, 1, 0, 1, 0),
       (1645250225, 1643103502, 1644232801, 1645580152, 'Product Manufactured', 'Product Manufactured', 2, true, 1, 0, 1, 0),
       (1645250226, 1643103502, 1644232801, 1645580153, 'Batch No', 'Batch No', 3, true, 1, 0, 1, 0),
       (1645250227, 1643103502, 1644232801, 1645580154, 'Room ID', 'Room ID', 4, false, 1, 0, 1, 0),
       (1645250228, 1643103502, 1644232801, 1645580155, 'Block ID', 'Block ID', 5, true, 1, 0, 1, 0),
       (1645250229, 1643103502, 1644232801, 1645580156, 'Capacity', 'Capacity', 6, true, 1, 0, 1, 0);

-- changeset mokshesh:202203110844-dev-data-11
-- comment enabling Line Clearance use case related CHECKLIST properties at facility: London, organisation: Leucine II
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645250130, 1643103502, 1644232802, 1645580201, 'Manufacturing Stage', 'Manufacturing Stage', 1, true, 1, 0, 1, 0),
       (1645250131, 1643103502, 1644232802, 1645580202, 'Change Control Reference', 'Change Control Reference', 2, false, 1, 0, 1, 0),
       (1645250132, 1643103502, 1644232802, 1645580203, 'Document ID', 'Document ID', 3, true, 1, 0, 1, 0);

-- changeset mokshesh:202203110844-dev-data-12
-- comment enabling Line Clearance use case related JOB properties at facility: London, organisation: Leucine II
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645250133, 1643103502, 1644232802, 1645580251, 'Product Manufactured', 'Product Manufactured', 1, true, 1, 0, 1, 0),
       (1645250134, 1643103502, 1644232802, 1645580252, 'Batch Number', 'Batch Number', 2, true, 1, 0, 1, 0),
       (1645250135, 1643103502, 1644232802, 1645580253, 'Area ID / Room ID', 'Area ID / Room ID', 3, true, 1, 0, 1, 0);

-- changeset mokshesh:202203110844-dev-data-13
-- comment enabling Cleaning use case at facility: Sydney, organisation: Leucine II
INSERT INTO facility_use_case_mapping (facilities_id, use_cases_id, quota, created_by, created_at, modified_by, modified_at)
VALUES (1643103503, 1644232801, 0, 1, 1643103502, 1, 1643103502);

-- changeset mokshesh:202203110844-dev-data-14
-- comment enabling Cleaning use case related CHECKLIST properties at facility: Sydney, organisation: Leucine II
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645250136, 1643103503, 1644232801, 1645580101, 'Type of Equipment', 'Type of Equipment. e.g. Blender', 1, true, 1, 0, 1, 0),
       (1645250137, 1643103503, 1644232801, 1645580102, 'Change Control Reference', 'Change Control Reference', 2, false, 1, 0, 1, 0),
       (1645250138, 1643103503, 1644232801, 1645580103, 'Document ID', 'Document ID', 3, true, 1, 0, 1, 0);

-- changeset mokshesh:202203110844-dev-data-15
-- comment enabling Cleaning use case related JOB properties at facility: Sydney, organisation: Leucine II
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645250139, 1643103503, 1644232801, 1645580151, 'Equipment ID', 'Equipment ID', 1, true, 1, 0, 1, 0),
       (1645250140, 1643103503, 1644232801, 1645580152, 'Product Manufactured', 'Product Manufactured', 2, true, 1, 0, 1, 0),
       (1645250141, 1643103503, 1644232801, 1645580153, 'Batch No', 'Batch No', 3, true, 1, 0, 1, 0),
       (1645250142, 1643103503, 1644232801, 1645580154, 'Room ID', 'Room ID', 4, false, 1, 0, 1, 0),
       (1645250143, 1643103503, 1644232801, 1645580155, 'Block ID', 'Block ID', 5, true, 1, 0, 1, 0),
       (1645250144, 1643103503, 1644232801, 1645580156, 'Capacity', 'Capacity', 6, true, 1, 0, 1, 0);
