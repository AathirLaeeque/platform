package com.leucine.streem.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ImportTaskRequest extends TaskRequest {
  private String id;
  private String timerOperator;
  private boolean hasStop;
  private boolean isTimed;
  private Long maxPeriod;
  private Long minPeriod;
  private boolean isMandatory;
  private List<ImportParameterRequest> parameterRequests;
  private List<AutomationRequest> automationRequests;
  private List<ImportMediaRequest> mediaRequests;
}
