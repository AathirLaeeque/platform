package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartialAuditDto implements Serializable {
  @Serial
  private static final long serialVersionUID = -6426986497440727369L;

  private Long modifiedAt;
  private UserAuditDto modifiedBy;
}
