package com.leucine.streem.dto.request;

import com.leucine.streem.constant.Misc;
import com.leucine.streem.dto.FacilityDto;
import com.leucine.streem.dto.RoleDto;
import lombok.Data;

import java.util.List;

@Data
public class UserAddRequest {
  private String employeeId;
  private String firstName;
  private String lastName;
  private String department;
  private String email;
  private List<FacilityDto> facilities;
  private List<RoleDto> roles;
  private Misc.UserType userType;
  private String username;
}