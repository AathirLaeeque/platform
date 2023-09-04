package com.leucine.streem.repository.impl;

import com.leucine.streem.constant.Queries;
import com.leucine.streem.constant.Type;
import com.leucine.streem.model.Code;
import com.leucine.streem.repository.ICodeRepository;
import com.leucine.streem.repository.mapper.CodeRowMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CodeRepository implements ICodeRepository {
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public CodeRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Transactional
  @Override
  public Code getCode(Long organisationId, Type.EntityType type, Integer clause) {
    return jdbcTemplate.queryForObject(Queries.CREATE_OR_UPDATE_CODE, new Object[]{type.name(), clause, organisationId},
            new CodeRowMapper());
  }
}

