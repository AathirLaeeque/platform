package com.leucine.streem.service;

import com.leucine.streem.dto.ChallengeQuestionsAnswerUpdateRequest;
import com.leucine.streem.dto.PartialUserDto;
import com.leucine.streem.dto.UserBasicInformationUpdateRequest;
import com.leucine.streem.dto.UserDto;
import com.leucine.streem.dto.request.*;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.helper.PrincipalUser;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface IUserService {

  Response<Object> isEmployeeIdAvailable(EmployeeIdCheckRequest employeeIdCheckRequest);

  Response<Object> isEmailAvailable(EmailCheckRequest emailCheckRequest);

  UserDto create(UserAddRequest userAddRequest);

  Response<Object> isUsernameAvailable(UsernameCheckRequest usernameCheckRequest);

  Response<Object> getById(Long id);

  Response<Object> getAll(boolean archived, String filters, Pageable pageable);

  Response<Object> getAll(String filters, Pageable pageable);

  Response<Object> getAllByRoles(Set<String> roles);

  Response<Object> getAllByRoles(List<String> roles, String filters, Pageable pageable);

  Response<Object> getAllByRoles(List<String> roles, String filters, boolean isAssigned, Set<String> assignees, Pageable pageable);

  UserDto archive(Long id) throws ResourceNotFoundException, StreemException;

  UserDto unarchive(Long id) throws ResourceNotFoundException;

  UserDto unlock(Long id);

  UserDto update(UserUpdateRequest userUpdateRequest);

  UserDto updateBasicInformation(UserBasicInformationUpdateRequest userBasicInformationUpdateRequest);

  UserDto resetToken(Long userId);

  UserDto cancelToken(Long userId);

  Response<Object> updatePassword(PasswordUpdateRequest passwordUpdateRequest);

  Response<Object> getChallengeQuestionsAnswer(Long usersId, String token);

  Response<Object> updateChallengeQuestionsAnswer(ChallengeQuestionsAnswerUpdateRequest challengeQuestionsAnswerUpdateRequest);

  Response<Object> getAllUserAudit(String filters, Pageable pageable);

  Response<Object>  switchFacility(Long usersId, Long facilityId);

  boolean syncUser(PrincipalUser principalUser);

  Response<List<PartialUserDto>> searchDirectoryUsers(String query, int limit);
}
