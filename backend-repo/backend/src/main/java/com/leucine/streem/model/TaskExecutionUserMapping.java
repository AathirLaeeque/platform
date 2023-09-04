package com.leucine.streem.model;

import com.leucine.streem.constant.State;
import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.compositekey.TaskExecutionUserCompositeKey;
import com.leucine.streem.model.helper.UserAuditBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.TASK_EXECUTION_USER_MAPPING)
public class TaskExecutionUserMapping extends UserAuditBase implements Serializable {
  private static final long serialVersionUID = 1451874300783043851L;

  @EmbeddedId
  private TaskExecutionUserCompositeKey taskExecutionAssigneeId;

  @Column(columnDefinition = "boolean default false", nullable = false)
  private boolean actionPerformed = false;

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private State.TaskExecutionAssignee state;

  @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
  @JoinColumn(name = "task_executions_id", nullable = false, insertable = false, updatable = false)
  private TaskExecution taskExecution;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "users_id", nullable = false, insertable = false, updatable = false)
  private User user;

  @Column(columnDefinition = "bigint", name = "users_id", updatable = false, insertable = false)
  private Long usersId;

  public TaskExecutionUserMapping(TaskExecution taskExecution, User user, User principalUser) {
    this.taskExecution = taskExecution;
    this.user = user;
    actionPerformed = false;
    state = State.TaskExecutionAssignee.IN_PROGRESS;
    createdBy = principalUser;
    modifiedBy = principalUser;
    taskExecutionAssigneeId = new TaskExecutionUserCompositeKey(taskExecution.getId(), user.getId());
  }
}
