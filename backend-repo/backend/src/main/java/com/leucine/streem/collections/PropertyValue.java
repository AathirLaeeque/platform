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
public class PropertyValue implements Serializable {
    private static final long serialVersionUID = -4745538810687284344L;
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String externalId;
    private String displayName;
    private String value;
    private List<PropertyOption> choices; // List of selected choices ids

}
