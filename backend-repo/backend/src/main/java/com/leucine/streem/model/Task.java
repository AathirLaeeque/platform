package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.leucine.streem.dto.request.AutomationRequest;
import com.leucine.streem.model.helper.UserAuditIdentifiableBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.springframework.data.util.Pair;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.TASKS)
public class Task extends UserAuditIdentifiableBase implements Serializable {
  private static final long serialVersionUID = -6192033289683199381L;

  @Column(columnDefinition = "varchar", length = 512, nullable = false)
  private String name;

  @Column(columnDefinition = "integer", nullable = false)
  private Integer orderTree;

  @Column(columnDefinition = "boolean default false", nullable = false)
  private boolean hasStop = false;

  @Column(columnDefinition = "boolean default false", nullable = false)
  private boolean isTimed = false;

  //This is not enum because it can be null
  // TODO Make this enum and have one of its value NA. also the set the default of columnDefinition to NA
  @Column(columnDefinition = "varchar", length = 50)
  private String timerOperator;

  @Column(nullable = false, columnDefinition = "boolean default false")
  private boolean archived = false;

  @Column(nullable = false, columnDefinition = "boolean default false")
  private boolean isMandatory = false;

  @Column(columnDefinition = "bigint")
  private Long minPeriod;

  @Column(columnDefinition = "bigint")
  private Long maxPeriod;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "stages_id", nullable = false, updatable = false)
  private Stage stage;

  @Column(columnDefinition = "bigint", name = "stages_id", updatable = false, insertable = false)
  private Long stageId;

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
  @OrderBy("order_tree")
  @Where(clause = "archived =  false")
  private Set<Parameter> parameters = new HashSet<>();

  //TODO this was added because everytime something gets updated in task
  // we send whole response and media gets reordered
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "task", cascade = CascadeType.ALL)
  @OrderBy("created_at")
  private Set<TaskMediaMapping> medias = new HashSet<>();

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
  private Set<TaskExecution> taskExecutions = new HashSet<>();

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @OrderBy("order_tree")
  private Set<TaskAutomationMapping> automations = new HashSet<>();

  public void addMedia(Media media, User principalUserEntity) {
    TaskMediaMapping taskMediaMapping = new TaskMediaMapping(this, media, principalUserEntity);
    medias.add(taskMediaMapping);
  }

  public void addParameter(Parameter parameter) {
    parameter.setTask(this);
    parameters.add(parameter);
  }

  public void addAutomations(Map<Long, Pair<Automation, AutomationRequest>> automationIdAndAutomationRequestMap, User principalUserEntity) {
    Map<Long, TaskAutomationMapping> existingAutomations = automations.stream().collect(Collectors.toMap(TaskAutomationMapping::getAutomationId, Function.identity()));
    TaskAutomationMapping taskAutomationMapping;
    for(Map.Entry<Long, Pair<Automation, AutomationRequest>> automationAutomationRequestEntry : automationIdAndAutomationRequestMap.entrySet() ) {
      if (existingAutomations.containsKey(automationAutomationRequestEntry.getKey())) {
        taskAutomationMapping = existingAutomations.get(automationAutomationRequestEntry.getKey());
        taskAutomationMapping.setOrderTree(automationAutomationRequestEntry.getValue().getSecond().getOrderTree());
        taskAutomationMapping.setDisplayName(automationAutomationRequestEntry.getValue().getSecond().getDisplayName());
      } else {
        Pair<Automation, AutomationRequest> automationRequestPair = automationAutomationRequestEntry.getValue();
        taskAutomationMapping = new TaskAutomationMapping(this, automationRequestPair.getFirst(), automationRequestPair.getSecond().getOrderTree(), automationRequestPair.getSecond().getDisplayName(), principalUserEntity);
        automations.add(taskAutomationMapping);
      }
    }
  }
}
