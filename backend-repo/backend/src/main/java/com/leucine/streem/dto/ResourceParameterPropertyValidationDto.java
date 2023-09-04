package com.leucine.streem.dto;

import com.leucine.streem.constant.CollectionMisc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceParameterPropertyValidationDto implements Serializable {
  private String id;
  private String parameterId;
  private String value;
  private String propertyId;
  private String propertyExternalId;
  private String propertyDisplayName;
  private CollectionMisc.PropertyValidationConstraint constraint;
  private CollectionMisc.PropertyType propertyInputType;
  private String errorMessage;
}
