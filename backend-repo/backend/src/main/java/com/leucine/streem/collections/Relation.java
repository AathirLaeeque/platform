package com.leucine.streem.collections;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@ToString
public class Relation implements Serializable {
    private static final long serialVersionUID = -2680854434235771545L;
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String externalId;
    private String displayName;
    private boolean usable;
    private RelationTarget target;
    private int sortOrder;
    private Map<String, String> variables;
    private String description;
    private int usageStatus;
    private String objectTypeId;
    private Integer flags;
}
