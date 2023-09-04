package com.leucine.streem.controller.impl;

import com.leucine.streem.constant.Misc;
import com.leucine.streem.controller.IUserController;
import com.leucine.streem.dto.ChallengeQuestionsAnswerUpdateRequest;
import com.leucine.streem.dto.PartialUserDto;
import com.leucine.streem.dto.UserBasicInformationUpdateRequest;
import com.leucine.streem.dto.UserDto;
import com.leucine.streem.dto.request.*;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserController implements IUserController {
  private IUserService userService;

  @Autowired
  public UserController(IUserService userService) {
    this.userService = userService;
  }

  @Override
  public Response<Object> getAllUsers(boolean archived, String filters, Pageable pageable) {
    return userService.getAll(archived, filters, pageable);
  }

  @Override
  public Response<Object> getAll(String filters, Pageable pageable) {
    return userService.getAll(filters, pageable);
  }

  @Override
  public Response<Object> getAllUsersForTaskAssignments(String filters, Pageable pageable) {
    return userService.getAllByRoles(Misc.TASK_ROLES, filters, pageable);
  }

  @Override
  public Response<Object> getAllUsersForAuthorAssignments(String filters, Pageable pageable) {
    return userService.getAllByRoles(Misc.AUTHOR_ROLES, filters, pageable);
  }

  @Override
  public Response<Object> getAllUsersForReviewersAssignments(String filters, Pageable pageable) {
    return userService.getAllByRoles(Misc.REVIEWERS_ROLES, filters, pageable);
  }

  @Override
  public Response<Object> getAllGlobalUsersForAuthorAssignments(String filters, Pageable pageable) {
    return userService.getAllByRoles(Misc.GLOBAL_AUTHOR_ROLES, filters, pageable);
  }

  @Override
  public Response<Object> getAllGlobalUsersForReviewersAssignments(String filters, Pageable pageable) {
    return userService.getAllByRoles(Misc.GLOBAL_REVIEWERS_ROLES, filters, pageable);
  }

  @Override
  public Response<Object> checkUsernameAvailability(UsernameCheckRequest usernameCheckRequest) {
    return userService.isUsernameAvailable(usernameCheckRequest);
  }

  @Override
  public Response<Object> checkEmailAvailability(EmailCheckRequest emailCheckRequest) {
    return userService.isEmailAvailable(emailCheckRequest);
  }

  @Override
  public Response<Object> checkEmployeeIdAvailability(EmployeeIdCheckRequest employeeIdCheckRequest) {
    return userService.isEmployeeIdAvailable(employeeIdCheckRequest);
  }

  @Override
  public Response<Object> getUser(Long userId) {
    return userService.getById(userId);
  }

  @Override
  public Response<UserDto> update(Long usersId, UserUpdateRequest userUpdateRequest) {
    userUpdateRequest.setId(usersId);
    return Response.builder().data(userService.update(userUpdateRequest)).build();
  }

  @Override
  public Response<UserDto> updateBasicInformation(Long usersId, UserBasicInformationUpdateRequest userBasicInformationUpdateRequest) {
    userBasicInformationUpdateRequest.setId(usersId);
    return Response.builder().data(userService.updateBasicInformation(userBasicInformationUpdateRequest)).build();
  }

  @Override
  public Response<Object> updatePassword(Long usersId, PasswordUpdateRequest passwordUpdateRequest) {
    passwordUpdateRequest.setUserId(usersId);
    return userService.updatePassword(passwordUpdateRequest);
  }

  @Override
  public Response<UserDto> create(UserAddRequest userAddRequest) {
    return Response.builder().data(userService.create(userAddRequest)).build();
  }

  @Override
  public Response<UserDto> resetToken(Long userId) {
    return Response.builder().data(userService.resetToken(userId)).build();
  }

  @Override
  public Response<UserDto> cancelToken(Long userId) {
    return Response.builder().data(userService.cancelToken(userId)).build();
  }

  @Override
  public Response<UserDto> archiveUser(Long userId) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(userService.archive(userId)).build();
  }

  @Override
  public Response<UserDto> unarchiveUser(Long userId) throws ResourceNotFoundException {
    return Response.builder().data(userService.unarchive(userId)).build();
  }

  @Override
  public Response<UserDto> unlockUser(Long userId) {
    return Response.builder().data(userService.unlock(userId)).build();
  }

  @Override
  public Response<Object> getChallengeQuestionsAnswer(Long usersId, String token) {
    return userService.getChallengeQuestionsAnswer(usersId, token);
  }

  @Override
  public Response<Object> updateChallengeQuestionsAnswer(Long usersId, ChallengeQuestionsAnswerUpdateRequest challengeQuestionsAnswerUpdateRequest) {
    challengeQuestionsAnswerUpdateRequest.setUserId(usersId);
    return userService.updateChallengeQuestionsAnswer(challengeQuestionsAnswerUpdateRequest);
  }

  @Override
  public Response<Object> switchFacility(Long usersId, Long facilityId) {
    return userService.switchFacility(usersId, facilityId);
  }

  @Override
  public Response<Object> userAudits(String filters, Pageable pageable) {
    return userService.getAllUserAudit(filters, pageable);
  }

  @Override
  public Response<List<PartialUserDto>> searchUsers(String query, int limit) {
    return userService.searchDirectoryUsers(query, limit);
  }
}
