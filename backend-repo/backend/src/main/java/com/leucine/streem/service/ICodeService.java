package com.leucine.streem.service;

import com.leucine.streem.constant.Type;

public interface ICodeService {
  String getCode(Type.EntityType entityType, Long organisationsId);
}
