package com.leucine.streem.controller.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leucine.streem.controller.IParameterVerificationController;
import com.leucine.streem.dto.ParameterVerificationDto;
import com.leucine.streem.dto.ParameterVerificationListViewDto;
import com.leucine.streem.dto.request.ParameterVerificationRequest;
import com.leucine.streem.dto.request.PeerAssignRequest;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.service.IParameterVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class ParameterVerificationController implements IParameterVerificationController {

  private final IParameterVerificationService parameterVerificationService;

  @Autowired
  public ParameterVerificationController(IParameterVerificationService parameterVerificationService) {
    this.parameterVerificationService = parameterVerificationService;
  }


  @Override
  public Response<ParameterVerificationDto> initiateSelfVerification(Long jobId, Long parameterId) throws ResourceNotFoundException, StreemException, JsonProcessingException {
    return Response.builder().data(parameterVerificationService.initiateSelfVerification(jobId, parameterId)).build();
  }

  @Override
  public Response<ParameterVerificationDto> acceptSelfVerification(Long jobId, Long parameterId) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(parameterVerificationService.acceptSelfVerification(jobId, parameterId)).build();
  }

  @Override
  public Response<ParameterVerificationDto> sendForPeerVerification(Long jobId, Long parameterId, PeerAssignRequest peerAssignRequest) throws ResourceNotFoundException, StreemException, JsonProcessingException {
    return Response.builder().data(parameterVerificationService.sendForPeerVerification(jobId, parameterId, peerAssignRequest)).build();
  }

  @Override
  public Response<ParameterVerificationDto> recallPeerVerification(Long jobId, Long parameterId) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(parameterVerificationService.recallPeerVerification(jobId, parameterId)).build();
  }

  @Override
  public Response<ParameterVerificationDto> recallSelfVerification(Long jobId, Long parameterId) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(parameterVerificationService.recallSelfVerification(jobId, parameterId)).build();
  }

  @Override
  public Response<ParameterVerificationDto> rejectPeerVerification(Long jobId, Long parameterId, ParameterVerificationRequest parameterVerificationRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(parameterVerificationService.rejectPeerVerification(jobId, parameterId, parameterVerificationRequest)).build();
  }

  @Override
  public Response<ParameterVerificationDto> acceptPeerVerification(Long jobId, Long parameterId) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(parameterVerificationService.acceptPeerVerification(jobId, parameterId)).build();
  }

  @Override
  public Response<Object> getAssignees(Long jobId) {
    return parameterVerificationService.getAssignees(jobId);
  }

  @Override
  public Response<Page<ParameterVerificationListViewDto>> getUserAssignedAndRequestedVerifications(String status, Long jobId, Long requestedTo, Long requestedBy, String parameterName, Pageable pageable) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(parameterVerificationService.getVerifications(status, jobId, requestedTo, requestedBy, parameterName, pageable)).build();
  }

}
