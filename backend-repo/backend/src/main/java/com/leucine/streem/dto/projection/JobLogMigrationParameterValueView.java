package com.leucine.streem.dto.projection;

import com.fasterxml.jackson.databind.JsonNode;

public interface JobLogMigrationParameterValueView {

  Long getId();
  Long getParameterId();
  String getValue();
  String getChoices();

}
