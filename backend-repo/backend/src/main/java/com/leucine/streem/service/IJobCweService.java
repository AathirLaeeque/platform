package com.leucine.streem.service;

import com.leucine.streem.dto.JobCweDto;
import com.leucine.streem.dto.request.JobCweDetailRequest;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.model.Job;
import com.leucine.streem.model.User;

public interface IJobCweService {
  JobCweDto getJobCweDetail(Long id) throws ResourceNotFoundException;
  void createJobCweDetail(JobCweDetailRequest jobCweDetailRequest, Job job, User principalUserEntity);
}
