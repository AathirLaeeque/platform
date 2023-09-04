package com.leucine.streem.model.compositekey;

import com.leucine.streem.constant.Type;
import com.leucine.streem.model.Organisation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class CodeCompositeKey implements Serializable {
  private static final long serialVersionUID = -8807855507593861605L;

  @Column(columnDefinition = "varchar", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private Type.EntityType type;

  @Column(columnDefinition = "smallint")
  private short clause;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organisations_id", nullable = false)
  private Organisation organisation;
}
