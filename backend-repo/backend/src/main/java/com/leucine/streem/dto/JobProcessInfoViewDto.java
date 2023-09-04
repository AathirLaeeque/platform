package com.leucine.streem.dto;

import com.leucine.streem.dto.projection.JobProcessInfoView;
import lombok.Data;


@Data
public class JobProcessInfoViewDto implements JobProcessInfoView {
  private String jobId;
  private String jobCode;
  private String processName;
  private String processId;
  private String processCode;
}
