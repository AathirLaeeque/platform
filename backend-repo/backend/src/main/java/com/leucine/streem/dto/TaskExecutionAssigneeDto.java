package com.leucine.streem.dto;

import com.leucine.streem.constant.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskExecutionAssigneeDto implements Serializable {
  private static final long serialVersionUID = -8596725265758149905L;

  private String id;
  private String employeeId;
  private String firstName;
  private String lastName;
  private String email;
  private State.TaskExecutionAssignee state;
  private boolean actionPerformed;
}
