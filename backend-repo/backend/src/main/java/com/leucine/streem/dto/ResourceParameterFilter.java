package com.leucine.streem.dto;

import com.leucine.streem.model.helper.search.SearchOperator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceParameterFilter implements Serializable {
  private SearchOperator op;
  private List<ResourceParameterFilterField> fields;
}
