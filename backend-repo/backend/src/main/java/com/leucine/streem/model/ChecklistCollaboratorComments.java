package com.leucine.streem.model;

import com.leucine.streem.constant.State;
import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.helper.UserAuditIdentifiableBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = TableName.CHECKLIST_COLLABORATOR_COMMENTS)
public class ChecklistCollaboratorComments extends UserAuditIdentifiableBase implements Serializable {

  private static final long serialVersionUID = 4160488966562238516L;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "checklist_collaborator_mappings_id", nullable = false, insertable = true, updatable = false)
  private ChecklistCollaboratorMapping checklistCollaboratorMapping;

  @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = {CascadeType.DETACH})
  @JoinColumn(name = "checklists_id", nullable = false, insertable = true, updatable = false)
  private Checklist checklist;

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private State.ChecklistCollaborator reviewState;

  @Column(columnDefinition = "text", nullable = false)
  private String comments;
}
