package com.leucine.streem.model.helper.parameter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.leucine.streem.dto.request.ExecuteMediaPrameterRequest;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MediaParameterBase {
  List<ExecuteMediaPrameterRequest> medias;
}
