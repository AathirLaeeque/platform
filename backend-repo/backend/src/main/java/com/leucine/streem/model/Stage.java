package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.helper.UserAuditIdentifiableBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@NamedEntityGraph(
  name = "readStage",
  attributeNodes = {
    @NamedAttributeNode(value = "tasks", subgraph = "stage.tasks")
  },
  subgraphs = {
    @NamedSubgraph(name = "stage.tasks", attributeNodes = {
      @NamedAttributeNode("parameters"),
      @NamedAttributeNode(value = "medias", subgraph = "taskMedias")
    }),
    @NamedSubgraph(name = "taskMedias", attributeNodes = {
      @NamedAttributeNode("media")
    }),
  }
)
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.STAGES)
public class Stage extends UserAuditIdentifiableBase implements Serializable {
  private static final long serialVersionUID = 6160273870407941009L;

  @Column(columnDefinition = "varchar", length = 512, nullable = false)
  private String name;

  @Column(columnDefinition = "integer", nullable = false)
  private Integer orderTree;

  @Column(columnDefinition = "boolean default false")
  private boolean archived = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "checklists_id", nullable = false, updatable = false)
  private Checklist checklist;

  @Column(columnDefinition = "bigint", name = "checklists_id", updatable = false, insertable = false)
  private Long checklistId;

  //TODO where clause can be replaced with spec ?
  @OneToMany(mappedBy = "stage", cascade = CascadeType.ALL)
  @OrderBy("order_tree")
  @Where(clause = "archived =  false")
  private Set<Task> tasks = new HashSet<>();

  public void addTask(Task task) {
    task.setStage(this);
    tasks.add(task);
  }
}
