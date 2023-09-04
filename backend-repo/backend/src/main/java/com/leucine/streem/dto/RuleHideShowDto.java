package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleHideShowDto {
  private Set<String> hide = new HashSet<>();
  private Set<String> show = new HashSet<>();
}
