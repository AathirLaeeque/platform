package com.leucine.streem.dto.request;

import com.leucine.streem.dto.FacilityDto;
import com.leucine.streem.dto.RoleDto;
import lombok.Data;

import java.util.List;

@Data
public class UserUpdateRequest {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String department;
  private List<RoleDto> roles;
  private List<FacilityDto> facilities;
}
