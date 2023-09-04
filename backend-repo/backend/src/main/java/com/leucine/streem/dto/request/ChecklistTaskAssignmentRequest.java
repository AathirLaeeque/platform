package com.leucine.streem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChecklistTaskAssignmentRequest {
  private Set<Long> taskIds;
  private Set<Long> assignedUserIds;
  private Set<Long> unassignedUserIds;
}
