package com.leucine.streem.controller.impl;

import com.leucine.streem.controller.IUseCaseController;
import com.leucine.streem.dto.UseCaseDto;
import com.leucine.streem.service.IUseCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UseCaseController implements IUseCaseController {

  private final IUseCaseService useCaseService;

  @Autowired
  public UseCaseController(IUseCaseService useCaseService) {
    this.useCaseService = useCaseService;
  }

  @Override
  public List<UseCaseDto> getUseCases(String filters, Pageable pageable) {
    return useCaseService.getUseCases(filters, pageable);
  }
}
