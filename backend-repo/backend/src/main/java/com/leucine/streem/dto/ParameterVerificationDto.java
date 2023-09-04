package com.leucine.streem.dto;

import com.leucine.streem.constant.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParameterVerificationDto {
  private String id;
//  TODO change this to audit object
  private UserAuditDto modifiedBy;
  private UserAuditDto createdBy;
  private UserAuditDto requestedTo;
  private String modifiedAt;
  private String createdAt;
  private String verificationType;
  private String verificationStatus;
  private String comments;
  private State.ParameterExecution evaluationState;
}
