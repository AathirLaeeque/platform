package com.leucine.streem.service;

import com.leucine.streem.dto.response.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

public interface IChallengeQuestionService {
  Response<Object> getAll(boolean archived);
}
