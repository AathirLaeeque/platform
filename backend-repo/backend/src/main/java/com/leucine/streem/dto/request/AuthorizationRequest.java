package com.leucine.streem.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorizationRequest {
  private String path;
  private String method;
}