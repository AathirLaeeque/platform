package com.leucine.streem.migration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leucine.streem.constant.State;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.model.Checklist;
import com.leucine.streem.model.User;
import com.leucine.streem.repository.IChecklistRepository;
import com.leucine.streem.repository.IUserRepository;
import com.leucine.streem.service.IChecklistCollaboratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutoInitializedParameterMigration202308121736 {
  private final IChecklistRepository checklistRepository;
  private final IChecklistCollaboratorService checklistCollaboratorService;
  private final IUserRepository userRepository;

  public BasicDto execute() throws JsonProcessingException {
    Set<Long> processIds = checklistRepository.findByStateNot(State.Checklist.BEING_BUILT);
    User user = userRepository.findById(User.SYSTEM_USER_ID).get();
    for (Long processId : processIds) {
      Checklist checklist = checklistRepository.readById(processId).get();

      checklistCollaboratorService.updateAutoInitializedParametersEntity(checklist, user);
    }
    return new BasicDto(null, "Success", null);
  }
}
