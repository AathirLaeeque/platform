package com.leucine.streem.dto.request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MediaRequest {
  private Long mediaId;
  private String name;
  private String description;
}
