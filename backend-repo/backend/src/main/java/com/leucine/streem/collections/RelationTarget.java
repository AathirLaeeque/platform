package com.leucine.streem.collections;

import com.leucine.streem.constant.CollectionMisc;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class RelationTarget implements Serializable {
  private static final long serialVersionUID = 2209456320628485404L;

  private CollectionMisc.RelationType type;
  private CollectionMisc.Cardinality cardinality;
  private String urlPath;
}
