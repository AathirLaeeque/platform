package com.leucine.streem.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.constant.Action;
import com.leucine.streem.constant.AuditEvent;
import com.leucine.streem.constant.ObjectType;
import com.leucine.streem.constant.Severity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class JobAuditDto implements Serializable {
  private static final long serialVersionUID = 7142929199287611545L;

  private String id;
  private String jobId;
  private String stageId;
  private String taskId;
  private ObjectType object;
  private AuditEvent auditEvent;
  private Action.Audit action;
  private Severity severity;
  private String details;
  private JsonNode parameters;
  private Long triggeredAt;
  private Long triggeredBy;
}
