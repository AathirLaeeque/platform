package com.leucine.streem.dto;

import com.leucine.streem.constant.CollectionMisc;
import com.leucine.streem.model.helper.search.SearchOperator;
import com.leucine.streem.model.helper.search.Selector;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceParameterFilterField implements Serializable {
  private SearchOperator op;
  private String field; // searchable field in the object
  private CollectionMisc.ChangeLogType fieldType; // RELATION OR PROPERTY
  private List<Object> values; // constants in case of selector being CONSTANT, Options Ids in case of
  private Selector selector; // indicates if the filter is to be done on a constant value or should the value be referenced through a parameter
  private String referencedParameterId; // in case of selector being PARAMETER this is the id of the parameter
  // in case of selector being PARAMETER this is the type of the parameter, can be null for constant selectors and field type is relation
  // hence the property type is string and not an enum
  private String propertyType;

  // Following fields are required for UI to show display name, external Id etc
  // In case of single select or multi select these are going to be displayName of the options
  // In case of relation it's going to be object type display name
  private String displayName; // can be null for constant selectors
  private String externalId; // can be null for constant selectors and single select, multi select
}
