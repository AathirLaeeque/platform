package com.leucine.streem.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.collections.PropertyOption;
import com.leucine.streem.constant.CollectionMisc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

// TODO Separate this dto for number parameter validations
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterRelationPropertyValidationDto implements Serializable {
    private String id;
    private String value;
    private List<PropertyOption> options;
    private CollectionMisc.DateUnit dateUnit;
    private CollectionMisc.PropertyValidationConstraint constraint; // TODO sep enum
    private String propertyId;
    private String propertyExternalId;
    private String propertyDisplayName;
    private CollectionMisc.PropertyType propertyInputType;
    private String collection;
    private String urlPath;
    private JsonNode variables;
    private String relationId;
    private String objectTypeId;
    private String objectTypeExternalId;
    private String objectTypeDisplayName;
    private String errorMessage;
}
