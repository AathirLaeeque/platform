package com.leucine.streem.dto.request;

import lombok.Data;

@Data
public class RefreshTokenRequest {
  private String accessToken;
  private String refreshToken;
}
