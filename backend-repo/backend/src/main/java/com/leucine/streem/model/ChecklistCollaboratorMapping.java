package com.leucine.streem.model;

import com.leucine.streem.constant.State;
import com.leucine.streem.constant.TableName;
import com.leucine.streem.constant.Type;
import com.leucine.streem.model.helper.UserAuditIdentifiableBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = TableName.CHECKLIST_COLLABORATOR_MAPPING)
public class ChecklistCollaboratorMapping extends UserAuditIdentifiableBase implements Serializable {

  private static final long serialVersionUID = 4160488966562238516L;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "checklists_id", nullable = false, insertable = true, updatable = false)
  private Checklist checklist;

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private Type.Collaborator type;

  @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = {CascadeType.DETACH})
  @JoinColumn(name = "users_id", nullable = false, insertable = true, updatable = false)
  private User user;

  @OneToMany(mappedBy = "checklistCollaboratorMapping", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @OrderBy("createdAt")
  private List<ChecklistCollaboratorComments> comments = new ArrayList<>();

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private State.ChecklistCollaborator state;

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private State.ChecklistCollaboratorPhaseType phaseType;

  /*-- phase is maintained and initialized independently for every phaseType --*/
  @Column(columnDefinition = "integer default 1", nullable = false)
  private Integer phase = 1;

  @Column(columnDefinition = "integer default 1", nullable = false)
  private Integer orderTree = 1;


  public ChecklistCollaboratorMapping(Checklist checklist, User user, Type.Collaborator type, Integer phase, State.ChecklistCollaboratorPhaseType phaseType, User principalUserEntity) {
    this.checklist = checklist;
    this.user = user;
    this.type = type;
    state = State.ChecklistCollaborator.NOT_STARTED;
    this.phase = phase;
    createdBy = principalUserEntity;
    modifiedBy = principalUserEntity;
    this.phaseType = phaseType;
  }


  public boolean isPrimary(){
    return type==Type.Collaborator.PRIMARY_AUTHOR;
  }

}
