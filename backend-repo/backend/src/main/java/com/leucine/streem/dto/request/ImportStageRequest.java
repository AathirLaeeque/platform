package com.leucine.streem.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ImportStageRequest extends StageRequest {
  private String id;
  private List<ImportTaskRequest> taskRequests;
}
