package com.leucine.streem.controller.impl;

import com.leucine.streem.controller.IMediaController;
import com.leucine.streem.dto.MediaDto;
import com.leucine.streem.dto.request.MediaRequest;
import com.leucine.streem.dto.response.MediaUploadResponse;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.service.IMediaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@AllArgsConstructor
public class MediaController implements IMediaController {
  private final IMediaService mediaService;

  @Override
  public Response<MediaUploadResponse> upload(String name, String description, MultipartFile file) {
    return Response.builder().data(mediaService.save(new MediaRequest().setName(name).setDescription(description), file)).build();
  }

  @Override
  public Response<List<MediaUploadResponse>> uploads(MultipartFile[] files) {
    return Response.builder().data(mediaService.save(files)).build();
  }

  @Override
  public Response<MediaDto> update(Long mediaId, MediaRequest mediaRequest) throws ResourceNotFoundException {
    return Response.builder().data(mediaService.update(mediaId, mediaRequest)).build();
  }

  @Override
  public Response<MediaDto> archive(Long mediaId) throws ResourceNotFoundException {
    return Response.builder().data(mediaService.archive(mediaId)).build();
  }

}
