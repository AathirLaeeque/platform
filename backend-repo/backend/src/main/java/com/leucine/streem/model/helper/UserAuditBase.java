package com.leucine.streem.model.helper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leucine.streem.model.User;
import com.leucine.streem.util.DateTimeUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@MappedSuperclass
public class UserAuditBase {
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "created_by", referencedColumnName = "id", updatable = false, nullable = false)
  public User createdBy;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "modified_by", referencedColumnName = "id", nullable = false)
  public User modifiedBy;

  @JsonIgnore
  @Column(columnDefinition = "bigint", updatable = false, nullable = false)
  private Long createdAt;

  @JsonIgnore
  @Column(columnDefinition = "bigint", nullable = false)
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
