package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.AssigneeSignOffDto;
import com.leucine.streem.dto.UserAuditDto;
import com.leucine.streem.dto.UserDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.User;
import com.leucine.streem.model.helper.PrincipalUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IUserMapper extends IBaseMapper<UserDto, User> {
  UserAuditDto toUserAuditDto(User user);

  UserAuditDto toUserAuditDto(PrincipalUser principalUser);

  UserDto toDto(PrincipalUser principalUser);

  AssigneeSignOffDto toAssigneeSignOffDto(User user);

  @Mapping(target = "organisation", ignore = true)
  PrincipalUser toPrincipalUser(User user);
}
