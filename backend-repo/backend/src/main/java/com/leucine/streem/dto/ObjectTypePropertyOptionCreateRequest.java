package com.leucine.streem.dto;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class ObjectTypePropertyOptionCreateRequest {
  private ObjectId id;
  private String displayName;
}
