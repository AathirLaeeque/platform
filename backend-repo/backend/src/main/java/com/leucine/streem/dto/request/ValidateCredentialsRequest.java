package com.leucine.streem.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ValidateCredentialsRequest {
  private String password;
  private String purpose;
}
