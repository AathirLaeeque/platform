package com.leucine.streem.model.compositekey;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
public class ParameterValueMediaCompositeKey implements Serializable {
  private static final long serialVersionUID = 5252607487636908014L;

  @Column(name = "parameter_values_id", columnDefinition = "bigint")
  private Long paramterValueId;

  @Column(name = "medias_id", columnDefinition = "bigint")
  private Long mediaId;

  public ParameterValueMediaCompositeKey(Long parameterValueId, Long mediaId) {
    this.paramterValueId = parameterValueId;
    this.mediaId = mediaId;
  }
}
