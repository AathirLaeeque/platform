package com.leucine.streem.controller;

import com.leucine.streem.dto.UserDto;
import com.leucine.streem.dto.request.*;
import com.leucine.streem.dto.response.Response;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
public interface IAuthController {

  @PatchMapping("/register")
  Response<Object> register(@RequestBody UserRegistrationRequest userRegistrationRequest);

  @PostMapping("/login")
  @ResponseBody
  Response<Object> authenticate(@RequestBody AuthenticationRequest authenticationRequest);

  @PostMapping("/re-login")
  @ResponseBody
  Response<Object> relogin(@RequestBody ReloginRequest reloginRequest);

  @PostMapping("/logout")
  @ResponseBody
  Response<Object> logout();

  @PatchMapping("/credentials/validate")
  Response<Object> validateCredentials(@RequestBody ValidateCredentialsRequest validateCredentialsRequest);

  @PatchMapping("/additional/verification")
  Response<Object> additionalVerificationRequest(@RequestBody AdditionalVerificationRequest additionalVerificationRequest);

  @PatchMapping("/token/validate")
  Response<Object> validateToken(@RequestBody ValidateTokenRequest validateTokenRequest);

  @PostMapping("/token/refresh")
  Response<Object> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest);

  @PatchMapping("/identity/validate")
  Response<Object> validateIdentity(@RequestBody ValidateIdentityRequest validateIdentityRequest);

  @PatchMapping("/token/reset")
  Response<Object> resetToken(@RequestBody TokenRequest tokenRequest);

  @PatchMapping("/admin/notify")
  Response<Object> notifyAdmin(@RequestBody NotifyAdminRequest notifyAdminRequest);

  @PatchMapping("/challenge-questions/validate")
  Response<Object> validateChallengeQuestionsAnswer(@RequestBody ChallengeQuestionsAnswerRequest challengeQuestionsAnswerRequest);

  @PatchMapping("/password")
  Response<Object> updatedPassword(@RequestBody PasswordUpdateRequest passwordUpdateRequest);

  @GetMapping("/account/lookup")
  Response<Object> accountLookup(@RequestParam(name = "username") String username);

  @GetMapping("/extras")
  Response<Object> getExtras(@RequestParam(name = "fqdn") String fqdn);

  @PostMapping("/sso/verify")
  Response<Object> verifySSO(@RequestBody SSOVerificationRequest ssoVerificationRequest);
}
