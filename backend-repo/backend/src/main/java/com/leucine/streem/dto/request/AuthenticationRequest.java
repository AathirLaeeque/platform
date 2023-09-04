package com.leucine.streem.dto.request;

import lombok.Data;

@Data
public class AuthenticationRequest {
  private String username;
  private String password;
  private String idToken;
}