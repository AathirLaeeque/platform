package com.leucine.streem.service.impl;

import com.leucine.streem.config.JaasServiceProperty;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.service.IFacilityService;
import com.leucine.streem.util.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacilityService implements IFacilityService {
  private final RestTemplate jaasRestTemplate;
  private final JaasServiceProperty jaasServiceProperty;

  @Override
  public Response<Object> getAllFacilities(String filters, Pageable pageable) {
    log.info("[getAllFacilities] Request to get all facilitties, filters: {}, pageable: {}", filters, pageable);
    HttpEntity<Response> response = jaasRestTemplate.exchange(
        Utility.toUriString(jaasServiceProperty.getFacilityUrl(), filters, pageable), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Response.class);
    return response.getBody();

  }
}
