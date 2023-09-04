package com.leucine.streem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leucine.streem.constant.State;
import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.helper.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.PARAMETER_VALUE_APPROVALS)
public class ParameterValueApproval extends BaseEntity implements Serializable {
  private static final long serialVersionUID = 2117036308404556959L;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH})
  @JoinColumn(name = "users_id", referencedColumnName = "id", nullable = false)
  public User user;

  @JsonIgnore
  @Column(columnDefinition = "bigint", updatable = false, nullable = false)
  private Long createdAt;

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private State.ParameterValue state;

}

