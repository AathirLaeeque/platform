package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class VersionDto implements Serializable {
  private static final long serialVersionUID = -6080625787084473132L;
  private String id;
  private String code;
  private String name;
  private Integer versionNumber;
  private Long deprecatedAt;
  private AuditDto audit;
}
