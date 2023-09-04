package com.leucine.streem.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ValidateTokenRequest {
  private String token;
  private String type;
}
