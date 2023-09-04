package com.leucine.streem.model.helper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leucine.streem.dto.FacilityDto;
import com.leucine.streem.dto.OrganisationDto;
import com.leucine.streem.dto.RoleDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class PrincipalUser implements Serializable {
  private static final long serialVersionUID = -8714903674405335802L;
  private Long id;
  private String employeeId;
  private String firstName;
  private String lastName;
  private String username;
  private String email;
  private String token;
  private String serviceId;
  private Long organisationId;
  private OrganisationDto organisation;
  private List<FacilityDto> facilities;
  private Long currentFacilityId;
  private List<RoleDto> roles;
  private Set<String> roleNames;
  private String state;
  private boolean archived;

  @JsonIgnore
  private String idAsString;
}