package com.leucine.streem.collections.changelogs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.leucine.streem.constant.CollectionMisc;
import com.leucine.streem.constant.CollectionName;
import com.leucine.streem.dto.UserAuditDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Document(CollectionName.CHANGE_LOGS)
@Builder
@ToString
public class EntityObjectChangeLog implements Serializable  {
  @Serial
  private static final long serialVersionUID = 1699084697626665174L;
  @Id
  @JsonSerialize(using = ToStringSerializer.class)
  private ObjectId id;
  private String objectTypeId;

  private String objectId;
  private String collection;
  private String externalId;
  private String reason;
  private String entityId;
  private CollectionMisc.ChangeLogType entityType;
  private String entityExternalId;
  private String entityCollection;
  private String entityDisplayName;
  private CollectionMisc.ChangeLogInputType entityInputType;
  @Field("old")
  @JsonProperty("old")
  private List<ChangeLogInputData> oldEntityData;
  @Field("new")
  @JsonProperty("new")
  private List<ChangeLogInputData> newEntityData;
  private Info info;
  private UserAuditDto modifiedBy;
  private Long modifiedAt;
  private Integer version;
  private Long createdAt;
  private EntityObjectUsageStatus usageStatus;
  private String facilityId;
}
