package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.compositekey.FacilityUseCaseCompositeKey;
import com.leucine.streem.model.helper.UserAuditBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.FACILITY_USE_CASE_MAPPING)
public class FacilityUseCaseMapping extends UserAuditBase implements Serializable {
  public static final String FACILITY_ID = "facilityId";
  public static final String ORGANISATION_ID = "organisationId";

  @Serial
  private static final long serialVersionUID = 7389267779086746651L;

  @EmbeddedId
  private FacilityUseCaseCompositeKey facilityUseCaseId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "facilities_id", nullable = false, insertable = false, updatable = false)
  private Facility facility;

  @Column(columnDefinition = "bigint", name = "facilities_id", updatable = false, insertable = false)
  private Long facilityId;
  
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "use_cases_id", nullable = false, insertable = false, updatable = false)
  private UseCase useCase;

  @Column(columnDefinition = "bigint", name = "use_cases_id", updatable = false, insertable = false)
  private Long useCaseId;

  @Column(columnDefinition = "integer", nullable = false)
  private Integer quota;

  public FacilityUseCaseMapping(final Facility facility, final UseCase useCase, final User user) {
    this.facility = facility;
    this.useCase = useCase;
    this.createdBy = user;
    this.modifiedBy = user;
    this.facilityUseCaseId = new FacilityUseCaseCompositeKey(facility.getId(), useCase.getId());
  }
}
