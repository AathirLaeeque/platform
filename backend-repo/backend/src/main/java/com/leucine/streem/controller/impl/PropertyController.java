package com.leucine.streem.controller.impl;

import com.leucine.streem.constant.Type;
import com.leucine.streem.controller.IPropertyController;
import com.leucine.streem.dto.PropertyDto;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.service.IPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PropertyController implements IPropertyController {
  private final IPropertyService propertyService;

  @Autowired
  public PropertyController(IPropertyService propertyService) {
    this.propertyService = propertyService;
  }

  @Override
  public Response<List<PropertyDto>> getAll(Type.PropertyType type, Long useCaseId, boolean archived, String filters, Pageable pageable) {
    return Response.builder().data(propertyService.getAllPropertiesByType(type, useCaseId, archived, filters, pageable)).build();
  }
}
