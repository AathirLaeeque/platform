package com.leucine.streem.service;

import com.leucine.streem.collections.CustomView;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.request.CustomViewRequest;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICustomViewService {
  Page<CustomView> getAllCustomViews(String filters, Pageable pageable);

  CustomView getCustomViewById(String customViewId) throws ResourceNotFoundException;

  CustomView createCustomView(Long checklistId, CustomViewRequest customViewRequest) throws ResourceNotFoundException, StreemException;

  CustomView createCustomView(CustomViewRequest customViewRequest) throws StreemException;

  CustomView editCustomView(String customViewId, CustomViewRequest customViewRequest) throws ResourceNotFoundException;

  BasicDto archiveCustomView(String customViewId) throws ResourceNotFoundException;
}
