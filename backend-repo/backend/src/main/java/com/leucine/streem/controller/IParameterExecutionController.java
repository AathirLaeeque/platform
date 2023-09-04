package com.leucine.streem.controller;

import com.leucine.streem.dto.ParameterDto;
import com.leucine.streem.dto.RuleHideShowDto;
import com.leucine.streem.dto.TempParameterDto;
import com.leucine.streem.dto.request.ParameterExecuteRequest;
import com.leucine.streem.dto.request.ParameterStateChangeRequest;
import com.leucine.streem.dto.request.ParameterTemporaryExecuteRequest;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/v1/parameters")
public interface IParameterExecutionController {

  @PatchMapping("/execute")
  @ResponseBody
  Response<ParameterDto> executeParameter(@RequestBody ParameterExecuteRequest parameterExecuteRequest) throws IOException, StreemException, ResourceNotFoundException;

  @PatchMapping("/error-correction")
  @ResponseBody
  Response<TempParameterDto> fixError(@RequestBody ParameterExecuteRequest parameterExecuteRequest) throws IOException, StreemException, ResourceNotFoundException;

  @PatchMapping("/reject")
  @ResponseBody
  Response<ParameterDto> rejectParameter(@RequestBody ParameterStateChangeRequest parameterStateChangeRequest) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/approve")
  @ResponseBody
  Response<ParameterDto> approveParameter(@RequestBody ParameterStateChangeRequest parameterStateChangeRequest) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/execute/temporary")
  Response<RuleHideShowDto> executeTemporary(@RequestBody ParameterTemporaryExecuteRequest parameterTemporaryExecuteRequest) throws IOException;


}

