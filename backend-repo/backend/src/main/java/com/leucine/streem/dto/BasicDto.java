package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class BasicDto implements Serializable {
  private static final long serialVersionUID = 4378771848941731114L;

  private String id;
  private String message;
  private PartialAuditDto audit;
}
