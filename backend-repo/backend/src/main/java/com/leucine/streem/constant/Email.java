package com.leucine.streem.constant;

public final class Email {
  private Email() {}

  /* Attributes */
  public static final String ATTRIBUTE_LOGIN_URL = "streemlogin";
  public static final String ATTRIBUTE_JOB = "job";
  public static final String ATTRIBUTE_CHECKLIST = "checklist";

  /* Template */
  public static final String TEMPLATE_USER_ASSIGNED_TO_JOB = "USER_ASSIGNED_TO_JOB";
  public static final String TEMPLATE_USER_UNASSIGNED_FROM_JOB = "USER_UNASSIGNED_FROM_JOB";
  public static final String TEMPLATE_PARAMETER_APPROVAL_REQUEST = "APPROVAl_REQUEST";
  public static final String TEMPLATE_REVIEWER_ASSIGNED_TO_CHECKLIST = "REVIEWER_ASSIGNED_TO_CHECKLIST";
  public static final String TEMPLATE_REVIEWER_UNASSIGNED_FROM_CHECKLIST = "REVIEWER_UNASSIGNED_FROM_CHECKLIST";
  public static final String TEMPLATE_PROTOTYPE_READY_FOR_SIGNING = "PROTOTYPE_READY_FOR_SIGNING";
  public static final String TEMPLATE_PROTOTYPE_SIGNING_REQUEST = "PROTOTYPE_SIGNING_REQUEST";
  public static final String TEMPLATE_PROTOTYPE_REQUESTED_CHANGES = "PROTOTYPE_REQUESTED_CHANGES";
  public static final String TEMPLATE_REVIEW_SUBMIT_REQUEST = "PROTOTYPE_REVIEW_SUBMIT_REQUEST";
  public static final String TEMPLATE_AUTHOR_CHECKLIST_CONFIGURATION = "AUTHOR_CHECKLIST_CONFIGURATION";


  /* Subject */
  public static final String SUBJECT_USER_ASSIGNED_TO_JOB = "You are assigned to a Job";
  public static final String SUBJECT_USER_UNASSIGNED_FROM_JOB = "You are unassigned from a Job";
  public static final String SUBJECT_PARAMETER_APPROVAL_REQUEST = "An Operator has requested for your Approval on a Task";
  public static final String SUBJECT_REVIEWER_ASSIGNED_TO_CHECKLIST = "A Prototype has been submitted for your Review";
  public static final String SUBJECT_REVIEWER_UNASSIGNED_FROM_CHECKLIST = "You are unassigned from Checklist";
  public static final String SUBJECT_PROTOTYPE_READY_FOR_SIGNING="A Prototype is Ready for Signing";
  public static final String SUBJECT_PROTOTYPE_SIGNING_REQUEST = "A Prototype needs your sign";
  public static final String SUBJECT_PROTOTYPE_REQUESTED_CHANGES="Modification requested by Reviewers";
  public static final String SUBJECT_REVIEW_SUBMIT_REQUEST = "Finish your Review";
  public static final String SUBJECT_AUTHOR_CHECKLIST_CONFIGURATION = "Configure a Checklist";


}
