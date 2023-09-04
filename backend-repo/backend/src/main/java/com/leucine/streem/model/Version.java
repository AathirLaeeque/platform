package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.leucine.streem.constant.Type;
import com.leucine.streem.model.helper.UserAuditIdentifiableBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@Entity
@Table(name = TableName.VERSIONS)
public class Version extends UserAuditIdentifiableBase implements Serializable {
  private static final long serialVersionUID = 462253080637571057L;

  @Column(columnDefinition = "bigint", nullable = false)
  private Long ancestor;

  @Column(columnDefinition = "bigint")
  private Long parent;

  @Column(columnDefinition = "bigint")
  private Long self;

  @Column(columnDefinition = "bigint")
  private Long versionedAt;

  @Column(columnDefinition = "bigint")
  private Long deprecatedAt;

  @Column(columnDefinition = "bigint")
  private Integer version;

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private Type.EntityType type;

}
