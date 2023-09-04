package com.leucine.streem.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.helper.UserAuditIdentifiableBase;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.USE_CASE)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class UseCase extends UserAuditIdentifiableBase implements Serializable {

  @Serial
  private static final long serialVersionUID = 8755861581918714170L;
  public static final String FACILITY_ID = "checklist.facility.id";
  public static final String DEFAULT_SORT = "orderTree";

  @Column(columnDefinition = "varchar", length = 255, nullable = false)
  private String name;

  @Column(columnDefinition = "varchar", length = 255)
  private String label;

  @Column(columnDefinition = "text")
  private String description;

  @Column(columnDefinition = "integer", nullable = false)
  private Integer orderTree;

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb default '{}'", nullable = false)
  private JsonNode metadata;

  @Column(columnDefinition = "boolean default false", nullable = false)
  private boolean archived = false;
}
