package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.compositekey.TaskMediaCompositeKey;
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
@Table(name = TableName.TASK_MEDIA_MAPPING)
public class TaskMediaMapping extends UserAuditBase implements Serializable {

  private static final long serialVersionUID = 1995114683824719193L;

  @EmbeddedId
  private TaskMediaCompositeKey taskMediaId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "tasks_id", nullable = false, insertable = false, updatable = false)
  private Task task;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "medias_id", nullable = false, insertable = false, updatable = false)
  private Media media;

  public TaskMediaMapping(Task task, Media media, User principalUserEntity) {
    this.task = task;
    this.media = media;
    createdBy = principalUserEntity;
    modifiedBy = principalUserEntity;
    taskMediaId = new TaskMediaCompositeKey(task.getId(), media.getId());
  }
}
