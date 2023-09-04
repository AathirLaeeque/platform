package com.leucine.streem.service.impl;

import com.leucine.streem.config.AppUrl;
import com.leucine.streem.constant.Email;
import com.leucine.streem.constant.Misc;
import com.leucine.streem.dto.PartialUserDto;
import com.leucine.streem.email.dto.EmailRequest;
import com.leucine.streem.model.Organisation;
import com.leucine.streem.model.User;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.repository.IOrganisationRepository;
import com.leucine.streem.repository.IUserRepository;
import com.leucine.streem.service.IEmailService;
import com.leucine.streem.service.INotificationService;
import com.leucine.streem.service.IUserService;
import com.leucine.streem.util.JsonUtils;
import com.leucine.streem.util.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {
  private final AppUrl appUrl;
  private final IEmailService emailService;
  private final IOrganisationRepository organisationRepository;
  private final IUserRepository userRepository;
  private final IUserService userService;

  @Override
  @Async
  @Transactional
  public void notifyAssignedUsers(Set<Long> assignIds, Long jobId, Long organisationId) {
    if (!Utility.isEmpty(assignIds)) {
      Organisation organisation = organisationRepository.getReferenceById(organisationId);
      String fqdn = organisation.getFqdn();
      if (!"/".equals(fqdn.substring(fqdn.length() - 1))) {
        fqdn = fqdn + "/";
      }
      List<User> users = userRepository.findAllById(assignIds);

      Set<String> toEmailIds = users.stream().map(User::getEmail).collect(Collectors.toSet());
      Map<String, String> attributes = new HashMap<>();
      attributes.put(Email.ATTRIBUTE_LOGIN_URL, fqdn + appUrl.getLoginPath());
      attributes.put(Email.ATTRIBUTE_JOB, fqdn + appUrl.getJobPath(jobId));

      EmailRequest emailRequest = EmailRequest.builder()
        .to(toEmailIds)
        .templateName(Email.TEMPLATE_USER_ASSIGNED_TO_JOB)
        .subject(Email.SUBJECT_USER_ASSIGNED_TO_JOB)
        .attributes(attributes)
        .build();
      emailService.sendEmail(emailRequest);
    }
  }

  @Override
  @Async
  @Transactional
  public void notifyUnassignedUsers(Set<Long> unassignIds, Long organisationId) {
    if (!Utility.isEmpty(unassignIds)) {
      Organisation organisation = organisationRepository.getOne(organisationId);
      String fqdn = organisation.getFqdn();
      if (!"/".equals(fqdn.substring(fqdn.length() - 1))) {
        fqdn = fqdn + "/";
      }
      List<User> users = userRepository.findAllById(unassignIds);

      Set<String> toEmailIds = users.stream().map(User::getEmail).collect(Collectors.toSet());
      Map<String, String> attributes = new HashMap<>();
      attributes.put(Email.ATTRIBUTE_LOGIN_URL, fqdn + appUrl.getLoginPath());

      EmailRequest emailRequest = EmailRequest.builder()
        .to(toEmailIds)
        .templateName(Email.TEMPLATE_USER_UNASSIGNED_FROM_JOB)
        .subject(Email.SUBJECT_USER_UNASSIGNED_FROM_JOB)
        .attributes(attributes).build();
      emailService.sendEmail(emailRequest);
    }
  }

  @Override
  @Async
  @Transactional
  public void notifyAllShouldBeParameterReviewersForApproval(Long jobId, Long organisationId) throws IOException {
    Organisation organisation = organisationRepository.getOne(organisationId);
    String fqdn = organisation.getFqdn();
    if (!"/".equals(fqdn.substring(fqdn.length() - 1))) {
      fqdn = fqdn + "/";
    }
    Object users = userService.getAllByRoles(Misc.SHOULD_BE_PARAMETER_REVIEWER).getData();
    List<PartialUserDto> reviewers = JsonUtils.jsonToCollectionType(users, List.class, PartialUserDto.class);
    Set<String> emailIds = reviewers.stream().map(PartialUserDto::getEmail).collect(Collectors.toSet());
    Map<String, String> attributes = new HashMap<>();
    attributes.put(Email.ATTRIBUTE_LOGIN_URL, fqdn + appUrl.getLoginPath());
    attributes.put(Email.ATTRIBUTE_JOB, fqdn + appUrl.getJobPath(jobId));
    EmailRequest emailRequest = EmailRequest.builder().to(emailIds)
      .templateName(Email.TEMPLATE_PARAMETER_APPROVAL_REQUEST).subject(Email.SUBJECT_PARAMETER_APPROVAL_REQUEST).attributes(attributes).build();
    emailService.sendEmail(emailRequest);
  }

  @Override
  @Async
  @Transactional
  public void notifyChecklistCollaborators(Set<Long> userIds, String template, String subject, Long checklistId, Long organisationId) {
    if (!Utility.isEmpty(userIds)) {
      Organisation organisation = organisationRepository.getOne(organisationId);
      String fqdn = organisation.getFqdn();
      if (!"/".equals(fqdn.substring(fqdn.length() - 1))) {
        fqdn = fqdn + "/";
      }
      List<User> users = userRepository.findAllById(userIds);
      Set<String> emailIds = users.stream().map(User::getEmail).collect(Collectors.toSet());
      Map<String, String> attributes = new HashMap<>();
      attributes.put(Email.ATTRIBUTE_LOGIN_URL, fqdn + appUrl.getLoginPath());
      attributes.put(Email.ATTRIBUTE_CHECKLIST, fqdn + appUrl.getChecklistPath(checklistId));
      EmailRequest emailRequest = EmailRequest.builder().to(emailIds)
        .templateName(template).subject(subject).attributes(attributes).build();
      emailService.sendEmail(emailRequest);
    }
  }

  @Override
  @Async
  @Transactional
  public void notifyAuthors(Set<Long> ids, Long checklistId, Long organisationId) {
    if (!Utility.isEmpty(ids)) {
      PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Organisation organisation = organisationRepository.getOne(principalUser.getOrganisationId());
      String fqdn = organisation.getFqdn();
      if (!"/".equals(fqdn.substring(fqdn.length() - 1))) {
        fqdn = fqdn + "/";
      }
      List<User> users = userRepository.findAllById(ids);

      Set<String> emailIds = users.stream().map(User::getEmail).collect(Collectors.toSet());
      Map<String, String> attributes = new HashMap<>();
      attributes.put(Email.ATTRIBUTE_LOGIN_URL, fqdn + appUrl.getLoginPath());
      attributes.put(Email.ATTRIBUTE_CHECKLIST, fqdn + appUrl.getChecklistPath(checklistId));

      EmailRequest emailRequest = EmailRequest.builder().to(emailIds)
        .templateName(Email.TEMPLATE_AUTHOR_CHECKLIST_CONFIGURATION).subject(Email.SUBJECT_AUTHOR_CHECKLIST_CONFIGURATION).attributes(attributes).build();
      emailService.sendEmail(emailRequest);
    }
  }
}
