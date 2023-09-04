package com.leucine.streem.dto;

import com.leucine.streem.constant.Action;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ChecklistAuditDto implements Serializable {
  private static final long serialVersionUID = 7142929199287611545L;

  private String id;
  private String checklistId;
  private Action.ChecklistAudit action;
  private String details;
  private Long triggeredAt;
  private Long triggeredBy;
}
