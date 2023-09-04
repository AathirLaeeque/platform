package com.leucine.streem.controller.impl;

import com.leucine.streem.controller.IChecklistAuditController;
import com.leucine.streem.dto.ChecklistAuditDto;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.service.IChecklistAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class ChecklistAuditController implements IChecklistAuditController {
  private final IChecklistAuditService checklistAuditService;

  @Autowired
  public ChecklistAuditController(IChecklistAuditService checklistAuditService) {
    this.checklistAuditService = checklistAuditService;
  }

  @Override
  public Response<Page<ChecklistAuditDto>> getAuditsByChecklistId(Long checklistId, String filters, Pageable pageable) throws StreemException {
    return Response.builder().data(checklistAuditService.getAuditsByChecklistId(checklistId, filters, pageable)).build();
  }

}
