package com.leucine.streem.controller;

import com.leucine.streem.collections.shortcode.ShortCodeData;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.ShortCodeDto;
import com.leucine.streem.dto.request.GenerateShortCodeRequest;
import com.leucine.streem.dto.request.ShortCodeModifyRequest;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/short-code")
public interface IShortCodeController {
  @PostMapping
  Response<ShortCodeDto> generateShortCode(@RequestBody GenerateShortCodeRequest generateShortCodeRequest) throws StreemException, ResourceNotFoundException;

  @PatchMapping
  Response<BasicDto> editShortCode(@RequestBody ShortCodeModifyRequest shortCodeModifyRequest) throws ResourceNotFoundException, StreemException;

  @GetMapping
  Response<ShortCodeData> getShortCodeData(@RequestParam("shortCode") String shortCode) throws StreemException, ResourceNotFoundException;
}
