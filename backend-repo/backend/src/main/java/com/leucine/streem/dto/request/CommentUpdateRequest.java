package com.leucine.streem.dto.request;

import lombok.Data;

@Data
public class CommentUpdateRequest {
  private Long id;
  private String comments;
}
