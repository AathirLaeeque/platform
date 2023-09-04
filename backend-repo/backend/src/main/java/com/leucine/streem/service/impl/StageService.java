package com.leucine.streem.service.impl;

import com.leucine.streem.constant.State;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.StageDto;
import com.leucine.streem.dto.mapper.IStageMapper;
import com.leucine.streem.dto.request.StageReorderRequest;
import com.leucine.streem.dto.request.StageRequest;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.exception.ExceptionType;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.*;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.repository.IChecklistRepository;
import com.leucine.streem.repository.IStageRepository;
import com.leucine.streem.repository.IUserRepository;
import com.leucine.streem.service.IChecklistService;
import com.leucine.streem.service.IStageService;
import com.leucine.streem.util.DateTimeUtils;
import com.leucine.streem.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageService implements IStageService {
  private final IChecklistService checklistService;
  private final IStageMapper stageMapper;
  private final IStageRepository stageRepository;
  private final IUserRepository userRepository;
  private final IChecklistRepository checklistRepository;

  @Override
  public StageDto createStage(Long checklistId, StageRequest stageRequest) throws ResourceNotFoundException, StreemException {
    log.info("[createStage] Request to create stage, checklistId: {}, stageRequest: {}", checklistId, stageRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Checklist checklist = checklistService.findById(checklistId);
    checklistService.validateChecklistModificationState(checklistId, checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());

    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Stage stage = new Stage();
    stage.setChecklist(checklist);
    stage.setOrderTree(stageRequest.getOrderTree());
    stage.setName(stageRequest.getName());
    stage.setCreatedBy(principalUserEntity);
    stage.setModifiedBy(principalUserEntity);

    return stageMapper.toDto(stageRepository.save(stage));
  }

  @Override
  public StageDto updateStage(Long stageId, StageRequest stageRequest) throws ResourceNotFoundException, StreemException {
    log.info("[updateStage] Request to update stage, stageId: {}, stageRequest: {}", stageId, stageRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Stage stage = stageRepository.findById(stageId).orElseThrow(() -> new ResourceNotFoundException(stageId, ErrorCode.STAGE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Checklist checklist = stage.getChecklist();
    checklistService.validateChecklistModificationState(checklist.getId(), checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());

    stage.setName(stageRequest.getName());
    stage.setModifiedBy(principalUserEntity);

    return stageMapper.toDto(stageRepository.save(stage));
  }

  @Override
  public BasicDto reorderStages(StageReorderRequest stageReorderRequest) throws StreemException {
    log.info("[reorderStages] Request to reorder stages, stageReorderRequest: {}", stageReorderRequest);
    Optional<Long> optionalStageId = stageReorderRequest.getStagesOrder().keySet().stream().findAny();
    if (optionalStageId.isPresent()) {
      var checklistState = checklistRepository.findByStageId(optionalStageId.get());
      if (!State.CHECKLIST_EDIT_STATES.contains(checklistState)) {
        ValidationUtils.invalidate(optionalStageId.get(), ErrorCode.PROCESS_CANNOT_BE_MODFIFIED);
      }
    }
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    stageReorderRequest.getStagesOrder().forEach((stageId, order) -> stageRepository.reorderStage(stageId, order, principalUser.getId(), DateTimeUtils.now()));

    BasicDto basicDto = new BasicDto();
    basicDto.setMessage("success");
    return basicDto;
  }

  @Override
  public StageDto archiveStage(Long stageId) throws StreemException, ResourceNotFoundException {
    log.info("[archiveStage] Request to archive stage, stageId: {}", stageId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Stage stage = stageRepository.findById(stageId).orElseThrow(() -> new ResourceNotFoundException(stageId, ErrorCode.STAGE_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Checklist checklist = stage.getChecklist();
    checklistService.validateChecklistModificationState(checklist.getId(), checklist.getState());
    checklistService.validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());

    for (Task task : stage.getTasks()) {
      task.setArchived(true);
      for (Parameter parameter : task.getParameters()) {
        parameter.setArchived(true);
      }
    }

    stage.setArchived(true);
    stage.setModifiedBy(principalUserEntity);
    return stageMapper.toDto(stageRepository.save(stage));
  }
}
