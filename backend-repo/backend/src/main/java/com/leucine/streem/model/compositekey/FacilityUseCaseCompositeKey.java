package com.leucine.streem.model.compositekey;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class FacilityUseCaseCompositeKey implements Serializable {

  @Serial
  private static final long serialVersionUID = -7053386652044617061L;

  @Column(name = "facilities_id", columnDefinition = "bigint")
  private Long facilityId;

  @Column(name = "use_cases_id", columnDefinition = "bigint")
  private Long useCaseId;

  public FacilityUseCaseCompositeKey() {
  }

  public FacilityUseCaseCompositeKey(Long facilityId, Long useCaseId) {
    this.facilityId = facilityId;
    this.useCaseId = useCaseId;
  }
}
