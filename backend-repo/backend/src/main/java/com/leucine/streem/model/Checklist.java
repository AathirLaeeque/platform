package com.leucine.streem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.constant.State;
import com.leucine.streem.constant.TableName;
import com.leucine.streem.constant.Type;
import com.leucine.streem.model.helper.UserAuditIdentifiableBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@NamedEntityGraphs({
  @NamedEntityGraph(
    name = "readChecklist",
    attributeNodes = {
      @NamedAttributeNode(value = "stages", subgraph = "checklist.stages"),
      @NamedAttributeNode(value = "checklistPropertyValues", subgraph = "checklist.propertyValues"),
      @NamedAttributeNode(value = "relations", subgraph = "checklist.relations"),
      @NamedAttributeNode(value = "version"),
      @NamedAttributeNode(value = "createdBy"),
      @NamedAttributeNode(value = "modifiedBy"),
    },
    subgraphs = {
      @NamedSubgraph(name = "checklist.stages", attributeNodes = {
        @NamedAttributeNode(value = "tasks", subgraph = "tasks.parameters")
      }),
      @NamedSubgraph(name = "tasks.parameters", attributeNodes = {
        @NamedAttributeNode("parameters"),
        @NamedAttributeNode(value = "automations", subgraph = "automationMapping.automation"),
        @NamedAttributeNode(value = "medias", subgraph = "task.medias")
      }),
      @NamedSubgraph(name = "checklist.propertyValues", attributeNodes = {
        @NamedAttributeNode(value = "facilityUseCasePropertyMapping", subgraph = "facilityUseCasePropertyMapping.properties")
      }),
      @NamedSubgraph(name = "facilityUseCasePropertyMapping.properties", attributeNodes = {
        @NamedAttributeNode(value = "property")
      }),
      @NamedSubgraph(name = "task.medias", attributeNodes = {
        @NamedAttributeNode("media"),
      }),
      @NamedSubgraph(name = "automationMapping.automation", attributeNodes = {
        @NamedAttributeNode("automation"),
      })
    }
  ),
  @NamedEntityGraph(name = "checklistInfo",
    attributeNodes = {
      @NamedAttributeNode(value = "version"),
      @NamedAttributeNode(value = "createdBy"),
      @NamedAttributeNode(value = "modifiedBy"),
      @NamedAttributeNode(value = "checklistPropertyValues", subgraph = "checklist.propertyValues"),
    },
    subgraphs = {
      @NamedSubgraph(name = "checklist.propertyValues", attributeNodes = {
        @NamedAttributeNode(value = "facilityUseCasePropertyMapping", subgraph = "facilityUseCasePropertyMapping.properties")
      }),
      @NamedSubgraph(name = "facilityUseCasePropertyMapping.properties", attributeNodes = {
        @NamedAttributeNode(value = "property")
      }),
    })
})
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.CHECKLISTS)
public class Checklist extends UserAuditIdentifiableBase implements Serializable {
  public static final String FACILITY_ID = "facilities.facilityId";
  public static final String ORGANISATION_ID = "organisationId";
  private static final long serialVersionUID = -6754115216548818688L;
  @Column(columnDefinition = "varchar", length = 512)
  private String name;

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private State.Checklist state;

  @Column(columnDefinition = "varchar", length = 20, nullable = false, updatable = false)
  private String code;

  @org.hibernate.annotations.Type(type = "jsonb")
  @Column(name = "job_log_columns", columnDefinition = "jsonb default '[]'", nullable = false)
  private JsonNode jobLogColumns;

  @Column(columnDefinition = "boolean default false", nullable = false)
  private boolean archived = false;

  @OneToOne(cascade = {CascadeType.DETACH})
  @JoinColumn(name = "versions_id")
  private Version version;

  @OneToMany(mappedBy = "checklist", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ChecklistFacilityMapping> facilities = new HashSet<>();


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organisations_id", referencedColumnName = "id", nullable = false)
  private Organisation organisation;

  @Column(columnDefinition = "bigint", name = "organisations_id", updatable = false, insertable = false)
  private Long organisationId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "use_cases_id", nullable = false, updatable = false)
  private UseCase useCase;

  @Column(columnDefinition = "bigint", name = "use_cases_id", updatable = false, insertable = false)
  private Long useCaseId;

  @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @OrderBy("order_tree")
  @Where(clause = "archived =  false")
  private Set<Stage> stages = new HashSet<>();

  @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL)
  @LazyCollection(LazyCollectionOption.EXTRA)
  private Set<Job> jobs = new HashSet<>();

  @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ChecklistPropertyValue> checklistPropertyValues = new HashSet<>();

  @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @OrderBy("order_tree")
  private Set<Relation> relations = new HashSet<>();

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "checklist", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  private Set<ChecklistCollaboratorMapping> collaborators = new HashSet<>();

  @Column(columnDefinition = "integer default 1", nullable = false)
  private Integer reviewCycle = 1;

  @Column(columnDefinition = "text")
  private String description;

  @JsonIgnore
  @Column(columnDefinition = "bigint")
  private Long releasedAt;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH})
  @JoinColumn(name = "released_by", referencedColumnName = "id")
  private User releasedBy;

  @Column(name = "is_global", columnDefinition = "boolean default false", nullable = false)
  private boolean isGlobal = false;

  public void addProperty(FacilityUseCasePropertyMapping facilityUseCasePropertyMapping, String value, User principalUserEntity) {
    ChecklistPropertyValue checklistPropertyValue = new ChecklistPropertyValue(this, facilityUseCasePropertyMapping, value, principalUserEntity);
    checklistPropertyValues.add(checklistPropertyValue);
  }


  public void addAuthor(User user, Integer phase, User principalUserEntity) {
    ChecklistCollaboratorMapping checklistCollaboratorMapping = new ChecklistCollaboratorMapping(this, user, Type.Collaborator.AUTHOR, phase, State.ChecklistCollaboratorPhaseType.BUILD, principalUserEntity);
    collaborators.add(checklistCollaboratorMapping);
  }


  public void addPrimaryAuthor(User user, Integer phase, User principalUserEntity) {
    ChecklistCollaboratorMapping checklistCollaboratorMapping = new ChecklistCollaboratorMapping(this, user, Type.Collaborator.PRIMARY_AUTHOR, phase, State.ChecklistCollaboratorPhaseType.BUILD, principalUserEntity);
    collaborators.add(checklistCollaboratorMapping);
  }

  public void addStage(Stage stage) {
    stage.setChecklist(this);
    stages.add(stage);
  }

  public void addRelation(Relation relation) {
    relation.setChecklist(this);
    relations.add(relation);
  }

  public void addFacility(Facility facility, User principalUserEntity) {
    ChecklistFacilityMapping checklistFacilityMapping = new ChecklistFacilityMapping(this, facility, principalUserEntity);
    this.facilities.add(checklistFacilityMapping);
  }

  public void addFacility(Set<Facility> facilities, User principalUserEntity) {
    for (Facility facility : facilities) {
      ChecklistFacilityMapping checklistFacilityMapping = new ChecklistFacilityMapping(this, facility, principalUserEntity);
      this.facilities.add(checklistFacilityMapping);
    }
  }

  public void addFacility(Set<ChecklistFacilityMapping> checklistFacilityMappings) {
    this.facilities.addAll(checklistFacilityMappings);
  }

}
