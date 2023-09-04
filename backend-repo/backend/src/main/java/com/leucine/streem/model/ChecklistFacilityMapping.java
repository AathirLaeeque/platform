package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.compositekey.ChecklistFacilityCompositeKey;
import com.leucine.streem.model.helper.UserAuditBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.CHECKLIST_FACILITY_MAPPING)
public class ChecklistFacilityMapping extends UserAuditBase implements Serializable {

  private static final long serialVersionUID = 1021039613417650482L;
  @EmbeddedId
  private ChecklistFacilityCompositeKey checklistFacilityId;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "checklists_id", nullable = false, insertable = false, updatable = false)
  private Checklist checklist;

  @Column(columnDefinition = "bigint", name = "checklists_id", updatable = false, insertable = false)
  private Long checklistId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "facilities_id", nullable = false, insertable = false, updatable = false)
  private Facility facility;

  @Column(columnDefinition = "bigint", name = "facilities_id", updatable = false, insertable = false)
  private Long facilityId;

  public ChecklistFacilityMapping(Checklist checklist, Facility facility, User principalUserEntity) {
    this.checklist = checklist;
    this.facility = facility;
    createdBy = principalUserEntity;
    modifiedBy = principalUserEntity;
    checklistFacilityId = new ChecklistFacilityCompositeKey(facility.getId(), checklist.getId());
  }
}
