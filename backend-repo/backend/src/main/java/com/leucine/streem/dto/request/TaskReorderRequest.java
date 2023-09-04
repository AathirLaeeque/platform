package com.leucine.streem.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class TaskReorderRequest {
  private Map<Long, Long> tasksOrder = new HashMap<>();
  private long checklistId;
}
