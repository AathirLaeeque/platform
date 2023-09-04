package com.leucine.streem.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.constant.CollectionMisc;
import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.helper.UserAuditIdentifiableBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.RELATIONS)
public class Relation extends UserAuditIdentifiableBase implements Serializable {
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "checklists_id", nullable = false, updatable = false)
  private Checklist checklist;

  @Column(name = "checklists_id", columnDefinition = "bigint", insertable = false, updatable = false)
  private Long checklistId;

  @Column(name = "external_id", columnDefinition = "varchar", nullable = false)
  private String externalId;

  @Column(name = "display_name", columnDefinition = "varchar", nullable = false)
  private String displayName;

  @Column(name = "url_path", columnDefinition = "text", nullable = false)
  private String urlPath;

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb default '{}'::jsonb")
  private JsonNode variables;

  @Column(name = "cardinality")
  @Enumerated(EnumType.STRING)
  private CollectionMisc.Cardinality cardinality;

  @Column(name = "object_type_id")
  private String objectTypeId;

  @Column(name = "collection")
  private String collection;

  @Column(columnDefinition = "integer", nullable = false)
  private Integer orderTree;

  @Type(type = "jsonb")
  @Column(name = "validations", columnDefinition = "jsonb default '{}'", nullable = false)
  private JsonNode validations;

  @Column(name = "is_mandatory", nullable = false, columnDefinition = "boolean default false")
  private boolean isMandatory;

}
