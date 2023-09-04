package com.leucine.streem.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class Error {
  private String id;
  private String userId;
  private String type;
  private String code;
  private String message;
}
