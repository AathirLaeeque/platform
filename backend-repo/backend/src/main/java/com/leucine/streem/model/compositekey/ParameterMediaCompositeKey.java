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
public class ParameterMediaCompositeKey implements Serializable {
  private static final long serialVersionUID = 5252607487636908014L;

  @Column(name = "parameters_id", columnDefinition = "bigint")
  private Long parameterId;

  @Column(name = "medias_id", columnDefinition = "bigint")
  private Long mediaId;

  public ParameterMediaCompositeKey(Long parameterId, Long mediaId) {
    this.parameterId = parameterId;
    this.mediaId = mediaId;
  }
}
