package com.leucine.streem.constant;

public final class ErrorMessage {
  private ErrorMessage() {
  }

  public static final String COULD_NOT_CREATE_CHECKLIST = "Could not create Checklist";
  public static final String COULD_NOT_CREATE_JOB = "Could not create Job";
  public static final String COULD_NOT_CREATE_THE_DIRECTORY_WHERE_THE_UPLOADED_FILES_WILL_BE_STORED = "Could not create the directory where the uploaded files will be stored";
  public static final String COULD_NOT_UPDATE_CHECKLIST = "Could not update checklist";
  public static final String MANDATORY_PARAMETERS_NOT_COMPLETED = "Mandatory parameters not completed";
  public static final String PENDING_VERIFICATION_PARAMETERS = "parameters verification pending";
  public static final String TASKS_INCOMPLETE = "Task not completed";
  public static final String TASKS_NOT_SIGNED_OFF = "Tasks not signed off";

  /* Media Upload Errors */
  public static final String FILENAME_CONTAINS_INVALID_CHARACTERS = "Filename contains invalid characters: {0}";
  public static final String FILE_STORAGE = "Error storing file: {0}";
  public static final String DATA_INCONSISTENCY_ERROR = "There has been issues in syncing data.";

  public static final String ERROR_CREATING_A_SCHEDULER = "Error creating a Scheduler";
  public static final String ERROR_STOPPING_A_SCHEDULER = "Error stopping a Scheduler";

  public static final String FILE_SIZE_EXCEEDED = "File size exceeded, file size: {%d}, max file size: {%d}";
  public static final String REQUEST_SIZE_EXCEEDED = "Request size exceeded, request size: {%d}, max request size: {%d}";
  public static final String INVALID_EXTENSION = "Invalid file extension provided";
}
