package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParameterVerificationListViewDto {
  private String id;
  private UserAuditDto modifiedBy;
  private UserAuditDto createdBy;
  private UserAuditDto requestedTo;
  private String modifiedAt;
  private String createdAt;
  private String jobId;
  private String code;
  private String taskId;
  private String taskName;
  private String stageId;
  private String parameterValueId;
  private String parameterName;
  private String processName;
  private String verificationType;
  private String verificationStatus;
  private String comments;
}
