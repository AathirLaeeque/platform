package com.leucine.streem.migration.properties;

public final class Queries {
  public static final String GET_PROPERTIES_OF_TYPE = "SELECT id FROM properties WHERE type = ?";
  public static final String GET_FUPM_BY_PROPERTY_IDS = "SELECT * FROM facility_use_case_property_mapping WHERE properties_id IN (%s)";
  public static final String GET_ALL_CHECKLIST_FACILITY_MAPPING = "SELECT * FROM checklist_facility_mapping";
  public static final String GET_ALL_USE_CASES_ID_FROM_CHECKLIST = "SELECT use_cases_id FROM checklists WHERE id = ?";
  public static final String INSERT_INTO_PARAMETERS = """
          INSERT INTO parameters
          (id, type, target_entity_type, label, description, order_tree, is_mandatory, archived, data, checklists_id, validations, is_auto_initialized, auto_initialize, rules, created_at, modified_at, created_by, modified_by)
          VALUES (?, ?, ?, ?, ?, ?, ?, ?, CAST(? AS jsonb), ?, CAST(? AS jsonb), ?, CAST(? AS jsonb), CAST(? AS jsonb), ?, ?, ?, ?)
          """;
  public static final String GET_ALL_HACK_NODE_VALUE_STORED_IN_RULES = "SELECT rules FROM parameters where rules is not null";
  public static final String GET_ALL_JOB_PROPERTY_VALUES_WITH_FUPM_ID = "SELECT * FROM job_property_values WHERE facility_use_case_property_mapping_id = ?";
  public static final String GET_ALL_JOB_WITH_IDS = "SELECT id, checklists_id FROM jobs WHERE id IN (%s)";
  public static final String INSERT_INTO_PARAMETER_VALUES = """
            INSERT INTO parameter_values
            (id, value, reason, state, choices, jobs_id, parameters_id, created_at, modified_at, created_by, modified_by, hidden)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

  public static final String RESTORE_ALL_HACK_NODES_STORED_IN_RULES = "UPDATE parameters SET rules = null WHERE rules IS NOT NULL";

}
