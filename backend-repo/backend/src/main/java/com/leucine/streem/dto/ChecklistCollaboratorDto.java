package com.leucine.streem.dto;

import com.leucine.streem.constant.State;
import com.leucine.streem.constant.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class ChecklistCollaboratorDto implements Serializable {
  private static final long serialVersionUID = 2634304794990892000L;

  private String id;
  private State.ChecklistCollaborator state;
  private String employeeId;
  private String email;
  private String firstName;
  private String lastName;
  private int phase;
  private State.ChecklistCollaboratorPhaseType phaseType;
  private Type.Collaborator type;
}
