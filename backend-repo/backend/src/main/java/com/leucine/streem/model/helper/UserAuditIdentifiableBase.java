package com.leucine.streem.model.helper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leucine.streem.model.User;
import com.leucine.streem.util.DateTimeUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serial;

@Getter
@Setter
@Accessors(chain = true)
@MappedSuperclass
public class UserAuditIdentifiableBase extends BaseEntity {

  @Serial
  private static final long serialVersionUID = -4489966120680536718L;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH})
  @JoinColumn(name = "created_by", referencedColumnName = "id", updatable = false, nullable = false)
  public User createdBy;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH})
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
