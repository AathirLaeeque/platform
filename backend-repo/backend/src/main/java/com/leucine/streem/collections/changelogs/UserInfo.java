package com.leucine.streem.collections.changelogs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
  private String id;
  private String employeeId;
  private String firstName;
  private String lastName;
}
