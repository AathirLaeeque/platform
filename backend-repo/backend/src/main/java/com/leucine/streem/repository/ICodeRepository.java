package com.leucine.streem.repository;

import com.leucine.streem.constant.Type;
import com.leucine.streem.model.Code;
import org.springframework.stereotype.Repository;

@Repository
public interface ICodeRepository {
  Code getCode(Long organisationId, Type.EntityType type, Integer clause);
}
