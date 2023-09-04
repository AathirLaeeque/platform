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
public class TempParameterValueMediaCompositeKey implements Serializable {
  private static final long serialVersionUID = 5252607487636908014L;

  @Column(name = "temp_parameter_values_id", columnDefinition = "bigint")
  private Long tempParameterValueId;

  @Column(name = "medias_id", columnDefinition = "bigint")
  private Long mediaId;

  public TempParameterValueMediaCompositeKey(Long tempParameterValueId, Long mediaId) {
    this.tempParameterValueId = tempParameterValueId;
    this.mediaId = mediaId;
  }
}
