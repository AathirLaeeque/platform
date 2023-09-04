package com.leucine.streem.model.helper;

import com.leucine.streem.exception.SpecificationBuilderException;
import com.leucine.streem.model.Checklist;
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
import java.util.*;

public class ChecklistSpecificationBuilder {
  public ChecklistSpecificationBuilder() {
    super();
  }

  public static Specification<Checklist> createSpecification(String filters, List<SearchCriteria> mandatorySearchCriterias) {
    ChecklistSpecificationBuilder specificationBuilder = new ChecklistSpecificationBuilder();
    Specification<Checklist> specification = null;
    if (!Utility.isEmpty(filters)) {
      try {
        SearchFilter searchFilter = JsonUtils.readValue(URLDecoder.decode(filters, StandardCharsets.UTF_8.toString()), SearchFilter.class);
        if (!Utility.isEmpty(mandatorySearchCriterias)) {
          Specification<Checklist> mandatorySpecification = specificationBuilder.createSpecification(mandatorySearchCriterias, SearchOperator.AND.toString());
          Specification<Checklist> filtersSpecification = specificationBuilder.createSpecification(searchFilter.getFields(), searchFilter.getOp());
          specification = mandatorySpecification.and(filtersSpecification);
        } else {
          specification = specificationBuilder.createSpecification(searchFilter.getFields(), searchFilter.getOp());
        }
      } catch (Exception exception) {
        throw new SpecificationBuilderException("Error parsing filters");
      }
    } else {
      specification = specificationBuilder.createSpecification(mandatorySearchCriterias, SearchOperator.AND.toString());
    }
    return specification;
  }

  public Specification<Checklist> createSpecification(List<SearchCriteria> searchCriteria, String builderCondition) {
    return (root, query, criteriaBuilder) -> {
      return this.buildSpecification(searchCriteria, root, criteriaBuilder, builderCondition, query);
    };
  }

  protected Predicate buildSpecification(List<SearchCriteria> searchCriteriaList, Root<Checklist> root,
                                         CriteriaBuilder criteriaBuilder, String builderCondition, CriteriaQuery<?> query) {
    query.distinct(true);
    List<Predicate> predicates = new ArrayList();
    Iterator var = searchCriteriaList.iterator();

    while(var.hasNext()) {
      SearchCriteria searchCriteria = (SearchCriteria)var.next();
      if (searchCriteria != null && !ObjectUtils.isEmpty(searchCriteria.getValues())) {
        this.generatePredicate(root, criteriaBuilder, predicates, searchCriteria);
      }
    }

    switch(SearchOperator.valueOf(builderCondition)) {
      case AND:
        return criteriaBuilder.and((Predicate[])predicates.toArray(new Predicate[0]));
      case OR:
        return criteriaBuilder.or((Predicate[])predicates.toArray(new Predicate[0]));
      default:
        throw new IllegalArgumentException(MessageFormat.format("Invalid filter condition : {0}", builderCondition));
    }
  }

  protected void generatePredicate(Root<Checklist> root, CriteriaBuilder criteriaBuilder,
                                   List<Predicate> predicates, SearchCriteria searchCriteria) {
    List<Object> list = searchCriteria.getValues();
    String field = searchCriteria.getField();
    String operator = searchCriteria.getOp();
    SearchOperator searchOperatorOperation = SearchOperator.valueOf(operator);
    this.addSearchConditions(searchOperatorOperation, list, field, operator, root, criteriaBuilder, predicates);
  }

  protected void addSearchConditions(SearchOperator searchOperatorOperation, List<Object> list, String field,
                                     String operator, Root<Checklist> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
    Expression expression = null;

    if(field.equals("not.a.collaborator")) {
      CriteriaQuery<String> c = criteriaBuilder.createQuery(String.class);
      Subquery<List> sq = c.subquery(List.class);
      Root<Checklist> project = sq.from(Checklist.class);
      Join<Object, Object>  rootJoinject =  project.join("collaborators").join("user");
      sq.select(project.get("id"));
      sq.where(criteriaBuilder.equal(rootJoinject.get("id").as(Long.class), list.get(0).toString()));

      expression = root.get("id");
      Predicate predicate = expression.in(sq).not();
      predicates.add(predicate);
    } else {

      if(field.contains(".")) {
        String[] parts = field.split("\\.");
        Path<?> path = root;
        for (String part : parts) {
          if(path.get(part).getJavaType() == Set.class || path.get(part).getJavaType() == List.class){
            path = getOrCreateJoin(root, part);
          } else{
            path = path.get(part);
          }
        }
        expression = path;
      }
      expression = expression == null ? root.get(field) : expression;

      switch (searchOperatorOperation) {
        case EQ:
          predicates.add(criteriaBuilder.equal(expression.as(String.class), list.get(0).toString()));
          break;
        case ANY:
          expression = handleEnumConversion(expression);
          Predicate predicate = expression.in(list);
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
          predicates.add(criteriaBuilder.like(criteriaBuilder.lower(expression.as(String.class)), "%" + list.get(0).toString().toLowerCase() + "%"));
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
        default:
          throw new IllegalArgumentException(MessageFormat.format("Innvalid filter search operation : {0}", operator));
      }
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
