package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceParameterChoiceDto implements Serializable {
  private String objectId;
  private String objectDisplayName;
  private String objectExternalId;
  private String collection;
}
