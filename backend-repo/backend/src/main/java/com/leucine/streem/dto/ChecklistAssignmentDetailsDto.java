package com.leucine.streem.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ChecklistAssignmentDetailsDto implements Serializable {
  @Serial
  private static final long serialVersionUID = -6441632246668634140L;
  private String id;
  private String employeeId;
  private String firstName;
  private String lastName;
  private boolean completelyAssigned;
  private int assignedTasks;
}
