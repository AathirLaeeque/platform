package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelationValueDto implements Serializable {
    private String id;
    private String externalId; // Object type external id
    private String displayName; // Object type display name
    private List<RelationValueTargetDto> targets;
}
