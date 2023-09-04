package com.leucine.streem.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditDto implements Serializable {
  private static final long serialVersionUID = -6080625787084473132L;

  private Long createdAt;
  private Long modifiedAt;
  private UserAuditDto modifiedBy;
  private UserAuditDto createdBy;
}
