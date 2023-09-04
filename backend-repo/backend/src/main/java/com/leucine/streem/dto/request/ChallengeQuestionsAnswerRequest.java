package com.leucine.streem.dto.request;

import lombok.Data;

@Data
public class ChallengeQuestionsAnswerRequest {
  private Integer id;
  private String answer;
  private String token;
}
