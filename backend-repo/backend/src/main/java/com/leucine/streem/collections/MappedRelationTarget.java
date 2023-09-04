package com.leucine.streem.collections;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.leucine.streem.constant.CollectionMisc;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class MappedRelationTarget implements Serializable {
    private static final long serialVersionUID = -7835326032487939598L;
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private CollectionMisc.RelationType type;
    private String collection;
    private String externalId;
    private String displayName;
}
