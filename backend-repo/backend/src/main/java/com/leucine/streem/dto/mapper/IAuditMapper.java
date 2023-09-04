package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.PartialAuditDto;
import com.leucine.streem.dto.UserAuditDto;
import com.leucine.streem.model.User;
import com.leucine.streem.model.helper.PrincipalUser;
import org.mapstruct.Mapper;

@Mapper
public interface IAuditMapper {
  static PartialAuditDto createAuditDto(User modifiedBy, Long modifiedAt) {
    PartialAuditDto partialAuditDto = new PartialAuditDto();
    UserAuditDto modifiedByDto = null;

    if (null != modifiedBy) {
      modifiedByDto = new UserAuditDto();
      modifiedByDto.setId(modifiedBy.getIdAsString());
      modifiedByDto.setFirstName(modifiedBy.getFirstName());
      modifiedByDto.setLastName(modifiedBy.getLastName());
      modifiedByDto.setEmployeeId(modifiedBy.getEmployeeId());
    }
    partialAuditDto.setModifiedBy(modifiedByDto);
    partialAuditDto.setModifiedAt(modifiedAt);
    return partialAuditDto;
  }

  static PartialAuditDto createAuditDtoFromPrincipalUser(PrincipalUser principalUser, Long modifiedAt) {
    PartialAuditDto partialAuditDto = new PartialAuditDto();
    UserAuditDto modifiedByDto = new UserAuditDto();

    if (null != principalUser) {
      modifiedByDto.setFirstName(principalUser.getFirstName());
      modifiedByDto.setLastName(principalUser.getLastName());
      modifiedByDto.setEmployeeId(principalUser.getEmployeeId());
    }

    partialAuditDto.setModifiedBy(modifiedByDto);
    partialAuditDto.setModifiedAt(modifiedAt);
    return partialAuditDto;
  }
}
