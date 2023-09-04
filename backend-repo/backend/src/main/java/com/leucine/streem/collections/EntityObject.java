package com.leucine.streem.collections;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.leucine.streem.collections.changelogs.UserInfo;
import com.leucine.streem.collections.partial.PartialObjectType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class EntityObject implements Serializable {
  @Serial
  private static final long serialVersionUID = -511445554097248613L;
  @Id
  @JsonSerialize(using = ToStringSerializer.class)
  private ObjectId id;
  private ObjectId objectTypeId;
  private Integer version;
  private String collection;
  private String displayName;
  private String externalId;
  private PartialObjectType objectType;
  private List<PropertyValue> properties = new ArrayList<>();
  private List<MappedRelation> relations = new ArrayList<>();
  private Long modifiedAt;
  private Long createdAt;
  private UserInfo createdBy;
  private UserInfo modifiedBy;
  private int usageStatus;
  private String facilityId;
  private String shortCode;
  private Map<ObjectId, Object> searchable = new HashMap<>();
}
