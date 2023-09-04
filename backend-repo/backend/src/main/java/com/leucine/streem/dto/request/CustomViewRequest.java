package com.leucine.streem.dto.request;


import com.leucine.streem.collections.CustomViewColumn;
import com.leucine.streem.collections.CustomViewFilter;
import com.leucine.streem.constant.Type;
import lombok.Data;

import java.util.List;

@Data
public class CustomViewRequest {
  private String label;
  private Long useCaseId;
  private Type.ConfigurableViewTargetType targetType;
  private List<CustomViewColumn> columns;
  private List<CustomViewFilter> filters;
}
