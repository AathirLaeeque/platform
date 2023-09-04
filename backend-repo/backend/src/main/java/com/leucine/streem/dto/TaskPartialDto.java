package com.leucine.streem.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskPartialDto implements Serializable {
  private static final long serialVersionUID = 7996544705089209858L;

  private String id;
  private String name;
}
