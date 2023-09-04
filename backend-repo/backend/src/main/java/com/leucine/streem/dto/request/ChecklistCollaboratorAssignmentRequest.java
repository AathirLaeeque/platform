package com.leucine.streem.dto.request;

import lombok.Data;

import java.util.Set;

@Data
public class ChecklistCollaboratorAssignmentRequest {
  private Set<Long> assignedUserIds;
  private Set<Long> unassignedUserIds;
}
