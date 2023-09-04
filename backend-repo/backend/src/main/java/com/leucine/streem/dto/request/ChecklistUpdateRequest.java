package com.leucine.streem.dto.request;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ChecklistUpdateRequest {
  private String name;
  private Long facilityId;
  private List<PropertyRequest> properties;
  private Set<Long> addAuthorIds;
  private Set<Long> removeAuthorIds;
  private String description;
}
