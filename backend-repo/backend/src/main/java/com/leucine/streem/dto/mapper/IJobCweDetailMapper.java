package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.JobCweDto;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.JobCweDetail;
import org.mapstruct.Mapper;

@Mapper(uses = {IJobCweDetailMediaMapper.class})
public interface IJobCweDetailMapper extends IBaseMapper<JobCweDto, JobCweDetail> {
}
