package com.leucine.streem.model;

import com.leucine.streem.constant.State;
import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.helper.UserAuditIdentifiableBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@NamedEntityGraphs({
  @NamedEntityGraph(
    name = "readJob",
    attributeNodes = {
      @NamedAttributeNode(value = "checklist", subgraph = "checklist"),
      @NamedAttributeNode(value = "relationValues", subgraph = "job.relationValues")
    },
    subgraphs = {
      @NamedSubgraph(name = "checklist", attributeNodes = {
        @NamedAttributeNode(value = "stages", subgraph = "checklist.stages")
      }),
      @NamedSubgraph(name = "checklist.stages", attributeNodes = {
        @NamedAttributeNode(value = "tasks", subgraph = "tasks.parameters")
      }),
      @NamedSubgraph(name = "tasks.parameters", attributeNodes = {
        @NamedAttributeNode("parameters"),
        @NamedAttributeNode(value = "automations", subgraph = "automationMapping.automation"),
        @NamedAttributeNode(value = "medias", subgraph = "taskMedias")
      }),
      @NamedSubgraph(name = "automationMapping.automation", attributeNodes = {
        @NamedAttributeNode("automation")
      }),
      @NamedSubgraph(name = "taskMedias", attributeNodes = {
        @NamedAttributeNode("media")
      }),
    }
  ),
  @NamedEntityGraph(name = "jobInfo",
    attributeNodes = {
      @NamedAttributeNode(value = "checklist", subgraph = "checklist"),
      @NamedAttributeNode(value = "parameterValues", subgraph = "parameterValues.parameter"),
      @NamedAttributeNode(value = "scheduler")
    },
    subgraphs = {
      @NamedSubgraph(name = "checklist", attributeNodes = {
        @NamedAttributeNode(value = "version"),
      }),
      @NamedSubgraph(name = "parameterValues", attributeNodes = {
        @NamedAttributeNode(value = "parameter"),
      })
    }
  )
})
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.JOBS)
public class Job extends UserAuditIdentifiableBase implements Serializable {
  private static final long serialVersionUID = 2117036308404556959L;
  public static final String FACILITY_ID = "facilityId";
  public static final String ORGANISATION_ID = "organisationId";

  @Column(columnDefinition = "varchar", length = 20, nullable = false, updatable = false)
  private String code;

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private State.Job state;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "checklists_id", nullable = false, updatable = false)
  private Checklist checklist;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "facilities_id", nullable = false, updatable = false)
  private Facility facility;

  @Column(columnDefinition = "bigint", name = "facilities_id", updatable = false, insertable = false)
  private Long facilityId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organisations_id", referencedColumnName = "id", nullable = false)
  private Organisation organisation;

  @Column(columnDefinition = "bigint", name = "organisations_id", updatable = false, insertable = false)
  private Long organisationId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "use_cases_id", nullable = false, updatable = false)
  private UseCase useCase;

  @Column(columnDefinition = "bigint", name = "use_cases_id", updatable = false, insertable = false)
  private Long useCaseId;

  @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<TaskExecution> taskExecutions = new HashSet<>();

  @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ParameterValue> parameterValues = new HashSet<>();

  @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<RelationValue> relationValues = new HashSet<>();

  @Column(columnDefinition = "bigint")
  private Long startedAt;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "started_by", referencedColumnName = "id")
  public User startedBy;

  @Column(columnDefinition = "bigint")
  private Long endedAt;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "ended_by", referencedColumnName = "id")
  public User endedBy;

  @Column(columnDefinition = "boolean default false", nullable = false)
  private boolean isScheduled = false;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "schedulers_id")
  private Scheduler scheduler;

  @Column(columnDefinition = "bigint", name = "schedulers_id", updatable = false, insertable = false)
  private Long schedulerId;

  @Column(columnDefinition = "bigint")
  private Long expectedStartDate; // TODO expectedStartAt ? endAt ?

  @Column(columnDefinition = "bigint")
  private Long expectedEndDate;

  @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ParameterVerification> parameterVerifications = new HashSet<>();

  public void addTaskExecution(TaskExecution taskExecution) {
    taskExecution.setJob(this);
    taskExecutions.add(taskExecution);
  }

  public void addParameterValue(ParameterValue parameterValue) {
    parameterValue.setJob(this);
    parameterValues.add(parameterValue);
  }

}

