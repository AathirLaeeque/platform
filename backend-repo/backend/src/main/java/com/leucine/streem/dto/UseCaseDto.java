package com.leucine.streem.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UseCaseDto implements Serializable {
  @Serial
  private static final long serialVersionUID = -8820480057296959948L;

  private String id;
  private String name;
  private String label;
  private Integer orderTree;
  private String description;
  private JsonNode metadata;
  private boolean enabled;
}
