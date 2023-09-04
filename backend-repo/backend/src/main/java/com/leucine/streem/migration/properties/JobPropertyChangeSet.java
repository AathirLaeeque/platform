package com.leucine.streem.migration.properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

public class JobPropertyChangeSet implements CustomTaskChange {
  @Override
  public String getConfirmationMessage() {
    return null;
  }

  @Override
  public void setUp() {

  }

  @Override
  public void setFileOpener(ResourceAccessor resourceAccessor) {

  }

  @Override
  public ValidationErrors validate(Database database) {
    return null;
  }

  @Override
  public void execute(Database database) throws CustomChangeException {
    JobProperties051520231310 jobProperties051520231310 = new JobProperties051520231310();
    try {
      jobProperties051520231310.execute();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
