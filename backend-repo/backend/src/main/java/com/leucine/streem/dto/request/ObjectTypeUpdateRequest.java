package com.leucine.streem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectTypeUpdateRequest {
  private String externalId;
  private String displayName;
  private String description;
}
