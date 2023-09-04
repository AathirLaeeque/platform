package com.leucine.streem.collections;

import com.leucine.streem.constant.CollectionName;
import com.leucine.streem.constant.Type;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(CollectionName.CODE)
@CompoundIndex(name = "UNIQUE_CODE", def = "{'type': 1, 'clause': 1}", unique = true)
public class Code {
  private String type;
  private Integer clause;
  private Integer counter;
}
