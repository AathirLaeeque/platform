package com.leucine.streem.dto.request;

import java.util.Map;

public record ParameterTemporaryExecuteRequest(Map<Long, ParameterExecuteRequest> parameterValues, Long checklistId) {
}
