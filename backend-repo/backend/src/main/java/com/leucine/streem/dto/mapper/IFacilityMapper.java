package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.FacilityDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.Facility;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IFacilityMapper extends IBaseMapper<FacilityDto, Facility> {

}
