package com.leucine.streem.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.helper.UserAuditIdentifiableBase;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.PARAMETER)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Parameter extends UserAuditIdentifiableBase implements Serializable {
  private static final long serialVersionUID = 5557529931910517186L;

  public static final String CHECKLIST_ID = "checklistId";
  public static final String TARGET_ENTITY_TYPE = "targetEntityType";
  public static final String TYPE = "type";


  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private com.leucine.streem.constant.Type.Parameter type;

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private com.leucine.streem.constant.Type.ParameterTargetEntityType targetEntityType;

  @Column(name = "verification_type" , columnDefinition = "varchar default NONE", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private com.leucine.streem.constant.Type.VerificationType verificationType;

  @Column(columnDefinition = "varchar", length = 255, nullable = true)
  private String label;

  @Column(columnDefinition = "text", nullable = true)
  private String description;

  @Column(columnDefinition = "integer", nullable = false)
  private Integer orderTree;

  @Column(columnDefinition = "boolean default false", nullable = false)
  private boolean isMandatory = false;

  @Column(columnDefinition = "boolean default false", nullable = false)
  private boolean archived = false;

  @OneToMany(mappedBy = "parameter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<ParameterValue> parameterValues = new HashSet<>();

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb default '{}'", nullable = false)
  private JsonNode data;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tasks_id")
  private Task task;

  @Column(columnDefinition = "bigint", name = "tasks_id", updatable = false, insertable = false)
  private Long taskId;

  @org.hibernate.annotations.Type(type = "jsonb")
  @Column(name = "validations", columnDefinition = "jsonb default '{}'", nullable = false)
  private JsonNode validations;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "checklists_id", nullable = false, updatable = false, insertable = false)
  private Checklist checklist;

  @Column(columnDefinition = "bigint", name = "checklists_id")
  private Long checklistId;

  @Column(name = "is_auto_initialized", columnDefinition = "boolean default false", nullable = false)
  private boolean isAutoInitialized = false;

  @Type(type = "jsonb")
  @Column(name = "auto_initialize", columnDefinition = "jsonb", nullable = true)
  private JsonNode autoInitialize;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "parameter", cascade = CascadeType.ALL)
  private List<ParameterMediaMapping> medias = new ArrayList<>();

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private JsonNode rules;

  @Column(name = "hidden", nullable = false, columnDefinition = "boolean default false")
  private boolean hidden;

  public void addMedia(Media media, User principalUserEntity) {
    ParameterMediaMapping parameterMediaMapping = new ParameterMediaMapping(this, media, principalUserEntity);
    medias.add(parameterMediaMapping);
  }

}

