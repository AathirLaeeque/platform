package com.leucine.streem.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RuleDto implements Serializable {
  @Serial
  private static final long serialVersionUID = 5985319503197090228L;

  private String id;
  private String constraint;
  private String[] input;
  private RuleEntityIdDto hide;
  private RuleEntityIdDto show;
}
