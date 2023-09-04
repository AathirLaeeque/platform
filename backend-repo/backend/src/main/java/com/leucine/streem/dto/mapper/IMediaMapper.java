package com.leucine.streem.dto.mapper;

import com.leucine.streem.config.MediaConfig;
import com.leucine.streem.dto.MediaDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.dto.request.MediaRequest;
import com.leucine.streem.dto.response.MediaUploadResponse;
import com.leucine.streem.model.Media;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class IMediaMapper implements IBaseMapper<MediaDto, Media> {
  @Autowired
  protected MediaConfig mediaConfig;

  @Override
  @Mapping(target = "link", expression = "java(mediaConfig.getCdn() + java.io.File.separator + entity.getRelativePath() + java.io.File.separator + entity.getFilename())")
  public abstract MediaDto toDto(Media entity);

  @Mapping(source = "id", target = "mediaId")
  @Mapping(target = "link", expression = "java(mediaConfig.getCdn() + java.io.File.separator + entity.getRelativePath() + java.io.File.separator + entity.getFilename())")
  public abstract MediaUploadResponse toMediaUploadResponsse(Media entity);

  public abstract List<MediaUploadResponse> toMediaUploadResponsse(List<Media> entity);

  public abstract Media mediaRequestToEntity(MediaRequest mediaRequest);

  public abstract void update(MediaRequest mediaRequest, @MappingTarget Media media);
}
