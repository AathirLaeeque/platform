package com.leucine.streem.dto;


import com.leucine.streem.constant.CollectionMisc;
import lombok.Data;

@Data
public class AutoInitializeObjectPropertyDto {
  private String id;
  private String externalId;
  private String displayName;
  private CollectionMisc.PropertyType inputType;
}
