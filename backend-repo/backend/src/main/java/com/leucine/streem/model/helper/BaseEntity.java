package com.leucine.streem.model.helper;

import com.leucine.commons.id.IdGenerator;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity implements Serializable {
  public static final String ID = "id";
  @Serial
  private static final long serialVersionUID = 7495074399890921482L;

  @Id
  @Column(columnDefinition = "bigint", updatable = false, nullable = false)
  private Long id;

  public String getIdAsString() {
    return String.valueOf(id);
  }

  @PrePersist
  protected void setIdBeforePersist() {
    if (null == id) {
      id = IdGenerator.getInstance().nextId();
    }
  }
}
