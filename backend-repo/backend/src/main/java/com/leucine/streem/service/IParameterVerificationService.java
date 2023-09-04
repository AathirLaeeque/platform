package com.leucine.streem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leucine.streem.dto.ParameterVerificationDto;
import com.leucine.streem.dto.ParameterVerificationListViewDto;
import com.leucine.streem.dto.request.ParameterVerificationRequest;
import com.leucine.streem.dto.request.PeerAssignRequest;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface IParameterVerificationService {
  ParameterVerificationDto initiateSelfVerification(Long jobId, Long parameterId) throws ResourceNotFoundException, StreemException, JsonProcessingException;

  ParameterVerificationDto acceptSelfVerification(Long jobId, Long parameterId) throws ResourceNotFoundException, StreemException;

  ParameterVerificationDto sendForPeerVerification(Long jobId, Long parameterId, PeerAssignRequest peerAssignRequest) throws ResourceNotFoundException, StreemException, JsonProcessingException;

  ParameterVerificationDto recallSelfVerification(Long jobId, Long parameterId) throws ResourceNotFoundException, StreemException;

  ParameterVerificationDto recallPeerVerification(Long jobId, Long parameterId) throws ResourceNotFoundException, StreemException;

  ParameterVerificationDto rejectPeerVerification(Long jobId, Long parameterId, ParameterVerificationRequest parameterVerificationRequest) throws ResourceNotFoundException, StreemException;

  ParameterVerificationDto acceptPeerVerification(Long jobId, Long parameterId) throws ResourceNotFoundException, StreemException;

  Page<ParameterVerificationListViewDto> getVerifications(String status, Long jobId, Long requestedTo, Long requestedBy, String parameterName, Pageable pageable) throws ResourceNotFoundException, StreemException;

  Response<Object> getAssignees(Long jobId);
}
