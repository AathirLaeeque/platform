package com.leucine.streem.service;

import com.leucine.streem.collections.EntityObject;
import com.leucine.streem.collections.shortcode.ShortCodeData;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.ShortCodeDto;
import com.leucine.streem.dto.request.GenerateShortCodeRequest;
import com.leucine.streem.dto.request.ShortCodeModifyRequest;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.helper.PrincipalUser;

public interface IShortCodeService {
  ShortCodeDto generateShortCode(GenerateShortCodeRequest generateShortCodeRequest) throws StreemException, ResourceNotFoundException;

  BasicDto editShortCode(ShortCodeModifyRequest shortCodeModifyRequest) throws ResourceNotFoundException, StreemException;

  ShortCodeData getShortCodeData(String shortCode) throws StreemException, ResourceNotFoundException;

  void generateAndSaveShortCode(PrincipalUser principalUser, EntityObject entityObject);

}
