package com.leucine.streem.dto.request;

import com.leucine.streem.collections.partial.PartialEntityObject;
import com.leucine.streem.dto.JobProcessInfoViewDto;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class EntityObjectValueRequest {
  private String objectTypeId;
  private Map<String, Object> properties = new HashMap<>();
  private Map<String, List<PartialEntityObject>> relations = new HashMap<>();
  private String reason;
  private JobProcessInfoViewDto info;
}
