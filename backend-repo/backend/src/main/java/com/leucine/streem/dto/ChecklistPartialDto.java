package com.leucine.streem.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.constant.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistPartialDto implements Serializable {
  private static final long serialVersionUID = 3539655595812907063L;

  private String id;
  private String name;
  private String code;
  private State.Checklist state;
  private Integer version;
  private boolean archived = false;
  private JsonNode jobLogColumns;
  private List<PropertyValueDto> properties;
  private List<RelationDto> relations;
  private AuditDto audit;
  private boolean isGlobal;
}
