package com.leucine.streem.constant;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public final class State {

  public static final Set<Checklist> CHECKLIST_EDIT_STATES = Collections.unmodifiableSet(EnumSet.of(Checklist.BEING_BUILT, Checklist.REQUESTED_CHANGES));
  public static final Set<TaskExecution> TASK_COMPLETED_STATES = Collections.unmodifiableSet(EnumSet.of(TaskExecution.COMPLETED, TaskExecution.SKIPPED, TaskExecution.COMPLETED_WITH_EXCEPTION));
  public static final Set<Job> JOB_COMPLETED_STATES = Collections.unmodifiableSet(EnumSet.of(Job.COMPLETED, Job.COMPLETED_WITH_EXCEPTION));
  public static final Set<TaskExecution> TASK_EXECUTION_EXCEPTION_STATE = Collections.unmodifiableSet(EnumSet.of(TaskExecution.COMPLETED_WITH_EXCEPTION, TaskExecution.SKIPPED));

  public enum Checklist {
    ILLEGAL, // Illegal state
    BEING_BUILT, // when checklist is being editing, before primary author submits for any review.
    SUBMITTED_FOR_REVIEW, // when checklist has been submitted for review by the primary author
    BEING_REVIEWED, // when any of the reviewer starts reviewing
    REQUESTED_CHANGES, // when any of the reviewer gives their respective comments (for making changes) and send back to author. authors can edit the checklist
    READY_FOR_SIGNING, // when all the reviewers agree with "All OK" and send back to author
    SIGN_OFF_INITIATED, // when primary author initiated the sequential sign-off process
    SIGNING_IN_PROGRESS, // when any of the authors signs off
    READY_FOR_RELEASE, // when all the collaborators signs off
    PUBLISHED, // when the checklist has been published by any authorized user
    DEPRECATED, // when an existing checklist is revised & published then the old/previous version of checklist goes to this state
    BEING_REVISED, // when an existing checklist is revised. i.e for a published checklist there exist a prototype
  }

  public enum ChecklistCollaborator {
    ILLEGAL, // Illegal state
    NOT_STARTED, // reviewer has not started reviewing
    BEING_BUILT, // author is editing the checklist
    AWAITING, // author is waiting for reviewers to complete the review
    BEING_REVIEWED, // reviewer has started reviewing
    COMMENTED_OK, // reviewer agrees with the checklist created by the authors
    COMMENTED_CHANGES, // reviewer does not agree with the checklist and has suggested certain changes
    REQUESTED_CHANGES, // reviewer submits to the author with few comments suggesting changes
    REQUESTED_NO_CHANGES, // reviewer submits to the author with no changes
    SIGNED, // collaborator signed so that checklist can be released
  }


  public enum ChecklistCollaboratorPhaseType {
    REVIEW,
    SIGN_OFF,
    BUILD
  }

  public enum Job {
    ASSIGNED("Assigned"),
    COMPLETED("Completed"),
    COMPLETED_WITH_EXCEPTION("Completed With Exception"),
    IN_PROGRESS("In Progress"),
    UNASSIGNED("Unassigned"),
    BLOCKED("Blocked");

    private final String displayName;

    Job(String displayName) {
      this.displayName = displayName;
    }

    public String getDisplayName() {
      return displayName;
    }
  }

  public enum ParameterValue {
    APPROVED,
    REJECTED
  }


  public enum Selection {
    NOT_SELECTED,
    SELECTED,
  }

  public enum TaskExecution {
    COMPLETED,
    COMPLETED_WITH_EXCEPTION,
    IN_PROGRESS,
    NOT_STARTED,
    SKIPPED,
    PAUSED
  }


  /*--TODO: Need to add state execution lifecycle--*/
  public enum ParameterExecution {
    BEING_EXECUTED,
    ENABLED_FOR_CORRECTION,
    EXECUTED,
    NOT_STARTED,
    PENDING_FOR_APPROVAL, // this state is specifically for should be parameter
    APPROVAL_PENDING, // this state is specifically for parameter verifications
    VERIFICATION_PENDING,
    BEING_EXECUTED_AFTER_REJECTED
  }

  public enum ParameterVerification {
    PENDING,
    RECALLED,
    REJECTED,
    ACCEPTED,
  }

  public enum Timer {
    IDLE,
    PAUSED,
    RUNNING,
    STOPPED
  }

  public enum ChecklistAuthor {
    IN_PROGRESS,
    READY_FOR_RELEASE,
  }

  public enum ChecklistReview {
    DONE,
    DONE_WITH_CR,
    IN_PROGRESS,
    NOT_STARTED,
    READY_FOR_RELEASE,
    SUBMITTED_FOR_CR,
  }

  public enum TaskExecutionAssignee {
    IN_PROGRESS,
    SIGNED_OFF
  }

  public enum Scheduler {
    PUBLISHED,
    DEPRECATED
  }
}
