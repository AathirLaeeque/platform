package com.leucine.streem.controller.impl;

import com.leucine.streem.controller.IChallengeQuestionController;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.service.IChallengeQuestionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ChallengeQuestionController implements IChallengeQuestionController {

  private final IChallengeQuestionService challengeQuestionService;


  @Override
  public Response<Object> getAll(boolean archived) {
    return challengeQuestionService.getAll(archived);
  }
}
