package com.leucine.streem.dto.request;

import com.leucine.streem.dto.FacilityDto;
import com.leucine.streem.dto.RoleDto;
import lombok.Data;

import java.util.List;

@Data
public class UsernameCheckRequest {
  private String username;
}
