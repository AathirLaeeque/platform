package com.leucine.streem.collections;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.leucine.streem.constant.CollectionMisc;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
public class Property implements Serializable {
    private static final long serialVersionUID = -482133153972661037L;
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String externalId;
    private String displayName;
    private String description;
    private String placeHolder;
    private int flags;
    private String autogeneratePrefix;
    private int sortOrder;
    private List<PropertyOption> options;
    private List<PropertyValidation> validations;
    private CollectionMisc.PropertyType inputType;
    private int usageStatus;
}
