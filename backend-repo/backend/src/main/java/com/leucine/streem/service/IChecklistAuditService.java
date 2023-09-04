package com.leucine.streem.service;

import com.leucine.streem.dto.ChecklistAuditDto;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.helper.PrincipalUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IChecklistAuditService {
  Page<ChecklistAuditDto> getAuditsByChecklistId(Long checklistId, String filters, Pageable pageable) throws StreemException;

  void create(Long checklistId, String code, PrincipalUser principalUser);

  void publish(Long checklistId, String code, PrincipalUser principalUser);

  void archive(Long checklistId, String code, String reason, PrincipalUser principalUser);

  void unarchive(Long checklistId, String code, String reason, PrincipalUser principalUser);

  void revise(Long checklistId, String code, PrincipalUser principalUser);

  void deprecate(Long checklistId, String code, String revisedChecklistCode, PrincipalUser principalUser);

}
