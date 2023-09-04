package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.compositekey.ParameterMediaCompositeKey;
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
@Table(name = TableName.PARAMETER_MEDIA_MAPPING)
public class ParameterMediaMapping extends UserAuditBase implements Serializable {
  private static final long serialVersionUID = -7357026893827532134L;

  @EmbeddedId
  private ParameterMediaCompositeKey parameterMediaId;

  @Column(columnDefinition = "boolean default false")
  private boolean archived = false;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "parameters_id", nullable = false, insertable = false, updatable = false)
  private Parameter parameter;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "medias_id", nullable = false, insertable = false, updatable = false)
  private Media media;

  public ParameterMediaMapping(Parameter parameter, Media media, User principalUserEntity) {
    this.parameter = parameter;
    this.media = media;
    createdBy = principalUserEntity;
    modifiedBy = principalUserEntity;
    parameterMediaId = new ParameterMediaCompositeKey(parameter.getId(), media.getId());
  }

}
