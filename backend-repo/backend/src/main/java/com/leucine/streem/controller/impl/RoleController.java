package com.leucine.streem.controller.impl;

import com.leucine.streem.controller.IRoleController;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.service.IRoleService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;


@Component
public class RoleController implements IRoleController {

  private final IRoleService roleService;

  public RoleController(IRoleService roleService) {
    this.roleService = roleService;
  }

  @Override
  public Response<Object> getRoles(String filters, Pageable pageable) {
    return roleService.getRoles(filters, pageable);
  }
}
