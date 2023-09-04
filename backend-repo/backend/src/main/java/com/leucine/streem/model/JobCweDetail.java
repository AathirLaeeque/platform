package com.leucine.streem.model;

import com.leucine.streem.constant.JobCweReason;
import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.helper.UserAuditIdentifiableBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = TableName.JOB_CWE_DETAILS)
public class JobCweDetail extends UserAuditIdentifiableBase implements Serializable {
  private static final long serialVersionUID = 7505792325791972206L;

  @Column(columnDefinition = "varchar", length = 45)
  @Enumerated(EnumType.STRING)
  private JobCweReason reason;

  @Column(columnDefinition = "text")
  private String comment;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "jobs_id")
  private Job job;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "jobCweDetail", cascade = CascadeType.ALL)
  private Set<JobCweDetailMediaMapping> medias = new HashSet<>();

  public void addAllMedias(List<Media> medias, User user) {
    for (Media media : medias) {
      JobCweDetailMediaMapping jobCweDetailMediaMapping = new JobCweDetailMediaMapping(this, media, user);
      this.medias.add(jobCweDetailMediaMapping);
    }
  }
}
