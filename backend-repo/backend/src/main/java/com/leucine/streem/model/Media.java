package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.helper.UserAuditIdentifiableBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = TableName.MEDIAS)
public class Media extends UserAuditIdentifiableBase implements Serializable {
  private static final long serialVersionUID = -3623930069948465033L;

  @Column(columnDefinition = "varchar", length = 255)
  private String name;

  @Column(columnDefinition = "text")
  private String description;

  @Column(columnDefinition = "varchar", length = 255, nullable = false)
  private String originalFilename;

  @Column(columnDefinition = "varchar", length = 255, nullable = false)
  private String filename;

  @Column(columnDefinition = "varchar", length = 255, nullable = false)
  private String type;

  @Column(columnDefinition = "text", nullable = false)
  private String relativePath;

  @Column(columnDefinition = "boolean default false")
  private boolean archived = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organisations_id", referencedColumnName = "id", nullable = false)
  private Organisation organisation;

}
