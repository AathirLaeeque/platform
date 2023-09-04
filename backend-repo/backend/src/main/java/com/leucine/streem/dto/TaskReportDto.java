package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskReportDto implements Serializable {
  @Serial
  private static final long serialVersionUID = -2581245892829871957L;

  private String id;
  private String name;
  private int orderTree;
  private List<TaskExceptionDto> exceptions = new ArrayList<>();
}
