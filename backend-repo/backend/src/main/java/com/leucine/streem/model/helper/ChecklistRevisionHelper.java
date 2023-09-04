package com.leucine.streem.model.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.model.Parameter;
import com.leucine.streem.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChecklistRevisionHelper {
  Map<Long, Task> revisedParameterTaskMap = new HashMap<>();
  Map<Long, Task> parameterToBeRevisedTaskMap = new HashMap<>();
  List<Parameter> calculationParameterList = new ArrayList<>();
  Map<Long, Parameter> calculationParametersMap = new HashMap<>();
  Map<Long, Parameter> revisedParameters = new HashMap<>();
  Map<Long, JsonNode> parameterHavingValidations = new HashMap<>();
}
