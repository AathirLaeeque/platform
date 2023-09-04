package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.compositekey.TaskAutomationCompositeKey;
import com.leucine.streem.model.compositekey.TaskMediaCompositeKey;
import com.leucine.streem.model.helper.PrincipalUser;
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
@Table(name = TableName.TASK_AUTOMATION_MAPPING)
public class TaskAutomationMapping extends UserAuditBase implements Serializable {
  private static final long serialVersionUID = 6357544475000744744L;

  @EmbeddedId
  private TaskAutomationCompositeKey taskAutomationId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tasks_id", updatable = false, insertable = false, nullable = false)
  private Task task;

  @Column(columnDefinition = "bigint", name = "tasks_id", updatable = false, insertable = false)
  private Long taskId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "automations_id", updatable = false, insertable = false, nullable = false)
  private Automation automation;

  @Column(columnDefinition = "bigint", name = "automations_id", updatable = false, insertable = false)
  private Long automationId;

  @Column(columnDefinition = "integer", nullable = false)
  private Integer orderTree;

  @Column(columnDefinition = "text", nullable = false)
  private String displayName;

  public TaskAutomationMapping(Task task, Automation automation, Integer orderTree, String displayName, User principalUserEntity) {
    this.task = task;
    this.automation = automation;
    this.orderTree = orderTree;
    this.displayName = displayName;
    createdBy = principalUserEntity;
    modifiedBy = principalUserEntity;
    taskAutomationId = new TaskAutomationCompositeKey(task.getId(), automation.getId());
  }

}
