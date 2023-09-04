package com.leucine.streem.service.impl;

import com.leucine.streem.config.JaasServiceProperty;
import com.leucine.streem.dto.ChallengeQuestionsAnswerUpdateRequest;
import com.leucine.streem.dto.PartialUserDto;
import com.leucine.streem.dto.UserBasicInformationUpdateRequest;
import com.leucine.streem.dto.UserDto;
import com.leucine.streem.dto.mapper.IUserMapper;
import com.leucine.streem.dto.request.*;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ExceptionType;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.User;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.repository.*;
import com.leucine.streem.service.IUserService;
import com.leucine.streem.util.Utility;
import com.leucine.streem.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
  private final IUserRepository userRepository;
  private final IOrganisationRepository organisationRepository;
  private final IFacilityRepository facilityRepository;
  private final RestTemplate jaasRestTemplate;
  private final JaasServiceProperty jaasServiceProperty;
  private final IJobRepository jobRepository;
  private final ITaskExecutionAssigneeRepository taskExecutionAssigneeRepository;
  private final IUserMapper mapper;

  @Override
  public Response<Object> getAll(String filters, Pageable pageable) {
    log.info("[getAll] Request to get all users, filters: {}, pageable: {}", filters, pageable);
    HttpEntity<Response> response = jaasRestTemplate.exchange(
      Utility.toUriString(jaasServiceProperty.getUserAllUrl(), filters, pageable), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Response.class);
    return response.getBody();
  }

  @Override
  public Response<Object> getAll(boolean archived, String filters, Pageable pageable) {
    log.info("[getAll] Request to get all users, archived: {}, filters: {}, pageable: {}", archived, filters, pageable);
    HttpEntity<Response> response = jaasRestTemplate.exchange(
      Utility.toUriString(jaasServiceProperty.getUserUrl(), Map.of("archived", archived, "filters", filters), pageable),
      HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Response.class
    );
    return response.getBody();
  }

  @Override
  public Response<Object> getAllByRoles(Set<String> roles) {
    log.info("[getAllByRoles] Request to get all users, roles: {}", roles);
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(jaasServiceProperty.getUserByRolesUrl()).queryParam("roles", roles);
    ResponseEntity<Response> responseEntity = jaasRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Response.class);
    return responseEntity.getBody();
  }

  @Override
  public Response<Object> getAllByRoles(List<String> roles, String filters, boolean isAssigned, Set<String> assignees, Pageable pageable) {
    log.info("[getAllByRoles] Request to get all users, roles: {}, filters: {}, isAssigned: {}, assignees: {}, pageable: {}", roles, filters, isAssigned, assignees, pageable);
    HttpEntity<Response> response = jaasRestTemplate.exchange(
      Utility.toUriString(jaasServiceProperty.getUserByRolesUrl(),
        Map.of("roles", roles, "filters", filters, "isAssigned", isAssigned, "assignees", assignees), pageable),
      HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Response.class
    );
    return response.getBody();
  }

  @Override
  public Response<Object> getAllByRoles(List<String> roles, String filters, Pageable pageable) {
    log.info("[getAllByRoles] Request to get all users, roles: {}, filters: {}, pageable: {}", roles, filters, pageable);
    HttpEntity<Response> response = jaasRestTemplate.exchange(
      Utility.toUriString(jaasServiceProperty.getUserByRolesUrl(), Map.of("roles", roles, "filters", filters), pageable),
      HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Response.class
    );
    return response.getBody();
  }

  @Override
  public Response<Object> getAllUserAudit(String filters, Pageable pageable) {
    log.info("[getAllUserAudit] Request to get all user audits, filters: {}, pageable: {}", filters, pageable);
    HttpEntity<Response> response = jaasRestTemplate.exchange(
      Utility.toUriString(jaasServiceProperty.getUserAuditsUrl(), filters, pageable),
      HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Response.class
    );
    return response.getBody();
  }

  @Override
  public Response<Object> switchFacility(Long usersId, Long facilityId) {
    log.info("[switchFacility] Request to switch facility, usersId {}, facilityId {}", usersId, facilityId);
    HttpEntity<PasswordUpdateRequest> entity = new HttpEntity<>(new HttpHeaders());
    ResponseEntity<Response> responseEntity = jaasRestTemplate.exchange(jaasServiceProperty.getSwitchFacilityUrl(usersId, facilityId), HttpMethod.PATCH, entity, Response.class);
    return responseEntity.getBody();
  }

  @Override
  public Response<Object> getById(Long userId) {
    log.info("[getById] Request to get user, userId: {}", userId);
    ResponseEntity<Response> response = jaasRestTemplate.getForEntity(jaasServiceProperty.getUserUrl(userId), Response.class);
    return response.getBody();
  }

  @Override
  public UserDto create(UserAddRequest userAddRequest) {
    log.info("[create] Request to add an user, userAddRequest: {}", userAddRequest);
    ResponseEntity<PrincipalUser> responseEntity = jaasRestTemplate.postForEntity(jaasServiceProperty.getUserUrl(), userAddRequest, PrincipalUser.class);
    PrincipalUser principalUser = responseEntity.getBody();
    syncUser(principalUser);
    return mapper.toDto(principalUser);
  }

  @Override
  public Response<Object> isUsernameAvailable(UsernameCheckRequest usernameCheckRequest) {
    log.info("[isUsernameAvailable] Request to check Username availability, usernameCheckRequest: {}", usernameCheckRequest);
    ResponseEntity<Response> responseEntity = jaasRestTemplate.postForEntity(jaasServiceProperty.getCheckUsernameUrl(), usernameCheckRequest, Response.class);
    return responseEntity.getBody();
  }

  @Override
  public Response<Object> isEmailAvailable(EmailCheckRequest emailCheckRequest) {
    log.info("[isEmailAvailable] Request to check Email availability, emailCheckRequest: {}", emailCheckRequest);
    ResponseEntity<Response> responseEntity = jaasRestTemplate.postForEntity(jaasServiceProperty.getCheckEmailUrl(), emailCheckRequest, Response.class);
    return responseEntity.getBody();
  }

  @Override
  public Response<Object> isEmployeeIdAvailable(EmployeeIdCheckRequest employeeIdCheckRequest) {
    log.info("[isEmployeeIdAvailable] Request to Employee Id availability, employeeIdCheckRequest: {}", employeeIdCheckRequest);
    ResponseEntity<Response> responseEntity = jaasRestTemplate.postForEntity(jaasServiceProperty.getCheckEmployeeIdUrl(), employeeIdCheckRequest, Response.class);
    return responseEntity.getBody();
  }

  @Override
  public UserDto update(final UserUpdateRequest userUpdateRequest) {
    log.info("[update] Request to update an user, userUpdateRequest: {}", userUpdateRequest);
    HttpEntity<UserUpdateRequest> entity = new HttpEntity<>(userUpdateRequest, new HttpHeaders());
    ResponseEntity<PrincipalUser> responseEntity = jaasRestTemplate.exchange(jaasServiceProperty.getUserUrl(userUpdateRequest.getId()), HttpMethod.PATCH, entity, PrincipalUser.class);
    PrincipalUser principalUser = responseEntity.getBody();
    syncUser(principalUser);
    return mapper.toDto(principalUser);
  }

  @Override
  public UserDto updateBasicInformation(final UserBasicInformationUpdateRequest userBasicInformationUpdateRequest) {
    log.info("[updateBasicInformation] Request to update basic information of user, userBasicInformationUpdateRequest: {}", userBasicInformationUpdateRequest);
    HttpEntity<UserBasicInformationUpdateRequest> entity = new HttpEntity<>(userBasicInformationUpdateRequest, new HttpHeaders());
    ResponseEntity<PrincipalUser> responseEntity = jaasRestTemplate.exchange(jaasServiceProperty.getUpdateUserBasicInformationUrl(userBasicInformationUpdateRequest.getId()), HttpMethod.PATCH, entity, PrincipalUser.class);
    PrincipalUser principalUser = responseEntity.getBody();
    syncUser(principalUser);
    return mapper.toDto(principalUser);
  }

  @Override
  public Response<Object> updatePassword(PasswordUpdateRequest passwordUpdateRequest) {
    log.info("[updatePassword] Request to update password");
    HttpEntity<PasswordUpdateRequest> entity = new HttpEntity<>(passwordUpdateRequest, new HttpHeaders());
    ResponseEntity<Response> responseEntity = jaasRestTemplate.exchange(jaasServiceProperty.getUpdateUserPasswordUrl(passwordUpdateRequest.getUserId()), HttpMethod.PATCH, entity, Response.class);
    return responseEntity.getBody();
  }

  @Override
  public Response<Object> getChallengeQuestionsAnswer(Long usersId, String token) {
    log.info("[getChallengeQuestionsAnswer] Request to get challenge question details");
    ResponseEntity<Response> responseEntity = jaasRestTemplate.exchange(
      Utility.toUriString(jaasServiceProperty.getChallengeQuestionsAnswerUrl(usersId), Collections.singletonMap("token", token)),
      HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Response.class
    );
    return responseEntity.getBody();
  }

  @Override
  public Response<Object> updateChallengeQuestionsAnswer(ChallengeQuestionsAnswerUpdateRequest challengeQuestionsAnswerUpdateRequest) {
    log.info("[updateChallengeQuestionsAnswer] Request to update challenge question details");
    HttpEntity<ChallengeQuestionsAnswerUpdateRequest> entity = new HttpEntity<>(challengeQuestionsAnswerUpdateRequest, new HttpHeaders());
    ResponseEntity<Response> responseEntity = jaasRestTemplate.exchange(jaasServiceProperty.getChallengeQuestionsAnswerUrl(challengeQuestionsAnswerUpdateRequest.getUserId()), HttpMethod.PATCH, entity, Response.class);
    return responseEntity.getBody();
  }

  @Override
  public UserDto resetToken(Long userId) {
    log.info("[resetToken] Request to reset token, userId: {}", userId);
    ResponseEntity<PrincipalUser> responseEntity = jaasRestTemplate.exchange(jaasServiceProperty.getResetTokenUrl(userId), HttpMethod.PATCH, new HttpEntity<>(new HttpHeaders()), PrincipalUser.class);
    PrincipalUser principalUser = responseEntity.getBody();
    return mapper.toDto(principalUser);
  }

  @Override
  public UserDto cancelToken(Long userId) {
    log.info("[cancelToken] Request to cancel token, userId: {}", userId);
    ResponseEntity<PrincipalUser> responseEntity = jaasRestTemplate.exchange(jaasServiceProperty.getCancelTokenUrl(userId), HttpMethod.PATCH, new HttpEntity<>(new HttpHeaders()), PrincipalUser.class);
    PrincipalUser principalUser = responseEntity.getBody();
    return mapper.toDto(principalUser);
  }

  @Override
  public UserDto archive(Long userId) throws ResourceNotFoundException, StreemException {
    log.info("[archive] Request to archive user, userId: {}", userId);
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new ResourceNotFoundException(userId, ErrorCode.USER_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    if (taskExecutionAssigneeRepository.isUserAssignedToInProgressTasks(userId)) {
      ValidationUtils.invalidate(userId, ErrorCode.CANNOT_ARCHIVE_USER);
    }
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    ResponseEntity<PrincipalUser> responseEntity = jaasRestTemplate.exchange(jaasServiceProperty.getArchiveUserUrl(userId),
      HttpMethod.PATCH, new HttpEntity<>(new HttpHeaders()), PrincipalUser.class);
    PrincipalUser principalUser = responseEntity.getBody();
//    User user = userRepository.findById(Long.valueOf(principalUser.getId())).get();
    user.setArchived(true);
    userRepository.save(user);

    //TODO Sathyam : reuse unassign logic
    //unassign users from non completed tasks
    taskExecutionAssigneeRepository.unassignUsersFromNonStartedTasks(userId);
    //if archived user was the only user assigned to job
    //set job state to UNASSIGNED
    jobRepository.updateJobToUnassignedIfNoUserAssigned();
    return mapper.toDto(principalUser);
  }

  @Override
  public UserDto unarchive(Long userId) throws ResourceNotFoundException {
    log.info("[unarchive] Request to unarchiveUser user, userId: {}", userId);
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new ResourceNotFoundException(userId, ErrorCode.USER_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    ResponseEntity<PrincipalUser> responseEntity = jaasRestTemplate.exchange(jaasServiceProperty.getUnarchiveUserUrl(userId), HttpMethod.PATCH, new HttpEntity<>(new HttpHeaders()), PrincipalUser.class);

    PrincipalUser principalUser = responseEntity.getBody();
    user.setArchived(false);
    userRepository.save(user);
    return mapper.toDto(principalUser);
  }

  @Override
  public UserDto unlock(Long userId) {
    log.info("[unlock] Request to unlock user, userId: {}", userId);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    ResponseEntity<PrincipalUser> responseEntity = jaasRestTemplate.exchange(jaasServiceProperty.getUnlockUserUrl(userId), HttpMethod.PATCH, new HttpEntity<>(new HttpHeaders()), PrincipalUser.class);
    PrincipalUser principalUser = responseEntity.getBody();
    return mapper.toDto(principalUser);
  }

  @Override
  public Response<List<PartialUserDto>> searchDirectoryUsers(String query, int limit) {
    log.info("[getAll] Request to get all users like {}", query);
    HttpEntity<String> entity = new HttpEntity<>(new HttpHeaders());
    ResponseEntity<Response> response = jaasRestTemplate.exchange(jaasServiceProperty.getDirectoryUsersUrl(query, limit), HttpMethod.GET, entity, Response.class);
    return response.getBody();
  }

  @Override
  public boolean syncUser(PrincipalUser principalUser) {
    if (null != principalUser) {
      Optional<User> optionalUser = userRepository.findById(principalUser.getId());
      if (optionalUser.isEmpty()) {
//      List<Facility> facilities = facilityRepository.findAllById(principalUser.getFacilities().stream().map(facilityDto -> Long.valueOf(facilityDto.getId())).collect(Collectors.toSet()));
        User user = new User();
        user.setId(principalUser.getId());
        user.setEmployeeId(principalUser.getEmployeeId());
        user.setFirstName(principalUser.getFirstName());
        user.setLastName(principalUser.getLastName());
        user.setEmail(principalUser.getEmail());
        user.setOrganisation(organisationRepository.findById(principalUser.getOrganisationId()).orElse(null));
        userRepository.save(user);
      } else {
        User user = optionalUser.get();
        user.setEmployeeId(principalUser.getEmployeeId());
        user.setFirstName(principalUser.getFirstName());
        user.setLastName(principalUser.getLastName());
        user.setEmail(principalUser.getEmail());
        user.setArchived(principalUser.isArchived());
        userRepository.save(user);
      }
      return true;
    }
    return false;
  }

}
