package com.leucine.streem.model.helper.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SearchCriteria {
  private String field;
  private String op;
  private List<Object> values = new ArrayList<>();
}
