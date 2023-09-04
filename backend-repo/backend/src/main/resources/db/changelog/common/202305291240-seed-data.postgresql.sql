-- liquibase formatted sql

--changeset siba:202305291240-seed-data-1
--comment: converting relation data to job properties

INSERT INTO public.parameters (id, archived, order_tree, data, created_at, modified_at, created_by, modified_by,
                               checklists_id, target_entity_type, is_auto_initialized, auto_initialize, rules, type,
                               label)
SELECT id,
       false,
       order_tree,
       jsonb_build_object('urlPath', url_path, 'collection', collection, 'objectTypeId', object_type_id,
                          'objectTypeExternalId', external_id, 'objectTypeDisplayName', display_name,
                          'propertyValidations', '[]'::jsonb),
       created_at,
       modified_at,
       created_by,
       modified_by,
       checklists_id,
       'PROCESS',
       false,
       null::jsonb,
       null::jsonb,
       'RESOURCE',
       display_name
FROM public.relations;

--changeset siba:202305291240-seed-data-2
--comment: converting relation values data to parameter values

INSERT INTO public.parameter_values (id, value, reason, state, choices, jobs_id, parameters_id, created_at, modified_at,
                                     created_by, modified_by, parameter_value_approval_id, hidden)
SELECT rv.id,
       null,
       null,
       'EXECUTED',
       jsonb_build_array(jsonb_build_object(
         'objectId', rv.object_id,
         'collection', rv.collection,
         'objectExternalId', rv.object_external_id,
         'objectDisplayName', rv.object_display_name
         )),
       rv.jobs_id,
       rv.relations_id,
       rv.created_at,
       rv.modified_at,
       rv.created_by,
       rv.modified_by,
       null,
       false
FROM public.relation_values rv;
