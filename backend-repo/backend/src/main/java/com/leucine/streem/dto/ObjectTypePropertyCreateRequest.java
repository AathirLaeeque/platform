package com.leucine.streem.dto;

import com.leucine.streem.collections.PropertyOption;
import com.leucine.streem.constant.CollectionMisc;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Getter
@Setter
@ToString
public class ObjectTypePropertyCreateRequest {

  private String externalId;
  private String displayName;
  private String description;
  private String placeHolder;
  private int flags;
  private String autogeneratePrefix;
  private int sortOrder;
  private List<PropertyOption> options;
  private CollectionMisc.PropertyType inputType;
}
