package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.UseCaseDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.UseCase;
import org.mapstruct.Mapper;

@Mapper
public interface IUseCaseMapper extends IBaseMapper<UseCaseDto, UseCase> {
}
