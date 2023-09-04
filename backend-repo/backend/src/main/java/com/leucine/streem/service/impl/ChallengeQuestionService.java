package com.leucine.streem.service.impl;

import com.leucine.streem.config.JaasServiceProperty;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.service.IChallengeQuestionService;
import com.leucine.streem.service.IRoleService;
import com.leucine.streem.util.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeQuestionService implements IChallengeQuestionService {

  private final RestTemplate jaasRestTemplate;
  private final JaasServiceProperty jaasServiceProperty;

  @Override
  public Response<Object> getAll(boolean archived) {
    log.info("[getAll] Request to get all challenge questions, archived: {}", archived);
    HttpEntity<Response> response = jaasRestTemplate.exchange(
        Utility.toUriString(jaasServiceProperty.getChallengeQuestionsUrl(), Collections.singletonMap("archived", archived)), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Response.class);
    return response.getBody();
  }

}
