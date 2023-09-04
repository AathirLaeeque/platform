package com.leucine.streem.controller;

import com.leucine.streem.dto.MediaDto;
import com.leucine.streem.dto.request.MediaRequest;
import com.leucine.streem.dto.response.MediaUploadResponse;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/medias")
public interface IMediaController {

  @PostMapping("/upload")
  Response<MediaUploadResponse> upload(@RequestParam(name = "name", required = false) String name,
                                       @RequestParam(name = "description", required = false) String description,
                                       @RequestParam("file") MultipartFile file);

  @PatchMapping("/{mediaId}")
  Response<MediaDto> update(@PathVariable Long mediaId, @RequestBody MediaRequest mediaRequest) throws ResourceNotFoundException;

  @PostMapping("/uploads")
  Response<List<MediaUploadResponse>> uploads(@RequestParam("file") MultipartFile[] files);

  @DeleteMapping("/{mediaId}")
  @ResponseBody
  Response<MediaDto> archive(@PathVariable Long mediaId) throws ResourceNotFoundException;

}
