package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelationValueTargetDto implements Serializable {
    private String id; // Object id
    private String externalId; // Object external Id
    private String displayName; // Object display name
    private String collection;
}
