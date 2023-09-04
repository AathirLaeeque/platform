package com.leucine.streem.dto.request;

import lombok.Data;

@Data
public class UserRegistrationRequest {
  private String username;
  private String password;
  private String confirmPassword;
  private String token;
}
