package com.leucine.streem.model.helper;

import com.leucine.streem.exception.SpecificationBuilderException;
import com.leucine.streem.model.helper.search.SearchCriteria;
import com.leucine.streem.model.helper.search.SearchFilter;
import com.leucine.streem.model.helper.search.SearchOperator;
import com.leucine.streem.util.JsonUtils;
import com.leucine.streem.util.Utility;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import javax.persistence.criteria.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SpecificationBuilder<T> {
  public static final String CANNOT_PARSE_FILTERS = "Error parsing filters";
  private static final String INVALID_SEARCH_OPERATION = "Invalid filter search operation : {0}";
  private static final String INVALID_FILTER_CONDITION = "Invalid filter condition : {0}";
  private static final String WILDCARD_PERCENT = "%";

  public static <E> Specification<E> createSpecification(String filters, List<SearchCriteria> mandatorySearchCriterias) {
    SpecificationBuilder<E> specificationBuilder = new SpecificationBuilder<>();
    Specification<E> specification = null;
    if (!Utility.isEmpty(filters)) {
      try {
        SearchFilter searchFilter = JsonUtils.readValue(URLDecoder.decode(filters, StandardCharsets.UTF_8.toString()), SearchFilter.class);
        if (!Utility.isEmpty(mandatorySearchCriterias)) {
          Specification<E> mandatorySpecification = specificationBuilder.createSpecification(mandatorySearchCriterias, SearchOperator.AND.toString());
          Specification<E> filtersSpecification = specificationBuilder.createSpecification(searchFilter.getFields(), searchFilter.getOp());
          specification = mandatorySpecification.and(filtersSpecification);
        } else {
          specification = specificationBuilder.createSpecification(searchFilter.getFields(), searchFilter.getOp());
        }
      } catch (Exception exception) {
        throw new SpecificationBuilderException(CANNOT_PARSE_FILTERS);
      }
    } else {
      specification = specificationBuilder.createSpecification(mandatorySearchCriterias, SearchOperator.AND.toString());
    }
    return specification;
  }

  public Specification<T> createSpecification(List<SearchCriteria> searchCriteria, String builderCondition) {
    return (root, query, criteriaBuilder) -> {
      return buildSpecification(searchCriteria, root, criteriaBuilder, builderCondition, query);
    };
  }

  protected Predicate buildSpecification(List<SearchCriteria> searchCriteriaList, Root<T> root,
                                         CriteriaBuilder criteriaBuilder, String builderCondition, CriteriaQuery<?> query) {
    query.distinct(true);
    List<Predicate> predicates = new ArrayList();
    Iterator var = searchCriteriaList.iterator();

    while (var.hasNext()) {
      SearchCriteria searchCriteria = (SearchCriteria) var.next();
      if (searchCriteria != null && (!ObjectUtils.isEmpty(searchCriteria.getValues()) || SearchOperator.IS_NOT_SET.name().equals(searchCriteria.getOp()))) {
        this.generatePredicate(root, criteriaBuilder, predicates, searchCriteria);
      }
    }

    switch (SearchOperator.valueOf(builderCondition)) {
      case AND:
        return criteriaBuilder.and((Predicate[]) predicates.toArray(new Predicate[0]));
      case OR:
        return criteriaBuilder.or((Predicate[]) predicates.toArray(new Predicate[0]));
      default:
        throw new IllegalArgumentException(MessageFormat.format(INVALID_FILTER_CONDITION, builderCondition));
    }
  }

  protected void generatePredicate(Root<T> root, CriteriaBuilder criteriaBuilder,
                                   List<Predicate> predicates, SearchCriteria searchCriteria) {
    List<Object> list = searchCriteria.getValues();
    String field = searchCriteria.getField();
    String operator = searchCriteria.getOp();
    SearchOperator searchOperator = SearchOperator.valueOf(operator);
    this.addSearchConditions(searchOperator, list, field, operator, root, criteriaBuilder, predicates);
  }

  protected void addSearchConditions(SearchOperator searchOperator, List<Object> list, String field,
                                     String operator, Root<T> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
    Expression expression = null;


    if (field.contains(".")) {
      /*
      TODO: remove this check, assignees filter is not working on getOrCreate.
       Path should be passed as parameter instead of root
     */
      if (field.contains("assignees")) {
        String[] fields = field.split("\\.");
        Join<Object, Object> rootJoin = root.join(fields[0]);

        int i;
        for (i = 1; i < fields.length - 1; ++i) {
          rootJoin = rootJoin.join(fields[i]);
        }

        expression = rootJoin.get(fields[i]);
      } else {
        String[] parts = field.split("\\.");
        Path<?> path = root;
        for (String part : parts) {
          if (path.get(part).getJavaType() == Set.class || path.get(part).getJavaType() == List.class) {
            path = getOrCreateJoin(root, part);
          } else {
            path = path.get(part);
          }
        }
        expression = path;
      }
    }

    expression = expression == null ? root.get(field) : expression;

    switch (searchOperator) {
      case EQ:
        expression = handleEnumConversion(expression);
        predicates.add(criteriaBuilder.equal(expression, list.get(0)));
        break;
      case ANY:
        expression = handleEnumConversion(expression);
        Predicate predicate = expression.in(list);
        predicates.add(predicate);
        break;
      case NIN:
        expression = handleEnumConversion(expression);
        predicate = expression.in(list).not();
        predicates.add(predicate);
        break;
      case LT:
        predicates.add(criteriaBuilder.lessThan(expression.as(String.class), list.get(0).toString()));
        break;
      case GT:
        predicates.add(criteriaBuilder.greaterThan(expression.as(String.class), list.get(0).toString()));
        break;
      case LIKE:
        //TODO check for a different approach than using sql lower function
        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(expression.as(String.class)), WILDCARD_PERCENT + list.get(0).toString().toLowerCase() + WILDCARD_PERCENT));
        break;
      case LTE, LOE:
        predicates.add(criteriaBuilder.lessThanOrEqualTo(expression.as(String.class), list.get(0).toString()));
        break;
      case GTE, GOE:
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(expression.as(String.class), list.get(0).toString()));
        break;
      case NE:
        predicates.add(criteriaBuilder.notEqual(expression.as(String.class), list.get(0).toString()));
        break;
      case IS_NOT_SET:
        predicates.add(criteriaBuilder.isNull(expression));
        break;
      default:
        throw new IllegalArgumentException(MessageFormat.format(INVALID_SEARCH_OPERATION, operator));
    }
  }

  private Expression<String> handleEnumConversion(Expression<String> exp) {
    if (exp.getJavaType().isEnum()) {
      exp = exp.as(String.class);
    }

    return exp;
  }

  private Join<?, ?> getOrCreateJoin(From<?, ?> from, String attribute) {
    for (Join<?, ?> join : from.getJoins()) {
      boolean sameName = join.getAttribute().getName().equals(attribute);
      if (sameName && join.getJoinType().equals(JoinType.LEFT)) {
        return join;
      }
    }
    return from.join(attribute, JoinType.LEFT);
  }
}

