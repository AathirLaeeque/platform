package com.leucine.streem.model.helper.parameter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalculationParameterVariable {
    private String label;
    private String taskId;
    private String parameterId;
}
