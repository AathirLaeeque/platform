package com.leucine.streem.dto.request;

import com.leucine.streem.constant.JobCweReason;
import lombok.Data;

import java.util.List;

@Data
public class JobCweDetailRequest {
  private JobCweReason reason;
  private String comment;
  private List<MediaRequest> medias;
}
