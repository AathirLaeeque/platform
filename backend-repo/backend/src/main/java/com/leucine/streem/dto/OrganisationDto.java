package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganisationDto implements Serializable {
  private static final long serialVersionUID = -4694896674582176925L;

  private String id;
  private String name;
}
