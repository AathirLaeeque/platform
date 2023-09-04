package com.leucine.streem.dto.request;

import lombok.Data;

@Data
public class ReloginRequest {
  private String username;
  private String password;
  private String idToken;
  private String accessToken;
}