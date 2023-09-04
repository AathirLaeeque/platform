package com.leucine.streem.service;

import com.leucine.streem.dto.JobDto;
import com.leucine.streem.dto.request.CreateJobRequest;
import com.leucine.streem.exception.MultiStatusException;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.Facility;
import com.leucine.streem.model.Scheduler;
import com.leucine.streem.model.helper.PrincipalUser;

import java.io.IOException;

public interface ICreateJobService {
  JobDto createJob(CreateJobRequest createJobRequest, PrincipalUser principalUser, Facility facility, boolean isScheduled,
                   Scheduler scheduler, Long nextExpectedStartDate) throws ResourceNotFoundException, StreemException, IOException, MultiStatusException;

  void createScheduledJob(Long schedulerId, Long dateTime) throws ResourceNotFoundException, StreemException, MultiStatusException, IOException;
}
