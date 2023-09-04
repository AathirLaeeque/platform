package com.leucine.streem.collections.partial;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@ToString
public class PartialObjectType {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String externalId;
    private String displayName;
    private Integer version;
}
