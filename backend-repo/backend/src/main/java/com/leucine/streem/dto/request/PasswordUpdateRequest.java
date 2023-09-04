package com.leucine.streem.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PasswordUpdateRequest {
  private Long userId;
  private String password;
  private String confirmPassword;
  private String token;
}
