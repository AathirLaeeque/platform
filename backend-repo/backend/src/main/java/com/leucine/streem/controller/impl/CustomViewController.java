package com.leucine.streem.controller.impl;

import com.leucine.streem.collections.CustomView;
import com.leucine.streem.constant.Type;
import com.leucine.streem.controller.ICustomViewController;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.request.CustomViewRequest;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.service.ICustomViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class CustomViewController implements ICustomViewController {
  private final ICustomViewService customViewService;

  @Autowired
  public CustomViewController(ICustomViewService customViewService) {
    this.customViewService = customViewService;
  }

  @Override
  public Response<Page<CustomView>> getAllCustomViews(String filters, Pageable pageable) {
    return Response.builder().data(customViewService.getAllCustomViews(filters, pageable)).build();
  }

  @Override
  public Response<CustomView> getCustomViewById(String customViewId) throws ResourceNotFoundException {
    return Response.builder().data(customViewService.getCustomViewById(customViewId)).build();
  }

  @Override
  public Response<CustomView> createCustomView(Long checklistId, CustomViewRequest customViewRequest) throws ResourceNotFoundException, StreemException {
    customViewRequest.setTargetType(Type.ConfigurableViewTargetType.PROCESS);
    return Response.builder().data(customViewService.createCustomView(checklistId, customViewRequest)).build();
  }

  @Override
  public Response<CustomView> createCustomView(CustomViewRequest customViewRequest) throws StreemException {
    return Response.builder().data(customViewService.createCustomView(customViewRequest)).build();
  }


  @Override
  public Response<CustomView> editCustomView(String customViewId, CustomViewRequest customViewRequest) throws ResourceNotFoundException {
    return Response.builder().data(customViewService.editCustomView(customViewId, customViewRequest)).build();
  }

  @Override
  public Response<BasicDto> archiveCustomView(String customViewId) throws ResourceNotFoundException {
    return Response.builder().data(customViewService.archiveCustomView(customViewId)).build();
  }
}
