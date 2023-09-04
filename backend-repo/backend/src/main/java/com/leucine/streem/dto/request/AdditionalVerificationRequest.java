package com.leucine.streem.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdditionalVerificationRequest {
  private String identifier;
  private String type;
  private String token;
}
