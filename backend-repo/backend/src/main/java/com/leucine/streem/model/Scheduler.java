package com.leucine.streem.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.constant.State;
import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.helper.UserAuditIdentifiableBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@NamedEntityGraphs({
  @NamedEntityGraph(name = "readScheduler",
    attributeNodes = {
      @NamedAttributeNode(value = "version")
    }
  )
})
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.SCHEDULERS)
public class Scheduler extends UserAuditIdentifiableBase implements Serializable {

  public static final String PARAMETER_VALUES = "parameterValues";
  public static final String PROCESS_ID = "processId";

  @Serial
  private static final long serialVersionUID = -3359753349999401564L;

  @Column(name = "name", columnDefinition = "varchar", length = 512)
  private String name;

  @Column(columnDefinition = "text", nullable = true)
  private String description;

  // TODO we have a problem in this code, id - 6, 16 (in order 16 comes before 6)
  @Column(columnDefinition = "varchar", length = 20, nullable = false, updatable = false)
  private String code;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "checklists_id", nullable = false, updatable = false)
  private Checklist checklist;

  @Column(columnDefinition = "bigint", name = "checklists_id", updatable = false, insertable = false)
  private Long checklistId;

  @Column(name = "checklists_name", columnDefinition = "varchar", length = 512)
  private String checklistName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "facilities_id", nullable = false, updatable = false)
  private Facility facility;

  @Column(columnDefinition = "bigint", name = "facilities_id", updatable = false, insertable = false)
  private Long facilityId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "use_cases_id", nullable = false, updatable = false)
  private UseCase useCase;

  @Column(columnDefinition = "bigint", name = "use_cases_id", updatable = false, insertable = false)
  private Long useCaseId;

  @Column(columnDefinition = "bigint",  name = "expected_start_date", nullable = false)
  private Long expectedStartDate;

  @Column(columnDefinition = "integer")
  private Integer dueDateInterval;

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb default '{}'", nullable = false)
  private JsonNode dueDateDuration;

  @Column(columnDefinition = "boolean default false", nullable = false)
  private boolean isRepeated = false;

  @Column(name = "recurrence_rule", columnDefinition = "text")
  private String recurrenceRule;

  @Column(columnDefinition = "boolean default false", nullable = false)
  private boolean isCustomRecurrence = false;

  @Column(columnDefinition = "boolean default true", nullable = false)
  private boolean enabled = false;

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb default '{}'", nullable = false)
  private JsonNode data;

  @OneToOne(cascade = {CascadeType.DETACH})
  @JoinColumn(name = "versions_id")
  private Version version;

  @Column(columnDefinition = "boolean default false", nullable = false)
  private boolean archived = false;

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private State.Scheduler state;

  @Column(columnDefinition = "bigint")
  private Long deprecatedAt;
}
