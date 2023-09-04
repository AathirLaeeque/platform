package com.leucine.streem.model.compositekey;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class ChecklistPropertyValueCompositeKey implements Serializable {
  private static final long serialVersionUID = -2849904307466701467L;

  @Column(name = "checklists_id", columnDefinition = "bigint")
  private Long checklistId;

  @Column(name = "facility_use_case_property_mapping_id", columnDefinition = "bigint")
  private Long facilityUseCasePropertyMappingId;

  public ChecklistPropertyValueCompositeKey(Long checklistId, Long facilityUseCasePropertyMappingId) {
    this.checklistId = checklistId;
    this.facilityUseCasePropertyMappingId = facilityUseCasePropertyMappingId;
  }
}
