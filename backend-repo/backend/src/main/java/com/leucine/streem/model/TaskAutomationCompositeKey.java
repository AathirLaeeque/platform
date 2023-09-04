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
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
public class TaskAutomationCompositeKey implements Serializable {
  private static final long serialVersionUID = 5252607487636908014L;

  @Column(name = "tasks_id", columnDefinition = "bigint")
  private Long taskId;

  @Column(name = "automations_id", columnDefinition = "bigint")
  private Long automationId;

  public TaskAutomationCompositeKey(Long taskId, Long automationId) {
    this.taskId = taskId;
    this.automationId = automationId;
  }
}
