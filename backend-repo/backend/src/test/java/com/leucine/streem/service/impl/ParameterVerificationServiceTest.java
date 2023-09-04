//package com.leucine.streem.service.impl;
//
//import com.leucine.streem.constant.State;
//import com.leucine.streem.constant.Type;
//import com.leucine.streem.dto.ParameterVerificationDto;
//import com.leucine.streem.dto.mapper.IParameterVerificationMapper;
//import com.leucine.streem.dto.request.ParameterVerificationRequest;
//import com.leucine.streem.dto.request.PeerAssignRequest;
//import com.leucine.streem.exception.ResourceNotFoundException;
//import com.leucine.streem.exception.StreemException;
//import com.leucine.streem.handler.ParameterVerificationHandler;
//import com.leucine.streem.model.*;
//import com.leucine.streem.model.helper.PrincipalUser;
//import com.leucine.streem.repository.*;
//import com.leucine.streem.service.IJobAuditService;
//import com.leucine.streem.service.IParameterVerificationService;
//import com.leucine.streem.service.IUserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import javax.persistence.EntityManager;
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//
//class ParameterVerificationServiceTest {
//
//  private IParameterVerificationService parameterVerificationService;
//  private IUserRepository userRepository;
//  private IParameterValueRepository parameterValueRepository;
//  private IParameterVerificationRepository parameterVerificationRepository;
//  private ParameterVerificationHandler parameterVerificationHandler;
//  private IJobAuditService jobAuditService;
//  private IParameterVerificationMapper parameterVerificationMapper;
//  private IJobRepository jobRepository;
//  private Authentication mockAuthentication;
//  private PrincipalUser principalUser;
//  private User principalUserEntity;
//  private EntityManager entityManager;
//  private ITaskExecutionAssigneeRepository taskExecutionAssigneeRepository;
//  private IUserService userService;
//
//  @BeforeEach
//  void setUp() {
//    userRepository = mock(IUserRepository.class);
//    jobRepository = mock(IJobRepository.class);
//    parameterVerificationRepository = mock(IParameterVerificationRepository.class);
//    parameterValueRepository = mock(IParameterValueRepository.class);
//    parameterVerificationHandler = mock(ParameterVerificationHandler.class);
//    jobAuditService = Mockito.mock(IJobAuditService.class);
//    parameterVerificationMapper = Mockito.mock(IParameterVerificationMapper.class);
//    entityManager = mock(EntityManager.class);
//
//    mockAuthentication = Mockito.mock(Authentication.class);
//
//    SecurityContext securityContext = mock(SecurityContext.class);
//    when(securityContext.getAuthentication()).thenReturn(mockAuthentication);
//    SecurityContextHolder.setContext(securityContext);
//
//    parameterVerificationService = new ParameterVerificationService(
//      userRepository,
//      jobRepository,
//      parameterVerificationRepository,
//      parameterValueRepository,
//      parameterVerificationHandler,
//      jobAuditService,
//      parameterVerificationMapper,
//      entityManager,
//      taskExecutionAssigneeRepository, userService);
//
//    principalUser = new PrincipalUser();
//    principalUser.setId(789L);
//    principalUser.setUsername("testuser");
//
//    principalUserEntity = new User();
//    principalUserEntity.setId(789L);
//    when(mockAuthentication.getPrincipal()).thenReturn(principalUser);
//    doReturn(principalUserEntity).when(userRepository).getOne(10001L);
//  }
//
//  @Test
//  void initiateSelfVerification() throws StreemException, ResourceNotFoundException {
//    // Mock input data
//    Long jobId = 123L;
//    Long parameterId = 456L;
//    ParameterValue parameterValue = new ParameterValue();
//    parameterValue.setId(1001L);
//
//    when(userRepository.findById(principalUser.getId())).thenReturn(Optional.of(principalUserEntity));
//    when(parameterValueRepository.findByJobIdAndParameterId(jobId, parameterId)).thenReturn(parameterValue);
//    doNothing().when(parameterVerificationHandler).canInitiateSelfVerification(principalUserEntity, parameterValue);
//    when(jobRepository.getReferenceById(jobId)).thenReturn(new Job());
//    when(parameterValueRepository.save(parameterValue)).thenReturn(parameterValue);
//    when(parameterVerificationRepository.save(any(ParameterVerification.class))).thenReturn(new ParameterVerification());
//    doNothing().when(jobAuditService).initiateSelfVerification(eq(jobId), any(ParameterVerification.class), eq(principalUser));
//
//    // Perform the test
//    ParameterVerificationDto result = parameterVerificationService.initiateSelfVerification(jobId, parameterId);
//
//    // Verify the calls and assertions
//    verify(userRepository, times(1)).findById(principalUser.getId());
//    verify(parameterValueRepository, times(1)).findByJobIdAndParameterId(jobId, parameterId);
//    verify(parameterVerificationHandler, times(1)).canInitiateSelfVerification(principalUserEntity, parameterValue);
//    verify(jobRepository, times(1)).getReferenceById(jobId);
//    verify(parameterValueRepository, times(1)).save(parameterValue);
//    verify(parameterVerificationRepository, times(1)).save(any(ParameterVerification.class));
//    verify(parameterVerificationMapper, times(1)).toDto(any(ParameterVerification.class));
//    verify(jobAuditService, times(1)).initiateSelfVerification(eq(jobId), any(ParameterVerification.class), eq(principalUser));
//  }
//
//  @Test
//  void acceptSelfVerification() throws StreemException, ResourceNotFoundException {
//    Authentication authentication = mock(Authentication.class);
//    SecurityContext securityContext = mock(SecurityContext.class);
//    when(securityContext.getAuthentication()).thenReturn(authentication);
//    SecurityContextHolder.setContext(securityContext);
//    when(authentication.getPrincipal()).thenReturn(principalUser);
//
//    when(userRepository.findById(principalUser.getId())).thenReturn(Optional.of(principalUserEntity));
//    Parameter parameter = new Parameter();
//    parameter.setVerificationType(Type.VerificationType.SELF);
//    ParameterValue parameterValue = new ParameterValue();
//    parameterValue.setId(1001L);
//    parameterValue.setParameter(parameter);
//    ParameterVerification parameterVerification = new ParameterVerification();
//    parameterVerification.setVerificationStatus(State.ParameterVerification.PENDING);
//    when(parameterValueRepository.findByJobIdAndParameterId(anyLong(), anyLong())).thenReturn(parameterValue);
//    when(parameterVerificationRepository.findByJobIdAndParameterValueIdAndVerificationType(anyLong(), anyLong(), anyString())).thenReturn(parameterVerification);
//    Job job = new Job();
//    when(jobRepository.getReferenceById(anyLong())).thenReturn(job);
//    when(parameterVerificationRepository.save(any(ParameterVerification.class))).thenReturn(parameterVerification);
//    when(parameterVerificationMapper.toDto(parameterVerification)).thenReturn(new ParameterVerificationDto());
//
//    // Call the method
//    ParameterVerificationDto result = parameterVerificationService.acceptSelfVerification(1L, 2L);
//
//    // Perform assertions or verifications
//    verify(userRepository).findById(principalUser.getId());
//    verify(parameterValueRepository).findByJobIdAndParameterId(1L, 2L);
//    verify(parameterVerificationHandler).canCompleteSelfVerification(principalUserEntity, 2L, parameterVerification);
//    verify(jobRepository).getReferenceById(1L);
//    verify(parameterVerificationRepository).save(any(ParameterVerification.class));
//    verify(parameterValueRepository).save(any(ParameterValue.class));
//    verify(jobAuditService).completeSelfVerification(eq(1L), any(ParameterVerification.class), eq(principalUser));
//    verify(parameterVerificationMapper).toDto(any(ParameterVerification.class));
//  }
//
//  @Test
//  void sendForPeerVerification() throws StreemException, ResourceNotFoundException {
//    Authentication authentication = mock(Authentication.class);
//    SecurityContext securityContext = mock(SecurityContext.class);
//    when(securityContext.getAuthentication()).thenReturn(authentication);
//    SecurityContextHolder.setContext(securityContext);
//    when(authentication.getPrincipal()).thenReturn(principalUser);
//
//    when(userRepository.findById(principalUser.getId())).thenReturn(Optional.of(principalUserEntity));
//    Parameter parameter = new Parameter();
//    parameter.setVerificationType(Type.VerificationType.SELF);
//    ParameterValue parameterValue = new ParameterValue();
//    parameterValue.setId(1001L);
//    parameterValue.setParameter(parameter);
//    ParameterVerification parameterVerification = new ParameterVerification();
//    when(parameterValueRepository.findByJobIdAndParameterId(anyLong(), anyLong())).thenReturn(parameterValue);
//    //this will fetch last peer record and will find null
//    when(parameterVerificationRepository.findByJobIdAndParameterValueIdAndVerificationType(anyLong(), anyLong(), anyString())).thenReturn(null);
//    Job job = new Job();
//    when(jobRepository.getReferenceById(anyLong())).thenReturn(job);
//    when(parameterVerificationRepository.save(any(ParameterVerification.class))).thenReturn(parameterVerification);
//    when(parameterVerificationMapper.toDto(parameterVerification)).thenReturn(new ParameterVerificationDto());
//
//    PeerAssignRequest request = new PeerAssignRequest();
//    request.setUserId(3L);
//
//    // Call the method
//    ParameterVerificationDto result = parameterVerificationService.sendForPeerVerification(1L, 2L, request);
//
//    // Perform assertions or verifications
//    verify(userRepository).findById(principalUser.getId());
//    verify(parameterValueRepository).findByJobIdAndParameterId(1L, 2L);
//    verify(jobRepository).getReferenceById(1L);
//    verify(parameterVerificationRepository).save(any(ParameterVerification.class));
//    verify(jobAuditService).sendForPeerVerification(eq(1L), any(ParameterVerification.class), eq(principalUser));
//    verify(parameterVerificationMapper).toDto(any(ParameterVerification.class));
//  }
//
//  @Test
//  void recallPeerVerification() throws StreemException, ResourceNotFoundException {
//    Authentication authentication = mock(Authentication.class);
//    SecurityContext securityContext = mock(SecurityContext.class);
//    when(securityContext.getAuthentication()).thenReturn(authentication);
//    SecurityContextHolder.setContext(securityContext);
//    when(authentication.getPrincipal()).thenReturn(principalUser);
//
//    when(userRepository.findById(principalUser.getId())).thenReturn(Optional.of(principalUserEntity));
//    Parameter parameter = new Parameter();
//    parameter.setVerificationType(Type.VerificationType.PEER);
//    ParameterValue parameterValue = new ParameterValue();
//    parameterValue.setId(1001L);
//    parameterValue.setParameter(parameter);
//    ParameterVerification parameterVerification = new ParameterVerification();
//    parameterVerification.setVerificationStatus(State.ParameterVerification.PENDING);
//    when(parameterValueRepository.findByJobIdAndParameterId(anyLong(), anyLong())).thenReturn(parameterValue);
//    when(parameterVerificationRepository.findByJobIdAndParameterValueIdAndVerificationType(anyLong(), anyLong(), anyString())).thenReturn(parameterVerification);
//    Job job = new Job();
//    when(jobRepository.getReferenceById(anyLong())).thenReturn(job);
//    when(parameterVerificationRepository.save(any(ParameterVerification.class))).thenReturn(parameterVerification);
//    when(parameterVerificationMapper.toDto(parameterVerification)).thenReturn(new ParameterVerificationDto());
//
//    // Call the method
//    ParameterVerificationDto result = parameterVerificationService.recallPeerVerification(1L, 2L);
//
//    // Perform assertions or verifications
//    verify(userRepository).findById(principalUser.getId());
//    verify(parameterValueRepository).findByJobIdAndParameterId(1L, 2L);
//    verify(jobRepository).getReferenceById(1L);
//    verify(parameterVerificationRepository).save(any(ParameterVerification.class));
//    verify(parameterValueRepository).save(any(ParameterValue.class));
//    verify(jobAuditService).recallVerification(eq(1L), any(ParameterVerification.class), eq(principalUser));
//    verify(parameterVerificationMapper).toDto(any(ParameterVerification.class));
//  }
//
//  @Test
//  void recallSelfVerification() throws StreemException, ResourceNotFoundException {
//    Authentication authentication = mock(Authentication.class);
//    SecurityContext securityContext = mock(SecurityContext.class);
//    when(securityContext.getAuthentication()).thenReturn(authentication);
//    SecurityContextHolder.setContext(securityContext);
//    when(authentication.getPrincipal()).thenReturn(principalUser);
//
//    when(userRepository.findById(principalUser.getId())).thenReturn(Optional.of(principalUserEntity));
//    Parameter parameter = new Parameter();
//    parameter.setVerificationType(Type.VerificationType.SELF);
//    ParameterValue parameterValue = new ParameterValue();
//    parameterValue.setId(1001L);
//    parameterValue.setParameter(parameter);
//    ParameterVerification parameterVerification = new ParameterVerification();
//    parameterVerification.setVerificationStatus(State.ParameterVerification.PENDING);
//    when(parameterValueRepository.findByJobIdAndParameterId(anyLong(), anyLong())).thenReturn(parameterValue);
//    when(parameterVerificationRepository.findByJobIdAndParameterValueIdAndVerificationType(anyLong(), anyLong(), anyString())).thenReturn(parameterVerification);
//    Job job = new Job();
//    when(jobRepository.getReferenceById(anyLong())).thenReturn(job);
//    when(parameterVerificationRepository.save(any(ParameterVerification.class))).thenReturn(parameterVerification);
//    when(parameterVerificationMapper.toDto(parameterVerification)).thenReturn(new ParameterVerificationDto());
//
//    // Call the method
//    ParameterVerificationDto result = parameterVerificationService.recallPeerVerification(1L, 2L);
//
//    // Perform assertions or verifications
//    verify(userRepository).findById(principalUser.getId());
//    verify(parameterValueRepository).findByJobIdAndParameterId(1L, 2L);
//    verify(jobRepository).getReferenceById(1L);
//    verify(parameterVerificationRepository).save(any(ParameterVerification.class));
//    verify(parameterValueRepository).save(any(ParameterValue.class));
//    verify(jobAuditService).recallVerification(eq(1L), any(ParameterVerification.class), eq(principalUser));
//    verify(parameterVerificationMapper).toDto(any(ParameterVerification.class));
//  }
//
//  @Test
//  void acceptPeerVerification() throws StreemException, ResourceNotFoundException {
//    Authentication authentication = mock(Authentication.class);
//    SecurityContext securityContext = mock(SecurityContext.class);
//    when(securityContext.getAuthentication()).thenReturn(authentication);
//    SecurityContextHolder.setContext(securityContext);
//    when(authentication.getPrincipal()).thenReturn(principalUser);
//
//    when(userRepository.findById(principalUser.getId())).thenReturn(Optional.of(principalUserEntity));
//    Parameter parameter = new Parameter();
//    parameter.setVerificationType(Type.VerificationType.PEER);
//    ParameterValue parameterValue = new ParameterValue();
//    parameterValue.setId(1001L);
//    parameterValue.setParameter(parameter);
//    ParameterVerification parameterVerification = new ParameterVerification();
//    parameterVerification.setVerificationStatus(State.ParameterVerification.PENDING);
//    parameterVerification.setUser(principalUserEntity);
//    when(parameterValueRepository.findByJobIdAndParameterId(anyLong(), anyLong())).thenReturn(parameterValue);
//    when(parameterVerificationRepository.findByJobIdAndParameterValueIdAndVerificationType(anyLong(), anyLong(), anyString())).thenReturn(parameterVerification);
//    Job job = new Job();
//    when(jobRepository.getReferenceById(anyLong())).thenReturn(job);
//    when(parameterVerificationRepository.save(any(ParameterVerification.class))).thenReturn(parameterVerification);
//    when(parameterVerificationMapper.toDto(parameterVerification)).thenReturn(new ParameterVerificationDto());
//
//    // Call the method
//    ParameterVerificationDto result = parameterVerificationService.acceptPeerVerification(1L, 2L);
//
//    // Perform assertions or verifications
//    verify(userRepository).findById(principalUser.getId());
//    verify(parameterValueRepository).findByJobIdAndParameterId(1L, 2L);
//    verify(jobRepository).getReferenceById(1L);
//    verify(parameterVerificationRepository).save(any(ParameterVerification.class));
//    verify(parameterValueRepository).save(any(ParameterValue.class));
//    verify(jobAuditService).acceptPeerVerification(eq(1L), any(ParameterVerification.class), eq(principalUser));
//    verify(parameterVerificationMapper).toDto(any(ParameterVerification.class));
//  }
//
//  @Test
//  void rejectPeerVerification() throws StreemException, ResourceNotFoundException {
//    Authentication authentication = mock(Authentication.class);
//    SecurityContext securityContext = mock(SecurityContext.class);
//    when(securityContext.getAuthentication()).thenReturn(authentication);
//    SecurityContextHolder.setContext(securityContext);
//    when(authentication.getPrincipal()).thenReturn(principalUser);
//
//    when(userRepository.findById(principalUser.getId())).thenReturn(Optional.of(principalUserEntity));
//    Parameter parameter = new Parameter();
//    parameter.setVerificationType(Type.VerificationType.PEER);
//    ParameterValue parameterValue = new ParameterValue();
//    parameterValue.setId(1001L);
//    parameterValue.setParameter(parameter);
//    ParameterVerification parameterVerification = new ParameterVerification();
//    parameterVerification.setVerificationStatus(State.ParameterVerification.PENDING);
//    parameterVerification.setUser(principalUserEntity);
//    when(parameterValueRepository.findByJobIdAndParameterId(anyLong(), anyLong())).thenReturn(parameterValue);
//    when(parameterVerificationRepository.findByJobIdAndParameterValueIdAndVerificationType(anyLong(), anyLong(), anyString())).thenReturn(parameterVerification);
//    Job job = new Job();
//    when(jobRepository.getReferenceById(anyLong())).thenReturn(job);
//    when(parameterVerificationRepository.save(any(ParameterVerification.class))).thenReturn(parameterVerification);
//    when(parameterVerificationMapper.toDto(parameterVerification)).thenReturn(new ParameterVerificationDto());
//
//    // Call the method
//    ParameterVerificationRequest request = new ParameterVerificationRequest();
//    request.setComments("test");
//    ParameterVerificationDto result = parameterVerificationService.rejectPeerVerification(1L, 2L, request);
//
//    // Perform assertions or verifications
//    verify(userRepository).findById(principalUser.getId());
//    verify(parameterValueRepository).findByJobIdAndParameterId(1L, 2L);
//    verify(jobRepository).getReferenceById(1L);
//    verify(parameterVerificationRepository).save(any(ParameterVerification.class));
//    verify(parameterValueRepository).save(any(ParameterValue.class));
//    verify(jobAuditService).rejectPeerVerification(eq(1L), any(ParameterVerification.class), eq(principalUser));
//    verify(parameterVerificationMapper).toDto(any(ParameterVerification.class));
//  }
//
//}
