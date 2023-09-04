package com.leucine.streem.dto.request;

import com.leucine.streem.collections.partial.PartialEntityObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CreateJobRequest {
  private Long checklistId;
  private List<PropertyRequest> properties = new ArrayList<>();
  private Map<Long, List<PartialEntityObject>> relations = new HashMap<>();
  private Map<Long, ParameterExecuteRequest> parameterValues = new HashMap<>();
}
