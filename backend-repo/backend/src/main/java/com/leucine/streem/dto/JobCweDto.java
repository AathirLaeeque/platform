package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobCweDto implements Serializable {
  private static final long serialVersionUID = -495272068356150492L;

  private String reason;
  private String comment;
  private List<MediaDto> medias;
}
