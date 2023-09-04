package com.leucine.streem.repository.mapper;

import com.leucine.streem.constant.Type;
import com.leucine.streem.model.Code;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.leucine.streem.model.compositekey.CodeCompositeKey;
import org.springframework.jdbc.core.RowMapper;

public class CodeRowMapper implements RowMapper<Code> {

  @Override
  public Code mapRow(ResultSet rs, int rowNum) throws SQLException {
    Code code = new Code();
    CodeCompositeKey codeId = new CodeCompositeKey();
    codeId.setClause(rs.getShort("clause"));
    codeId.setType(Type.EntityType.valueOf(rs.getString("type")));
    code.setCodeId(codeId);
    code.setCounter(rs.getInt("counter"));
    return code;
  }
}
