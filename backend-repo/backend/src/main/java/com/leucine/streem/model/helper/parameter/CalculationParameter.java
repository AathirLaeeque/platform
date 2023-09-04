package com.leucine.streem.model.helper.parameter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalculationParameter {
    private String expression;
    private String uom;
    private Map<String, CalculationParameterVariable> variables;
}
