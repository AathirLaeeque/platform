package com.leucine.streem.dto;

import com.leucine.streem.constant.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobStateDto implements Serializable {
  private static final long serialVersionUID = 8665246750593307900L;
  private String id;
  private String code;
  private State.Job state;
}
