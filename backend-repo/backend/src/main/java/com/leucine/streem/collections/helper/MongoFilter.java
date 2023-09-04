package com.leucine.streem.collections.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.model.helper.search.SearchCriteria;
import com.leucine.streem.model.helper.search.SearchFilter;
import com.leucine.streem.model.helper.search.SearchOperator;
import com.leucine.streem.util.JsonUtils;
import com.leucine.streem.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Slf4j
public class MongoFilter {
  private static final String REGEX_LIKE = "^.*(?i)%s.*$";
  private static final String REGEX_STARTS_WITH = "^(?i)%s";
  private static final String INVALID_SEARCH_OPERATION = "Invalid filter search operation : {0}";

  public static Query buildQuery(String filters) {
    return buildQuery(filters, Collections.emptyList());
  }

  public static Query buildQueryWithFacilityId(String filters, String facilityId) {
    SearchCriteria searchCriteria = new SearchCriteria();
    searchCriteria.setOp(SearchOperator.EQ.name());
    searchCriteria.setField("facilityId");
    List<Object> value = new ArrayList<>();
    value.add(facilityId);
    searchCriteria.setValues(value);

    return buildQuery(filters, List.of(searchCriteria));
  }

  public static Query buildQuery(String filters, List<SearchCriteria> additionalSearchCriterias) {
    final Query query = new Query();

    List<Criteria> criteriaList = new ArrayList<>();
    SearchOperator op = SearchOperator.AND;
    try {
      if (!Utility.isEmpty(filters)) {
        // We are converting json formatted string to json node
        // In case of {} and Json NULL, we convert this to json node and check if its an empty json node
        JsonNode jsonNode = JsonUtils.valueToNode(filters);
        if (!Utility.isEmpty(jsonNode)) {
          SearchFilter searchFilter = JsonUtils.readValue(URLDecoder.decode(filters, StandardCharsets.UTF_8), SearchFilter.class);
          op = handleEnum(searchFilter.getOp());
          query.fields().include(searchFilter.getProjection().toArray(new String[0]));
          for (SearchCriteria searchCriteria : searchFilter.getFields()) {
            addCriteria(searchCriteria, criteriaList, op);
          }
        }
      }

      if (!Utility.isEmpty(additionalSearchCriterias)) {
        for (SearchCriteria searchCriteria : additionalSearchCriterias) {
          addCriteria(searchCriteria, criteriaList, op);
        }
      }

      if (!criteriaList.isEmpty()) {
        if (op == SearchOperator.AND) {
          query.addCriteria(new Criteria().andOperator(criteriaList));
        } else if (op == SearchOperator.OR) {
          query.addCriteria(new Criteria().orOperator(criteriaList));
        } else {
          throw new IllegalArgumentException(MessageFormat.format(INVALID_SEARCH_OPERATION, op));
        }

      }

    } catch (JsonProcessingException e) {
      log.error("Incorrect Filter or Encoding", e);
    }

    return query;
  }

  private static void addCriteria(SearchCriteria searchCriteria, List<Criteria> criteria, SearchOperator operator) {
    if (!searchCriteria.getField().equals("usageStatus") && operator == SearchOperator.OR) {
      return;
    }
    var op = handleEnum(searchCriteria.getOp());
    switch (op) {
      case EQ -> criteria.add(Criteria.where(searchCriteria.getField()).is(searchCriteria.getValues().get(0)));
      case NE -> criteria.add(Criteria.where(searchCriteria.getField()).ne(searchCriteria.getValues().get(0)));
      case ANY -> criteria.add(Criteria.where(searchCriteria.getField()).in(searchCriteria.getValues()));
      case NIN -> criteria.add(Criteria.where(searchCriteria.getField()).nin(searchCriteria.getValues()));
      case GT -> criteria.add(Criteria.where(searchCriteria.getField()).gt(searchCriteria.getValues().get(0)));
      case GTE, GOE -> criteria.add(Criteria.where(searchCriteria.getField()).gte(searchCriteria.getValues().get(0)));
      case LT -> criteria.add(Criteria.where(searchCriteria.getField()).lt(searchCriteria.getValues().get(0)));
      case LTE, LOE -> criteria.add(Criteria.where(searchCriteria.getField()).lte(searchCriteria.getValues().get(0)));
      case LIKE ->
        criteria.add(Criteria.where(searchCriteria.getField()).regex(String.format(REGEX_LIKE, searchCriteria.getValues().get(0).toString())));
      case STARTS_WITH ->
        criteria.add(Criteria.where(searchCriteria.getField()).regex(String.format(REGEX_STARTS_WITH, searchCriteria.getValues().get(0).toString())));
      default -> throw new IllegalArgumentException(MessageFormat.format(INVALID_SEARCH_OPERATION, op));
    }
  }

  private static SearchOperator handleEnum(String op) {
    return SearchOperator.valueOf(op);
  }
}
