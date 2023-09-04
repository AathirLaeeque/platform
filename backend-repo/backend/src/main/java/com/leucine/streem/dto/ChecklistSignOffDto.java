package com.leucine.streem.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class ChecklistSignOffDto implements Serializable {
  private static final long serialVersionUID = -8731596727898707629L;
  private String id;
  private String employeeId;
  private String email;
  private String firstName;
  private String lastName;
  private int orderTree;
  private String state;
  private Long signedAt;
}
