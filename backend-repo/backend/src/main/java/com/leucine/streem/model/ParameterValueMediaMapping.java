package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.compositekey.ParameterValueMediaCompositeKey;
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
@Table(name = TableName.PARAMETER_VALUE_MEDIA_MAPPING)
public class ParameterValueMediaMapping extends UserAuditBase implements Serializable {
  private static final long serialVersionUID = 6357544475000744744L;

  @EmbeddedId
  private ParameterValueMediaCompositeKey parameterValueMediaId;

  @Column(columnDefinition = "boolean default false")
  private boolean archived = false;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "parameter_values_id", nullable = false, insertable = false, updatable = false)
  private ParameterValue parameterValue;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "medias_id", nullable = false, insertable = false, updatable = false)
  private Media media;

  public ParameterValueMediaMapping(ParameterValue parameterValue, Media media, User principalUserEntity) {
    this.parameterValue = parameterValue;
    this.media = media;
    createdBy = principalUserEntity;
    modifiedBy = principalUserEntity;
    parameterValueMediaId = new ParameterValueMediaCompositeKey(parameterValue.getId(), media.getId());
  }

}
