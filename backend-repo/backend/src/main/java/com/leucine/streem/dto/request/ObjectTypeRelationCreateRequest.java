package com.leucine.streem.dto.request;

import com.leucine.streem.dto.ObjectTypeRelationTargetCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObjectTypeRelationCreateRequest {

  private String objectTypeId;
  private String displayName;
  private ObjectTypeRelationTargetCreateRequest target;
  private int sortOrder;
  private Map<String, String> variables;
  private String description;
  private int usageStatus;
  private int flags;
}
