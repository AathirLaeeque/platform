package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistDefaultUserDto implements Serializable {
  @Serial
  private static final long serialVersionUID = -265263058061656140L;
  private String id;
  private String employeeId;
  private String firstName;
  private String lastName;
  private Set<String> taskIds = new HashSet<>();
}
