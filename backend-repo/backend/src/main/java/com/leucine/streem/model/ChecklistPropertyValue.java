package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.compositekey.ChecklistPropertyValueCompositeKey;
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
@Table(name = TableName.CHECKLIST_PROPERTY_VALUES)
public class ChecklistPropertyValue extends UserAuditBase implements Serializable {
  private static final long serialVersionUID = 259679594081867369L;

  @EmbeddedId
  private ChecklistPropertyValueCompositeKey checklistPropertyValueId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "facility_use_case_property_mapping_id", nullable = false, insertable = false, updatable = false)
  private FacilityUseCasePropertyMapping facilityUseCasePropertyMapping;

  @Column(columnDefinition = "bigint", name = "facility_use_case_property_mapping_id", updatable = false, insertable = false)
  private Long facilityUseCasePropertyMappingId;

  @Column(columnDefinition = "varchar", length = 255)
  private String value;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "checklists_id", nullable = false, insertable = false, updatable = false)
  private Checklist checklist;

  @Column(columnDefinition = "bigint", name = "checklists_id", updatable = false, insertable = false)
  private Long checklistId;

  public ChecklistPropertyValue(Checklist checklist, FacilityUseCasePropertyMapping facilityUseCasePropertyMapping, String value, User principalUserEntity) {
    this.facilityUseCasePropertyMapping = facilityUseCasePropertyMapping;
    this.checklist = checklist;
    this.value = value;
    createdBy = principalUserEntity;
    modifiedBy = principalUserEntity;
    checklistPropertyValueId = new ChecklistPropertyValueCompositeKey(checklist.getId(), facilityUseCasePropertyMapping.getId());
  }
}
