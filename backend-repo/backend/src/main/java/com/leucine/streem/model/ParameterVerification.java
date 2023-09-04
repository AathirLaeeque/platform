package com.leucine.streem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leucine.streem.constant.State;
import com.leucine.streem.constant.TableName;
import com.leucine.streem.constant.Type;
import com.leucine.streem.model.helper.BaseEntity;
import com.leucine.streem.util.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serial;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TableName.PARAMETER_VERIFICATIONS)
public class ParameterVerification extends BaseEntity {
  @Serial
  private static final long serialVersionUID = 4593012668682993733L;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "parameter_values_id", updatable = false)
  private ParameterValue parameterValue;

  @Column(name = "parameter_values_id", columnDefinition = "bigint", insertable = false, updatable = false)
  private Long parameterValueId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "jobs_id", updatable = false)
  private Job job;

  @Column(name = "jobs_id", columnDefinition = "bigint", insertable = false, updatable = false)
  private Long jobId;

  // it stores the user who was assigned for peer verification
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "users_id", updatable = false, nullable = false)
  private User user;

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private Type.VerificationType verificationType;

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private State.ParameterVerification verificationStatus;

  // it stores the user who added the parameter value
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH})
  @JoinColumn(name = "created_by", referencedColumnName = "id", nullable = false)
  private User createdBy;

  // it stores the user who performed action
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH})
  @JoinColumn(name = "modified_by", referencedColumnName = "id", nullable = false)
  private User modifiedBy;

  @JsonIgnore
  @Column(columnDefinition = "bigint", updatable = false, nullable = false)
  private Long modifiedAt;

  @JsonIgnore
  @Column(columnDefinition = "bigint", updatable = false, nullable = false)
  private Long createdAt;

  @Column(columnDefinition = "text")
  private String comments;

  @PrePersist
  public void beforePersist() {
    modifiedAt = DateTimeUtils.now();
  }
}
