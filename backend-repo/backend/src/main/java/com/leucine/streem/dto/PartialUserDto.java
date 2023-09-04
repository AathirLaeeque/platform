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
public class PartialUserDto implements Serializable {
  private static final long serialVersionUID = 265263058061656140L;
  private String id;
  private String employeeId;
  private String email;
  private String firstName;
  private String lastName;
  private String department;
}
