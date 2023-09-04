package com.leucine.streem.model.helper.parameter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaterialParameter {
  private String id;
  private String mediaId;
  private String name;
  private String originalFilename;
  private String filename;
  private String link;
  private String type;
  private String description;
  private int quantity;
}
