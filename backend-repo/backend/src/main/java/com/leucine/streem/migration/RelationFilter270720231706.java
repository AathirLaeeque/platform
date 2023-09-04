package com.leucine.streem.migration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leucine.streem.collections.ObjectType;
import com.leucine.streem.collections.Property;
import com.leucine.streem.constant.CollectionMisc;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.ResourceParameterFilter;
import com.leucine.streem.dto.ResourceParameterFilterField;
import com.leucine.streem.model.Parameter;
import com.leucine.streem.model.Relation;
import com.leucine.streem.model.helper.parameter.ResourceParameter;
import com.leucine.streem.model.helper.search.SearchOperator;
import com.leucine.streem.model.helper.search.Selector;
import com.leucine.streem.repository.IObjectTypeRepository;
import com.leucine.streem.repository.IParameterRepository;
import com.leucine.streem.repository.IRelationRepository;
import com.leucine.streem.service.IEntityObjectService;
import com.leucine.streem.util.JsonUtils;
import com.leucine.streem.util.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class RelationFilter270720231706 implements Serializable {
  @Serial
  private static final long serialVersionUID = -8002354194363851086L;

  private final IRelationRepository relationRepository;
  private final IParameterRepository parameterRepository;
  private final IObjectTypeRepository objectTypeRepository;
  private final IEntityObjectService entityObjectService;

  public BasicDto execute() throws JsonProcessingException {
    entityObjectService.enableSearchable();
    List<Relation> relationList = relationRepository.findAll();
    List<Parameter> parameters = parameterRepository.findAllById(relationList.stream().map(Relation::getId).collect(Collectors.toSet()));
    Map<Long, Parameter> parameterMap = parameters.stream().collect(Collectors.toMap(Parameter::getId, parameter -> parameter));
    for (Relation relation : relationList) {
      String urlPath = relation.getUrlPath();
      boolean isFilterPresentCheck = urlPath.contains("&");

      if (isFilterPresentCheck)
        processRelationsAndMapToResourceParameter(relation, urlPath, parameterMap);
    }
    return new BasicDto(null, "Success", null);
  }

  private void processRelationsAndMapToResourceParameter(Relation relation, String urlPath, Map<Long, Parameter> parameterMap) throws JsonProcessingException {
    String filterPart = urlPath.substring(urlPath.indexOf("&") + 1);
    String[] filterPairs = filterPart.split("&");

    ResourceParameter resourceParameter = JsonUtils.readValue(parameterMap.get(relation.getId()).getData().toString(), ResourceParameter.class);
    ResourceParameterFilter propertyFilters = new ResourceParameterFilter();
    propertyFilters.setOp(SearchOperator.AND);

    List<ResourceParameterFilterField> fields = new ArrayList<>();

    for (String filterPair : filterPairs) {
      String[] filterPairParts = filterPair.split("=");
      String filterKey = filterPairParts[0];

      ObjectType objectType = objectTypeRepository.findById(relation.getObjectTypeId()).get();
      objectType.getProperties().stream().map(Property::getExternalId).toList().forEach(System.out::println);
      Property property = null;
      for (Property p : objectType.getProperties()) {
        if (p.getExternalId().equals(filterKey)) {
          property = p;
          break;
        }
      }

      if (!Utility.isEmpty(property)) {
        createResourceParameterFilterField(property, filterPairs, fields);
      }
    }
    propertyFilters.setFields(fields);
    resourceParameter.setPropertyFilters(propertyFilters);
    resourceParameter.setUrlPath(urlPath.substring(0, urlPath.indexOf("&")));

    Parameter parameter = parameterMap.get(relation.getId());
    parameter.setData(JsonUtils.valueToNode(resourceParameter));
    parameterRepository.save(parameter);
  }

  private static void createResourceParameterFilterField(Property property, String[] filterPairs, List<ResourceParameterFilterField> fields) {
    ResourceParameterFilterField field = new ResourceParameterFilterField();
    field.setOp(SearchOperator.EQ);
    field.setField("searchable." + property.getId());
    field.setValues(List.of(filterPairs[1].split("=")[1]));
    field.setSelector(Selector.CONSTANT);
    field.setFieldType(CollectionMisc.ChangeLogType.PROPERTY);
    fields.add(field);
  }
}
