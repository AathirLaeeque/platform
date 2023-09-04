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
public class ChecklistDto implements Serializable {
  private static final long serialVersionUID = -3026728201572430753L;

  private String id;
  private String name;
  private String description;
  private String code;
  private Long useCaseId;
  private State.Checklist state;
  private Integer versionNumber;
  private boolean archived = false;
  private List<StageDto> stages;
  private List<PropertyValueDto> properties;
  private List<RelationDto> relations;
  private AuditDto audit;
  private int phase;
  private JsonNode jobLogColumns;
  private List<ChecklistCollaboratorDto> collaborators;
  private List<CollaboratorCommentDto> comments;
  private boolean isGlobal;
  private List<ParameterDto> parameters;
}
