package com.leucine.streem.collections;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.leucine.streem.collections.changelogs.UserInfo;
import com.leucine.streem.constant.CollectionName;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(CollectionName.OBJECT_TYPES)
public class ObjectType implements Serializable {
    private static final long serialVersionUID = 5852979962435251779L;
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private Integer version;
    private String collection;
    private String externalId;
    private String displayName;
    private String pluralName;
    private String description;
    private List<Property> properties = new ArrayList<>();
    private List<Relation> relations = new ArrayList<>();
    // TODO see enum UsageStatus
    private int usageStatus;
    private Long modifiedAt;
    private UserInfo modifiedBy;
    private Long createdAt;
    private UserInfo createdBy;
    private Integer flags;
}

