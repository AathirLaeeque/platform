package com.leucine.streem.dto.mapper;

import com.leucine.streem.config.MediaConfig;
import com.leucine.streem.dto.MediaDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.ParameterValueMediaMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class IParameterValueMediaMapper implements IBaseMapper<MediaDto, ParameterValueMediaMapping> {
  @Autowired
  protected MediaConfig mediaConfig;

  @Override
  @Mapping(source = "media.id", target = "id")
  @Mapping(source = "media.name", target = "name")
  @Mapping(source = "media.filename", target = "filename")
  @Mapping(source = "media.originalFilename", target = "originalFilename")
  @Mapping(source = "media.description", target = "description")
  @Mapping(expression = "java(mediaConfig.getCdn() + java.io.File.separator + entity.getMedia().getRelativePath() + java.io.File.separator + entity.getMedia().getFilename())", target = "link")
  @Mapping(source = "media.type", target = "type")
  @Mapping(source = "archived", target = "archived")
  public abstract MediaDto toDto(ParameterValueMediaMapping entity);

}
