package com.leucine.streem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaUploadResponse implements Serializable {
  private static final long serialVersionUID = 7544203864098458893L;

  private String mediaId;
  private String name;
  private String originalFilename;
  private String filename;
  private String description;
  private String link;
  private String type;
  private boolean archived;
}

