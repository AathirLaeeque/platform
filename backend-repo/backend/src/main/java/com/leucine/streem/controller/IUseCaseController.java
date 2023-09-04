package com.leucine.streem.controller;

import com.leucine.streem.dto.UseCaseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/use-cases")
public interface IUseCaseController {

  @GetMapping
  @ResponseBody
  List<UseCaseDto> getUseCases(@RequestParam(name = "filters", defaultValue = "") String filters, Pageable pageable);
}