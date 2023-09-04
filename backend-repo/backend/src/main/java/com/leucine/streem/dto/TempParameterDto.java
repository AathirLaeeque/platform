package com.leucine.streem.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TempParameterDto implements Serializable {
  private static final long serialVersionUID = -7639615260770390827L;

  private String id;
  private int orderTree;
  private boolean isMandatory;
  private String type;
  private String label;
  private JsonNode data;
  private TempParameterValueDto response;
}
