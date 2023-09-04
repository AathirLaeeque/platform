package com.leucine.streem.dto.request;

import lombok.Data;

import java.util.Set;

@Data
public class ChecklistSignOffUser {
  private Long userId;
  private Integer orderTree;
}
