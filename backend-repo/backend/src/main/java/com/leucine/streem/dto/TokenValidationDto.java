package com.leucine.streem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class TokenValidationDto implements Serializable {
  private static final long serialVersionUID = -5925343435676863588L;

  private String message;
  private String token;
}
