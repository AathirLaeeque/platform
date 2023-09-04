package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaDto implements Serializable {
  private static final long serialVersionUID = 3528349742418350679L;
  private String id;
  private String name;
  private String originalFilename;
  private String filename;
  private String description;
  private String link;
  private String type;
  private boolean archived;
}
