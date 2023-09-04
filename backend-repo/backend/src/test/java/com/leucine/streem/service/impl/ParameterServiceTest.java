//package com.leucine.streem.service.impl;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.leucine.streem.constant.Type;
//import com.leucine.streem.dto.ParameterDto;
//import com.leucine.streem.dto.mapper.IParameterMapper;
//import com.leucine.streem.dto.mapper.IParameterMapperImpl;
//import com.leucine.streem.dto.request.ParameterUpdateRequest;
//import com.leucine.streem.exception.ResourceNotFoundException;
//import com.leucine.streem.exception.StreemException;
//import com.leucine.streem.model.*;
//import com.leucine.streem.model.helper.PrincipalUser;
//import com.leucine.streem.repository.*;
//import com.leucine.streem.service.IChecklistService;
//import com.leucine.streem.service.IParameterService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//
////TODO: add failure tests and tests for viewing flow of verification parameters
//
//class ParameterServiceTest {
//  private IParameterService parameterService;
//
//  private IParameterMapper parameterMapper;
//
//  private IParameterRepository parameterRepository;
//
//  private IChecklistService checklistService;
//
//  private IUserRepository userRepository;
//
//  private ITaskRepository taskRepository;
//
//  private IStageRepository stageRepository;
//
//  private IMediaRepository mediaRepository;
//
//  private Authentication mockAuthentication;
//
//  @BeforeEach
//  void setUp() {
//    parameterMapper = new IParameterMapperImpl();
//    parameterRepository = mock(IParameterRepository.class);
//    checklistService = mock(IChecklistService.class);
//    userRepository = mock(IUserRepository.class);
//    taskRepository = mock(ITaskRepository.class);
//    stageRepository = mock(IStageRepository.class);
//    mediaRepository = mock(IMediaRepository.class);
//    mockAuthentication = Mockito.mock(Authentication.class);
//
//    SecurityContext securityContext = mock(SecurityContext.class);
//    when(securityContext.getAuthentication()).thenReturn(mockAuthentication);
//    SecurityContextHolder.setContext(securityContext);
//
//    parameterService = new ParameterService(
//      parameterMapper,
//      parameterRepository,
//      checklistService,
//      userRepository,
//      taskRepository,
//      stageRepository,
//      mediaRepository
//    );
//  }
//
//  @Test
//  void testEnableSelfVerificationParameter() throws StreemException, ResourceNotFoundException, JsonProcessingException {
//    long parameterId = 1233333333L;
//    Checklist checklist = new Checklist();
//    Parameter parameter = new Parameter();
//    parameter.setChecklist(checklist);
//    parameter.setId(parameterId);
//    parameter.setTargetEntityType(Type.ParameterTargetEntityType.TASK);
//
//    ParameterUpdateRequest parameterUpdateRequest = new ParameterUpdateRequest();
//    parameterUpdateRequest.setVerificationType(Type.VerificationType.SELF);
//    parameterUpdateRequest.setData(null);
//    parameterUpdateRequest.setLabel("number param");
//
//    when(mockAuthentication.getPrincipal()).thenReturn(new PrincipalUser());
//    doReturn(new User()).when(userRepository).getOne(10001L);
//    doReturn(Optional.of(parameter)).when(parameterRepository).findById(parameterId);
//    doNothing().when(checklistService).validateChecklistModificationState(anyLong(), any());
//    doNothing().when(checklistService).validateIfUserIsAuthorForPrototype(anyLong(), anyLong());
//    doReturn(parameter).when(parameterRepository).save(parameter);
//
//    ParameterDto parameterDto = parameterService.updateParameter(parameterId, parameterUpdateRequest);
//    assertEquals(parameterDto.getVerificationType(), Type.VerificationType.SELF);
//  }
//
//  @Test
//  void testEnablePeerVerificationParameter() throws StreemException, ResourceNotFoundException, JsonProcessingException {
//    long parameterId = 1233333333L;
//    Checklist checklist = new Checklist();
//    Parameter parameter = new Parameter();
//    parameter.setChecklist(checklist);
//    parameter.setId(parameterId);
//    parameter.setTargetEntityType(Type.ParameterTargetEntityType.TASK);
//
//    ParameterUpdateRequest parameterUpdateRequest = new ParameterUpdateRequest();
//    parameterUpdateRequest.setVerificationType(Type.VerificationType.PEER);
//    parameterUpdateRequest.setData(null);
//    parameterUpdateRequest.setLabel("number param");
//
//    when(mockAuthentication.getPrincipal()).thenReturn(new PrincipalUser());
//    doReturn(new User()).when(userRepository).getOne(10001L);
//    doReturn(Optional.of(parameter)).when(parameterRepository).findById(parameterId);
//    doNothing().when(checklistService).validateChecklistModificationState(anyLong(), any());
//    doNothing().when(checklistService).validateIfUserIsAuthorForPrototype(anyLong(), anyLong());
//    doReturn(parameter).when(parameterRepository).save(parameter);
//
//    ParameterDto parameterDto = parameterService.updateParameter(parameterId, parameterUpdateRequest);
//    assertEquals(parameterDto.getVerificationType(), Type.VerificationType.PEER);
//  }
//}
