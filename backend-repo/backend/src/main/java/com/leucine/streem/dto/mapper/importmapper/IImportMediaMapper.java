package com.leucine.streem.dto.mapper.importmapper;

import com.leucine.streem.dto.MediaDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.dto.request.ImportMediaRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IImportMediaMapper extends IBaseMapper<ImportMediaRequest, MediaDto> {

  @Mapping(source = "id", target = "mediaId")
  @Mapping(source = "filename", target = "fileName")
  ImportMediaRequest toDto(MediaDto e);
}
