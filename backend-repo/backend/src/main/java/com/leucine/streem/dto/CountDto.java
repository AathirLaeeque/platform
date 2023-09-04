package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountDto implements Serializable {
  private static final long serialVersionUID = -4351644386311885170L;

  private String count;
}
