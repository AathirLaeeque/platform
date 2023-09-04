package com.leucine.streem.model.helper.search;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchFilter {
  private List<SearchCriteria> fields = new ArrayList<>();
  private String op;
  private List<String> projection = new ArrayList<>();
}
