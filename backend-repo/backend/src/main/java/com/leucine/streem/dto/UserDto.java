package com.leucine.streem.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto implements Serializable {
  private static final long serialVersionUID = 265263058061656140L;
  private String id;
  private String employeeId;
  private String username;
  private String firstName;
  private String lastName;
  private String email;
  private String department;
  private String token;
  private OrganisationDto organisation;
  private List<FacilityDto> facilities;
  private List<RoleDto> roles;
  private String state;
  private boolean archived;
}
