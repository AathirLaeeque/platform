package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.PropertyDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.Property;
import org.mapstruct.Mapper;

@Mapper
public interface IPropertyMapper extends IBaseMapper<PropertyDto, Property> {
}
