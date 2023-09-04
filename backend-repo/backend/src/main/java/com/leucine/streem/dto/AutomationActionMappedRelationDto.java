package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutomationActionMappedRelationDto implements Serializable {
  @Serial
  private static final long serialVersionUID = 5823487383132047876L;

  private String relationId;
  private String relationExternalId;
  private String relationDisplayName;
  private String parameterId;
  private String referencedParameterId;
  private String relationObjectTypeId;

}
