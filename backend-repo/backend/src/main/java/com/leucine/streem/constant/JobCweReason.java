package com.leucine.streem.constant;

public enum JobCweReason {
  JOB_GOT_CANCELLED("Job got cancelled"),
  JOB_CREATED_BY_MISTAKE("Job created by mistake"),
  JOB_COMPLETED_OFFLINE("Job completed offline"),
  OTHER("Other");

  private final String text;

  JobCweReason(String text) {
    this.text = text;
  }

  public String get() {
    return text;
  }
}
