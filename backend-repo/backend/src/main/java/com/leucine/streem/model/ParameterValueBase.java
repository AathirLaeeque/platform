package com.leucine.streem.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.constant.State;
import com.leucine.streem.model.helper.UserAuditOptionalBase;
import com.leucine.streem.util.DateTimeUtils;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@MappedSuperclass
public abstract class ParameterValueBase extends UserAuditOptionalBase {
  @Serial
  private static final long serialVersionUID = 4593012668682993733L;

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private State.ParameterExecution state;

  @Column(columnDefinition = "boolean default false", nullable = false)
  private boolean verified = false;

  //TODO possibly have reasons in separate entity with dedicated columns
  @Column(columnDefinition = "text")
  private String reason;

  @Column(columnDefinition = "text")
  private String value;

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private JsonNode choices;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "parameters_id", updatable = false)
  private Parameter parameter;

  @Column(name = "parameters_id", columnDefinition = "bigint", insertable = false, updatable = false)
  private Long parameterId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "jobs_id", updatable = false)
  private Job job;

  @Column(name = "jobs_id", columnDefinition = "bigint", insertable = false, updatable = false)
  private Long jobId;

  @PrePersist
  public void beforePersist() {
    createdAt = DateTimeUtils.now();
  }

  @OneToOne(cascade = {CascadeType.ALL})
  @JoinColumn(name = "parameter_value_approval_id")
  private ParameterValueApproval parameterValueApproval;

  @Column(name = "hidden", nullable = false, columnDefinition = "boolean default false")
  private boolean hidden;

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  private JsonNode impactedBy;

  @OneToMany(mappedBy = "parameterValue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @OrderBy("modified_by DESC")
  private List<ParameterVerification> parameterVerifications = new ArrayList<>();

  public abstract void addMedia(Media media, User principalUserEntity);

  public abstract void archiveMedia(Media media, User principalUserEntity);

  public abstract void addAllMedias(List<Media> medias, User principalUserEntity);
}
