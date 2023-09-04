package com.leucine.streem.collections;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomViewColumn {
  private String id;
  private String type;
  private String displayName;
  private String triggerType;
  private Integer orderTree;
}
