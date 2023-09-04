package com.leucine.streem.email.model;

import com.leucine.streem.util.DateTimeUtils;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "email_audits")
@TypeDefs({
    @TypeDef(
        name = "string-array",
        typeClass = StringArrayType.class
    )
})
public class EmailAudit {
  @Id
  @Column(columnDefinition = "bigint", updatable = false, nullable = false)
  private Long id;

  @Column(columnDefinition = "text", nullable = false)
  private String fromAddress;

  @Column(columnDefinition = "text[]")
  @Type(type = "string-array")
  private String[] toAddresses;

  @Column(columnDefinition = "text[]")
  @Type(type = "string-array")
  private String[] cc;

  @Column(columnDefinition = "text[]")
  @Type(type = "string-array")
  private String[] bcc;

  @Column(columnDefinition = "text")
  private String body;

  @Column(columnDefinition = "text")
  private String subject;

  @Column(columnDefinition = "smallint")
  private short retryAttempts = 0;

  @Column(columnDefinition = "smallint")
  private short maxAttempts = 3;

  @Column(columnDefinition = "bigint")
  private Long createdOn;

  @PrePersist
  public void beforePersist() {
    createdOn = DateTimeUtils.now();
  }

}
