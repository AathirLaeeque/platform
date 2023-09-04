package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.leucine.streem.constant.TaskPauseReason;
import com.leucine.streem.model.helper.UserAuditIdentifiableBase;
import lombok.*;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = TableName.TASK_EXECUTION_TIMER)
public class TaskExecutionTimer extends UserAuditIdentifiableBase implements Serializable {
  @Serial
  private static final long serialVersionUID = 8871269725539214596L;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "task_executions_id", updatable = false, nullable = false, insertable = false)
  private TaskExecution taskExecution;

  @Column(columnDefinition = "bigint", name = "task_executions_id", updatable = true, insertable = true)
  private Long taskExecutionId;

  @Column(columnDefinition = "bigint", nullable = false)
  private Long pausedAt;

  @Column(columnDefinition = "bigint")
  private Long resumedAt;

  @Column(columnDefinition = "text")
  @Enumerated(EnumType.STRING)
  private TaskPauseReason reason;

  @Column(columnDefinition = "text")
  private String comment;
}
