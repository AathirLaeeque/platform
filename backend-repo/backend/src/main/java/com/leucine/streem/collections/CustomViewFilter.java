package com.leucine.streem.collections;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomViewFilter {
  private String constraint;
  private String displayName;
  private String key;
  private String value;
}
