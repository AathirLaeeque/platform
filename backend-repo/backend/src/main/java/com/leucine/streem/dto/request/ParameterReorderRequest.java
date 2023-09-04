package com.leucine.streem.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class ParameterReorderRequest {
  Map<Long, Integer> parametersOrder = new HashMap<>();
}
