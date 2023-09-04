package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.compositekey.JobCweDetailMediaCompositeKey;
import com.leucine.streem.model.helper.UserAuditBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.JOB_CWE_DETAIL_MEDIA_MAPPING)
public class JobCweDetailMediaMapping extends UserAuditBase implements Serializable {
  private static final long serialVersionUID = 7977029827882125743L;

  @EmbeddedId
  private JobCweDetailMediaCompositeKey jobCweDetailMediaId;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "job_cwe_details_id", nullable = false, insertable = false, updatable = false)
  private JobCweDetail jobCweDetail;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "medias_id", nullable = false, insertable = false, updatable = false)
  private Media media;

  public JobCweDetailMediaMapping(final JobCweDetail jobCweDetail, final Media media, final User user) {
    this.jobCweDetail = jobCweDetail;
    this.media = media;
    this.createdBy = user;
    this.modifiedBy = user;
    this.jobCweDetailMediaId = new JobCweDetailMediaCompositeKey(jobCweDetail.getId(), media.getId());
  }
}
