package com.leucine.streem.dto.mapper;

import com.leucine.streem.collections.Property;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.dto.request.ObjectTypePropertyCreateRequest;
import org.mapstruct.Mapper;

@Mapper
public interface IObjectTypePropertyMapper extends IBaseMapper<ObjectTypePropertyCreateRequest, Property> {

}
