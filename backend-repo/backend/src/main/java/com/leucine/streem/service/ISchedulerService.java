package com.leucine.streem.service;

import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.SchedulerDto;
import com.leucine.streem.dto.SchedulerInfoDto;
import com.leucine.streem.dto.SchedulerPartialDto;
import com.leucine.streem.dto.request.CreateProcessSchedulerRequest;
import com.leucine.streem.dto.request.UpdateSchedulerRequest;
import com.leucine.streem.exception.MultiStatusException;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface ISchedulerService {
  SchedulerPartialDto createScheduler(CreateProcessSchedulerRequest createProcessSchedulerRequest) throws ResourceNotFoundException, StreemException, MultiStatusException, IOException;

  Page<SchedulerPartialDto> getAllScheduler(String filters, Pageable pageable);

  SchedulerDto getScheduler(Long schedulerId) throws ResourceNotFoundException;

  SchedulerDto updateScheduler(Long schedulerId, UpdateSchedulerRequest updateSchedulerRequest) throws ResourceNotFoundException, StreemException;

  SchedulerInfoDto getSchedulerInfo(Long schedulerId) throws ResourceNotFoundException;

  BasicDto archiveScheduler(Long schedulerId) throws ResourceNotFoundException, StreemException;

  void findAndDeprecateSchedulersForChecklist(Long checklistId, User user) throws StreemException;
}
