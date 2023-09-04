package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.helper.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = TableName.CHECKLIST_DEFAULT_USERS, uniqueConstraints = {@UniqueConstraint(name = "UniqueDefaultUserConstraint", columnNames = {"checklists_id", "users_id", "tasks_id"})})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChecklistDefaultUsers extends BaseEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = 984496697480536718L;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "checklists_id", nullable = false)
  private Checklist checklist;

  @Column(columnDefinition = "bigint", name = "checklists_id", updatable = false, insertable = false)
  private Long checklistId;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "users_id", referencedColumnName = "id")
  private User user;

  @Column(columnDefinition = "bigint", name = "users_id", updatable = false, insertable = false)
  private Long userId;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tasks_id", updatable = false, nullable = false)
  private Task task;

  @Column(columnDefinition = "bigint", name = "tasks_id", updatable = false, insertable = false)
  private Long taskId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "facilities_id", nullable = true, updatable = false)
  private Facility facility;

  @Column(columnDefinition = "bigint", name = "facilities_id", updatable = false, insertable = false)
  private Long facilityId;
}
