package com.leucine.streem.service.impl;

import com.leucine.commons.id.IdGenerator;
import com.leucine.streem.dto.JobCweDto;
import com.leucine.streem.dto.mapper.IJobCweDetailMapper;
import com.leucine.streem.dto.request.JobCweDetailRequest;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.exception.ExceptionType;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.model.Job;
import com.leucine.streem.model.JobCweDetail;
import com.leucine.streem.model.User;
import com.leucine.streem.repository.IJobCweDetailRepository;
import com.leucine.streem.repository.IMediaRepository;
import com.leucine.streem.service.IJobCweService;
import com.leucine.streem.util.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobCweService implements IJobCweService {

  private final IJobCweDetailRepository jobCweDetailRepository;
  private final IJobCweDetailMapper jobCweDetailMapper;
  private final IMediaRepository mediaRepository;

  @Override
  public JobCweDto getJobCweDetail(Long id) throws ResourceNotFoundException {
    JobCweDetail jobCweDetail = jobCweDetailRepository.findByJobId(id)
        .orElseThrow(() -> new ResourceNotFoundException(id, ErrorCode.JOB_CWE_DETAIL_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    return jobCweDetailMapper.toDto(jobCweDetail);
  }

  @Override
  public void createJobCweDetail(JobCweDetailRequest jobCweDetailRequest, Job job, User principalUserEntity) {
    JobCweDetail jobCweDetail = new JobCweDetail();
    jobCweDetail.setJob(job)
        .setComment(jobCweDetailRequest.getComment())
        .setReason(jobCweDetailRequest.getReason())
        .setCreatedBy(principalUserEntity)
        .setModifiedBy(principalUserEntity)
        .setId(IdGenerator.getInstance().nextId());

    if (!Utility.isEmpty(jobCweDetailRequest.getMedias())) {
      jobCweDetail.addAllMedias(mediaRepository.findAll((jobCweDetailRequest.getMedias().stream().map(m -> m.getMediaId()).collect(Collectors.toSet()))), principalUserEntity);
    }

    jobCweDetailRepository.save(jobCweDetail);
  }
}
