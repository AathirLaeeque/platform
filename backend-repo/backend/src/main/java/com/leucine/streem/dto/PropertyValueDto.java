package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyValueDto implements Serializable {
  private static final long serialVersionUID = -9093479117040775702L;

  private String id;
  private String name;
  private String label;
  private String value;
}
