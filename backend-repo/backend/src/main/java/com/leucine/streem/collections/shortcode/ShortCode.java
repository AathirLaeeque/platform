package com.leucine.streem.collections.shortcode;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.leucine.streem.collections.changelogs.UserInfo;
import com.leucine.streem.constant.CollectionName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Builder
@Document(CollectionName.SHORT_CODE)
@CompoundIndex(def = "{'shortCode': 1, 'facilityId': 1}", unique = true)
public class ShortCode implements Serializable {
  @Serial
  private static final long serialVersionUID = 4453012158956961789L;
  @Id
  @JsonSerialize(using = ToStringSerializer.class)
  private ObjectId id;
  private ShortCodeData data;
  private String shortCode;
  private String facilityId;
  private Long createdAt;
  private Long modifiedAt;
  private UserInfo createdBy;
  private UserInfo modifiedBy;
}
