package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserAuditDto implements Serializable {
  private static final long serialVersionUID = 1389994268792869092L;

  private String id;
  private String employeeId;
  private String firstName;
  private String lastName;
}
