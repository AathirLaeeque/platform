package com.leucine.streem.dto;

import com.leucine.streem.collections.PropertyOption;
import com.leucine.streem.constant.CollectionMisc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistRelationPropertyValidationDto implements Serializable {
    // TODO is this required ?
    private String id;
    private String value;
    private List<PropertyOption> options;
    private CollectionMisc.DateUnit dateUnit;
    private CollectionMisc.PropertyValidationConstraint constraint; // TODO sep enum
    private String propertyId;
    private String propertyDisplayName;
    private CollectionMisc.PropertyType propertyInputType;
    private String errorMessage;
}
