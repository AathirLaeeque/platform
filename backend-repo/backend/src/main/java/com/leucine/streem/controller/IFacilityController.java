package com.leucine.streem.controller;

import com.leucine.streem.dto.response.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/facilities")
public interface IFacilityController {

  @GetMapping
  @ResponseBody
  Response<Object> getFacilities(@RequestParam(name = "filters", defaultValue = "") String filters, Pageable pageable);
}
