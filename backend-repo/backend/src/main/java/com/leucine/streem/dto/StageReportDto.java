package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StageReportDto implements Serializable {
  private static final long serialVersionUID = -2318280603826263586L;

  private String id;
  private String name;
  private int orderTree;
  private Long totalDuration;
  private Long averageTaskCompletionDuration;
  private int totalTaskExceptions = 0;

  private List<TaskReportDto> tasks = new ArrayList<>();
}
