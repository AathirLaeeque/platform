package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"ruleId", "parameterId"})
public class RuleImpactedByDto implements Serializable {
  @Serial
  private static final long serialVersionUID = -5147036599583153291L;
  private String ruleId;
  private Long parameterId;
}
