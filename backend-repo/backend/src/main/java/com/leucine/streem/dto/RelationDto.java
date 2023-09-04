package com.leucine.streem.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelationDto implements Serializable {
    private String id;
    private String externalId;
    private String displayName;
    private JsonNode variables;
    private String objectTypeId;
    private Integer orderTree;
    private RelationTargetDto target;
    private Boolean isMandatory;
}
