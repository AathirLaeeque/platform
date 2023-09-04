package com.leucine.streem.email.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "email_templates")
public class EmailTemplate implements Serializable {
  private static final long serialVersionUID = -7076912044397972740L;

  @Id
  @Column(columnDefinition = "bigint", updatable = false, nullable = false)
  private Long id;

  @Column(columnDefinition = "varchar", length = 255, nullable = false)
  private String name;

  @Column(columnDefinition = "text", nullable = false)
  private String content;
}
