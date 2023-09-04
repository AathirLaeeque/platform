package com.leucine.streem.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class StageReorderRequest {
  Map<Long, Long> stagesOrder = new HashMap<>();
}
