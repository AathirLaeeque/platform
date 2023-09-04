package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.compositekey.CodeCompositeKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.CODES)
public class Code implements Serializable {
  private static final long serialVersionUID = -293634315712382501L;

  @EmbeddedId
  private CodeCompositeKey codeId;

  @Column(columnDefinition = "integer")
  private Integer counter;

}
