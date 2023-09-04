package com.leucine.streem.model.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Accessors(chain = true)
public class JobAuditParameter {
  Map<Integer, JobAuditParameterValue> parameters = new HashMap<>();
}
