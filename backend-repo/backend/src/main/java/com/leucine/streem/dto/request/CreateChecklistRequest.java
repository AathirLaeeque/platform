package com.leucine.streem.dto.request;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class CreateChecklistRequest {
  private String name;
  private Long facilityId;
  private Long useCaseId;
  private List<PropertyRequest> properties;
  private Set<Long> authors;
  private String description;
  private boolean isGlobal;
}
