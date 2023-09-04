package com.leucine.streem.dto.request;

import lombok.Data;

@Data
public class SSOVerificationRequest {
  private String clientId;
  private String tenantId;
  private String secretId;
}
