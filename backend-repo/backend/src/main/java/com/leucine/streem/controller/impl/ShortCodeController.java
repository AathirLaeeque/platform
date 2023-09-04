package com.leucine.streem.controller.impl;

import com.leucine.streem.collections.shortcode.ShortCodeData;
import com.leucine.streem.controller.IShortCodeController;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.ShortCodeDto;
import com.leucine.streem.dto.request.GenerateShortCodeRequest;
import com.leucine.streem.dto.request.ShortCodeModifyRequest;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.service.IShortCodeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ShortCodeController implements IShortCodeController {
  private final IShortCodeService shortCodeService;

  @Override
  public Response<ShortCodeDto> generateShortCode(GenerateShortCodeRequest generateShortCodeRequest) throws StreemException, ResourceNotFoundException {
    return Response.<ShortCodeDto>builder().data(shortCodeService.generateShortCode(generateShortCodeRequest)).build();
  }

  @Override
  public Response<ShortCodeData> getShortCodeData(String shortCode) throws StreemException, ResourceNotFoundException {
    return Response.builder().data(shortCodeService.getShortCodeData(shortCode)).build();
  }

  @Override
  public Response<BasicDto> editShortCode(ShortCodeModifyRequest shortCodeModifyRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(shortCodeService.editShortCode(shortCodeModifyRequest)).build();
  }
}
