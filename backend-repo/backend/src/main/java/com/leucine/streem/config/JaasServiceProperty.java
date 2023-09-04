package com.leucine.streem.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.text.MessageFormat;

@Setter
@Configuration
@ConfigurationProperties(prefix = "jaas")
public class JaasServiceProperty {
  private String service;
  private String root;
  private Path path;

  public void setPath(Path path) {
    if (null != path) {
      this.path = path;
    }
  }

  public String getServiceId() {
    return service;
  }

  public String getAuthorizeUrl() {
    return root + path.authorize;
  }

  public String getCheckUsernameUrl() {
    return root + path.userUsernameCheck;
  }

  public String getCheckEmailUrl() {
    return root + path.userEmailCheck;
  }

  public String getCheckEmployeeIdUrl() {
    return root + path.userEmployeeIdCheck;
  }

  public String getResetTokenUrl(Long id) {
    return MessageFormat.format((root + path.userTokenReset), String.valueOf(id));
  }

  public String getCancelTokenUrl(Long id) {
    return MessageFormat.format((root + path.userTokenCancel), String.valueOf(id));
  }

  public String getUserUrl() {
    return root + path.users;
  }

  public String getUserAllUrl() {
    return root + path.userAll;
  }

  public String getChallengeQuestionsAnswerUrl(Long id) {
    return MessageFormat.format((root + path.userChallengeQuestions), String.valueOf(id));
  }

  public String getUserByRolesUrl() {
    return root + path.userByRoles;
  }

  public String getUpdateUserBasicInformationUrl(Long id) {
    return MessageFormat.format((root + path.userUpdateBasic), String.valueOf(id));
  }

  public String getUpdateUserPasswordUrl(Long id) {
    return MessageFormat.format((root + path.userUpdatePassword), String.valueOf(id));
  }

  public String getUserUrl(Long id) {
    return MessageFormat.format((root + path.user), String.valueOf(id));
  }

  public String getArchiveUserUrl(Long id) {
    return MessageFormat.format((root + path.userArchive), String.valueOf(id));
  }

  public String getUnarchiveUserUrl(Long id) {
    return MessageFormat.format((root + path.userUnarchive), String.valueOf(id));
  }

  public String getUnlockUserUrl(Long id) {
    return MessageFormat.format((root + path.userUnlock), String.valueOf(id));
  }

  public String getUserAuditsUrl() {
    return root + path.userAudits;
  }

  public String getLoginUrl() {
    return root + path.authLogin;
  }

  public String getReloginUrl() {
    return root + path.authRelogin;
  }

  public String getLogoutUrl() {
    return root + path.authLogout;
  }

  public String getRefreshTokenUrl() {
    return root + path.authRefreshToken;
  }

  public String getRegisterUrl() {
    return root + path.authRegister;
  }

  public String getResetPasswordUrl() {
    return root + path.authPasswordReset;
  }

  public String getUpdatePasswordUrl() {
    return root + path.authUpdatePassword;
  }

  public String getFacilityUrl() {
    return root + path.facility;
  }

  public String getRolesUrl() {
    return root + path.roles;
  }

  public String getValidateCredentialsUrl() {
    return root + path.authCredentialsValidate;
  }

  public String getValidateTokenUrl() {
    return root + path.authTokenValidate;
  }

  public String getAdditionalVerificationUrl() {
    return root + path.authAdditionalVerification;
  }

  public String getValidateIdentityUrl() {
    return root + path.authIdentityValidate;
  }

  public String getAdminNotifyUrl() {
    return root + path.authAdminNotify;
  }

  public String getResetTokenUrl() {
    return root + path.authTokenReset;
  }

  public String getAccountLookupUrl(String username) {
    return MessageFormat.format((root + path.accountLookup), username);
  }

  public String getExtras(String fqdn) {
    return MessageFormat.format((root + path.authExtras), fqdn);
  }


  public String getValidateChallengeQuestionUrl() {
    return root + path.authChallengeQuestionValidate;
  }

  public String getChallengeQuestionsUrl() {
    return root + path.challengeQuestions;
  }


  public String getSwitchFacilityUrl(Long usersId, Long facilityId) {
    return MessageFormat.format((root + path.userSwitchFacility), String.valueOf(usersId), String.valueOf(facilityId));
  }

  public String getDirectoryUsersUrl(String query, int limit) {
    return MessageFormat.format((root + path.directorySearch), query, String.valueOf(limit));
  }

  public String getSSOVerificationUrl() {
    return root + path.ssoVerify;
  }


  @Setter
  static class Path {
    private String authExtras;
    private String directorySearch;
    private String authAdditionalVerification;
    private String authCredentialsValidate;
    private String authLogin;
    private String authRelogin;
    private String authLogout;
    private String authPasswordReset;
    private String authRefreshToken;
    private String authTokenReset;
    private String authIdentityValidate;
    private String authAdminNotify;
    private String authChallengeQuestionValidate;
    private String authRegister;
    private String authTokenValidate;
    private String authUpdatePassword;
    private String authorize;
    private String user;
    private String userAll;
    private String userArchive;
    private String userAudits;
    private String userByRoles;
    private String userTokenCancel;
    private String userChallengeQuestions;
    private String userEmailCheck;
    private String userEmployeeIdCheck;
    private String userTokenReset;
    private String userUnarchive;
    private String userUnlock;
    private String userUpdateBasic;
    private String userUpdatePassword;
    private String userUsernameCheck;
    private String userSwitchFacility;
    private String users;
    private String facility;
    private String roles;
    private String challengeQuestions;

    private String accountLookup;
    private String ssoVerify;
  }

}
