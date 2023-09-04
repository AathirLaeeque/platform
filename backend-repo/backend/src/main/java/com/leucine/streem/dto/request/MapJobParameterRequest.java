package com.leucine.streem.dto.request;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class MapJobParameterRequest {
  private Map<Long, Integer> mappedParameters = new HashMap<>();
}
