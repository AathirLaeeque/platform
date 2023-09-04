package com.leucine.streem.collections;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
public class MappedRelation implements Serializable {
    private static final long serialVersionUID = 6621115003106358480L;
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String externalId;
    private String displayName;
    private List<MappedRelationTarget> targets;
    private String objectTypeId;
    private Integer flags;
}
