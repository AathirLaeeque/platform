package com.leucine.streem.dto;

import com.leucine.streem.constant.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Accessors(chain = true)
public class CollaboratorCommentDto implements Serializable {
  private static final long serialVersionUID = 6865853788788882547L;
  
  private String id;
  private String comments;
  private Long commentedAt;
  private UserAuditDto commentedBy;
  private Long modifiedAt;
  private State.ChecklistCollaborator state;
  private int phase;


}
