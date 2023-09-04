package com.leucine.streem.dto.request;

import lombok.Data;

//TODO check if media request can be used, separated out because link is required in job audit
@Data
public class ExecuteMediaPrameterRequest {
  private String mediaId;
  private String name;
  private String description;
  private String link;
  private String type;
  private String reason;
  private Boolean archived;
}
