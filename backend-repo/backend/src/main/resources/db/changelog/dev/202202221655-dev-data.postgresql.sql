-- liquibase formatted sql

-- changeset mokshesh:202202221655-dev-data-1
-- comment enabling Cleaning, Line Clearance and Area Clearance use cases at facility: Mumbai, organisation: Leucine I, organisation: Leucine I
INSERT INTO facility_use_case_mapping (facilities_id, use_cases_id, quota, created_by, created_at, modified_by, modified_at)
VALUES (1616367802, 1644232801, 0, 1, 1616367802, 1, 1616367802),
       (1616367802, 1644232802, 0, 1, 1616367802, 1, 1616367802),
       (1616367802, 1644232803, 0, 1, 1616367802, 1, 1616367802);

-- changeset mokshesh:202202221655-dev-data-2
-- comment enabling Cleaning use case related CHECKLIST properties at facility: Mumbai, organisation: Leucine I
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645280201, 1616367802, 1644232801, 1645580101, 'Type of Equipment', 'Type of Equipment. e.g. Blender', 1, true, 1, 0, 1, 0),
       (1645280202, 1616367802, 1644232801, 1645580102, 'Change Control Reference', 'Change Control Reference', 2, true, 1, 0, 1, 0),
       (1645280203, 1616367802, 1644232801, 1645580103, 'Document ID', 'Document ID', 3, true, 1, 0, 1, 0);

-- changeset mokshesh:202202221655-dev-data-3
-- comment enabling Cleaning use case related JOB properties at facility: Mumbai, organisation: Leucine I
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645280204, 1616367802, 1644232801, 1645580151, 'Equipment ID', 'Equipment ID', 1, true, 1, 0, 1, 0),
       (1645280205, 1616367802, 1644232801, 1645580152, 'Product Manufactured', 'Product Manufactured', 2, true, 1, 0, 1, 0),
       (1645280206, 1616367802, 1644232801, 1645580153, 'Batch No', 'Batch No', 3, true, 1, 0, 1, 0),
       (1645280207, 1616367802, 1644232801, 1645580154, 'Room ID', 'Room ID', 4, false, 1, 0, 1, 0),
       (1645280208, 1616367802, 1644232801, 1645580155, 'Block ID', 'Block ID', 5, true, 1, 0, 1, 0),
       (1645280209, 1616367802, 1644232801, 1645580156, 'Capacity', 'Capacity', 6, true, 1, 0, 1, 0);

-- changeset mokshesh:202202221655-dev-data-4
-- comment enabling Line Clearance use case related CHECKLIST properties at facility: Mumbai, organisation: Leucine I
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645280210, 1616367802, 1644232802, 1645580201, 'Manufacturing Stage', 'Manufacturing Stage', 1, true, 1, 0, 1, 0),
       (1645280211, 1616367802, 1644232802, 1645580202, 'Change Control Reference', 'Change Control Reference', 2, true, 1, 0, 1, 0),
       (1645280212, 1616367802, 1644232802, 1645580203, 'Document ID', 'Document ID', 3, true, 1, 0, 1, 0);

-- changeset mokshesh:202202221655-dev-data-5
-- comment enabling Line Clearance use case related JOB properties at facility: Mumbai, organisation: Leucine I
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645280213, 1616367802, 1644232802, 1645580251, 'Product Manufactured', 'Product Manufactured', 1, true, 1, 0, 1, 0),
       (1645280214, 1616367802, 1644232802, 1645580252, 'Batch Number', 'Batch Number', 2, true, 1, 0, 1, 0),
       (1645280215, 1616367802, 1644232802, 1645580253, 'Area ID / Room ID', 'Area ID / Room ID', 3, true, 1, 0, 1, 0);

-- changeset mokshesh:202202221655-dev-data-6
-- comment enabling Area Clearance use case related CHECKLIST properties at facility: Mumbai, organisation: Leucine I
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645280216, 1616367802, 1644232803, 1645580301, 'Manufacturing Stage', 'Manufacturing Stage', 1, true, 1, 0, 1, 0),
       (1645280217, 1616367802, 1644232803, 1645580302, 'Change Control Reference', 'Change Control Reference', 2, true, 1, 0, 1, 0),
       (1645280218, 1616367802, 1644232803, 1645580303, 'Document ID', 'Document ID', 3, true, 1, 0, 1, 0);

-- changeset mokshesh:202202221655-dev-data-7
-- comment enabling Area Clearance use case related JOB properties at facility: Mumbai, organisation: Leucine I
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645280219, 1616367802, 1644232803, 1645580351, 'Product Manufactured', 'Product Manufactured', 1, true, 1, 0, 1, 0),
       (1645280220, 1616367802, 1644232803, 1645580352, 'Batch Number', 'Batch Number', 2, true, 1, 0, 1, 0);

-- changeset mokshesh:202202221655-dev-data-8
-- comment enabling Cleaning and Line Clearance use cases at facility: Bangalore, organisation: Leucine I
INSERT INTO facility_use_case_mapping (facilities_id, use_cases_id, quota, created_by, created_at, modified_by, modified_at)
VALUES (1616367801, 1644232801, 0, 1, 1616367801, 1, 1616367801),
       (1616367801, 1644232802, 0, 1, 1616367801, 1, 1616367801);

-- changeset mokshesh:202202221655-dev-data-9
-- comment enabling Cleaning use case related CHECKLIST properties at facility: Bangalore, organisation: Leucine I
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645280221, 1616367801, 1644232801, 1645580101, 'Type of Equipment', 'Type of Equipment. e.g. Blender', 1, true, 1, 0, 1, 0),
       (1645280222, 1616367801, 1644232801, 1645580102, 'Change Control Reference', 'Change Control Reference', 2, true, 1, 0, 1, 0),
       (1645280223, 1616367801, 1644232801, 1645580103, 'Document ID', 'Document ID', 3, true, 1, 0, 1, 0);

-- changeset mokshesh:202202221655-dev-data-10
-- comment enabling Cleaning use case related JOB properties at facility: Bangalore, organisation: Leucine I
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645280224, 1616367801, 1644232801, 1645580151, 'Equipment ID', 'Equipment ID', 1, true, 1, 0, 1, 0),
       (1645280225, 1616367801, 1644232801, 1645580152, 'Product Manufactured', 'Product Manufactured', 2, true, 1, 0, 1, 0),
       (1645280226, 1616367801, 1644232801, 1645580153, 'Batch No', 'Batch No', 3, true, 1, 0, 1, 0),
       (1645280227, 1616367801, 1644232801, 1645580154, 'Room ID', 'Room ID', 4, false, 1, 0, 1, 0),
       (1645280228, 1616367801, 1644232801, 1645580155, 'Block ID', 'Block ID', 5, true, 1, 0, 1, 0),
       (1645280229, 1616367801, 1644232801, 1645580156, 'Capacity', 'Capacity', 6, true, 1, 0, 1, 0);

-- changeset mokshesh:202202221655-dev-data-11
-- comment enabling Line Clearance use case related CHECKLIST properties at facility: Bangalore, organisation: Leucine I
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645280230, 1616367801, 1644232802, 1645580201, 'Manufacturing Stage', 'Manufacturing Stage', 1, true, 1, 0, 1, 0),
       (1645280231, 1616367801, 1644232802, 1645580202, 'Change Control Reference', 'Change Control Reference', 2, true, 1, 0, 1, 0),
       (1645280232, 1616367801, 1644232802, 1645580203, 'Document ID', 'Document ID', 3, true, 1, 0, 1, 0);

-- changeset mokshesh:202202221655-dev-data-12
-- comment enabling Line Clearance use case related JOB properties at facility: Bangalore, organisation: Leucine I
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645280233, 1616367801, 1644232802, 1645580251, 'Product Manufactured', 'Product Manufactured', 1, true, 1, 0, 1, 0),
       (1645280234, 1616367801, 1644232802, 1645580252, 'Batch Number', 'Batch Number', 2, true, 1, 0, 1, 0),
       (1645280235, 1616367801, 1644232802, 1645580253, 'Area ID / Room ID', 'Area ID / Room ID', 3, true, 1, 0, 1, 0);

-- changeset mokshesh:202202221655-dev-data-13
-- comment enabling Cleaning use case at facility: Delhi, organisation: Leucine I
INSERT INTO facility_use_case_mapping (facilities_id, use_cases_id, quota, created_by, created_at, modified_by, modified_at)
VALUES (1616367803, 1644232801, 0, 1, 1616367801, 1, 1616367801);

-- changeset mokshesh:202202221655-dev-data-14
-- comment enabling Cleaning use case related CHECKLIST properties at facility: Delhi, organisation: Leucine I
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645280236, 1616367803, 1644232801, 1645580101, 'Type of Equipment', 'Type of Equipment. e.g. Blender', 1, true, 1, 0, 1, 0),
       (1645280237, 1616367803, 1644232801, 1645580102, 'Change Control Reference', 'Change Control Reference', 2, true, 1, 0, 1, 0),
       (1645280238, 1616367803, 1644232801, 1645580103, 'Document ID', 'Document ID', 3, true, 1, 0, 1, 0);

-- changeset mokshesh:202202221655-dev-data-15
-- comment enabling Cleaning use case related JOB properties at facility: Delhi, organisation: Leucine I
INSERT INTO facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
VALUES (1645280239, 1616367803, 1644232801, 1645580151, 'Equipment ID', 'Equipment ID', 1, true, 1, 0, 1, 0),
       (1645280240, 1616367803, 1644232801, 1645580152, 'Product Manufactured', 'Product Manufactured', 2, true, 1, 0, 1, 0),
       (1645280241, 1616367803, 1644232801, 1645580153, 'Batch No', 'Batch No', 3, true, 1, 0, 1, 0),
       (1645280242, 1616367803, 1644232801, 1645580154, 'Room ID', 'Room ID', 4, false, 1, 0, 1, 0),
       (1645280243, 1616367803, 1644232801, 1645580155, 'Block ID', 'Block ID', 5, true, 1, 0, 1, 0),
       (1645280244, 1616367803, 1644232801, 1645580156, 'Capacity', 'Capacity', 6, true, 1, 0, 1, 0);

-- changeset mokshesh:202202221655-dev-data-16
-- comment migrate all the existing CHECKLIST properties to properties of Cleaning use case for organisation: Leucine I
DELETE
FROM checklist_property_values
WHERE facility_use_case_property_mapping_id = 1;

UPDATE checklist_property_values
SET facility_use_case_property_mapping_id = 1645280221
WHERE facility_use_case_property_mapping_id = 2;

UPDATE checklist_property_values
SET facility_use_case_property_mapping_id = 1645280223
WHERE facility_use_case_property_mapping_id = 3;

UPDATE checklist_property_values
SET facility_use_case_property_mapping_id = 1645280222
WHERE facility_use_case_property_mapping_id = 9;
