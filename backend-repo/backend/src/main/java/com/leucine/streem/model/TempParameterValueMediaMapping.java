package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.compositekey.TempParameterValueMediaCompositeKey;
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
@Table(name = TableName.TEMP_PARAMETER_VALUE_MEDIA_MAPPING)
public class TempParameterValueMediaMapping extends UserAuditBase implements Serializable {
  private static final long serialVersionUID = 6357544475000744744L;

  @Column(columnDefinition = "boolean default false")
  private boolean archived = false;

  @EmbeddedId
  private TempParameterValueMediaCompositeKey parameterValueMediaId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "temp_parameter_values_id", nullable = false, insertable = false, updatable = false)
  private TempParameterValue tempParameterValue;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "medias_id", nullable = false, insertable = false, updatable = false)
  private Media media;

  public TempParameterValueMediaMapping(TempParameterValue tempParameterValue, Media media, User principalUserEntity) {
    this.tempParameterValue = tempParameterValue;
    this.media = media;
    createdBy = principalUserEntity;
    modifiedBy = principalUserEntity;
    parameterValueMediaId = new TempParameterValueMediaCompositeKey(tempParameterValue.getId(), media.getId());
  }

}
