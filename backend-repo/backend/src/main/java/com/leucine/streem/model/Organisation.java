package com.leucine.streem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.helper.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.ORGANISATIONS)
public class Organisation extends BaseEntity implements Serializable {
  private static final long serialVersionUID = -37466742770866907L;

  @Column(columnDefinition = "varchar", length = 255, nullable = false)
  private String name;

  @Column(columnDefinition = "boolean default false", nullable = false)
  private boolean archived = false;

  @OneToMany(mappedBy = "organisation", cascade = CascadeType.ALL)
  private Set<Facility> facilities;

  @Column(columnDefinition = "text", nullable = false)
  private String fqdn;

  @JsonIgnore
  @Column(columnDefinition = "bigint", updatable = false, nullable = false)
  private Long createdAt;

  @JsonIgnore
  @Column(columnDefinition = "bigint", nullable = false)
  private Long modifiedAt;

}
