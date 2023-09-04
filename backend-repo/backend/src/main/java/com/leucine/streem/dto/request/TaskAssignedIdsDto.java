package com.leucine.streem.dto.request;

import lombok.Data;

import java.util.Set;

@Data
public class TaskAssignedIdsDto {
  private Set<Long> ids;
}
