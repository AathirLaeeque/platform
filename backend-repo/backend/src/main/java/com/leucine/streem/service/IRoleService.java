package com.leucine.streem.service;

import com.leucine.streem.dto.response.Response;
import org.springframework.data.domain.Pageable;

public interface IRoleService {
  Response<Object> getRoles(String filters, Pageable pageable);
}
