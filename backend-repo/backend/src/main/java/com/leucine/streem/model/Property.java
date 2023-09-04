package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.leucine.streem.constant.Type;
import com.leucine.streem.model.helper.UserAuditIdentifiableBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.PROPERTIES)
public class Property extends UserAuditIdentifiableBase implements Serializable {
  public static final String ORGANISATION_ID = "organisationId";
  public static final String TYPE = "type";
  public static final String ARCHIVED = "archived";
  public static final String ORDER_TREE = "orderTree";

  @Serial
  private static final long serialVersionUID = -5315457474065248082L;

  @Column(columnDefinition = "varchar", length = 255, nullable = false)
  private String name;

  @Column(columnDefinition = "varchar", length = 255, nullable = false)
  private String label;

  @Column(columnDefinition = "varchar", length = 255)
  private String placeHolder;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "use_cases_id", nullable = false, insertable = false, updatable = false)
  private UseCase useCase;

  @Column(name = "use_cases_id", columnDefinition = "bigint", updatable = false, insertable = false)
  private Long useCaseId;

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private Type.PropertyType type;

  @Column(columnDefinition = "integer", nullable = false)
  private Integer orderTree;

  @Column(columnDefinition = "boolean default false", nullable = false)
  private boolean isGlobal = false;

  @Column(columnDefinition = "boolean default false", nullable = false)
  private boolean archived = false;
}
