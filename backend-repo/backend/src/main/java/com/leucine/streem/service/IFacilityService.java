package com.leucine.streem.service;

import com.leucine.streem.dto.response.Response;
import org.springframework.data.domain.Pageable;

public interface IFacilityService {
  Response<Object> getAllFacilities(String filters, Pageable pageable);
}
