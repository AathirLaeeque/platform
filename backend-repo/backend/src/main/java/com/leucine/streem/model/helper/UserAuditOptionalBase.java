package com.leucine.streem.model.helper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leucine.streem.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serial;

/**
 * Use this super class in models if modifiedAt, modifiedBy can be null
 */
@Getter
@Setter
@Accessors(chain = true)
@MappedSuperclass
public class UserAuditOptionalBase extends BaseEntity {

  @Serial
  private static final long serialVersionUID = 2606029114464879168L;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH})
  @JoinColumn(name = "created_by", referencedColumnName = "id", updatable = false, nullable = false)
  public User createdBy;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH})
  @JoinColumn(name = "modified_by", referencedColumnName = "id")
  public User modifiedBy;

  @JsonIgnore
  @Column(columnDefinition = "bigint", updatable = false, nullable = false)
  public Long createdAt;

  @Column(columnDefinition = "bigint")
  public Long modifiedAt;
}
