package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.ChecklistDefaultUserDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.User;
import org.mapstruct.Mapper;

@Mapper
public interface IChecklistDefaultUserMapper extends IBaseMapper<ChecklistDefaultUserDto, User> {
}
