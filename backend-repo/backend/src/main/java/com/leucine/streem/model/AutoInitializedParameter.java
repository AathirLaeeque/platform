package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.helper.UserAuditIdentifiableBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.AUTO_INITIALIZED_PARAMETER)
public class AutoInitializedParameter extends UserAuditIdentifiableBase implements Serializable {
  private static final long serialVersionUID = 1788299988819002886L;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "auto_initialized_parameters_id", updatable = false, nullable = false)
  private Parameter autoInitializedParameter;

  @Column(columnDefinition = "bigint", name = "auto_initialized_parameters_id", updatable = false, insertable = false)
  private Long autoInitializedParameterId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "referenced_parameters_id", updatable = false, nullable = false)
  private Parameter referencedParameter;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "checklists_id", updatable = false, nullable = false)
  private Checklist checklist;

  @Column(columnDefinition = "bigint", name = "checklists_id", updatable = false, insertable = false)
  private Long checklistId;
}
