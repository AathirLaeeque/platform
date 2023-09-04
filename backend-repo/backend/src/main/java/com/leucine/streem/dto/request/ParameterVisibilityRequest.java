package com.leucine.streem.dto.request;

import java.util.Set;

public record ParameterVisibilityRequest(Set<String> show, Set<String> hide) {
}
