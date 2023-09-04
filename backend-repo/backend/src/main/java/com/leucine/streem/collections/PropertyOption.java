package com.leucine.streem.collections;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class PropertyOption implements Serializable {
    private static final long serialVersionUID = 9198155002650981032L;
    @Field("_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String displayName;
}
