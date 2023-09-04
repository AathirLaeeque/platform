package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ChecklistReviewDto implements Serializable {
  private static final long serialVersionUID = 210794324153321214L;
  private ChecklistBasicDto checklist;
  private List<ChecklistCollaboratorDto> collaborators;
}
