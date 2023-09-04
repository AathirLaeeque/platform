package com.leucine.streem.controller;

import com.leucine.streem.dto.ChallengeQuestionsAnswerUpdateRequest;
import com.leucine.streem.dto.PartialUserDto;
import com.leucine.streem.dto.UserBasicInformationUpdateRequest;
import com.leucine.streem.dto.UserDto;
import com.leucine.streem.dto.request.*;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
public interface IUserController {

  @GetMapping("/all")
  @ResponseBody
  Response<Object> getAll(@RequestParam(name = "filters", defaultValue = "") String filters, Pageable pageable);

  @GetMapping
  @ResponseBody
  Response<Object> getAllUsers(@RequestParam(name = "archived", defaultValue = "false", required = false) boolean archived,
                               @RequestParam(name = "filters", defaultValue = "") String filters,
                               Pageable pageable);

  @GetMapping("/tasks")
  Response<Object> getAllUsersForTaskAssignments(@RequestParam(name = "filters", defaultValue = "") String filters, Pageable pageable);

  @GetMapping("/authors")
  Response<Object> getAllUsersForAuthorAssignments(@RequestParam(name = "filters", defaultValue = "") String filters, Pageable pageable);

  @GetMapping("/reviewers")
  Response<Object> getAllUsersForReviewersAssignments(@RequestParam(name = "filters", defaultValue = "") String filters, Pageable pageable);

  @GetMapping("/authors/global")
  Response<Object> getAllGlobalUsersForAuthorAssignments(@RequestParam(name = "filters", defaultValue = "") String filters, Pageable pageable);

  @GetMapping("/reviewers/global")
  Response<Object> getAllGlobalUsersForReviewersAssignments(@RequestParam(name = "filters", defaultValue = "") String filters, Pageable pageable);

  @GetMapping("/{userId}")
  @ResponseBody
  Response<Object> getUser(@PathVariable Long userId);

  @PostMapping("/username/check")
  Response<Object> checkUsernameAvailability(@RequestBody UsernameCheckRequest usernameCheckRequest);

  @PostMapping("/email/check")
  Response<Object> checkEmailAvailability(@RequestBody EmailCheckRequest emailCheckRequest);

  @PostMapping("/employee-id/check")
  Response<Object> checkEmployeeIdAvailability(@RequestBody EmployeeIdCheckRequest employeeIdCheckRequest);

  @PatchMapping("/{usersId}")
  @ResponseBody
  Response<UserDto> update(@PathVariable Long usersId, @RequestBody UserUpdateRequest userUpdateRequest);

  @PatchMapping("/{usersId}/basic")
  @ResponseBody
  Response<UserDto> updateBasicInformation(@PathVariable Long usersId, @RequestBody UserBasicInformationUpdateRequest userBasicInformationUpdateRequest);

  @PatchMapping("/{usersId}/password")
  Response<Object> updatePassword(@PathVariable Long usersId, @RequestBody PasswordUpdateRequest passwordUpdateRequest);

  @PostMapping
  @ResponseBody
  Response<UserDto> create(@RequestBody UserAddRequest userAddRequest);

  @PatchMapping("/{userId}/token/reset")
  @ResponseBody
  Response<UserDto> resetToken(@PathVariable Long userId);

  @PatchMapping("/{userId}/token/cancel")
  @ResponseBody
  Response<UserDto> cancelToken(@PathVariable Long userId);

  @PatchMapping("/{userId}/archive")
  Response<UserDto> archiveUser(@PathVariable Long userId) throws ResourceNotFoundException, StreemException, ResourceNotFoundException;

  @PatchMapping("/{userId}/unarchive")
  Response<UserDto> unarchiveUser(@PathVariable Long userId) throws ResourceNotFoundException;

  @PatchMapping("/{userId}/unlock")
  Response<UserDto> unlockUser(@PathVariable Long userId);

  @GetMapping("/{usersId}/challenge-questions")
  Response<Object> getChallengeQuestionsAnswer(@PathVariable Long usersId, @RequestParam String token);

  @PatchMapping("/{usersId}/challenge-questions")
  Response<Object> updateChallengeQuestionsAnswer(@PathVariable Long usersId, @RequestBody ChallengeQuestionsAnswerUpdateRequest challengeQuestionsAnswerUpdateRequest);

  @PatchMapping("/{usersId}/facilities/{facilityId}/switch")
  @ResponseBody
  Response<Object> switchFacility(@PathVariable Long usersId, @PathVariable Long facilityId);

  @GetMapping("/audits")
  @ResponseBody
  Response<Object> userAudits(@RequestParam(name = "filters", defaultValue = "") String filters, Pageable pageable);

  @GetMapping("/directory/users")
  Response<List<PartialUserDto>> searchUsers(@RequestParam(name = "query", defaultValue = "", required = false) String query, @RequestParam(name = "limit", defaultValue = "25") int limit);

}
