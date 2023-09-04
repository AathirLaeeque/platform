package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.ChecklistAuditDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.ChecklistAudit;
import org.mapstruct.Mapper;

@Mapper
public interface IChecklistAuditMapper extends IBaseMapper<ChecklistAuditDto, ChecklistAudit> {
}
