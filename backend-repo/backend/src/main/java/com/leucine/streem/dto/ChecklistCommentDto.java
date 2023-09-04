package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Accessors(chain = true)
  public class ChecklistCommentDto implements Serializable {
  private static final long serialVersionUID = 5473542386425089379L;
  private ChecklistBasicDto checklist;
  private CollaboratorCommentDto comment;
  private List<ChecklistCollaboratorDto> collaborators;

}
