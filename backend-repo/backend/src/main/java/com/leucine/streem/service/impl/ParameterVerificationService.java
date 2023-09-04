package com.leucine.streem.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.leucine.streem.constant.Misc;
import com.leucine.streem.constant.State;
import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.ParameterVerificationDto;
import com.leucine.streem.dto.ParameterVerificationListViewDto;
import com.leucine.streem.dto.mapper.IParameterVerificationMapper;
import com.leucine.streem.dto.projection.JobAssigneeView;
import com.leucine.streem.dto.request.ParameterVerificationRequest;
import com.leucine.streem.dto.request.PeerAssignRequest;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ExceptionType;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.handler.IParameterVerificationHandler;
import com.leucine.streem.handler.ParameterVerificationHandler;
import com.leucine.streem.model.*;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.repository.*;
import com.leucine.streem.service.IJobAuditService;
import com.leucine.streem.service.IParameterVerificationService;
import com.leucine.streem.service.IUserService;
import com.leucine.streem.util.DateTimeUtils;
import com.leucine.streem.util.JsonUtils;
import com.leucine.streem.util.Utility;
import com.leucine.streem.util.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;

//TODO: check if branching rules related changes are required
//TODO: After one cycle of self verify and peer verify is complete, wwe will create new entry"
@Slf4j
@Service
public class ParameterVerificationService implements IParameterVerificationService {
  private final IUserRepository userRepository;
  private final IJobRepository jobRepository;
  private final IParameterVerificationRepository parameterVerificationRepository;
  private final IParameterValueRepository parameterValueRepository;
  private final IParameterVerificationHandler parameterVerificationHandler;

  private final IJobAuditService jobAuditService;
  private final IParameterVerificationMapper parameterVerificationMapper;
  private final Map<State.ParameterVerification, Set<State.ParameterVerification>> stateMapSelf;
  private final Map<State.ParameterVerification, Set<State.ParameterVerification>> stateMapPeer;
  private final EntityManager entityManager;
  private final ITaskExecutionAssigneeRepository taskExecutionAssigneeRepository;
  private final IUserService userService;
  private final IParameterRepository parameterRepository;

  public ParameterVerificationService(IUserRepository userRepository, IJobRepository jobRepository, IParameterVerificationRepository parameterVerificationRepository, IParameterValueRepository parameterValueRepository, ParameterVerificationHandler parameterVerificationHandler, IJobAuditService jobAuditService, IParameterVerificationMapper parameterVerificationMapper, EntityManager entityManager, ITaskExecutionAssigneeRepository taskExecutionAssigneeRepository, IUserService userService, IParameterRepository parameterRepository) {
    this.userRepository = userRepository;
    this.jobRepository = jobRepository;
    this.parameterVerificationRepository = parameterVerificationRepository;
    this.parameterValueRepository = parameterValueRepository;
    this.parameterVerificationHandler = parameterVerificationHandler;
    this.jobAuditService = jobAuditService;
    this.parameterVerificationMapper = parameterVerificationMapper;
    this.entityManager = entityManager;
    this.taskExecutionAssigneeRepository = taskExecutionAssigneeRepository;
    this.userService = userService;
    this.parameterRepository = parameterRepository;
    this.stateMapSelf = new HashMap<>();
    this.stateMapPeer = new HashMap<>();
    this.init();
  }

  private void init() {
    // middle transition states
    stateMapSelf.put(State.ParameterVerification.PENDING, Set.of(State.ParameterVerification.ACCEPTED, State.ParameterVerification.RECALLED));
    stateMapSelf.put(State.ParameterVerification.ACCEPTED, Set.of(State.ParameterVerification.PENDING)); // peer initiated
    stateMapSelf.put(State.ParameterVerification.RECALLED, Set.of(State.ParameterVerification.PENDING));

    // starting from here, we will not allow any state transition
    stateMapSelf.put(null, Set.of(State.ParameterVerification.PENDING));


    stateMapPeer.put(State.ParameterVerification.PENDING, Set.of(State.ParameterVerification.RECALLED, State.ParameterVerification.ACCEPTED, State.ParameterVerification.REJECTED));
    stateMapPeer.put(State.ParameterVerification.REJECTED, Set.of(State.ParameterVerification.PENDING));
    stateMapPeer.put(State.ParameterVerification.RECALLED, Set.of(State.ParameterVerification.PENDING));
    stateMapPeer.put(State.ParameterVerification.ACCEPTED, Set.of(State.ParameterVerification.PENDING));

    // starting from here, we will not allow any state transition
    stateMapPeer.put(null, Set.of(State.ParameterVerification.PENDING));
  }

  /**
   * User executing the job will fill values in parameter and initiate self verification, entry is created in PV table with initiated SV status
   * we will take a lock on parameter values entry of parameter value table, so that it cannot be further modified
   */
  @Override
  @Transactional
  public ParameterVerificationDto initiateSelfVerification(Long jobId, Long parameterId) throws ResourceNotFoundException, StreemException, JsonProcessingException {
    log.info("[initiateSelfVerification] Request to initiate self verification for parameter, jobId: {}, parameterId: {}",
      jobId, parameterId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.findById(principalUser.getId())
      .orElseThrow(() -> new ResourceNotFoundException(principalUser.getId(), ErrorCode.USER_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    Type.VerificationType verificationType = Type.VerificationType.SELF;
    State.ParameterVerification expectedState = State.ParameterVerification.PENDING;
    ParameterValue parameterValue = parameterValueRepository.findByJobIdAndParameterId(jobId, parameterId);

    //this validation is only for type checklist parameter
    Parameter parameter = parameterRepository.getReferenceById(parameterId);
    if (Type.Parameter.CHECKLIST.equals(parameter.getType()) && !Utility.isEmpty(parameterValue) && !Utility.isEmpty(parameterValue.getChoices())) {
      Map<String, String> choices = JsonUtils.readValue(parameterValue.getChoices().toString(), new TypeReference<>() {});
      for (String status : choices.values()) {
        if (status.equals(State.Selection.NOT_SELECTED.name())) {
          ValidationUtils.invalidate(parameterId, ErrorCode.MANDATORY_ACTIVITY_PENDING);
        }
      }
    }

    validateSelfStateTransfer(jobId, parameterValue.getId(), expectedState);
    parameterVerificationHandler.canInitiateSelfVerification(principalUserEntity, parameterValue);
    Job job = jobRepository.getReferenceById(jobId);

    ParameterVerification parameterVerification = createParameterVerification(
      DateTimeUtils.now(),
      principalUserEntity,
      verificationType,
      expectedState,
      job,
      parameterValue
    );

    parameterValue.setVerified(false);
    parameterValue.setState(State.ParameterExecution.APPROVAL_PENDING);

    try {
      parameterValueRepository.save(parameterValue);
      parameterVerificationRepository.save(parameterVerification);
    } catch (Exception e) {
      log.error("[initiateSelfVerification] Error while initiating self verification for parameter, jobId: {}, parameterId: {}", jobId, parameterId, e);
      ValidationUtils.invalidate(parameterId, ErrorCode.PARAMETER_VERIFICATION_INITIATION_FAILED);
    }
    jobAuditService.initiateSelfVerification(jobId, parameterVerification, principalUser);
    return parameterVerificationMapper.toDto(parameterVerification);

  }

  /**
   * After initiate verification data of parameter is locked and user click on sign option enter password and credential verification call goes to jaas,
   * once successfully sign is done, this api call is made to complete the verification and unlock the data of parameter value table
   **/
  @Override
  @Transactional
  public ParameterVerificationDto acceptSelfVerification(Long jobId, Long parameterId) throws ResourceNotFoundException, StreemException {
    log.info("[completeSelfVerification] Request to complete self verification for parameter, jobId: {}, parameterId: {}", jobId, parameterId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.findById(principalUser.getId())
      .orElseThrow(() -> new ResourceNotFoundException(principalUser.getId(), ErrorCode.USER_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    Type.VerificationType verificationType = Type.VerificationType.SELF;
    State.ParameterVerification expectedState = State.ParameterVerification.ACCEPTED;
    ParameterValue parameterValue = parameterValueRepository.findByJobIdAndParameterId(jobId, parameterId);
    long createdAt = DateTimeUtils.now();
    ParameterVerification lastParameterVerification = validateSelfStateTransfer(jobId, parameterValue.getId(), expectedState);
    if (!Utility.isEmpty(lastParameterVerification)) {
        createdAt = lastParameterVerification.getCreatedAt();
    }
    parameterVerificationHandler.canCompleteSelfVerification(principalUserEntity, parameterId, lastParameterVerification);
    Job job = jobRepository.getReferenceById(jobId);

    ParameterVerification parameterVerification = createParameterVerification(
      createdAt,
      principalUserEntity,
      verificationType,
      expectedState,
      job,
      parameterValue
    );

    boolean verified = parameterValue.getParameter().getVerificationType().equals(Type.VerificationType.SELF);
    parameterValue.setVerified(verified);
    if (parameterValue.getParameter().getVerificationType().equals(Type.VerificationType.BOTH)) {
      parameterValue.setState(State.ParameterExecution.VERIFICATION_PENDING);
    } else {
      parameterValue.setState(State.ParameterExecution.EXECUTED);
    }

    try {
      parameterVerificationRepository.save(parameterVerification);
      parameterValueRepository.save(parameterValue);
    } catch (Exception e) {
      log.error("[completeSelfVerification] Error while completing self verification for parameter, jobId: {}, parameterId: {}", jobId, parameterId, e);
      ValidationUtils.invalidate(parameterId, ErrorCode.PARAMETER_VERIFICATION_COMPLETION_FAILED);
    }

    jobAuditService.completeSelfVerification(jobId, parameterVerification, principalUser);
    return parameterVerificationMapper.toDto(parameterVerification);
  }

  @Override
  @Transactional
  public ParameterVerificationDto sendForPeerVerification(Long jobId, Long parameterId, PeerAssignRequest peerAssignRequest) throws ResourceNotFoundException, StreemException, JsonProcessingException {
    log.info("[sendForPeerVerification] Request to send for peer verification for jobId: {}, parameterId: {}, peerAssignRequest: {}",
      jobId, parameterId, peerAssignRequest);

    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.findById(principalUser.getId())
      .orElseThrow(() -> new ResourceNotFoundException(principalUser.getId(), ErrorCode.USER_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    ParameterValue parameterValue = parameterValueRepository.findByJobIdAndParameterId(jobId, parameterId);

    //this validation is only for type checklist parameter
    Parameter parameter = parameterRepository.getReferenceById(parameterId);
    if (Type.Parameter.CHECKLIST.equals(parameter.getType()) && !Utility.isEmpty(parameterValue) && !Utility.isEmpty(parameterValue.getChoices())) {
      Map<String, String> choices = JsonUtils.readValue(parameterValue.getChoices().toString(), new TypeReference<>() {});
      for (String status : choices.values()) {
        if (status.equals(State.Selection.NOT_SELECTED.name())) {
          ValidationUtils.invalidate(parameterId, ErrorCode.MANDATORY_ACTIVITY_PENDING);
        }
      }
    }
    Type.VerificationType verificationType = Type.VerificationType.PEER;
    State.ParameterVerification expectedState = State.ParameterVerification.PENDING;
    validatePeerStateTransfer(jobId, parameterValue.getId(), expectedState);

    Job job = jobRepository.getReferenceById(jobId);
    ParameterVerification parameterVerification = createParameterVerification(
      DateTimeUtils.now(),
      principalUserEntity,
      verificationType,
      expectedState,
      job,
      parameterValue
    );
    parameterVerification.setUser(userRepository.getReferenceById(peerAssignRequest.getUserId()));
    parameterValue.setState(State.ParameterExecution.APPROVAL_PENDING);

    try {
      parameterValueRepository.save(parameterValue);
      parameterVerificationRepository.save(parameterVerification);
    } catch (Exception e) {
      log.error("[sendForPeerVerification] Error while sending verification for parameter, parameterId: {}",
        parameterId, e);
      ValidationUtils.invalidate(parameterId, ErrorCode.CANNOT_SEND_PARAMETER_FOR_PEER_VERIFICATION);
    }
    jobAuditService.sendForPeerVerification(jobId, parameterVerification, principalUser);
    return parameterVerificationMapper.toDto(parameterVerification);
  }

  @Override
  @Transactional
  public ParameterVerificationDto recallPeerVerification(Long jobId, Long parameterId) throws ResourceNotFoundException, StreemException {
    log.info("[recallPeerVerification] Request to recall  verification for jobId: {}, parameterId: {}",
      jobId, parameterId);

    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.findById(principalUser.getId())
      .orElseThrow(() -> new ResourceNotFoundException(principalUser.getId(), ErrorCode.USER_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    State.ParameterVerification expectedState = State.ParameterVerification.RECALLED;
    Type.VerificationType verificationType = Type.VerificationType.PEER;
    ParameterValue parameterValue = parameterValueRepository.findByJobIdAndParameterId(jobId, parameterId);
    long createdAt = DateTimeUtils.now();
    ParameterVerification lastParameterVerification = validatePeerStateTransfer(jobId, parameterValue.getId(), expectedState);
    if (!Utility.isEmpty(lastParameterVerification)) {
      createdAt = lastParameterVerification.getCreatedAt();
    }

    Job job = jobRepository.getReferenceById(jobId);

    ParameterVerification parameterVerification = createParameterVerification(
      createdAt,
      principalUserEntity,
      verificationType,
      expectedState,
      job,
      parameterValue
    );


    parameterValue.setState(State.ParameterExecution.VERIFICATION_PENDING);

    try {
      parameterValueRepository.save(parameterValue);
      parameterVerificationRepository.save(parameterVerification);
    } catch (Exception e) {
      log.error("[recallPeerVerification] Error while recalling verification for parameter, parameterId: {}",
        parameterId, e);
      ValidationUtils.invalidate(parameterId, ErrorCode.PARAMETER_VERIFICATION_RECALL_FAILED);
    }

    jobAuditService.recallVerification(jobId, parameterVerification, principalUser);
    return parameterVerificationMapper.toDto(parameterVerification);
  }

  @Override
  @Transactional
  public ParameterVerificationDto recallSelfVerification(Long jobId, Long parameterId) throws ResourceNotFoundException, StreemException {
    log.info("[recallSelfVerification] Request to recall  verification for jobId: {}, parameterId: {}",
      jobId, parameterId);

    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.findById(principalUser.getId())
      .orElseThrow(() -> new ResourceNotFoundException(principalUser.getId(), ErrorCode.USER_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    State.ParameterVerification expectedState = State.ParameterVerification.RECALLED;
    Type.VerificationType verificationType = Type.VerificationType.SELF;
    ParameterValue parameterValue = parameterValueRepository.findByJobIdAndParameterId(jobId, parameterId);
    long createdAt = DateTimeUtils.now();
    ParameterVerification lastParameterVerification = validateSelfStateTransfer(jobId, parameterValue.getId(), expectedState);
    if (!Utility.isEmpty(lastParameterVerification)) {
      createdAt = lastParameterVerification.getCreatedAt();
    }

    Job job = jobRepository.getReferenceById(jobId);

    ParameterVerification parameterVerification = createParameterVerification(
      createdAt,
      principalUserEntity,
      verificationType,
      expectedState,
      job,
      parameterValue
    );

    parameterValue.setState(State.ParameterExecution.BEING_EXECUTED);


    try {
      parameterValueRepository.save(parameterValue);
      parameterVerificationRepository.save(parameterVerification);
    } catch (Exception e) {
      log.error("[recallSelfVerification] Error while recalling verification for parameter, parameterId: {}",
        parameterId, e);
      ValidationUtils.invalidate(parameterId, ErrorCode.PARAMETER_VERIFICATION_RECALL_FAILED);
    }

    jobAuditService.recallVerification(jobId, parameterVerification, principalUser);
    return parameterVerificationMapper.toDto(parameterVerification);
  }

  @Override
  @Transactional
  public ParameterVerificationDto acceptPeerVerification(Long jobId, Long parameterId) throws ResourceNotFoundException, StreemException {
    log.info("[acceptPeerVerification] Request to accept peer verification for parameter, jobId: {}, parameterId: {}", jobId, parameterId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.findById(principalUser.getId())
      .orElseThrow(() -> new ResourceNotFoundException(principalUser.getId(), ErrorCode.USER_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    Type.VerificationType verificationType = Type.VerificationType.PEER;
    State.ParameterVerification expectedState = State.ParameterVerification.ACCEPTED;
    ParameterValue parameterValue = parameterValueRepository.findByJobIdAndParameterId(jobId, parameterId);
    long createdAt = DateTimeUtils.now();
    ParameterVerification lastParameterVerification = validatePeerStateTransfer(jobId, parameterValue.getId(), expectedState);
    if (Utility.isEmpty(lastParameterVerification.getUser())) {
      ValidationUtils.invalidate(parameterValue.getId(), ErrorCode.PARAMETER_VERIFICATION_NOT_ALLOWED);
    }
    if (!Utility.isEmpty(lastParameterVerification)) {
      createdAt = lastParameterVerification.getCreatedAt();
    }
    parameterVerificationHandler.canCompletePeerVerification(principalUserEntity, lastParameterVerification);
    Job job = jobRepository.getReferenceById(jobId);

    ParameterVerification parameterVerification = createParameterVerification(
      createdAt,
      lastParameterVerification.getUser(),
      verificationType,
      expectedState,
      job,
      parameterValue
    );
    parameterVerification.setCreatedBy(lastParameterVerification.getCreatedBy());
    parameterValue.setVerified(true);
    parameterValue.setState(State.ParameterExecution.EXECUTED);

    try {
      parameterVerificationRepository.save(parameterVerification);
      parameterValueRepository.save(parameterValue);
    } catch (Exception e) {
      log.error("[completePeerVerification] Error while completing peer verification for parameter, jobId: {}, parameterId: {}", jobId, parameterId, e);
      ValidationUtils.invalidate(parameterId, ErrorCode.PARAMETER_VERIFICATION_COMPLETION_FAILED);
    }

    jobAuditService.acceptPeerVerification(jobId, parameterVerification, principalUser);
    return parameterVerificationMapper.toDto(parameterVerification);
  }

  @Override
  @Transactional
  public ParameterVerificationDto rejectPeerVerification(Long jobId, Long parameterId, ParameterVerificationRequest parameterVerificationRequest) throws ResourceNotFoundException, StreemException {
    log.info("[rejectPeerVerification] Request to reject peer verification for parameter, jobId: {}, parameterId: {}", jobId, parameterId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.findById(principalUser.getId())
      .orElseThrow(() -> new ResourceNotFoundException(principalUser.getId(), ErrorCode.USER_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    Type.VerificationType verificationType = Type.VerificationType.PEER;
    State.ParameterVerification expectedState = State.ParameterVerification.REJECTED;
    ParameterValue parameterValue = parameterValueRepository.findByJobIdAndParameterId(jobId, parameterId);
    long createdAt = DateTimeUtils.now();
    ParameterVerification lastParameterVerification = validatePeerStateTransfer(jobId, parameterValue.getId(), expectedState);
    if (Utility.isEmpty(lastParameterVerification.getUser())) {
      ValidationUtils.invalidate(parameterValue.getId(), ErrorCode.PARAMETER_VERIFICATION_NOT_ALLOWED);
    }
    if (!Utility.isEmpty(lastParameterVerification)) {
      createdAt = lastParameterVerification.getCreatedAt();
    }
    parameterVerificationHandler.canCompletePeerVerification(principalUserEntity, lastParameterVerification);
    Job job = jobRepository.getReferenceById(jobId);

    ParameterVerification parameterVerification = createParameterVerification(
      createdAt,
      lastParameterVerification.getUser(),
      verificationType,
      expectedState,
      job,
      parameterValue
    );
    parameterVerification.setCreatedBy(lastParameterVerification.getCreatedBy());
    parameterVerification.setComments(parameterVerificationRequest.getComments());
    parameterValue.setState(State.ParameterExecution.VERIFICATION_PENDING);

    try {
      parameterVerificationRepository.save(parameterVerification);
      parameterValueRepository.save(parameterValue);
    } catch (Exception ex) {
      log.error("[rejectPeerVerification] Error while completing peer verification for parameter, jobId: {}, parameterId: {}", jobId, parameterId);
      ValidationUtils.invalidate(parameterId, ErrorCode.PARAMETER_VERIFICATION_COMPLETION_FAILED);
    }

    jobAuditService.rejectPeerVerification(jobId, parameterVerification, principalUser);
    return parameterVerificationMapper.toDto(parameterVerification);
  }

  @Override
  public Response<Object> getAssignees(Long jobId) {
    List<JobAssigneeView> jobAssignees = taskExecutionAssigneeRepository.getJobAssignees(Set.of(jobId));
    Set<String> userIds = jobAssignees.stream().map(JobAssigneeView::getId).collect(Collectors.toSet());
    return userService.getAllByRoles(Misc.ASSIGNEE_ROLES, "", true, userIds, PageRequest.of(0, Integer.MAX_VALUE));

  }

  //TODO: we have LAZY initializations, doing it this was will cause lot of N+1 queries to fire, go through getVerificationsModified and complete it with a projection queries this will solve the performance problem
  @Override
  public Page<ParameterVerificationListViewDto> getVerifications(String status, Long jobId, Long requestedTo, Long requestedBy, String parameterName, Pageable pageable) {
    log.info("[getUserAssignedAndRequestedVerifications] Request to get user parameter verifications for status: {}, jobId: {},  requestedTo: {}, requestedBy: {}, parameterName: {}", status, jobId, requestedTo, requestedBy, parameterName);

    StringBuilder query = new StringBuilder();
    query.append("""
      select pv1.* from parameter_verifications pv1 join
        (select jobs_id, parameter_values_id, verification_type, max(modified_at) as max_time from parameter_verifications
           GROUP BY jobs_id, parameter_values_id, verification_type) pv2
           on pv1.jobs_id = pv2.jobs_id and pv1.parameter_values_id = pv2.parameter_values_id and pv1.verification_type = pv2.verification_type
           where pv1.modified_at = pv2.max_time
      """);
    Map<String, Object> params = new HashMap<>();
    if (!Utility.isEmpty(jobId)) {
      query.append(" and pv1.jobs_id = :jobId");
      params.put("jobId", jobId);
    }
    if (!Utility.isEmpty(requestedBy)) {
      query.append(" and pv1.created_by = :requestedBy");
      params.put("requestedBy", requestedBy);
    }
    if (!Utility.isEmpty(requestedTo)) {
      query.append(" and pv1.users_id = :requestedTo");
      params.put("requestedTo", requestedTo);
    }
    if (!Utility.isEmpty(status)) {
      query.append(" and pv1.verification_status = :status");
      params.put("status", status);
    }
    if (!Utility.isEmpty(parameterName)) {
      query.append(" and pv1.parameter_values_id in (select pv.id from parameter_values pv join parameters p on pv.parameters_id = p.id where p.label like :parameterName)");
      params.put("parameterName", "%" + parameterName + "%");
    }
    // Count query to determine total results

    Query countJpaQuery = entityManager.createNativeQuery("SELECT COUNT(*) FROM (" + query + ") AS countQuery");
    for (Map.Entry<String, Object> entry : params.entrySet()) {
      countJpaQuery.setParameter(entry.getKey(), entry.getValue());
    }
    long totalCount = ((Number) countJpaQuery.getSingleResult()).longValue();

    // Actual query with pagination
    Query jpaQuery = entityManager.createNativeQuery(query.toString(), ParameterVerification.class);
    for (Map.Entry<String, Object> entry : params.entrySet()) {
      jpaQuery.setParameter(entry.getKey(), entry.getValue());
    }
    List<ParameterVerification> parameterVerificationList = jpaQuery.getResultList();
    int pageNumber = pageable.getPageNumber();
    int pageSize = pageable.getPageSize();
    int firstResult = pageNumber * pageSize;
    jpaQuery.setFirstResult(firstResult);
    jpaQuery.setMaxResults(pageSize);

    List<ParameterVerification> parameterVerifications = jpaQuery.getResultList();
    Page<ParameterVerification> parameterVerificationsPage = new PageImpl<>(parameterVerifications, PageRequest.of(pageNumber, pageSize), totalCount);
    List<ParameterVerificationListViewDto> parameterVerificationListViewDtos = new ArrayList<>();
    for (ParameterVerification parameterVerification : parameterVerificationsPage.getContent()) {
      ParameterVerificationListViewDto parameterVerificationListViewDto = parameterVerificationMapper.toParameterListViewDto(parameterVerification);
      parameterVerificationListViewDtos.add(parameterVerificationListViewDto);
    }
    return new PageImpl<>(parameterVerificationListViewDtos, pageable, parameterVerificationList.size());
  }

  private ParameterVerification validateSelfStateTransfer(long jobId, long parameterValueId, State.ParameterVerification expectedState) throws StreemException {
    State.ParameterVerification currentState = null;
    ParameterVerification parameterVerification = parameterVerificationRepository.findByJobIdAndParameterValueIdAndVerificationType(jobId, parameterValueId, Type.VerificationType.SELF.toString());
    if (!Utility.isEmpty(parameterVerification)) {
      currentState = parameterVerification.getVerificationStatus();
    }

    if (!this.stateMapSelf.containsKey(currentState) || !this.stateMapSelf.get(currentState).contains(expectedState)) {
      ValidationUtils.invalidate(parameterValueId, ErrorCode.PARAMETER_VERIFICATION_NOT_ALLOWED);
    }

    return parameterVerification;
  }

  private ParameterVerification validatePeerStateTransfer(long jobId, long parameterValueId, State.ParameterVerification expectedState) throws StreemException {
    State.ParameterVerification currentState = null;
    ParameterVerification parameterVerification = parameterVerificationRepository.findByJobIdAndParameterValueIdAndVerificationType(jobId, parameterValueId, Type.VerificationType.PEER.toString());
    if (!Utility.isEmpty(parameterVerification)) {
      currentState = parameterVerification.getVerificationStatus();
    }

    if (!this.stateMapPeer.containsKey(currentState) || !this.stateMapPeer.get(currentState).contains(expectedState)) {
      ValidationUtils.invalidate(parameterValueId, ErrorCode.PARAMETER_VERIFICATION_NOT_ALLOWED);
    }

    return parameterVerification;
  }

  private static ParameterVerification createParameterVerification(
    long createdAt,
    User userEntity,
    Type.VerificationType verificationType,
    State.ParameterVerification expectedState,
    Job job,
    ParameterValue parameterValue
  ) {
    ParameterVerification parameterVerification = new ParameterVerification();
    parameterVerification.setJob(job);
    parameterVerification.setParameterValue(parameterValue);
    parameterVerification.setVerificationType(verificationType);
    parameterVerification.setVerificationStatus(expectedState);
    parameterVerification.setCreatedBy(userEntity);
    parameterVerification.setModifiedBy(userEntity);
    parameterVerification.setUser(userEntity);
    parameterVerification.setCreatedAt(createdAt);

    return parameterVerification;
  }
}
