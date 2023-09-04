package com.leucine.streem.dto;

import com.leucine.streem.constant.State;
import com.leucine.streem.dto.projection.ChecklistCollaboratorView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistInfoDto implements Serializable {
  private static final long serialVersionUID = 2573546809858378656L;
  private String id;
  private String name;
  private String code;
  private String description;
  private State.Checklist state;
  private Integer versionNumber;
  private int phase;
  private List<ChecklistCollaboratorView> authors;
  private List<ChecklistSignOffDto> signOff;
  private ReleaseDto release;
  private List<VersionDto> versions;
  private AuditDto audit;
}
