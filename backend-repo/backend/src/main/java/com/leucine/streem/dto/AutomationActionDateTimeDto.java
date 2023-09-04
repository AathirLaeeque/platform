package com.leucine.streem.dto;


import com.leucine.streem.constant.CollectionMisc;
import com.leucine.streem.constant.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutomationActionDateTimeDto extends AutomationSetPropertyBaseDto implements Serializable {
  private static final long serialVersionUID = 1187009022495868339L;
  private Type.EntityType entityType;
  private String entityId;
  private Type.AutomationDateTimeCaptureType captureProperty;
  private CollectionMisc.DateUnit dateUnit;
  private Long value;
  private String referencedParameterId; // Resource parameter ID

}
