package com.leucine.streem.constant;

import java.util.EnumSet;
import java.util.Set;

public class Action {

  public static final Set<Parameter> PARAMETER_APROVAL_ACTIONS = EnumSet.of(Parameter.PENDING_FOR_APPROVAL, Parameter.REJECTED, Parameter.APPROVED);

  public enum Audit {
    EXECUTE_JOB
  }

  public enum ChecklistAudit {
    CREATE, PUBLISH, ARCHIVE, UNARCHIVE, REVISE, DEPRECATE
  }

  public enum Parameter {
    EXECUTE,
    PENDING_FOR_APPROVAL,
    APPROVED,
    REJECTED,
  }

  public enum Job {
    ASSIGN,
    UPDATE,
    START,
    COMPLETE,
    COMPLETE_WITH_EXCEPTION,
    BLOCKED,
    IN_PROGRESS;
  }

  public enum Task {
    ASSIGN,
    COMPLETE,
    COMPLETE_WITH_EXCEPTION,
    SIGN_OFF,
    START,
    SKIP,
  }

  public enum Timer {
    START,
    PAUSE,
    RESTART,
    RESUME,
    STOP;
  }

  public enum Collaborator {
    REVIEWER_ASSIGNMENT,
    SUBMIT_FOR_REVIEW,
    START_REVIEW,
    COMMENTED_OK,
    COMMENTED_CHANGES,
    SUBMIT_REVIEW,
    SUBMIT_FOR_SIGN_OFF,
    SIGN_OFF_SEQUENCE,
    SIGN_OFF,
    PUBLISH
  }
}
