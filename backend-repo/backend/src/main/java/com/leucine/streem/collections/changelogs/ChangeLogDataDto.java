package com.leucine.streem.collections.changelogs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLogDataDto {
  private String objectTypeId;
  private String objectId;
  private String collection;
  private String externalId;
  private String reason;
  private Integer oldUsageStatus;
  private Integer newUsageStatus;
  private Info jobInfo;
}
