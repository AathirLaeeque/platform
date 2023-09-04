package com.leucine.streem.controller;

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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/parameter-verifications")
public interface IParameterVerificationController {

  @PostMapping("jobs/{jobId}/parameters/{parameterId}/self/verify")
  Response<ParameterVerificationDto> initiateSelfVerification(@PathVariable Long jobId, @PathVariable Long parameterId) throws ResourceNotFoundException, StreemException, JsonProcessingException;

  @PatchMapping("jobs/{jobId}/parameters/{parameterId}/self/accept")
  Response<ParameterVerificationDto> acceptSelfVerification(@PathVariable Long jobId, @PathVariable Long parameterId) throws ResourceNotFoundException, StreemException;

  @PostMapping("jobs/{jobId}/parameters/{parameterId}/peer/assign")
  Response<ParameterVerificationDto> sendForPeerVerification(@PathVariable Long jobId, @PathVariable Long parameterId,
                                                             @RequestBody PeerAssignRequest peerAssignRequest) throws ResourceNotFoundException, StreemException, JsonProcessingException;

  @PatchMapping("jobs/{jobId}/parameters/{parameterId}/peer/recall")
  Response<ParameterVerificationDto> recallPeerVerification(@PathVariable Long jobId, @PathVariable Long parameterId) throws ResourceNotFoundException, StreemException;

  @PatchMapping("jobs/{jobId}/parameters/{parameterId}/self/recall")
  Response<ParameterVerificationDto> recallSelfVerification(@PathVariable Long jobId, @PathVariable Long parameterId) throws ResourceNotFoundException, StreemException;

  @PatchMapping("jobs/{jobId}/parameters/{parameterId}/peer/reject")
  Response<ParameterVerificationDto> rejectPeerVerification(@PathVariable Long jobId, @PathVariable Long parameterId,
                                                            @RequestBody ParameterVerificationRequest parameterVerificationRequest) throws ResourceNotFoundException, StreemException;

  @PatchMapping("jobs/{jobId}/parameters/{parameterId}/peer/accept")
  Response<ParameterVerificationDto> acceptPeerVerification(@PathVariable Long jobId, @PathVariable Long parameterId) throws ResourceNotFoundException, StreemException;

  @GetMapping
  Response<Page<ParameterVerificationListViewDto>> getUserAssignedAndRequestedVerifications(@RequestParam(required = false) String status, @RequestParam(required = false) Long jobId, @RequestParam(required = false) Long requestedTo, @RequestParam(required = false) Long requestedBy, @RequestParam(required = false) String parameterName, Pageable pageable) throws ResourceNotFoundException, StreemException;

  @GetMapping("jobs/{jobId}/assignees")
  Response<Object> getAssignees(@PathVariable Long jobId);
}
