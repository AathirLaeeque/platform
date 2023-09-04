package com.leucine.streem.dto.request;

import com.leucine.streem.constant.CollectionMisc;
import com.leucine.streem.dto.ObjectTypePropertyOptionCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectTypePropertyCreateRequest {
  private String externalId; // TODO Why is this even there ?
  private String displayName;
  private String description;
  private String placeHolder;
  private int flags;
  private int sortOrder;
  private List<ObjectTypePropertyOptionCreateRequest> options;
  private CollectionMisc.PropertyType inputType;

}
