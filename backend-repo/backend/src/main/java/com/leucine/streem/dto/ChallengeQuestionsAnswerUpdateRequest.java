package com.leucine.streem.dto;

import lombok.Data;

@Data
public class ChallengeQuestionsAnswerUpdateRequest {
  private Long userId;
  private Integer id;
  private String answer;
  private String token;
}
