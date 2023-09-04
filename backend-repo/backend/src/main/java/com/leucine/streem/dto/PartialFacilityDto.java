package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartialFacilityDto implements Serializable {
  private static final long serialVersionUID= -6278757887310693838L;

  private String id;
}

