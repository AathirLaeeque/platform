package com.leucine.streem.dto;

import lombok.Data;

@Data
public class UserBasicInformationUpdateRequest {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
}
