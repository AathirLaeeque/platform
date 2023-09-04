package com.leucine.streem.dto.request;

import lombok.Data;

@Data
public class ImportMediaRequest extends MediaRequest {
  private String link;
  private String fileName;
}
