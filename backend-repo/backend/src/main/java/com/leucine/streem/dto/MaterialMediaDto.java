package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialMediaDto implements Serializable {
  private static final long serialVersionUID = 5477614898574358360L;

  private String id;
  private String mediaId;
  private String name;
  private int quantity;
}
