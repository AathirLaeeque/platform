package com.leucine.streem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectTypeCreateRequest {

  private String externalId;
  private Integer version;
  private String collection;
  private String displayName;
  private String pluralName;
  private String description;
  private List<ObjectTypePropertyCreateRequest> properties;
  private Integer flags;

}
