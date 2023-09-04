package com.leucine.streem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leucine.streem.constant.Action;
import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.helper.BaseEntity;
import com.leucine.streem.util.DateTimeUtils;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = TableName.CHECKLIST_AUDITS)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ChecklistAudit extends BaseEntity implements Serializable {
  public static final String DEFAULT_SORT = "triggeredAt";
  @Serial
  private static final long serialVersionUID = -5993281771588465119L;

  @Column(columnDefinition = "bigint", nullable = false)
  private Long organisationsId;

  @Column(columnDefinition = "bigint", nullable = false)
  private Long triggeredBy;

  @Column(name = "checklists_id", columnDefinition = "bigint", nullable = false)
  private Long checklistId;

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private Action.ChecklistAudit action;

  @Column(columnDefinition = "text")
  private String details;

  @JsonIgnore
  @Column
  private Long triggeredAt;

  @PrePersist
  public void beforePersist() {
    triggeredAt = DateTimeUtils.now();
  }

}
