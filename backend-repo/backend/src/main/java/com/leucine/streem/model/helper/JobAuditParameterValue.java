package com.leucine.streem.model.helper;

import com.leucine.streem.constant.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Accessors(chain = true)
public class JobAuditParameterValue {
  Object value;
  Type.Parameter type;
}
