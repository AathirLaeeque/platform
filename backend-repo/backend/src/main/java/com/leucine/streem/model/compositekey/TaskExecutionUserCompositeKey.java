package com.leucine.streem.model.compositekey;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class TaskExecutionUserCompositeKey implements Serializable {
  private static final long serialVersionUID = -8550542287035667796L;

  @Column(name = "task_executions_id", columnDefinition = "bigint", insertable = false, updatable = false)
  private Long taskExecutionId;

  @Column(name = "users_id", columnDefinition = "bigint", insertable = false, updatable = false)
  private Long userId;

  public TaskExecutionUserCompositeKey(Long taskExecutionId, Long userId) {
    this.taskExecutionId = taskExecutionId;
    this.userId = userId;
  }
}
