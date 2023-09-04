package com.leucine.streem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.constant.Action;
import com.leucine.streem.constant.AuditEvent;
import com.leucine.streem.constant.Severity;
import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.helper.BaseEntity;
import com.leucine.streem.util.DateTimeUtils;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = TableName.JOB_AUDITS)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class JobAudit extends BaseEntity implements Serializable {
  public static final String DEFAULT_SORT = "triggeredAt";
  private static final long serialVersionUID = -5993281771588465119L;
  @Column(columnDefinition = "bigint", nullable = false)
  private Long organisationsId;

  @Column(columnDefinition = "bigint", nullable = false)
  private Long triggeredBy;

  @Column(name = "jobs_id", columnDefinition = "bigint", nullable = false)
  private Long jobId;

  @Column(name = "stages_id", columnDefinition = "bigint", nullable = true)
  private Long stageId;

  @Column(name = "tasks_id", columnDefinition = "bigint", nullable = true)
  private Long taskId;

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private Action.Audit action;

  @Column(columnDefinition = "text")
  private String details;

  @org.hibernate.annotations.Type(type = "jsonb")
  @Column(name = "parameters", columnDefinition = "jsonb default '{}'", nullable = false)
  private JsonNode parameters;

  @JsonIgnore
  @Column
  private Long triggeredAt;

  @PrePersist
  public void beforePersist() {
    triggeredAt = DateTimeUtils.now();
  }

}
