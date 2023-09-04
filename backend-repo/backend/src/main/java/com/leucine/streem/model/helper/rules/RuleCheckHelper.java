package com.leucine.streem.model.helper.rules;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"constraint", "input", "isHidden"})
public class RuleCheckHelper {
  private String constraint;
  private List<String> input;
  private boolean isHidden;
}
