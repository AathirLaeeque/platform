package com.leucine.streem.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.constant.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseParameterValueDto implements Serializable {
  private static final long serialVersionUID = 2726275850385527160L;

  private String value;
  private String reason;
  private State.ParameterExecution state;
  private JsonNode choices;
  private List<MediaDto> medias = new ArrayList<>();
  private PartialAuditDto audit;
  private boolean hidden;
}
