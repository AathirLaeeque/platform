package com.leucine.streem.dto;

import com.leucine.streem.constant.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskExecutionDto implements Serializable {
  @Serial
  private static final long serialVersionUID = -108793525629189945L;

  private String id;
  private Long period;
  private String correctionReason;
  private boolean correctionEnabled;
  private String reason;
  private List<TaskExecutionAssigneeDto> assignees;
  private UserAuditDto startedBy;
  private Long startedAt;
  private Long endedAt;
  private UserAuditDto endedBy;
  private State.TaskExecution state;
  private PartialAuditDto audit;
  private Set<Long> hide;
  private Set<Long> show;
  private UserAuditDto correctedBy;
  private Long correctedAt;
  private Long duration;
  private List<TaskPauseReasonOrComment> pauseReasons = new ArrayList<>();
}
