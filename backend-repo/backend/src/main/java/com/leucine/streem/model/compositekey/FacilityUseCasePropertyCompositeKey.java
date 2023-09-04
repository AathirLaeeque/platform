package com.leucine.streem.model.compositekey;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;

@Deprecated
@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class FacilityUseCasePropertyCompositeKey implements Serializable {

  @Serial
  private static final long serialVersionUID = 5667775335155557551L;

  @Column(name = "facilities_id", columnDefinition = "bigint")
  private Long facilityId;

  @Column(name = "use_cases_id", columnDefinition = "bigint")
  private Long useCaseId;

  @Column(name = "properties_id", columnDefinition = "bigint")
  private Long propertyId;

  public FacilityUseCasePropertyCompositeKey() {
  }

  public FacilityUseCasePropertyCompositeKey(Long facilityId, Long useCaseId, Long propertyId) {
    this.facilityId = facilityId;
    this.useCaseId = useCaseId;
    this.propertyId = propertyId;
  }
}
