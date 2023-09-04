package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RuleEntityIdDto implements Serializable {
  private static final long serialVersionUID = 4019400305115762149L;
  private List<String> stages = new ArrayList<>();
  private List<String> tasks = new ArrayList<>();
  private List<String> parameters = new ArrayList<>();
}
