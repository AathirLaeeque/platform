package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.helper.UserAuditIdentifiableBase;
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
@Table(name = TableName.FACILITY_USE_CASE_PROPERTY_MAPPING)
public class FacilityUseCasePropertyMapping extends UserAuditIdentifiableBase implements Serializable {
  public static final String FACILITY_ID = "facilityId";
  public static final String USECASE_ID = "useCaseId";
  public static final String TYPE = "property.type";
  public static final String PROPERTY_ARCHIVED = "property.archived";
  public static final String ORDER_TREE = "property.orderTree";

  @Serial
  private static final long serialVersionUID = 7409352522058309854L;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "facilities_id", nullable = false, insertable = false, updatable = false)
  private Facility facility;

  @Column(columnDefinition = "bigint", name = "facilities_id", updatable = false, insertable = false)
  private Long facilityId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "use_cases_id", nullable = false, insertable = false, updatable = false)
  private UseCase useCase;

  @Column(columnDefinition = "bigint", name = "use_cases_id", updatable = false, insertable = false)
  private Long useCaseId;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "properties_id", nullable = false, insertable = false, updatable = false)
  private Property property;

  @Column(columnDefinition = "bigint", name = "properties_id", updatable = false, insertable = false)
  private Long propertiesId;

  @Column(columnDefinition = "varchar", length = 255, nullable = false)
  private String labelAlias;

  @Column(columnDefinition = "varchar", length = 512, nullable = false)
  private String placeHolderAlias;

  @Column(columnDefinition = "integer", nullable = false)
  private Integer orderTree;

  @Column(columnDefinition = "boolean default false", nullable = false)
  private boolean isMandatory = false;

  public FacilityUseCasePropertyMapping(final Facility facility, final UseCase useCase, final Property property, final User user) {
    this.facility = facility;
    this.useCase = useCase;
    this.property = property;
    this.createdBy = user;
    this.modifiedBy = user;
  }
}
