-- liquibase formatted sql

--changeset sathyam:202301161426-seed-schema-data-1
ALTER TABLE activities ADD COLUMN target_entity_type VARCHAR(50);
UPDATE activities set target_entity_type = 'TASK';
ALTER TABLE activities ALTER COLUMN target_entity_type SET NOT NULL;

--changeset sathyam:202301161426-seed-schema-data-2
ALTER TABLE activities ADD COLUMN checklists_id BIGINT REFERENCES checklists (id);
UPDATE activities act set checklists_id = (select c.id from checklists c inner join stages s on s.checklists_id = c.id inner join tasks t on t.stages_id = s.id inner join activities a on a.tasks_id = t.id and a.id = act.id);
ALTER TABLE activities ALTER COLUMN checklists_id SET NOT NULL;


--changeset sathyam:202301161426-seed-schema-data-3
ALTER TABLE activities ALTER COLUMN tasks_id DROP NOT NULL;

--changeset sathyam:202301161426-seed-schema-data-4
ALTER TABLE activity_values RENAME COLUMN activities_id TO parameters_id;
ALTER TABLE activity_values RENAME COLUMN activity_value_approval_id to parameter_value_approval_id;
ALTER TABLE activity_media_mapping RENAME COLUMN activities_id TO parameters_id;
ALTER TABLE activity_value_media_mapping RENAME COLUMN activity_values_id to parameter_values_id;
ALTER TABLE temp_activity_values RENAME COLUMN activities_id TO parameters_id;
ALTER TABLE temp_activity_values RENAME COLUMN activity_value_approval_id to parameter_value_approval_id;
ALTER TABLE temp_activity_value_media_mapping RENAME COLUMN temp_activity_values_id to temp_parameter_values_id;

--changeset sathyam:202301161426-seed-schema-data-5
update activities set label = data->>'parameter' where type='PARAMETER';
update activities set type='MULTI_LINE' where type='TEXTBOX';
update activities set type='SHOULD_BE' where type='PARAMETER';

--changeset sathyam:202301161426-seed-schema-data-6
update automations set target_entity_type = 'RESOURCE_PARAMETER' where target_entity_type = 'RESOURCE_ACTIVITY';

--changeset sathyam:202301161426-seed-schema-data-7
UPDATE activities SET validations = replace(validations::text, '"resourceActivityValidations"', '"resourceParameterValidations"')::jsonb;
UPDATE activities SET validations = replace(validations::text, '"activityId"', '"parameterId"')::jsonb;
UPDATE activities SET data = replace(data::text, '"activityId"', '"parameterId"')::jsonb;
UPDATE relations SET validations = replace(validations::text, '"activityId"', '"parameterId"')::jsonb;
UPDATE automations SET action_details = replace(action_details::text, '"activityId"', '"parameterId"')::jsonb;
UPDATE automations SET action_details = replace(action_details::text, '"referencedActivityId"', '"referencedParameterId"')::jsonb;

--changeset sathyam:202301161426-seed-schema-data-8
ALTER TABLE activities RENAME TO parameters;
ALTER TABLE activity_media_mapping RENAME TO parameter_media_mapping;
ALTER TABLE activity_values RENAME TO parameter_values;
ALTER TABLE activity_value_approvals RENAME TO parameter_value_approvals;
ALTER TABLE activity_value_media_mapping RENAME TO parameter_value_media_mapping;
ALTER TABLE temp_activity_values RENAME TO temp_parameter_values;
ALTER TABLE temp_activity_value_media_mapping RENAME TO temp_parameter_value_media_mapping;
