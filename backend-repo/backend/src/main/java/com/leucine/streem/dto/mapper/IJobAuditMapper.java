package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.JobAuditDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.JobAudit;
import org.mapstruct.Mapper;

@Mapper
public interface IJobAuditMapper extends IBaseMapper<JobAuditDto, JobAudit> {
}
