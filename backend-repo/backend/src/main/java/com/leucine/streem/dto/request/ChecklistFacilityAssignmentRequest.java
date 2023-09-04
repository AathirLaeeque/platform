package com.leucine.streem.dto.request;

import lombok.Data;

import java.util.Set;
@Data
public class ChecklistFacilityAssignmentRequest {
  private Set<Long> assignedFacilityIds;
  private Set<Long> unassignedFacilityIds;
}
