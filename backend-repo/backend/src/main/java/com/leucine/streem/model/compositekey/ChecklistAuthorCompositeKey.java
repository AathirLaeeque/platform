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
public class ChecklistAuthorCompositeKey implements Serializable {
  private static final long serialVersionUID = 5252607487636908014L;

  @Column(name = "checklists_id", columnDefinition = "bigint")
  private Long checklistId;

  @Column(name = "users_id", columnDefinition = "bigint")
  private Long userId;

  public ChecklistAuthorCompositeKey(Long checklistId, Long userId) {
    this.checklistId = checklistId;
    this.userId = userId;
  }
}
