package com.leucine.streem.service;

import com.leucine.streem.dto.MediaDto;
import com.leucine.streem.dto.request.MediaRequest;
import com.leucine.streem.dto.response.MediaUploadResponse;
import com.leucine.streem.exception.ResourceNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IMediaService {
  MediaUploadResponse save(MediaRequest mediaRequest, MultipartFile file);

  MediaUploadResponse save(MultipartFile files);

  List<MediaUploadResponse> save(MultipartFile[] files);

  MediaDto update(Long mediaId, MediaRequest mediaRequest) throws ResourceNotFoundException;

  MediaDto archive(Long mediaId) throws ResourceNotFoundException;
}
