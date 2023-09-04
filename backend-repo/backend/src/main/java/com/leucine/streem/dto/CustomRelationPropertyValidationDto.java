package com.leucine.streem.dto;

import com.leucine.streem.constant.CollectionMisc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomRelationPropertyValidationDto implements Serializable {
    private String id;
    private String value;
    private CollectionMisc.PropertyValidationConstraint constraint; // TODO sep enum
    private CollectionMisc.PropertyType propertyInputType;
    private String errorMessage;
}
