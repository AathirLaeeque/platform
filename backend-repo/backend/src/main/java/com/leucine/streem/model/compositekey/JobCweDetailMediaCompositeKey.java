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
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class JobCweDetailMediaCompositeKey implements Serializable {
  private static final long serialVersionUID = 5252607487636908014L;

  @Column(name = "job_cwe_details_id", columnDefinition = "bigint")
  private Long jobCweDetailId;

  @Column(name = "medias_id", columnDefinition = "bigint")
  private Long mediaId;

  public JobCweDetailMediaCompositeKey(Long jobCweDetailId, Long mediaId) {
    this.jobCweDetailId = jobCweDetailId;
    this.mediaId = mediaId;
  }
}
