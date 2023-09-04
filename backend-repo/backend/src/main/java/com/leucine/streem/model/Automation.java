package com.leucine.streem.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.constant.TableName;
import com.leucine.streem.constant.Type;
import com.leucine.streem.model.helper.UserAuditIdentifiableBase;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.AUTOMATION)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Automation extends UserAuditIdentifiableBase implements Serializable {
  @Serial
  private static final long serialVersionUID = 5557529931910517186L;

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private Type.AutomationType type;

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private Type.AutomationActionType actionType;

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private Type.TargetEntityType targetEntityType;

  @org.hibernate.annotations.Type(type = "jsonb")
  @Column(columnDefinition = "jsonb default '{}'", nullable = false)
  private JsonNode actionDetails;

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private Type.AutomationTriggerType triggerType;

  @org.hibernate.annotations.Type(type = "jsonb")
  @Column(columnDefinition = "jsonb default '{}'", nullable = false)
  private JsonNode triggerDetails;

  @Column(columnDefinition = "boolean default false", nullable = false)
  private boolean archived = false;
}
