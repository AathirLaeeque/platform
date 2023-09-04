package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StagePartialDto implements Serializable {
  private static final long serialVersionUID = -9066465926685243311L;

  private String id;
  private String name;
}
