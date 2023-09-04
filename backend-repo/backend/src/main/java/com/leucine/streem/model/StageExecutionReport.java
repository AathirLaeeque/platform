package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.helper.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = TableName.STAGE_EXECUTION_REPORT)
public class StageExecutionReport extends BaseEntity implements Serializable {
  private static final long serialVersionUID = -4991094127943840060L;

  @Column(name = "jobs_id", updatable = false, nullable =  false)
  private Long jobId;

  @Column(name = "stages_id", updatable = false, nullable =  false)
  private Long stageId;

  @Column(updatable = false)
  private String stageName;

  @Column(columnDefinition = "integer", nullable = false)
  private Integer totalTasks = 0;

  @Column(columnDefinition = "integer", nullable = false)
  private Integer completedTasks;

  @Column(columnDefinition = "boolean")
  private Boolean tasksInProgress = false;
  
}
