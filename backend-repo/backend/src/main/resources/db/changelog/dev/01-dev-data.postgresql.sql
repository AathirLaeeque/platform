-- liquibase formatted sql

--changeset mokshesh:01-dev-data-1
--comment: adding organisation: Leucine I
INSERT INTO organisations (id, created_at, modified_at, archived, name)
VALUES (1617625401, 1598950845, 1598950845, false, 'Leucine I');

--changeset mokshesh:01-dev-data-2
--comment: adding facility: Bangalore, Mumbai & Delhi  for organisation: Leucine I
INSERT INTO facilities (id, archived, name, organisations_id, created_at, modified_at)
VALUES (1616367801, false, 'Bangalore', 1617625401, 1598950845, 1598950845),
       (1616367802, false, 'Mumbai', 1617625401, 1598950845, 1598950845),
       (1616367803, false, 'Delhi', 1617625401, 1598950845, 1598950845);

--changeset mokshesh:01-dev-data-3
--comment: adding users for organisation: Leucine I
INSERT INTO users (id, created_at, modified_at, organisations_id, employee_id, email, first_name, last_name, archived)
VALUES (1617625400, 1617625400, 1617625400, 1617625401, 'L-account.owner.01', 'ao.01@mailinator.com', 'Vivek', 'L', false),
       (1617625401, 1617625401, 1617625401, 1617625401, 'L-system.admin.01', 'sa.01@mailinator.com', 'Sachin', 'L', false),
       (1617625402, 1617625402, 1617625402, 1617625401, 'L-system.admin.02', 'sa.02@mailinator.com', 'Aisha', 'L', false),
       (1617625403, 1617625403, 1617625403, 1617625401, 'L-system.admin.03', 'sa.03@mailinator.com', 'Amrit', 'L', false),
       (1617625404, 1617625404, 1617625404, 1617625401, 'L-system.admin.04', 'sa.04@mailinator.com', 'Madhu', 'L', false),
       (1617625405, 1617625405, 1617625405, 1617625401, 'L-facility.admin.01', 'fa.01@mailinator.com', 'Pushpa', 'L', false),
       (1617625406, 1617625406, 1617625406, 1617625401, 'L-facility.admin.02', 'fa.02@mailinator.com', 'Anusha', 'L', false),
       (1617625407, 1617625407, 1617625407, 1617625401, 'L-facility.admin.03', 'fa.03@mailinator.com', 'Kajol', 'L', false),
       (1617625408, 1617625408, 1617625408, 1617625401, 'L-facility.admin.04', 'fa.04@mailinator.com', 'Devendra', 'L', false),
       (1617625409, 1617625409, 1617625409, 1617625401, 'L-facility.admin.05', 'fa.05@mailinator.com', 'Dhiraj', 'L', false),
       (1617625410, 1617625410, 1617625410, 1617625401, 'L-facility.admin.06', 'fa.06@mailinator.com', 'Dinesh', 'L', false),
       (1617625411, 1617625411, 1617625411, 1617625401, 'L-facility.admin.07', 'fa.07@mailinator.com', 'Diya', 'L', false),
       (1617625412, 1617625412, 1617625412, 1617625401, 'L-facility.admin.08', 'fa.08@mailinator.com', 'Falguni', 'L', false),
       (1617625413, 1617625413, 1617625413, 1617625401, 'L-supervisor.01', 's.01@mailinator.com', 'Fatima', 'L', false),
       (1617625414, 1617625414, 1617625414, 1617625401, 'L-supervisor.02', 's.02@mailinator.com', 'Rohini', 'L', false),
       (1617625415, 1617625415, 1617625415, 1617625401, 'L-supervisor.03', 's.03@mailinator.com', 'Hema', 'L', false),
       (1617625416, 1617625416, 1617625416, 1617625401, 'L-supervisor.04', 's.04@mailinator.com', 'Hina', 'L', false),
       (1617625417, 1617625417, 1617625417, 1617625401, 'L-supervisor.05', 's.05@mailinator.com', 'Kirti', 'L', false),
       (1617625418, 1617625418, 1617625418, 1617625401, 'L-supervisor.06', 's.06@mailinator.com', 'Kushal', 'L', false),
       (1617625419, 1617625419, 1617625419, 1617625401, 'L-supervisor.07', 's.07@mailinator.com', 'Kusum', 'L', false),
       (1617625420, 1617625420, 1617625420, 1617625401, 'L-supervisor.08', 's.08@mailinator.com', 'Madhavi', 'L', false),
       (1617625421, 1617625421, 1617625421, 1617625401, 'L-supervisor.09', 's.09@mailinator.com', 'Manoj', 'L', false),
       (1617625422, 1617625422, 1617625422, 1617625401, 'L-supervisor.10', 's.10@mailinator.com', 'Marlo', 'L', false),
       (1617625423, 1617625423, 1617625423, 1617625401, 'L-supervisor.11', 's.11@mailinator.com', 'Mohit', 'L', false),
       (1617625424, 1617625424, 1617625424, 1617625401, 'L-supervisor.12', 's.12@mailinator.com', 'Munaf', 'L', false),
       (1617625425, 1617625425, 1617625425, 1617625401, 'L-supervisor.13', 's.13@mailinator.com', 'Naina', 'L', false),
       (1617625426, 1617625426, 1617625426, 1617625401, 'L-supervisor.14', 's.14@mailinator.com', 'Nancy', 'L', false),
       (1617625427, 1617625427, 1617625427, 1617625401, 'L-supervisor.15', 's.15@mailinator.com', 'Nilima', 'L', false),
       (1617625428, 1617625428, 1617625428, 1617625401, 'L-supervisor.16', 's.16@mailinator.com', 'Peter', 'L', false),
       (1617625429, 1617625429, 1617625429, 1617625401, 'L-operator.01', 'o.01@mailinator.com', 'Pranab', 'L', false),
       (1617625430, 1617625430, 1617625430, 1617625401, 'L-operator.02', 'o.02@mailinator.com', 'Radhe', 'L', false),
       (1617625431, 1617625431, 1617625431, 1617625401, 'L-operator.03', 'o.03@mailinator.com', 'Rahim', 'L', false),
       (1617625432, 1617625432, 1617625432, 1617625401, 'L-operator.04', 'o.04@mailinator.com', 'Rajesh', 'L', false),
       (1617625433, 1617625433, 1617625433, 1617625401, 'L-operator.05', 'o.05@mailinator.com', 'Ramesh', 'L', false),
       (1617625434, 1617625434, 1617625434, 1617625401, 'L-operator.06', 'o.06@mailinator.com', 'Rupal', 'L', false),
       (1617625435, 1617625435, 1617625435, 1617625401, 'L-operator.07', 'o.07@mailinator.com', 'Rupesh', 'L', false),
       (1617625436, 1617625436, 1617625436, 1617625401, 'L-operator.08', 'o.08@mailinator.com', 'Savita', 'L', false),
       (1617625437, 1617625437, 1617625437, 1617625401, 'L-operator.09', 'o.09@mailinator.com', 'Shweta', 'L', false),
       (1617625438, 1617625438, 1617625438, 1617625401, 'L-operator.10', 'o.10@mailinator.com', 'Sumit', 'L', false),
       (1617625439, 1617625439, 1617625439, 1617625401, 'L-operator.11', 'o.11@mailinator.com', 'Vaishali', 'L', false),
       (1617625440, 1617625440, 1617625440, 1617625401, 'L-operator.12', 'o.12@mailinator.com', 'Varun', 'L', false);

--changeset mokshesh:01-dev-data-4
--comment: adding properties for organisation: Leucine I
INSERT INTO properties (id, name, place_holder, order_tree, is_mandatory, type, created_at, modified_at, created_by, modified_by, archived, organisations_id)
VALUES (2, 'TYPE OF EQUIPMENT', 'Type of Equipment', 1, true, 'CHECKLIST', 1591661391, 1591661391, 1, 1, false, 1617625401),
       (3, 'SOP NUMBER', 'SOP Number', 3, true, 'CHECKLIST', 1591661391, 1591661391, 1, 1, false, 1617625401),
       (7, 'CAPACITY', 'Capacity', 2, true, 'JOB', 1591661391, 1591661391, 1, 1, false, 1617625401),
       (8, 'BLOCK ID', 'Block ID', 5, true, 'JOB', 1591661391, 1591661391, 1, 1, false, 1617625401),
       (9, 'CHANGE CONTROL REFERENCE', 'Change Control Reference', 2, true, 'CHECKLIST', 1591661391, 1591661391, 1, 1, false, 1617625401),
       (10, 'EQUIPMENT ID', 'Equipment ID', 1, true, 'JOB', 1591661391, 1591661391, 1, 1, false, 1617625401),
       (4, 'PRODUCT MANUFACTURED', 'Product Manufactured', 3, true, 'JOB', 1591661391, 1591661391, 1, 1, false, 1617625401),
       (5, 'BATCH NO', 'Batch No', 4, true, 'JOB', 1591661391, 1591661391, 1, 1, false, 1617625401),
       (6, 'AREA ID', 'Area ID', 6, false, 'JOB', 1591661391, 1591661391, 1, 1, false, 1617625401);

--changeset mokshesh:01-dev-data-5
--comment: adding checklist: Cleaning Checklist (Type-A) of Air Swept Sifter (Single Deck)

INSERT INTO versions (id, created_at, modified_at, deprecated_at, parent, self, type, version, versioned_at, created_by, modified_by, ancestor)
VALUES (1620068208, 1614848022, 1615538495, NULL, NULL, 1620069400, 'CHECKLIST', 1, 1615538495, 1617625405, 1617625405, 1620069400);

INSERT INTO checklists (id, name, code, state, archived, versions_id, facilities_id, created_at, modified_at, created_by, modified_by, review_cycle, released_at, released_by,
                        description)
VALUES (1620069400, 'Cleaning Checklist (Type-A) of Air Swept Sifter (Single Deck)', 'CHK-JAN21-1', 'PUBLISHED', false, 1620068208, 1616367801, 1614848022, 1615538495, 1617625405,
        1617625405, 3, 1615538495, 1617625406, 'Reference PR No.: 491563 (CC/0307/2021/0192)');

INSERT INTO checklist_property_values (value, properties_id, checklists_id, created_at, modified_at, created_by, modified_by)
VALUES ('Air Swept Sifters (Single Deck)', 2, 1620069400, 1614848022, 1614848022, 1617625405, 1617625405),
       ('0307-SOP-MFG-00147', 3, 1620069400, 1614848022, 1614848022, 1617625405, 1617625405),
       ('CCF-21213-', 9, 1620069400, 1614848022, 1614848022, 1617625405, 1617625405);

INSERT INTO stages (id, name, archived, order_tree, checklists_id, created_at, modified_at, created_by, modified_by)
VALUES (1620069401, 'Cleaning Verification by Supervisor', false, 3, 1620069400, 1614848022, 1614848022, 1617625405, 1617625405),
       (1620069402, 'Cleaning Steps', false, 2, 1620069400, 1614848022, 1614848022, 1617625405, 1617625405),
       (1620069403, 'Equipment details', false, 1, 1620069400, 1614848022, 1614848814, 1617625405, 1617625405);

INSERT INTO tasks (id, name, archived, order_tree, has_stop, max_period, min_period, timer_operator, is_timed, is_mandatory, stages_id, created_at, modified_at, created_by,
                   modified_by)
VALUES (1620069404, 'Cleaning Verification by Supervisor', false, 1, false, NULL, NULL, NULL, false, false, 1620069401, 1614848022, 1614848022, 1617625405, 1617625405),
       (1620069405, 'Update the Equipment Sequential Log', false, 2, false, NULL, NULL, NULL, false, false, 1620069402, 1614848022, 1614848022, 1617625405, 1617625405),
       (1620069406, 'Record the Equipment ID', false, 1, true, NULL, NULL, NULL, false, false, 1620069403, 1614848022, 1614848944, 1617625405, 1617625405),
       (1620069407, 'Sifter Cleaning', false, 1, true, NULL, NULL, NULL, false, false, 1620069402, 1614848022, 1614848947, 1617625405, 1617625405);

INSERT INTO activities (id, archived, order_tree, data, label, is_mandatory, type, created_at, modified_at, created_by, modified_by, tasks_id)
VALUES (1620069408, false, 1, '{
  "text": "<p style=\"text-align:start;\"><span style=\"font-size: 14px;font-family: Segoe UI;\">This Cleaning Checklist is for the EquipmentGroup: Air Swept Sifters. Provide the Equipment ID of the Sifter that is being cleaned below.</span></p>\n"
}', '', false, 'INSTRUCTION', 1614848022, 1614848022, 1617625405, 1617625405, 1620069406),
       (1620069409, false, 2, '{}', '', true, 'TEXTBOX', 1614848022, 1614848022, 1617625405, 1617625405, 1620069406),
       (1620069410, false, 1, '[
         {
           "id": "92ec06cf-7742-4145-b4bd-2d639415e4cc",
           "name": "Verify that cleaning is done as per checklist"
         },
         {
           "id": "f1af2e2d-1711-4d97-9f89-3bb5ab225068",
           "name": "Verify that Equipment Sequential Log is updated"
         }
       ]', '', true, 'CHECKLIST', 1614848022, 1614848022, 1617625405, 1617625405, 1620069404),
       (1620069411, false, 2, '{}', '', false, 'TEXTBOX', 1614848022, 1614848022, 1617625405, 1617625405, 1620069404),
       (1620069412, false, 1, '[
         {
           "id": "da2300ec-e8c3-41ad-a1d2-ef2f46d6ea61",
           "name": "Record the details in the “Equipment Sequential Log” 0307-SOP-QA-00067-01."
         }
       ]', '', true, 'CHECKLIST', 1614848022, 1614849538, 1617625405, 1617625405, 1620069405),
       (1620069413, false, 1, '[
         {
           "id": "776a8513-24a2-4ada-a0d7-09a8bd800516",
           "name": "Switch off the main power supply"
         },
         {
           "id": "937b2333-f252-47a2-ac41-2bd07a1d9e0c",
           "name": "Ensure that previous product material is removed from inside the sifter."
         },
         {
           "id": "f4258746-01d2-4b33-a2be-ab5c6a8b3701",
           "name": "Clean the internal and external surface of the Air swept sifter and sieve with dry lint free cloth."
         }
       ]', '', true, 'CHECKLIST', 1614848022, 1614849428, 1617625405, 1617625405, 1620069407);

INSERT INTO checklist_collaborator_mapping (id, created_at, modified_at, order_tree, phase, state, type, created_by, modified_by, checklists_id, users_id, phase_type)
VALUES (1620067690, 1614848022, 1614848022, 1, 1, 'NOT_STARTED', 'PRIMARY_AUTHOR', 1617625405, 1617625405, 1620069400, 1617625405, 'BUILD'),
       (1620069414, 1615363526, 1615363802, 1, 3, 'SIGNED', 'SIGN_OFF_USER', 1617625405, 1617625405, 1620069400, 1617625405, 'SIGN_OFF'),
       (1620069415, 1615363526, 1615363875, 2, 3, 'SIGNED', 'SIGN_OFF_USER', 1617625405, 1617625405, 1620069400, 1617625413, 'SIGN_OFF'),
       (1620069416, 1615363526, 1615368447, 2, 3, 'SIGNED', 'SIGN_OFF_USER', 1617625405, 1617625405, 1620069400, 1617625414, 'SIGN_OFF'),
       (1620069417, 1615363526, 1615368764, 3, 3, 'SIGNED', 'SIGN_OFF_USER', 1617625405, 1617625405, 1620069400, 1617625415, 'SIGN_OFF'),
       (1620069418, 1615270856, 1615286789, 1, 1, 'REQUESTED_NO_CHANGES', 'REVIEWER', 1617625405, 1617625405, 1620069400, 1617625415, 'REVIEW'),
       (1620069419, 1614849707, 1615287447, 1, 1, 'REQUESTED_CHANGES', 'REVIEWER', 1617625405, 1617625405, 1620069400, 1617625414, 'REVIEW'),
       (1620069420, 1614849707, 1615289142, 1, 1, 'REQUESTED_NO_CHANGES', 'REVIEWER', 1617625405, 1617625405, 1620069400, 1617625413, 'REVIEW'),
       (1620069421, 1615289486, 1615296867, 1, 2, 'REQUESTED_NO_CHANGES', 'REVIEWER', 1617625405, 1617625405, 1620069400, 1617625415, 'REVIEW'),
       (1620069422, 1615289486, 1615355187, 1, 2, 'REQUESTED_NO_CHANGES', 'REVIEWER', 1617625405, 1617625405, 1620069400, 1617625414, 'REVIEW'),
       (1620069423, 1615289486, 1615363413, 1, 2, 'REQUESTED_NO_CHANGES', 'REVIEWER', 1617625405, 1617625405, 1620069400, 1617625413, 'REVIEW');

INSERT INTO checklist_collaborator_comments (id, created_at, modified_at, comments, review_state, created_by, modified_by, checklists_id, checklist_collaborator_mappings_id)
VALUES (1620069424, 1614850993, 1614850993, 'All OK, No Comments', 'COMMENTED_OK', 1617625413, 1617625413, 1620069400, 1620069420),
       (1620069425, 1615272305, 1615272305, '<p><span style="color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: 14px;font-family: Nunito, sans-serif;">PR No. to be corrected and New PR No- to be add</span>&nbsp;&nbsp;</p>
', 'COMMENTED_CHANGES', 1617625414, 1617625414, 1620069400, 1620069419),
       (1620069426, 1615286788, 1615286788, 'All OK, No Comments', 'COMMENTED_OK', 1617625415, 1617625415, 1620069400, 1620069418),
       (1620069427, 1615289978, 1615289978, 'All OK, No Comments', 'COMMENTED_OK', 1617625413, 1617625413, 1620069400, 1620069423),
       (1620069428, 1615290254, 1615290254, 'All OK, No Comments', 'COMMENTED_OK', 1617625414, 1617625414, 1620069400, 1620069422),
       (1620069429, 1615296834, 1615296834, 'All OK, No Comments', 'COMMENTED_OK', 1617625415, 1617625415, 1620069400, 1620069421);
--changeset mokshesh:01-dev-data-6
--comment: adding checklist: Super checklist

INSERT INTO versions (id, created_at, modified_at, deprecated_at, parent, self, type, version, versioned_at, created_by, modified_by, ancestor)
VALUES (1620069500, 1614848022, 1615538495, NULL, NULL, 1620069501, 'CHECKLIST', 1, 1615538495, 1617625405, 1617625405, 1620069501);

INSERT INTO checklists (id, name, code, state, archived, versions_id, facilities_id, created_at, modified_at, created_by, modified_by, review_cycle, released_at, released_by,
                        description)
VALUES (1620069501, 'Super Checklist', 'CHK-FEB21-1', 'PUBLISHED', false, 1620069500, 1616367801, 1613906841, 1613967224, 1617625405, 1617625405, 1, 1613967224, 1617625406,
        'Jo yahaan nahi, woh kahin nahi.');

INSERT INTO checklist_property_values (value, properties_id, checklists_id, created_at, modified_at, created_by, modified_by)
VALUES ('God Level', 2, 1620069501, 1614848022, 1614848022, 1617625405, 1617625405),
       ('2197-SOP-MFG-21147', 3, 1620069501, 1614848022, 1614848022, 1617625405, 1617625405),
       ('CCF-21324-', 9, 1620069501, 1614848022, 1614848022, 1617625405, 1617625405);

INSERT INTO stages (id, name, archived, order_tree, checklists_id, created_at, modified_at, created_by, modified_by)
VALUES (1620069502, 'Stops. They help you orchestrate  your elaborate symphonies.', false, 10, 1620069501, 1613909117, 1613909134, 1617625405, 1617625405),
       (1620069503, 'Instructions (Buckle up folks, cause this about to get messy.)', false, 2, 1620069501, 1613906868, 1613907981, 1617625405, 1617625405),
       (1620069504, 'Media
"Why read, when you can see?"
-said every Moviegoer ever', false, 4, 1620069501, 1613908519, 1613910624, 1617625405, 1617625405),
       (1620069505, 'Signatures and Comments (the least and most useful activities imo.)', false, 7, 1620069501, 1613908885, 1613909032, 1617625405, 1617625405),
       (1620069506, 'Timed Tasks. (cause time is the only thing which matters in the end)', false, 9, 1620069501, 1613909055, 1613909179, 1617625405, 1617625405),
       (1620069507, 'Checklists (our bread and butter tbh)', false, 1, 1620069501, 1613906841, 1613909182, 1617625405, 1617625405),
       (1620069508, 'Yes/No (This one is for the aspiring Sith Lords)', false, 8, 1620069501, 1613908914, 1613909222, 1617625405, 1617625405),
       (1620069509, 'Options (Multi-select or Single-Select! We give them all!)', false, 5, 1620069501, 1613908553, 1613908995, 1617625405, 1617625405),
       (1620069510, 'Parameters. (they are critical. You simply can''t mess them up.)', false, 6, 1620069501, 1613908857, 1613909004, 1617625405, 1617625405),
       (1620069511, 'Materials (Get them. Now!)', false, 3, 1620069501, 1613908055, 1613908103, 1617625405, 1617625405);

INSERT INTO tasks (id, name, archived, order_tree, has_stop, max_period, min_period, timer_operator, is_timed, is_mandatory, stages_id, created_at, modified_at, created_by,
                   modified_by)
VALUES (1620069512, 'DO NOT START THIS TASK!

1. First try starting the two tasks below.
2. Then start this task
3. Before completing it, try starting the two tasks below', false, 1, true, NULL, NULL, NULL, false, false, 1620069502, 1613909120, 1613966751, 1617625405, 1617625405),
       (1620069513, 'A task with some comprehensive instructions', false, 1, false, NULL, NULL, NULL, false, false, 1620069503, 1613907047, 1613907142, 1617625405, 1617625405),
       (1620069514, 'Criteria 6/6: In between
Step 1. Enter wrong values. (Edge cases should be accepted)
Step 2. See error messages.
Step 3. Enter right values.', false, 6, false, NULL, NULL, NULL, false, false, 1620069510, 1613911069, 1613914112, 1617625405, 1617625405),
       (1620069515, 'Single Select', false, 2, false, NULL, NULL, NULL, false, false, 1620069509, 1613908854, 1613910842, 1617625405, 1617625405),
       (1620069516, 'Task with required checklist. Complete this normally.', false, 2, false, NULL, NULL, NULL, false, false, 1620069507, 1613906895, 1613915019, 1617625405,
        1617625405),
       (1620069517, 'Multi -Select Required', false, 3, false, NULL, NULL, NULL, false, false, 1620069509, 1613910909, 1613910927, 1617625405, 1617625405),
       (1620069518, 'Criteria 4/6: Less than equal to x

Step 1. Enter wrong values.
Step 2. See error messages.
Step 3. Enter right values.', false, 4, false, NULL, NULL, NULL, false, false, 1620069510, 1613911045, 1613913658, 1617625405, 1617625405),
       (1620069519, 'Criteria 3/6: More than x

Step 1. Enter wrong values.
Step 2. See error messages.
Step 3. Enter right values.', false, 3, false, NULL, NULL, NULL, false, false, 1620069510, 1613911035, 1613913655, 1617625405, 1617625405),
       (1620069520, 'Task with one skippable checklist and one required checklist', false, 3, false, NULL, NULL, NULL, false, false, 1620069507, 1613906954, 1613906971, 1617625405,
        1617625405),
       (1620069521, 'Testing Material activity', false, 1, false, NULL, NULL, NULL, false, false, 1620069511, 1613908105, 1613908114, 1617625405, 1617625405),
       (1620069522, 'Media Images', false, 1, false, NULL, NULL, NULL, false, false, 1620069504, 1613909051, 1613910700, 1617625405, 1617625405),
       (1620069523, 'Task with a skippable checklist. Skip it please.', false, 1, false, NULL, NULL, NULL, false, false, 1620069507, 1613906841, 1613915008, 1617625405,
        1617625405),
       (1620069524, 'Multi Choice Optional', false, 1, false, NULL, NULL, NULL, false, false, 1620069509, 1613908558, 1613910942, 1617625405, 1617625405),
       (1620069525, 'Criteria 2/6: Less than x

Step 1. Enter wrong values.
Step 2. See error messages.
Step 3. Enter right values.', false, 2, false, NULL, NULL, NULL, false, false, 1620069510, 1613911020, 1613913650, 1617625405, 1617625405),
       (1620069526, 'Criteria 5/6: More than equal to x

Step 1. Enter wrong values.
Step 2. See error messages.
Step 3. Enter right values.', false, 5, false, NULL, NULL, NULL, false, false, 1620069510, 1613911057, 1613913661, 1617625405, 1617625405),
       (1620069527, 'Signature and comment required', false, 1, false, NULL, NULL, NULL, false, false, 1620069505, 1613908887, 1613914399, 1617625405, 1617625405),
       (1620069528, 'Criteria 1/6: Equality.

Step 1. Enter wrong values.
Step 2. See error messages.
Step 3. Enter right values.', false, 1, false, NULL, NULL, NULL, false, false, 1620069510, 1613908859, 1613913645, 1617625405, 1617625405),
       (1620069529, 'Complete this Task in 5 seconds. You should see no prompts to enter a reason for late completion', false, 1, false, 10, NULL, 'LESS_THAN', true, false,
        1620069506, 1613966184, 1613966313, 1617625405, 1617625405),
       (1620069530, 'Yes/No', false, 4, false, NULL, NULL, NULL, false, false, 1620069508, 1613914911, 1613914916, 1617625405, 1617625405),
       (1620069531, 'Use this to test approvals (Reject 0 times)

1. Enter wrong value
2. Provide reason
3. Submit
4. Log in as supervisor
5. Give Approval
6. View Audit Logs
', false, 7, false, NULL, NULL, NULL, false, false, 1620069510, 1613913582, 1613914258, 1617625405, 1617625405),
       (1620069532, 'Complete with Exception', false, 5, false, NULL, NULL, NULL, false, false, 1620069508, 1613914930, 1613914938, 1617625405, 1617625405),
       (1620069533, 'Use this to test approvals (Reject 1 time)

1. Enter wrong value
2. Provide reason
3. Submit
4. Log in as supervisor
5. Reject Approval
6. Enter right value
7. Submit
8. View the Audit logs', false, 8, false, NULL, NULL, NULL, false, false, 1620069510, 1613913871, 1613914271, 1617625405, 1617625405),
       (1620069534, 'Use this to test approvals (Reject 2 times)

1. Enter wrong value
2. Provide reason
3. Submit
4. Log in as supervisor
5. Reject Approval
6. Enter wrong value again
7. Submit again with reason
8. Reject Approval
9. Enter right value
10. View the Audit logs', false, 9, false, NULL, NULL, NULL, false, false, 1620069510, 1613913947, 1613914277, 1617625405, 1617625405),
       (1620069535, 'Use this to test approvals (Reject 3 times)

1. Enter wrong value
2. Provide reason
3. Submit
4. Log in as supervisor
5. Reject Approval
6. Enter wrong value again
7. Submit again with reason
8. Reject Approval
9. Enter right value
10. View the Audit logs', false, 10, false, NULL, NULL, NULL, false, false, 1620069510, 1613914030, 1613914288, 1617625405, 1617625405),
       (1620069536, 'Complete this task in 10 seconds. You should see no prompts to enter a reason for late completion', false, 7, false, 10, 5, 'NOT_LESS_THAN', true, false,
        1620069506, 1613966382, 1613966446, 1617625405, 1617625405),
       (1620069537, 'Signatures and comments not required', false, 2, false, NULL, NULL, NULL, false, false, 1620069505, 1613914401, 1613914415, 1617625405, 1617625405),
       (1620069538, 'Yes', false, 1, false, NULL, NULL, NULL, false, false, 1620069508, 1613914835, 1613914839, 1617625405, 1617625405),
       (1620069539, 'Task with another stop

1. First try starting the task below.
2. Then start this task
3. Before completing it, try starting the task below', false, 2, true, NULL, NULL, NULL, false, false, 1620069502, 1613966517, 1613966742, 1617625405, 1617625405),
       (1620069540, 'No', false, 2, false, NULL, NULL, NULL, false, false, 1620069508, 1613914854, 1613914859, 1617625405, 1617625405),
       (1620069541, 'Complete this Task in 15 seconds. You should see a prompt to enter a reason for late completion', false, 2, false, 10, NULL, 'LESS_THAN', true, false,
        1620069506, 1613966199, 1613966284, 1617625405, 1617625405),
       (1620069542, 'Complete this task in 4 seconds. You should see a prompt to enter a reason for early completion', false, 4, false, 10, 5, 'NOT_LESS_THAN', true, false,
        1620069506, 1613966331, 1613966462, 1617625405, 1617625405),
       (1620069543, 'Complete this with Exception', false, 4, false, NULL, NULL, NULL, false, false, 1620069507, 1613914987, 1613915060, 1617625405, 1617625405),
       (1620069544, 'Complete this task in 5 seconds. You should see no prompts to enter a reason for early completion', false, 5, false, 10, 5, 'NOT_LESS_THAN', true, false,
        1620069506, 1613966348, 1613966466, 1617625405, 1617625405),
       (1620069545, 'Yes/No', false, 3, false, NULL, NULL, NULL, false, false, 1620069508, 1613914884, 1613914904, 1617625405, 1617625405),
       (1620069546, 'Complete this task in 7 seconds. You should see no prompts to enter a reason for early/late completion', false, 6, false, 10, 5, 'NOT_LESS_THAN', true, false,
        1620069506, 1613966362, 1613966473, 1617625405, 1617625405),
       (1620069547, 'Complete this task in 15 seconds. You should see a prompts to enter a reason for late completion', false, 8, false, 10, 5, 'NOT_LESS_THAN', true, false,
        1620069506, 1613966396, 1613966479, 1617625405, 1617625405),
       (1620069548, 'Complete this task in 10 seconds. You should see no prompts to enter a reason for late completion', false, 3, false, 10, NULL, 'LESS_THAN', true, false,
        1620069506, 1613966210, 1613966308, 1617625405, 1617625405),
       (1620069549, 'Final Task. You should only be able to start this task after completing the above two tasks in this stage.', false, 3, false, NULL, NULL, NULL, false, false,
        1620069502, 1613966524, 1613966789, 1617625405, 1617625405);

INSERT INTO medias (id, name, original_filename, filename, description, type, relative_path, archived, created_at, modified_at, created_by, modified_by, organisations_id)
VALUES (1620069550, 'Photo 1', 'c62a6fc2-1db5-4039-8797-ff99bf449843.jpg', 'c62a6fc2-1db5-4039-8797-ff99bf449843.jpg', 'You should see a building kind of thing here.',
        'image/jpeg', 'medias/2021/02', false, 1613910458, 1613910458, 1617625405, 1617625405, 1617625401),
       (1620069551, 'Photo 2', 'c62a6fc2-1db5-4039-8797-ff99bf449843.jpg', '98fbce69-a939-4166-99e6-7c844afe0850.jpg', 'You should see a temple kind of thing here', 'image/jpeg',
        'medias/2021/02', false, 1613910480, 1613910480, 1617625405, 1617625405, 1617625401),
       (1620069552, 'A material with an image and any quantity', '0c4cc5f6-8d00-4b6e-9f3f-7bf53e9cf7fb.jpg', '0c4cc5f6-8d00-4b6e-9f3f-7bf53e9cf7fb.jpg', '', 'image/jpeg',
        'medias/2021/02', false, 1613910480, 1613910480, 1617625405, 1617625405, 1617625401),
       (1620069553, 'A material with an image and quantity 5', '60f027ae-fb1f-4d76-a7bb-b43b7f43216e.jpg', '60f027ae-fb1f-4d76-a7bb-b43b7f43216e.jpg', '', 'image/jpeg',
        'medias/2021/02', false, 1613910480, 1613910480, 1617625405, 1617625405, 1617625401);

INSERT INTO task_media_mapping (medias_id, tasks_id, created_at, modified_at, created_by, modified_by)
VALUES (1620069550, 1620069522, 1613910458, 1613910458, 1617625405, 1617625405),
       (1620069551, 1620069522, 1613910480, 1613910480, 1617625405, 1617625405);

INSERT INTO activities (id, archived, order_tree, data, label, is_mandatory, type, created_at, modified_at, created_by, modified_by, tasks_id)
VALUES (1620069552, false, 1, '[
  {
    "id": "b690ec27-a5f2-459f-b13a-32014cf5bb1c",
    "name": "Item 1 with many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many words"
  },
  {
    "id": "11823d80-059d-420c-a79e-1082cc1a090a",
    "name": "Item 2 with many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many more words"
  }
]', '', true, 'CHECKLIST', 1613906907, 1613906949, 1617625405, 1617625405, 1620069516),
       (1620069553, false, 1, '[
         {
           "id": "778a36c4-651f-4087-977e-921f08425fee",
           "name": "Item 1"
         },
         {
           "id": "8f6dbb20-25d0-499d-855e-024123efba88",
           "name": "Item 2"
         }
       ]', '', false, 'CHECKLIST', 1613906882, 1613906893, 1617625405, 1617625405, 1620069523),
       (1620069554, false, 2, '[
         {
           "id": "41ab3ef8-0bf7-43e5-907e-23f73d3916a4",
           "name": "Do item one. Period."
         },
         {
           "id": "49311ebd-82ab-4d70-9177-388bd84763b2",
           "name": "And while you are at it, also do item two. Period."
         }
       ]', '', true, 'CHECKLIST', 1613906979, 1613907031, 1617625405, 1617625405, 1620069520),
       (1620069555, false, 1, '[
         {
           "id": "52c19bc2-9b4a-458d-adb8-8369cf6ba525",
           "name": "Yes",
           "type": "yes"
         },
         {
           "id": "7f0b0b82-1baa-454e-ab42-08d5172cb3bb",
           "name": "No",
           "type": "no"
         }
       ]', 'Select No please, and enter a reason', true, 'YES_NO', 1613914862, 1613914876, 1617625405, 1617625405, 1620069540),
       (1620069556, false, 1, '[
         {
           "id": "c40ed4c5-354a-4c4c-b95f-cdf44b392493",
           "name": "Yes",
           "type": "yes"
         },
         {
           "id": "65e39945-48ec-49d9-a80b-84f61f1f695c",
           "name": "No",
           "type": "no"
         }
       ]', 'Select Yes please', true, 'YES_NO', 1613914843, 1613914853, 1617625405, 1617625405, 1620069538),
       (1620069557, false, 1, '[
         {
           "id": "1a43e168-1295-40dd-83e3-9067191b0e0e",
           "name": "Yes",
           "type": "yes"
         },
         {
           "id": "3fe9fb6d-21b6-49de-88e9-afa7947ee38f",
           "name": "No",
           "type": "no"
         }
       ]', 'Select Yes, then No', true, 'YES_NO', 1613914896, 1613914907, 1617625405, 1617625405, 1620069545),
       (1620069558, false, 1, '[
         {
           "id": "dae82169-9ca1-4cf1-85c4-7da6c4f4f2f2",
           "name": "Yes",
           "type": "yes"
         },
         {
           "id": "0deacaaf-e74f-46c2-8eaf-c44e9ce94985",
           "name": "No",
           "type": "no"
         }
       ]', 'Skip', false, 'YES_NO', 1613914918, 1613914926, 1617625405, 1617625405, 1620069530),
       (1620069559, false, 1, '[
         {
           "id": "a240dc0f-81cb-450b-9eba-487e6f6ba8dd",
           "name": "Select this"
         },
         {
           "id": "2875de71-6a7f-4551-a043-76a357d6b610",
           "name": "Don''t select this."
         }
       ]', '', false, 'CHECKLIST', 1613906977, 1613915037, 1617625405, 1617625405, 1620069520),
       (1620069560, false, 1, '[
         {
           "id": "4fd65d6b-ba6c-4548-9032-6044a52b64a3",
           "name": "Yes",
           "type": "yes"
         },
         {
           "id": "c1d848f1-ff2a-45c1-8656-8fc230be9899",
           "name": "No",
           "type": "no"
         }
       ]', 'Complete this with Exception', true, 'YES_NO', 1613914940, 1613914951, 1617625405, 1617625405, 1620069532),
       (1620069561, false, 1, '[
         {
           "id": "f71e8fc5-28e7-4a11-ab58-1627bce02bf6",
           "name": "Item 1"
         }
       ]', '', true, 'CHECKLIST', 1613966314, 1613966317, 1617625405, 1617625405, 1620069529),
       (1620069562, false, 1, '{
         "text": "<p>This is the beginning of our instruction. It has many, <span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many, many words.</span></p>\n<p></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\"><strong>Then</strong> it <strong>has</strong> some <strong>bold </strong>words <strong>in</strong> the <strong>next</strong> sentence.</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\"><ins>Then</ins> it <ins>has</ins> some <ins>underlined</ins><strong> </strong>words <ins>in</ins> the <ins>third</ins> sentence.</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\"><strong><ins>Incase</ins></strong> you <strong><ins>didn''t</ins></strong> notice, <strong><ins>every</ins></strong> alternate <strong><ins>word</ins></strong> above <strong><ins>was</ins></strong> formatted <strong><ins>specially</ins></strong></span></p>\n<p></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">Then we have lists:</span></p>\n<ul>\n<li><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">Random Item Apple</span></li>\n<li><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">Random Item Banana</span></li>\n<li><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">Random Item Orange</span></li>\n</ul>\n<p></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">Then we have <strong><ins>ordered</ins></strong> lists:</span></p>\n<ol>\n<li><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">HP and the Sorceror''s Stone</span></li>\n<li><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">HP and the Chamber of Secrets</span></li>\n<li><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">HP and the Prisoner of Azkaban</span></li>\n</ol>\n<p></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">Now some symbols to wake you up!</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">✅: Green Tick</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">👁: Eye, but I dont know why?</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">⭐: Star, Yeah Super Mario!</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">🔦: Torch, cause its time to venture into the unknown!</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">🧰: This is a bag</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">⛑: Health Safety Helmet?</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">🥽: Lab glasses for looking like Dexter</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">🧤: Hi! Said the muddy monster.</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">🦺: Safety Suits cause one day we''ll be in Construction too.</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">🚫: Stop</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">⛔: Detour?</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">🚯: I have no clue what this is. If it was tinier, maybe I could see it better.</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">♻: Recycle? Isn''t this always green though?</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">🆘: SOS! Really? What <strong>exactly </strong>do you think do pharma guys do?</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">🚩: Cause Mandir yahin banega</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">⚡: Electrical Hazard</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">🔥: I cannot imagine this being used seriously. Ever.</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">⚠: Somethings wrong, but we dont know what.</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">✋: Stop in the name of the law</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">🗑: Trash can. You know for all the paper waste we generate these days</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">❎: Green Cross? So is this good? or bad? I''m confused.</span></p>\n<p><span style=\"color: rgb(0,0,0);background-color: rgb(255,255,255);font-size: medium;font-family: Nunito, sans-serif;\">🔒: Lock them things now! The walkers are here!</span></p>\n"
       }', '', false, 'INSTRUCTION', 1613907128, 1613907901, 1617625405, 1617625405, 1620069513),
       (1620069563, false, 1, '[
         {
           "id": "174d87f7-50f9-4296-a035-bb5c1e48b5f5",
           "name": "Inme"
         },
         {
           "id": "38548ea9-958c-4211-922d-1b5f1017b722",
           "name": "Se"
         },
         {
           "id": "929aa555-405d-4a07-bd3b-29c4b9d25175",
           "name": "Minimum"
         },
         {
           "id": "19dd1b12-8dd1-43fc-924a-337cb19845ed",
           "name": "ek"
         },
         {
           "id": "93866e90-c6d1-4078-9b65-79e536fdd9d1",
           "name": "toh "
         },
         {
           "id": "6753b915-5a11-4384-b887-7ff6a2feaa1e",
           "name": "lena"
         },
         {
           "id": "5bc659a6-286f-4109-a677-7871a0e787e3",
           "name": "hi "
         },
         {
           "id": "aac693eb-cf0e-4153-a81d-841e0f6d3c87",
           "name": "padega."
         }
       ]', '', true, 'MULTISELECT', 1613910928, 1613910978, 1617625405, 1617625405, 1620069517),
       (1620069564, false, 1, '[
         {
           "id": "7d6bc76b-2d85-4962-af60-6afcebca85fc",
           "name": "A material with no images and any quantity",
           "quantity": 0
         },
         {
           "id": "3f58882e-4b33-428b-8c7f-fbc8ff0a2f4d",
           "mediaId": 1620069552,
           "name": "A material with an image and any quantity",
           "quantity": 0
         },
         {
           "id": "cf3a3ea6-5f0d-4a9b-a8af-341d7be7f1c4",
           "name": "A material with no images but quantity 3",
           "quantity": 3
         },
         {
           "id": "6f87c589-1bc3-4fa4-b57d-1ac8ce75e2cf",
           "mediaId": 1620069553,
           "name": "A material with an image and quantity 5",
           "quantity": 5
         }
       ]', '', false, 'MATERIAL', 1613908116, 1613908506, 1617625405, 1617625405, 1620069521),
       (1620069565, false, 2, '[
         {
           "id": "40580eb6-f84a-4ffa-8119-01af6b3b9be3",
           "name": "Please tick here if the above looked clear and comprehensive. "
         }
       ]', '', true, 'CHECKLIST', 1613907945, 1613907969, 1617625405, 1617625405, 1620069513),
       (1620069566, false, 1, '[
         {
           "id": "3cebc1ac-2217-43de-b2db-3d34cab9f2e5",
           "name": "Ye le lo"
         },
         {
           "id": "6be8889e-ca61-4e6a-841a-bbc57814c2da",
           "name": "Ye bhi le lo"
         },
         {
           "id": "9b1f248d-7d80-4f36-a97b-b6563662082a",
           "name": "Ye bhii le lo"
         },
         {
           "id": "397f7ddf-8d51-4ffa-a26d-1e982584c6fa",
           "name": "Ye bhiii le lo"
         },
         {
           "id": "ee68897f-9622-49e0-b8b6-c2d246e63d45",
           "name": "Ye bhiiii le lo"
         },
         {
           "id": "e5086534-5691-4c3f-a683-3d6344f67b4c",
           "name": "Ya kuch mat lo"
         }
       ]', '', false, 'MULTISELECT', 1613908561, 1613910897, 1617625405, 1617625405, 1620069524),
       (1620069567, false, 2, '[
         {
           "id": "b989b39b-c75f-4fbf-990b-43e76d97d218",
           "name": "Thats all folks!"
         }
       ]', '', true, 'CHECKLIST', 1613908509, 1613908516, 1617625405, 1617625405, 1620069521),
       (1620069568, false, 1, '{}', '', true, 'SIGNATURE', 1613908891, 1613908891, 1617625405, 1617625405, 1620069527),
       (1620069569, false, 1, '[
         {
           "id": "c43692f8-70ed-434b-894c-637cf2db0130",
           "name": "Either this"
         },
         {
           "id": "b52490d7-351a-4d1d-a289-c9520882600d",
           "name": "Or this"
         },
         {
           "id": "ddbdbd34-b170-4551-a8e2-c840aebb9bb5",
           "name": "third. final option. iska alawa aur kuch nahi hai hamare paas."
         }
       ]', '', true, 'SINGLE_SELECT', 1613910836, 1613910870, 1617625405, 1617625405, 1620069515),
       (1620069570, false, 1, '{
         "uom": "Bars",
         "type": "",
         "value": "50",
         "operator": "EQUAL_TO",
         "parameter": "Pressure"
       }', '', true, 'PARAMETER', 1613908861, 1613913506, 1617625405, 1617625405, 1620069528),
       (1620069571, false, 1, '[
         {
           "id": "d961f012-0cc5-4909-8a60-89881da96466",
           "name": "Tick here if you can see them photos nicely."
         }
       ]', '', true, 'CHECKLIST', 1613910703, 1613910748, 1617625405, 1617625405, 1620069522),
       (1620069572, false, 1, '{
         "uom": "Bars",
         "type": "",
         "value": "50",
         "operator": "MORE_THAN_EQUAL_TO",
         "parameter": "Pressure"
       }', '', true, 'PARAMETER', 1613913793, 1613913813, 1617625405, 1617625405, 1620069531),
       (1620069573, false, 1, '{
         "uom": "Bars",
         "type": "",
         "value": "",
         "operator": "BETWEEN",
         "parameter": "Pressure",
         "lowerValue": "25",
         "upperValue": "50"
       }', '', true, 'PARAMETER', 1613913429, 1613913550, 1617625405, 1617625405, 1620069514),
       (1620069574, false, 1, '{
         "uom": "Bars",
         "type": "",
         "value": "50",
         "operator": "LESS_THAN",
         "parameter": "Pressure"
       }', '', true, 'PARAMETER', 1613913442, 1613913518, 1617625405, 1617625405, 1620069525),
       (1620069575, false, 1, '{
         "uom": "Bars",
         "type": "",
         "value": "50",
         "operator": "LESS_THAN_EQUAL_TO",
         "parameter": "Pressure"
       }', '', true, 'PARAMETER', 1613913451, 1613913531, 1617625405, 1617625405, 1620069518),
       (1620069576, false, 1, '{
         "uom": "Bars",
         "type": "",
         "value": "50",
         "operator": "MORE_THAN",
         "parameter": "Pressure"
       }', '', true, 'PARAMETER', 1613913446, 1613913524, 1617625405, 1617625405, 1620069519),
       (1620069577, false, 1, '{
         "uom": "Bars",
         "type": "",
         "value": "",
         "operator": "BETWEEN",
         "parameter": "Pressure",
         "lowerValue": "4",
         "upperValue": "5"
       }', '', true, 'PARAMETER', 1613914075, 1613914088, 1617625405, 1617625405, 1620069533),
       (1620069578, false, 1, '{
         "uom": "Bars",
         "type": "",
         "value": "50",
         "operator": "MORE_THAN_EQUAL_TO",
         "parameter": "Pressure"
       }', '', true, 'PARAMETER', 1613913454, 1613913540, 1617625405, 1617625405, 1620069526),
       (1620069579, false, 1, '{
         "uom": "Bars",
         "type": "",
         "value": "50",
         "operator": "MORE_THAN",
         "parameter": "Pressure"
       }', '', true, 'PARAMETER', 1613914328, 1613914341, 1617625405, 1617625405, 1620069534),
       (1620069580, false, 2, '{}', '', true, 'TEXTBOX', 1613914390, 1613914390, 1617625405, 1617625405, 1620069527),
       (1620069581, false, 1, '{
         "uom": "Bars",
         "type": "",
         "value": "50",
         "operator": "LESS_THAN_EQUAL_TO",
         "parameter": "Pressure"
       }', '', true, 'PARAMETER', 1613914348, 1613914359, 1617625405, 1617625405, 1620069535),
       (1620069582, false, 2, '{}', '', false, 'TEXTBOX', 1613914427, 1613914429, 1617625405, 1617625405, 1620069537),
       (1620069583, false, 1, '{}', '', false, 'SIGNATURE', 1613914422, 1613914434, 1617625405, 1617625405, 1620069537),
       (1620069584, false, 1, '[
         {
           "id": "9b599b50-c75f-4ff2-ba87-ba0e1f25917c",
           "name": "Item 1"
         }
       ]', '', true, 'CHECKLIST', 1613966325, 1613966327, 1617625405, 1617625405, 1620069548),
       (1620069585, false, 1, '[
         {
           "id": "d8a94b11-9873-4807-ba9f-a8f53bd54f47",
           "name": "Item 1"
         }
       ]', '', true, 'CHECKLIST', 1613966320, 1613966329, 1617625405, 1617625405, 1620069541),
       (1620069586, false, 1, '[
         {
           "id": "c3b3a2a4-b74c-4313-9895-1cf3c52c68b3",
           "name": "Item 1"
         }
       ]', '', true, 'CHECKLIST', 1613966484, 1613966488, 1617625405, 1617625405, 1620069547),
       (1620069587, false, 1, '[
         {
           "id": "254b52e9-fe60-493b-979b-874127029b9d",
           "name": "Item 1"
         }
       ]', '', true, 'CHECKLIST', 1613966491, 1613966492, 1617625405, 1617625405, 1620069536),
       (1620069588, false, 1, '[
         {
           "id": "c61e54f2-f297-4386-952c-493ef906f2c3",
           "name": "Item 1"
         }
       ]', '', true, 'CHECKLIST', 1613966494, 1613966495, 1617625405, 1617625405, 1620069546),
       (1620069589, false, 1, '[
         {
           "id": "e2986cfc-4cf9-42c5-b996-7a8f68d3fefb",
           "name": "Item 1"
         }
       ]', '', true, 'CHECKLIST', 1613966498, 1613966499, 1617625405, 1617625405, 1620069544),
       (1620069590, false, 1, '[
         {
           "id": "0652a855-3e00-4e97-a6b2-6fa47f79ab1b",
           "name": "Item 1"
         }
       ]', '', true, 'CHECKLIST', 1613966501, 1613966502, 1617625405, 1617625405, 1620069542),
       (1620069591, false, 1, '[
         {
           "id": "b25bb3da-8a9d-4e9d-8b36-d4f26f485f29",
           "name": "Item 1"
         }
       ]', '', true, 'CHECKLIST', 1613966516, 1613966728, 1617625405, 1617625405, 1620069512),
       (1620069592, false, 1, '[
         {
           "id": "fd596b1d-ff69-46ab-aab6-7ae87bcf67ac",
           "name": "Item 1"
         }
       ]', '', true, 'CHECKLIST', 1613966732, 1613966744, 1617625405, 1617625405, 1620069539),
       (1620069593, false, 1, '[
         {
           "id": "3d36b8ab-8d44-45a0-aa37-d5b3be76077b",
           "name": "Item 1"
         }
       ]', '', true, 'CHECKLIST', 1613966746, 1613966748, 1617625405, 1617625405, 1620069549),
       (1620069594, false, 1, '[
         {
           "id": "6c511817-146d-4cce-abbc-1de3baf8b753",
           "name": "Do not touch this. Simply complete with exception."
         }
       ]', '', true, 'CHECKLIST', 1613966803, 1613966822, 1617625405, 1617625405, 1620069543),
       (1620069595, false, 3, '{}', '', true, 'MEDIA', 1613908891, 1613908891, 1617625405, 1617625405, 1620069527);

INSERT INTO activity_media_mapping (medias_id, activities_id, created_at, modified_at, created_by, modified_by)
VALUES (1620069552, 1620069564, 1613910458, 1613910458, 1617625405, 1617625405),
       (1620069553, 1620069564, 1613910480, 1613910480, 1617625405, 1617625405);

INSERT INTO checklist_collaborator_mapping (id, created_at, modified_at, order_tree, phase, state, type, created_by, modified_by, checklists_id, users_id, phase_type)
VALUES (1620069595, 1613967153, 1613967170, 1, 1, 'REQUESTED_NO_CHANGES', 'REVIEWER', 1617625405, 1617625405, 1620069501, 1617625413, 'REVIEW'),
       (1620069596, 1613906841, 1613906841, 1, 1, 'NOT_STARTED', 'PRIMARY_AUTHOR', 1617625405, 1617625405, 1620069501, 1617625405, 'BUILD'),
       (1620069597, 1613967190, 1613967198, 1, 1, 'SIGNED', 'SIGN_OFF_USER', 1617625405, 1617625405, 1620069501, 1617625405, 'SIGN_OFF'),
       (1620069598, 1613967190, 1613967211, 2, 1, 'SIGNED', 'SIGN_OFF_USER', 1617625405, 1617625405, 1620069501, 1617625413, 'SIGN_OFF'),
       (1620069599, 1613967190, 1613967216, 3, 1, 'SIGNED', 'SIGN_OFF_USER', 1617625405, 1617625405, 1620069501, 1617625413, 'SIGN_OFF');

INSERT INTO checklist_collaborator_comments (id, created_at, modified_at, comments, review_state, created_by, modified_by, checklists_id, checklist_collaborator_mappings_id)
VALUES (1620069600, 1613967168, 1613967168, 'All OK, No Comments', 'COMMENTED_OK', 1617625413, 1617625413, 1620069501, 1620069595);