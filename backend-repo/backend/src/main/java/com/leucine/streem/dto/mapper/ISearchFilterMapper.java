package com.leucine.streem.dto.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leucine.streem.collections.CustomViewFilter;
import com.leucine.streem.model.helper.search.SearchCriteria;
import com.leucine.streem.model.helper.search.SearchFilter;
import com.leucine.streem.util.JsonUtils;
import com.leucine.streem.util.Utility;
import org.mapstruct.Mapper;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Mapper
public interface ISearchFilterMapper {
  static SearchFilter toSearchCriteria(List<CustomViewFilter> filters) throws JsonProcessingException {
    SearchFilter searchFilter = new SearchFilter();
    searchFilter.setOp("AND");
    if (!Utility.isEmpty(filters)) {
      List<SearchCriteria> searchCriteriaList = new ArrayList<>();
      filters.forEach(customViewFilter -> {
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setValues(List.of(customViewFilter.getValue()));
        searchCriteria.setField(customViewFilter.getKey());
        searchCriteria.setOp(customViewFilter.getConstraint());
        searchCriteriaList.add(searchCriteria);
      });
      searchFilter.setFields(searchCriteriaList);
      return searchFilter;
    } else
      return searchFilter;
  }
}
