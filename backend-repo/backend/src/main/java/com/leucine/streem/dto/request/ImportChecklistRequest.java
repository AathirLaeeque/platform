package com.leucine.streem.dto.request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportChecklistRequest extends CreateChecklistRequest {
  private String id;
  private List<ImportStageRequest> stageRequests;
  private List<ImportParameterRequest> parameterRequests;
}
