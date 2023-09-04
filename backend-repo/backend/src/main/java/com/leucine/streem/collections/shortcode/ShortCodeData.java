package com.leucine.streem.collections.shortcode;

import com.leucine.streem.constant.CollectionMisc;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortCodeData {
  private String objectId;
  private String collection;
  private String externalId;
  private String objectTypeId;
  private String displayName;
  private CollectionMisc.RelationType entityType;
}
