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
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
public class ChecklistFacilityCompositeKey implements Serializable {

  private static final long serialVersionUID = 8455474539640422466L;

  @Column(name = "checklists_id", columnDefinition = "bigint")
  private Long checklistId;

  @Column(name = "facilities_id", columnDefinition = "bigint")
  private Long facilityId;

  public ChecklistFacilityCompositeKey(Long facilityId, Long checklistId) {
    this.facilityId = facilityId;
    this.checklistId = checklistId;
  }
}


