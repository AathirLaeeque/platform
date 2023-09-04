package com.leucine.streem.dto.mapper;

import com.leucine.streem.collections.changelogs.UserInfo;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.User;
import com.leucine.streem.model.helper.PrincipalUser;
import org.mapstruct.Mapper;

// TODO: rethink mappers for mongo entities
@Mapper
public interface IUserInfoMapper extends IBaseMapper<UserInfo, User> {
  UserInfo toUserInfo(PrincipalUser principalUser);
}
