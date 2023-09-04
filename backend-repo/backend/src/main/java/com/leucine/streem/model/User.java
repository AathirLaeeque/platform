package com.leucine.streem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.helper.BaseEntity;
import com.leucine.streem.util.DateTimeUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.USER)
public class User extends BaseEntity implements Serializable {
  public static final Long SYSTEM_USER_ID = 1L;
  private static final long serialVersionUID = 8092011369307681227L;

  @Column(columnDefinition = "varchar", length = 255, nullable = false)
  private String employeeId;

  @Column(columnDefinition = "varchar", length = 255, nullable = false)
  private String firstName;

  @Column(columnDefinition = "varchar", length = 255)
  private String lastName;

  @Column(columnDefinition = "varchar", length = 255, nullable = false)
  private String email;

  @Column(columnDefinition = "boolean default false", nullable = false)
  @JsonIgnore
  private boolean archived = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organisations_id", referencedColumnName = "id", nullable = false)
  private Organisation organisation;

  @Column(columnDefinition = "bigint", name = "organisations_id", updatable = false, insertable = false)
  private Long organisationId;

  @JsonIgnore
  @Column(updatable = false)
  private Long createdAt;

  @JsonIgnore
  @Column(updatable = false)
  private Long modifiedAt;

  @PrePersist
  public void beforePersist() {
    createdAt = DateTimeUtils.now();
    modifiedAt = DateTimeUtils.now();
  }

  @PreUpdate
  public void beforeUpdate() {
    modifiedAt = DateTimeUtils.now();
  }

}
