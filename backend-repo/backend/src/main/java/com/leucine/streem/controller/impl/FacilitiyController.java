package com.leucine.streem.controller.impl;

import com.leucine.streem.controller.IFacilityController;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.service.IFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

@Component
public class FacilitiyController implements IFacilityController {

  private final IFacilityService facilityService;

  @Autowired
  public FacilitiyController(IFacilityService facilityService) {
    this.facilityService = facilityService;
  }

  @Override
  public Response<Object> getFacilities(@RequestParam(name = "filters", defaultValue = "") String filters, Pageable pageable) {
    return facilityService.getAllFacilities(filters, pageable);
  }
}
