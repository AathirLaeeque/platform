package com.leucine.streem.dto;

import com.leucine.streem.constant.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StageDetailsDto implements Serializable {
  private static final long serialVersionUID = -6714070537687841563L;

  private String jobId;
  private State.Job jobState;
  private StageDto stage;
  private List<StageExecutionReportDto> stageReports;
}
