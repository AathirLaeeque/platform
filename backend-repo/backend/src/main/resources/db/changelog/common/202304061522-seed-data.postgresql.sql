-- liquibase formatted sql

--changeset siba:202304061522-seed-data-1
--comment: adding Global facility for organisation 'Leucine'
INSERT INTO facilities (id, archived, name, organisations_id, created_at, modified_at)
VALUES (-1, false, 'Global', 1, 0, 0) ON CONFLICT DO NOTHING;

-- changeset siba:202304061522-seed-data-2
-- comment mapping all usecases with Global facility
INSERT INTO facility_use_case_mapping (facilities_id, use_cases_id, quota, created_by, created_at, modified_by,
                                       modified_at)
SELECT -1, u.id, 0, 1, 0, 1, 0
FROM use_cases u ON CONFLICT DO NOTHING;

-- changeset siba:202304061522-seed-data-3
-- comment mapping properties with global facility in all usecases
INSERT INTO public.facility_use_case_property_mapping
(id, facilities_id, use_cases_id, properties_id, label_alias, place_holder_alias, order_tree, is_mandatory, created_by, created_at, modified_by, modified_at)
SELECT p.id, -1, p.use_cases_id, p.id, p."label", p.place_holder, p.order_tree, false, 1, 0, 1, 0 FROM properties p ON CONFLICT DO NOTHING;
