package com.leucine.streem.dto;

import com.leucine.streem.constant.CollectionMisc;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ObjectTypeRelationTargetCreateRequest {
  private CollectionMisc.RelationType type;
  private CollectionMisc.Cardinality cardinality;
  private String urlPath;
}
