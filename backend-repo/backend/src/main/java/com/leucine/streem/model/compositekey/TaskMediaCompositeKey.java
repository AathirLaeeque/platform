package com.leucine.streem.model.compositekey;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class TaskMediaCompositeKey implements Serializable {
  private static final long serialVersionUID = 5252607487636908014L;

  @Column(name = "tasks_id", columnDefinition = "bigint")
  private Long taskId;

  @Column(name = "medias_id", columnDefinition = "bigint")
  private Long mediaId;

  public TaskMediaCompositeKey() {
  }

  public TaskMediaCompositeKey(Long taskId, Long mediaId) {
    this.taskId = taskId;
    this.mediaId = mediaId;
  }
}
