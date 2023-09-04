package com.leucine.streem.collections.changelogs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityObjectUsageStatus {

  @Field("old")
  @JsonProperty("old")
  private Integer oldStatus;
  @Field("new")
  @JsonProperty("new")
  private Integer newStatus;
}
