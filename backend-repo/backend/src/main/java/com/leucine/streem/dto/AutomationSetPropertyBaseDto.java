package com.leucine.streem.dto;


import com.leucine.streem.constant.CollectionMisc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutomationSetPropertyBaseDto implements Serializable {
  private static final long serialVersionUID = 8338604815032499916L;

  private String propertyId;
  private String propertyDisplayName;
  private String propertyExternalId;
  private CollectionMisc.PropertyType propertyInputType;
}
