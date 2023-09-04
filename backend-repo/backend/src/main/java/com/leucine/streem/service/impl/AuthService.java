package com.leucine.streem.service.impl;

import com.leucine.streem.config.JaasServiceProperty;
import com.leucine.streem.dto.UserDto;
import com.leucine.streem.dto.mapper.IUserMapper;
import com.leucine.streem.dto.request.*;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.service.IAuthService;
import com.leucine.streem.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
  private final IUserService userService;
  private final RestTemplate jaasRestTemplate;
  private final JaasServiceProperty jaasServiceProperty;
  private final IUserMapper userMapper;

  @Override
  public Response<Object> authenticate(AuthenticationRequest authenticationRequest) {
    log.info("[authenticate] Request to authenticate user, username: {}", authenticationRequest.getUsername());
    ResponseEntity<Response> response = jaasRestTemplate.postForEntity(jaasServiceProperty.getLoginUrl(), authenticationRequest, Response.class);
    return response.getBody();
  }

  @Override
  public Response<Object> relogin(ReloginRequest reloginRequest) {
    log.info("[authenticate] Request to relogin user, username: {}", reloginRequest.getUsername());
    ResponseEntity<Response> response = jaasRestTemplate.postForEntity(jaasServiceProperty.getReloginUrl(), reloginRequest, Response.class);
    return response.getBody();
  }

  @Override
  public Response<Object> logout() {
    log.info("[logout] Request to logout");
    ResponseEntity<Response> response = jaasRestTemplate.postForEntity(jaasServiceProperty.getLogoutUrl(), "", Response.class);
    return response.getBody();
  }

  @Override
  public Response<Object> refreshToken(RefreshTokenRequest refreshTokenRequest) {
    log.info("[refreshToken] Request to refresh and generate new token");
    HttpEntity<RefreshTokenRequest> entity = new HttpEntity<>(refreshTokenRequest);
    ResponseEntity<Response> response = jaasRestTemplate.exchange(jaasServiceProperty.getRefreshTokenUrl(), HttpMethod.POST, entity, Response.class);
    return response.getBody();
  }

  @Override
  public Response<Object> register(UserRegistrationRequest userRegistrationRequest) {
    log.info("[register] Request to register, username: {}", userRegistrationRequest.getUsername());
    HttpEntity<UserRegistrationRequest> entity = new HttpEntity<>(userRegistrationRequest);
    ResponseEntity<Response> response = jaasRestTemplate.exchange(jaasServiceProperty.getRegisterUrl(), HttpMethod.PATCH, entity, Response.class);
    return response.getBody();
  }

  @Override
  public Response<Object> validateCredentials(ValidateCredentialsRequest validateCredentialsRequest) {
    log.info("[validateCredentials] Request to validate credentials");
    HttpEntity<ValidateCredentialsRequest> entity = new HttpEntity<>(validateCredentialsRequest);
    ResponseEntity<Response> response = jaasRestTemplate.exchange(jaasServiceProperty.getValidateCredentialsUrl(), HttpMethod.PATCH, entity, Response.class);
    return response.getBody();
  }

  @Override
  public Response<Object> additionalVerification(AdditionalVerificationRequest additionalVerificationRequest) {
    log.info("[additionalVerification] Request for additional verification");
    HttpEntity<AdditionalVerificationRequest> entity = new HttpEntity<>(additionalVerificationRequest);
    ResponseEntity<Response> response = jaasRestTemplate.exchange(jaasServiceProperty.getAdditionalVerificationUrl(), HttpMethod.PATCH, entity, Response.class);
    return response.getBody();
  }

  @Override
  public Response<Object> validateToken(ValidateTokenRequest validateTokenRequest) {
    log.info("[validateToken] Request to validate token");
    HttpEntity<ValidateTokenRequest> entity = new HttpEntity<>(validateTokenRequest);
    ResponseEntity<Response> response = jaasRestTemplate.exchange(jaasServiceProperty.getValidateTokenUrl(), HttpMethod.PATCH, entity, Response.class);
    return response.getBody();
  }

  @Override
  public Response<Object> updatedPassword(PasswordUpdateRequest passwordUpdateRequest) {
    log.info("[updatedPassword] Request to update password");
    ResponseEntity<Response> response = jaasRestTemplate.exchange(jaasServiceProperty.getUpdatePasswordUrl(), HttpMethod.PATCH, new HttpEntity<>(passwordUpdateRequest, new HttpHeaders()), Response.class);
    return response.getBody();
  }

  @Override
  public Response<Object> validateChallengeQuestionsAnswer(ChallengeQuestionsAnswerRequest challengeQuestionsAnswerRequest) {
    log.info("[validateChallengeQuestionsAnswer] Request to validate challenge question challengeQuestionsAnswerRequest {}", challengeQuestionsAnswerRequest);
    HttpEntity<ChallengeQuestionsAnswerRequest> entity = new HttpEntity<>(challengeQuestionsAnswerRequest);
    ResponseEntity<Response> response = jaasRestTemplate.exchange(jaasServiceProperty.getValidateChallengeQuestionUrl(), HttpMethod.PATCH, entity, Response.class);
    return response.getBody();
  }

  @Override
  public Response<Object> notifyAdmin(NotifyAdminRequest notifyAdminRequest) {
    log.info("[notifyAdmin] Request to notify admin");
    HttpEntity<NotifyAdminRequest> entity = new HttpEntity<>(notifyAdminRequest);
    ResponseEntity<Response> response = jaasRestTemplate.exchange(jaasServiceProperty.getAdminNotifyUrl(), HttpMethod.PATCH, entity, Response.class);
    return response.getBody();
  }

  @Override
  public Response<Object> resetToken(TokenRequest tokenRequest) {
    log.info("[resetToken] Request to reset token");
    HttpEntity<TokenRequest> entity = new HttpEntity<>(tokenRequest);
    ResponseEntity<Response> response = jaasRestTemplate.exchange(jaasServiceProperty.getResetTokenUrl(), HttpMethod.PATCH, entity, Response.class);
    return response.getBody();
  }

  @Override
  public Response<Object> verifySSO(SSOVerificationRequest ssoVerificationRequest) {
    log.info("[ssoVerify] Request to verify SSO configuration");
    ResponseEntity<Response> response = jaasRestTemplate.postForEntity(jaasServiceProperty.getSSOVerificationUrl(), ssoVerificationRequest, Response.class);
    return response.getBody();
  }

  @Override
  public Response<Object> getExtras(String fqdn) {
    log.info("[accountLookup] Request to get authExtras for fqdn {}", fqdn);
    HttpEntity<String> entity = new HttpEntity<>(null);
    ResponseEntity<Response> response = jaasRestTemplate.exchange(jaasServiceProperty.getExtras(fqdn), HttpMethod.GET, entity, Response.class);
    return response.getBody();
  }

  @Override
  public Response<Object> accountLookup(String username) {
    log.info("[accountLookup] Request to look for account {}", username);
    HttpEntity<String> entity = new HttpEntity<>(null);
    ResponseEntity<Response> response = jaasRestTemplate.exchange(jaasServiceProperty.getAccountLookupUrl(username), HttpMethod.GET, entity, Response.class);
    return response.getBody();
  }

  @Override
  public Response<Object> validateIdentity(ValidateIdentityRequest validateIdentityRequest) {
    log.info("[validateIdentity] Request to validate identity, validateIdentityRequest {}", validateIdentityRequest);
    HttpEntity<ValidateIdentityRequest> entity = new HttpEntity<>(validateIdentityRequest);
    ResponseEntity<Response> response = jaasRestTemplate.exchange(jaasServiceProperty.getValidateIdentityUrl(), HttpMethod.PATCH, entity, Response.class);
    return response.getBody();
  }
}
