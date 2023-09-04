package com.leucine.streem.service.impl;

import com.leucine.streem.constant.Action;
import com.leucine.streem.constant.Operator;
import com.leucine.streem.dto.ChecklistAuditDto;
import com.leucine.streem.dto.mapper.IChecklistAuditMapper;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.ChecklistAudit;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.model.helper.SpecificationBuilder;
import com.leucine.streem.model.helper.search.SearchCriteria;
import com.leucine.streem.repository.IChecklistAuditRepository;
import com.leucine.streem.service.IChecklistAuditService;
import com.leucine.streem.util.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChecklistAuditService implements IChecklistAuditService {
  public static final String CREATE_CHECKLIST = "{0} {1} (ID:{2}) created the Prototype (ID:{3})";
  public static final String PUBLISH_CHECKLIST = "{0} {1} (ID:{2}) published the Checklist (ID:{3})";
  public static final String ARCHIVE_CHECKLIST = "{0} {1} (ID:{2}) archived Checklist (ID:{3}) stating reason \"{4}\"";
  public static final String UNARCHIVE_CHECKLIST = "{0} {1} (ID:{2}) unarchived Checklist (ID:{3}) stating reason \"{4}\"";
  public static final String REVISE_CHECKLIST = "{0} {1} (ID:{2}) created a new Prototype (ID:{3}) as a revision to this checklist";
  public static final String DEPRECATE_CHECKLIST = "{0} {1} (ID:{2}) deprecated this Checklist (ID:{3}) by publishing the Checklist (ID:{4}) as a revision to this Checklist";
  private final IChecklistAuditRepository checklistAuditRepository;
  private final IChecklistAuditMapper checklistAuditMapper;

  @Override
  public Page<ChecklistAuditDto> getAuditsByChecklistId(Long checklistId, String filters, Pageable pageable) throws StreemException {
    SearchCriteria mandatorySearchCriteria = new SearchCriteria()
      .setField("checklistId")
      .setOp(Operator.Search.EQ.toString())
      .setValues(Collections.singletonList(checklistId));
    Specification<ChecklistAudit> specification = SpecificationBuilder.createSpecification(filters, Collections.singletonList(mandatorySearchCriteria));

    Page<ChecklistAudit> checklistAudits = checklistAuditRepository.findAll(specification, pageable);
    List<ChecklistAuditDto> checklistAuditDtos = checklistAuditMapper.toDto(checklistAudits.getContent());
    return new PageImpl<>(checklistAuditDtos, pageable, checklistAudits.getTotalElements());
  }

  @Async
  @Override
  public void create(Long checklistId, String code, PrincipalUser principalUser) {
    String details = formatMessage(CREATE_CHECKLIST, principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(), code);
    checklistAuditRepository.save(getChecklistAudit(details, checklistId, Action.ChecklistAudit.CREATE, principalUser));
    System.out.println("details = " + details);
  }

  @Async
  @Override
  public void publish(Long checklistId, String code, PrincipalUser principalUser) {
    String details = formatMessage(PUBLISH_CHECKLIST, principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(), code);
    checklistAuditRepository.save(getChecklistAudit(details, checklistId, Action.ChecklistAudit.PUBLISH, principalUser));
  }

  @Async
  @Override
  public void archive(Long checklistId, String code, String reason, PrincipalUser principalUser) {
    String details = formatMessage(ARCHIVE_CHECKLIST, principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(), code, reason);
    checklistAuditRepository.save(getChecklistAudit(details, checklistId, Action.ChecklistAudit.ARCHIVE, principalUser));
  }

  @Async
  @Override
  public void unarchive(Long checklistId, String code, String reason, PrincipalUser principalUser) {
    String details = formatMessage(UNARCHIVE_CHECKLIST, principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(), code, reason);
    checklistAuditRepository.save(getChecklistAudit(details, checklistId, Action.ChecklistAudit.UNARCHIVE, principalUser));
  }

  @Async
  @Override
  public void revise(Long checklistId, String code, PrincipalUser principalUser) {
    String details = formatMessage(REVISE_CHECKLIST, principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(), code);
    checklistAuditRepository.save(getChecklistAudit(details, checklistId, Action.ChecklistAudit.REVISE, principalUser));
  }

  @Async
  @Override
  public void deprecate(Long checklistId, String code, String parentChecklistcode, PrincipalUser principalUser) {
    String details = formatMessage(DEPRECATE_CHECKLIST, principalUser.getFirstName(), principalUser.getLastName(), principalUser.getEmployeeId(), code, parentChecklistcode);
    checklistAuditRepository.save(getChecklistAudit(details, checklistId, Action.ChecklistAudit.DEPRECATE, principalUser));
  }

  //TODO facility id probably needs to be the selected facility or
  // do we need facility id to be saved ?
  private ChecklistAudit getChecklistAudit(String details, Long checklistId, Action.ChecklistAudit action, PrincipalUser principalUser) {
    return new ChecklistAudit()
      .setChecklistId(checklistId)
      .setDetails(details)
      .setTriggeredAt(DateTimeUtils.now())
      .setTriggeredAt(DateTimeUtils.now())
      .setTriggeredBy(principalUser.getId())
      .setAction(action)
      .setOrganisationsId(principalUser.getOrganisationId());
  }

  private String formatMessage(String pattern, String... replacements) {
    for (int i = 0; i < replacements.length; i++) {
      pattern = pattern.replace("{" + i + "}", replacements[i]);
    }
    return pattern;
  }

}
