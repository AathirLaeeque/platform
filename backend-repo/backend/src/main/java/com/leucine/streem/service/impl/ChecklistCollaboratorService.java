package com.leucine.streem.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.streem.constant.Action;
import com.leucine.streem.constant.Email;
import com.leucine.streem.constant.State;
import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.*;
import com.leucine.streem.dto.mapper.IChecklistMapper;
import com.leucine.streem.dto.projection.ChecklistCollaboratorView;
import com.leucine.streem.dto.request.ChecklistCollaboratorAssignmentRequest;
import com.leucine.streem.dto.request.CommentAddRequest;
import com.leucine.streem.dto.request.SignOffOrderTreeRequest;
import com.leucine.streem.dto.response.Error;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.exception.ExceptionType;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.*;
import com.leucine.streem.model.helper.JobLogColumn;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.model.helper.parameter.CalculationParameter;
import com.leucine.streem.model.helper.parameter.CalculationParameterVariable;
import com.leucine.streem.repository.*;
import com.leucine.streem.service.*;
import com.leucine.streem.util.DateTimeUtils;
import com.leucine.streem.util.JsonUtils;
import com.leucine.streem.util.Utility;
import com.leucine.streem.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChecklistCollaboratorService implements IChecklistCollaboratorService {
  private final IChecklistRepository checklistsRepository;
  private final IChecklistCollaboratorCommentsRepository checklistCollaboratorCommentsRepository;
  private final IChecklistCollaboratorMappingRepository checklistCollaboratorMappingRepository;
  private final IUserRepository userRepository;
  private final IChecklistAuditService checklistAuditService;
  private final IChecklistMapper checklistMapper;
  private final IVersionService versionService;
  private final IStageRepository stageRepository;
  private final ITaskRepository taskRepository;
  private final IParameterRepository parameterRepository;
  private final IJobLogService jobLogService;
  private final INotificationService notificationService;
  private final IAutoInitializedParameterRepository autoInitializedParameterRepository;
  private final ISchedulerService schedulerService;

  @Override
  @Transactional
  public ChecklistBasicDto submitForReview(Long checklistId) throws ResourceNotFoundException, StreemException, JsonProcessingException {
    log.info("[submitForReview] Request to submit checklist for review, checklistId: {}", checklistId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Checklist checklist = checklistsRepository.findById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    /*-- Only primary Author is allowed to submit for review--*/
    validateIfPrincipalUserIsPrimaryAuthor(checklist, principalUser.getId());
    State.Checklist nextState = validateAndGetNextChecklistState(checklistId, checklist.getState(), Action.Collaborator.SUBMIT_FOR_REVIEW);
    Integer recentReviewCycle = null == checklist.getReviewCycle() ? 1 : checklist.getReviewCycle();

    Integer finalRecentReviewCycle = recentReviewCycle;
    Set<ChecklistCollaboratorMapping> reviewers = checklist.getCollaborators().stream()
      .filter(checklistCollaboratorMapping -> checklistCollaboratorMapping.getPhaseType().equals(State.ChecklistCollaboratorPhaseType.REVIEW) && checklistCollaboratorMapping.getPhase() == finalRecentReviewCycle)
      .collect(Collectors.toSet());

    //contains reviewer user ids to send email in case of review cycle change (when checklist is submitted to reviewers again)
    Set<Long> reviewerIds = new HashSet<>();

    if (reviewers.isEmpty()) {
      recentReviewCycle = 1;
      checklist.setReviewCycle(recentReviewCycle);
    } else {
      /*--For new review cycle, copy all the collaborators (of current review cycle) to the new review cycle.--*/
      Integer previousReviewCycle = recentReviewCycle;
      recentReviewCycle++;
      checklist.setReviewCycle(recentReviewCycle);
      List<ChecklistCollaboratorMapping> newChecklistCollaboratorMapping = new ArrayList<>();
      for (ChecklistCollaboratorMapping collaborator : reviewers) {
        if (collaborator.getPhaseType().equals(State.ChecklistCollaboratorPhaseType.REVIEW) && collaborator.getPhase().equals(previousReviewCycle)) {
          reviewerIds.add(collaborator.getUser().getId());
          newChecklistCollaboratorMapping.add(new ChecklistCollaboratorMapping(checklist, collaborator.getUser(), collaborator.getType(), recentReviewCycle, State.ChecklistCollaboratorPhaseType.REVIEW,
            principalUserEntity));
        }
      }
      if (!newChecklistCollaboratorMapping.isEmpty()) {
        checklistCollaboratorMappingRepository.saveAll(newChecklistCollaboratorMapping);
      }
    }
    checklist.setState(nextState);
    checklist.setModifiedBy(principalUserEntity);
    checklistsRepository.save(checklist);

    fixOrderTreeForStagesAndTasksAndParameters(checklistId);
    updateParameterRules(checklist);
    updateAutoInitializedParametersEntity(checklist, principalUserEntity);

    notificationService.notifyChecklistCollaborators(reviewerIds, Email.TEMPLATE_REVIEWER_ASSIGNED_TO_CHECKLIST, Email.SUBJECT_REVIEWER_ASSIGNED_TO_CHECKLIST, checklistId, principalUser.getOrganisationId());
    return checklistMapper.toChecklistBasicDto(checklist);
  }


  @Override
  public ChecklistBasicDto assignments(Long checklistId, ChecklistCollaboratorAssignmentRequest checklistCollaboratorAssignmentRequest) throws ResourceNotFoundException,
    StreemException {
    log.info("[assignments] Request to assign collaborators to checklist, checklistId: {}, checklistCollaboratorAssignmentRequest: {}", checklistId, checklistCollaboratorAssignmentRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    List<Error> errorList = new ArrayList<>();
    Checklist checklist = checklistsRepository.findById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    validateAndGetNextChecklistState(checklistId, checklist.getState(), Action.Collaborator.REVIEWER_ASSIGNMENT);
    /*-- Only primary Author is allowed to update the collaborators --*/
    validateIfPrincipalUserIsPrimaryAuthor(checklist, principalUser.getId());

    Set<Long> authorIds = checklist.getCollaborators().stream().filter(a -> (Type.AUTHOR_TYPES.contains(a.getType()))).map(a -> a.getUser().getId()).collect(Collectors.toSet());
    Set<Long> assignedUserIds = checklistCollaboratorAssignmentRequest.getAssignedUserIds();
    Set<Long> unassignedUserIds = checklistCollaboratorAssignmentRequest.getUnassignedUserIds();

    /*--Notify Users--*/
    notificationService.notifyChecklistCollaborators(assignedUserIds, Email.TEMPLATE_REVIEWER_ASSIGNED_TO_CHECKLIST, Email.SUBJECT_REVIEWER_ASSIGNED_TO_CHECKLIST, checklistId, principalUser.getOrganisationId());
    notificationService.notifyChecklistCollaborators(unassignedUserIds, Email.TEMPLATE_REVIEWER_UNASSIGNED_FROM_CHECKLIST, Email.SUBJECT_REVIEWER_UNASSIGNED_FROM_CHECKLIST, checklistId, principalUser.getOrganisationId());

    boolean isAssignedCollaboratorAnAuthor = false;
    for (Long authorId : authorIds) {
      if (assignedUserIds.contains(authorId)) {
        isAssignedCollaboratorAnAuthor = true;
        break;
      }
    }
    if (isAssignedCollaboratorAnAuthor) {
      ValidationUtils.invalidate(checklistId, ErrorCode.CANNOT_ASSIGN_COLLABORATOR);
    }
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Integer recentReviewCycle = checklist.getReviewCycle() == null ? 1 : checklist.getReviewCycle();
    Set<ChecklistCollaboratorMapping> checklistCollaboratorMapping = checklist.getCollaborators();
    /*--Filter the checklistCollaboratorMapping based on the current review cycle and convert it to a Map of usersId and its review state--*/
    Set<Long> collaborators = checklistCollaboratorMapping.stream().map(r -> r.getUser().getId()).collect(Collectors.toSet());
    Map<Long, State.ChecklistCollaborator> activeCollaborators = checklistCollaboratorMapping.stream()
      .filter(r -> r.getPhase().equals(recentReviewCycle))
      .collect(Collectors.toMap(r -> r.getUser().getId(), ChecklistCollaboratorMapping::getState));
    Set<Long> addCollaborators = new HashSet<>();
    Set<Long> removeCollaborators = new HashSet<>();
    /*-- Check if the request assignee is already assigned to the checklist --*/
    assignedUserIds.forEach(id -> {
      if (!activeCollaborators.containsKey(id)) {
        addCollaborators.add(id);
      } else {
        /*--TODO : Validate if send Response in error if already assigned used is been send--*/
        ValidationUtils.addError(id, errorList, ErrorCode.CANNOT_ASSIGN_COLLABORATOR);
      }
    });
    /*-- Check if the request un-assignee has not start reviewing the checklist or has not been part of any previous review cycle.--*/
    unassignedUserIds.forEach(id -> {
      if (collaborators.contains(id)) {
        if (activeCollaborators.containsKey(id)) {
          if (activeCollaborators.get(id).equals(State.ChecklistCollaborator.NOT_STARTED)) {
            removeCollaborators.add(id);
          } else {
            ValidationUtils.addError(id, errorList, ErrorCode.CANNOT_UNASSIGN_COLLABORATOR);
          }
        } else {
          ValidationUtils.addError(id, errorList, ErrorCode.CANNOT_UNASSIGN_COLLABORATOR);
        }
      } else {
        ValidationUtils.addError(id, errorList, ErrorCode.CANNOT_UNASSIGN_COLLABORATOR);
      }
    });
    if (!errorList.isEmpty()) {
      throw new StreemException("Could not complete assignments", errorList);
    }
    List<ChecklistCollaboratorMapping> newChecklistCollaboratorMapping =
      addCollaborators.stream().map(id -> new ChecklistCollaboratorMapping(checklist, userRepository.getOne(id), Type.Collaborator.REVIEWER, recentReviewCycle, State.ChecklistCollaboratorPhaseType.REVIEW, principalUserEntity)).collect(Collectors.toList());
    checklistCollaboratorMappingRepository.saveAll(newChecklistCollaboratorMapping);
    checklistCollaboratorMappingRepository.deleteAll(checklistId, recentReviewCycle, removeCollaborators);


    /*--TODO : Pending - if all other reviewer has submitted the review then we have to update the checklist state--*/

    return checklistMapper.toChecklistBasicDto(checklist);
  }

  @Override
  public List<ChecklistCollaboratorView> getAllAuthors(Long checklistId) {
    log.info("[getAllAuthors] Request to get authors of checklist, checklistId: {}", checklistId);
    return checklistCollaboratorMappingRepository.findAllByChecklistIdAndTypeIn(checklistId, Arrays.asList(Type.Collaborator.PRIMARY_AUTHOR.toString(), Type.Collaborator.AUTHOR.toString()));
  }

  @Override
  public List<ChecklistCollaboratorView> getAllReviewers(Long checklistId) {
    log.info("[getAllReviewers] Request to get reviewers of checklist, checklistId: {}", checklistId);
    return checklistCollaboratorMappingRepository.findAllByChecklistIdAndType(checklistId, Type.Collaborator.REVIEWER.toString()).stream()
      .collect(Collectors.toList());
  }

  @Override
  public List<ChecklistCollaboratorView> getAllSignOffUsers(Long checklistId) {
    log.info("[getAllSignOffUsers] Request to get signOff users of checklist, checklistId: {}", checklistId);
    return checklistCollaboratorMappingRepository.findAllByChecklistIdAndType(checklistId, Type.Collaborator.SIGN_OFF_USER.toString()).stream()
      .collect(Collectors.toList());
  }

  @Override
  public List<ChecklistCollaboratorView> getAllCollaborators(Long checklistId, State.ChecklistCollaboratorPhaseType phaseType) {
    log.info("[getAllCollaborators] Request to get all collaborators of checklist, checklistId: {}", checklistId);
    return checklistCollaboratorMappingRepository.findAllByChecklistIdAndPhaseType(checklistId, phaseType);
  }

  @Override
  @Transactional
  public ChecklistReviewDto startReview(Long checklistId) throws ResourceNotFoundException, StreemException {
    log.info("[startReview] Request to start review of checklist, checklistId: {}", checklistId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Checklist checklist = checklistsRepository.findById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    State.Checklist nextState = validateAndGetNextChecklistState(checklistId, checklist.getState(), Action.Collaborator.START_REVIEW);
    if (!checklist.getState().equals(State.Checklist.BEING_REVIEWED)) {
      checklist.setState(State.Checklist.BEING_REVIEWED);
      checklist.setModifiedBy(principalUserEntity);
      checklistsRepository.save(checklist);
    }
    Integer recentReviewCycle = checklist.getReviewCycle();
    ChecklistCollaboratorMapping checklistCollaboratorMapping = checklistCollaboratorMappingRepository.findByChecklistAndPhaseTypeAndPhaseAndUser(checklist, State.ChecklistCollaboratorPhaseType.REVIEW, recentReviewCycle, principalUserEntity)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND,
        ExceptionType.ENTITY_NOT_FOUND));
    State.ChecklistCollaborator state = checklistCollaboratorMapping.getState();
    State.ChecklistCollaborator collaboratorsNextState = validateAndGetNextChecklistState(checklistId, state, Action.Collaborator.START_REVIEW);
    checklistCollaboratorMapping.setState(collaboratorsNextState);
    checklistCollaboratorMapping = checklistCollaboratorMappingRepository.save(checklistCollaboratorMapping);

    /*--Updating the checklist state--*/
    if (checklist.getState().equals(State.Checklist.SUBMITTED_FOR_REVIEW)) {
      checklist.setState(nextState);
      checklist.setModifiedBy(principalUserEntity);
      checklist = checklistsRepository.save(checklist);
    }

    return checklistMapper.toChecklistReviewDto(checklist, Collections.singletonList(checklistCollaboratorMapping));
  }

  @Override
  @Transactional
  public ChecklistCommentDto commentedOk(Long checklistId) throws ResourceNotFoundException, StreemException {
    log.info("[commentedOk] Request to comment okay after reviewing checklist, checklistId: {}", checklistId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String comments = "All OK, No Comments";
    Checklist checklist = checklistsRepository.findById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Integer recentReviewCycle = checklist.getReviewCycle();

    validateAndGetNextChecklistState(checklistId, checklist.getState(), Action.Collaborator.COMMENTED_OK);

    ChecklistCollaboratorMapping activeChecklistCollaboratorMapping = checklistCollaboratorMappingRepository.findByChecklistAndPhaseTypeAndPhaseAndUser(checklist, State.ChecklistCollaboratorPhaseType.REVIEW, recentReviewCycle, principalUserEntity)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    State.ChecklistCollaborator state = activeChecklistCollaboratorMapping.getState();
    State.ChecklistCollaborator collaboratorsNextState = validateAndGetNextChecklistState(checklistId, state, Action.Collaborator.COMMENTED_OK);
    activeChecklistCollaboratorMapping.setState(collaboratorsNextState);
    ChecklistCollaboratorMapping checklistCollaboratorMapping = checklistCollaboratorMappingRepository.save(activeChecklistCollaboratorMapping);

    Set<ChecklistCollaboratorMapping> checklistCollaboratorMappings = checklist.getCollaborators();
    Integer reviewCycle = checklist.getReviewCycle();
    validateAndNotifyReviewersForSubmitReview(checklistCollaboratorMappings, reviewCycle, checklistId, principalUser.getOrganisationId());

    checklistCollaboratorCommentsRepository.deleteByChecklistCollaboratorMappingId(activeChecklistCollaboratorMapping.getId());

    ChecklistCollaboratorComments checklistCollaboratorComments = new ChecklistCollaboratorComments();
    checklistCollaboratorComments.setChecklist(checklist)
      .setComments(comments)
      .setReviewState(State.ChecklistCollaborator.COMMENTED_OK)
      .setChecklistCollaboratorMapping(activeChecklistCollaboratorMapping)
      .setModifiedBy(principalUserEntity)
      .setCreatedBy(principalUserEntity);

    checklistCollaboratorComments = checklistCollaboratorCommentsRepository.save(checklistCollaboratorComments);

    return checklistMapper.toChecklistCommentDto(checklist, checklistCollaboratorComments, Collections.singletonList(checklistCollaboratorMapping));
  }

  @Override
  @Transactional
  public ChecklistCommentDto commentedChanges(Long checklistId, CommentAddRequest commentAddRequest) throws ResourceNotFoundException, StreemException {
    log.info("[commentedChanges] Request to make changes to checklist configuration, checklistId: {}, commentAddRequest", checklistId, commentAddRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Checklist checklist = checklistsRepository.findById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    validateAndGetNextChecklistState(checklistId, checklist.getState(), Action.Collaborator.COMMENTED_CHANGES);
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Integer recentReviewCycle = checklist.getReviewCycle();
    ChecklistCollaboratorMapping checklistCollaboratorMapping = checklistCollaboratorMappingRepository.findByChecklistAndPhaseTypeAndPhaseAndUser(checklist, State.ChecklistCollaboratorPhaseType.REVIEW, recentReviewCycle, principalUserEntity)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    State.ChecklistCollaborator state = checklistCollaboratorMapping.getState();
    State.ChecklistCollaborator collaboratorsNextState = validateAndGetNextChecklistState(checklistId, state, Action.Collaborator.COMMENTED_CHANGES);
    checklistCollaboratorMapping.setState(collaboratorsNextState);
    checklistCollaboratorMapping = checklistCollaboratorMappingRepository.save(checklistCollaboratorMapping);
    checklistCollaboratorCommentsRepository.deleteByChecklistCollaboratorMappingId(checklistCollaboratorMapping.getId());

    ChecklistCollaboratorComments checklistCollaboratorComments = new ChecklistCollaboratorComments();
    checklistCollaboratorComments.setChecklist(checklist)
      .setComments(commentAddRequest.getComments())
      .setReviewState(State.ChecklistCollaborator.COMMENTED_CHANGES)
      .setChecklistCollaboratorMapping(checklistCollaboratorMapping)
      .setModifiedBy(principalUserEntity)
      .setCreatedBy(principalUserEntity);
    checklistCollaboratorComments = checklistCollaboratorCommentsRepository.save(checklistCollaboratorComments);

    checklist = checklistsRepository.findById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    Set<ChecklistCollaboratorMapping> checklistCollaboratorMappings = checklist.getCollaborators();
    Integer reviewCycle = checklist.getReviewCycle();
    validateAndNotifyReviewersForSubmitReview(checklistCollaboratorMappings, reviewCycle, checklistId, principalUser.getOrganisationId());

    return checklistMapper.toChecklistCommentDto(checklist, checklistCollaboratorComments, Collections.singletonList(checklistCollaboratorMapping));
  }

  @Override
  @Transactional
  public ChecklistReviewDto submitBack(Long checklistId) throws ResourceNotFoundException, StreemException {
    log.info("[submitBack] Request to submit back checklist, checklistId: {}", checklistId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Checklist checklist = checklistsRepository.findById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    validateAndGetNextChecklistState(checklistId, checklist.getState(), Action.Collaborator.SUBMIT_REVIEW);
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Integer recentReviewCycle = checklist.getReviewCycle();
    ChecklistCollaboratorMapping checklistCollaboratorMapping = checklistCollaboratorMappingRepository.findByChecklistAndPhaseTypeAndPhaseAndUser(checklist, State.ChecklistCollaboratorPhaseType.REVIEW, recentReviewCycle, principalUserEntity)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    State.ChecklistCollaborator state = checklistCollaboratorMapping.getState();
    State.ChecklistCollaborator collaboratorsNextState = validateAndGetNextChecklistState(checklistId, state, Action.Collaborator.SUBMIT_REVIEW);
    checklistCollaboratorMapping.setState(collaboratorsNextState);
    checklistCollaboratorMapping = checklistCollaboratorMappingRepository.save(checklistCollaboratorMapping);

    Set<ChecklistCollaboratorMapping> checklistCollaboratorMappings = checklist.getCollaborators().stream()
      .filter(a -> a.getPhaseType().equals(State.ChecklistCollaboratorPhaseType.REVIEW))
      .collect(Collectors.toSet());
    // Filter the checklistCollaboratorMapping based on the current review cycle and convert it to a Map of usersId and its review state
    Set<State.ChecklistCollaborator> collaboratorsStatuses = checklistCollaboratorMappings.stream()
      .filter(r -> r.getPhase().equals(recentReviewCycle))
      .map(ChecklistCollaboratorMapping::getState)
      .collect(Collectors.toSet());
    Set<State.ChecklistCollaborator> collaboratorsAllOkStatuses = collaboratorsStatuses.stream()
      .filter(r -> r.equals(State.ChecklistCollaborator.REQUESTED_NO_CHANGES))
      .collect(Collectors.toSet());
    Set<State.ChecklistCollaborator> collaboratorsCommentStatuses = collaboratorsStatuses.stream()
      .filter(s -> s.equals(State.ChecklistCollaborator.REQUESTED_CHANGES))
      .collect(Collectors.toSet());

    /*--Validate if all reviewers have submitted ok--*/
    if (collaboratorsAllOkStatuses.size() == collaboratorsStatuses.size()) {
      checklist.setState(State.Checklist.READY_FOR_SIGNING);
      checklist.setModifiedBy(principalUserEntity);
      checklistsRepository.save(checklist);
      /*--send primary author a notification--*/
      notificationService.notifyChecklistCollaborators(getPrimaryAuthors(checklist), Email.TEMPLATE_PROTOTYPE_READY_FOR_SIGNING, Email.SUBJECT_PROTOTYPE_READY_FOR_SIGNING, checklistId, principalUser.getOrganisationId());
    } else if ((collaboratorsCommentStatuses.size() + collaboratorsAllOkStatuses.size()) == collaboratorsStatuses.size()) {
      checklist.setState(State.Checklist.REQUESTED_CHANGES);
      checklist.setModifiedBy(principalUserEntity);
      checklistsRepository.save(checklist);
      /*--send primary author a notification--*/
      notificationService.notifyChecklistCollaborators(getPrimaryAuthors(checklist), Email.TEMPLATE_PROTOTYPE_REQUESTED_CHANGES, Email.SUBJECT_PROTOTYPE_REQUESTED_CHANGES, checklistId, principalUser.getOrganisationId());
    }

    return checklistMapper.toChecklistReviewDto(checklist, Collections.singletonList(checklistCollaboratorMapping));
  }


  @Override
  public ChecklistBasicDto initiateSignOff(Long checklistId) throws ResourceNotFoundException, StreemException {
    log.info("[initiateSignOff] Request initiate sign off on checklist, checklistId: {}", checklistId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    Checklist checklist = checklistsRepository.findById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    /*-- Only primary Author is allowed to initiate sign off--*/
    validateIfPrincipalUserIsPrimaryAuthor(checklist, principalUser.getId());
    State.Checklist nextState = validateAndGetNextChecklistState(checklistId, checklist.getState(), Action.Collaborator.SUBMIT_FOR_SIGN_OFF);
    checklist.setState(nextState);
    checklistsRepository.save(checklist);
    return checklistMapper.toChecklistBasicDto(checklist);
  }


  @Override
  @Transactional
  public ChecklistReviewDto signOffOrderTree(Long checklistId, SignOffOrderTreeRequest signOffOrderTreeRequest) throws ResourceNotFoundException, StreemException {
    log.info("[signOffOrderTree] Request to set sign off sequence for checklist, checklistId: {}, SignOffOrderTreeRequest: {}", checklistId, signOffOrderTreeRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Checklist checklist = checklistsRepository.findById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    State.Checklist nextState = validateAndGetNextChecklistState(checklistId, checklist.getState(), Action.Collaborator.SUBMIT_FOR_SIGN_OFF);

    /*-- Only primary Author is allowed to assign signOffUser sequence --*/
    validateIfPrincipalUserIsPrimaryAuthor(checklist, principalUser.getId());

    Set<Long> userIds = signOffOrderTreeRequest.getUsers().stream().map(a -> a.getUserId()).collect(Collectors.toSet());

    /*--Get AuthorIds--*/
    Set<Long> authorIds = checklist.getCollaborators().stream().filter(a -> Type.AUTHOR_TYPES.contains(a.getType())).map(a -> a.getUser().getId()).collect(Collectors.toSet());

    /*--Filter the checklistCollaboratorMapping based on the current review cycle and convert it to a Map of usersId and its review state--*/
    Set<ChecklistCollaboratorMapping> checklistCollaboratorMapping = checklist.getCollaborators();
    Set<Long> reviewerIds = checklistCollaboratorMapping.stream()
      .filter(r -> r.getPhase().equals(checklist.getReviewCycle())).map(a -> a.getUser().getId())
      .collect(Collectors.toSet());

    /*--signoff users must be authors or reviewer--*/
    for (Long userId : userIds) {
      if (!(authorIds.contains(userId) || reviewerIds.contains(userId))) {
        ValidationUtils.invalidate(checklistId, ErrorCode.CANNOT_ASSIGN_COLLABORATOR);
      }
    }

    User principalUserEntity = userRepository.getOne(principalUser.getId());
    List<ChecklistCollaboratorMapping> newChecklistCollaboratorMapping =
      signOffOrderTreeRequest.getUsers()
        .stream()
        .map(user -> new ChecklistCollaboratorMapping(checklist, userRepository.getOne(user.getUserId()), Type.Collaborator.SIGN_OFF_USER, 1, State.ChecklistCollaboratorPhaseType.SIGN_OFF, principalUserEntity).setOrderTree(user.getOrderTree())).collect(Collectors.toList());
    checklist.getCollaborators().addAll(newChecklistCollaboratorMapping);


    checklist.setState(nextState);
    checklist.setModifiedBy(principalUserEntity);
    checklistsRepository.save(checklist);

    /*--notify approvers--*/
    notifyNextApprovers(checklist, 0, principalUser.getOrganisationId());
    return checklistMapper.toChecklistReviewDto(checklist, newChecklistCollaboratorMapping);
  }


  @Override
  @Transactional
  public ChecklistReviewDto signOff(Long checklistId) throws ResourceNotFoundException, StreemException {
    log.info("[signOff] Request to sign off checklist, checklistId: {}", checklistId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Checklist checklist = checklistsRepository.findById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    validateAndGetNextChecklistState(checklistId, checklist.getState(), Action.Collaborator.SIGN_OFF);
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    ChecklistCollaboratorMapping checklistCollaboratorMapping = checklistCollaboratorMappingRepository.findFirstByChecklistAndPhaseTypeAndUserAndStateOrderByOrderTreeAsc
        (checklist, State.ChecklistCollaboratorPhaseType.SIGN_OFF,
          principalUserEntity, State.ChecklistCollaborator.NOT_STARTED)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND,
        ExceptionType.ENTITY_NOT_FOUND));

    Set<ChecklistCollaboratorMapping> checklistCollaboratorMappings = checklist.getCollaborators();

    /*--Validate state of of all previous signoff users*/
    Set<State.ChecklistCollaborator> previousCollaboratorsStatuses = checklistCollaboratorMappings.stream()
      .filter(r -> r.getPhaseType().equals(State.ChecklistCollaboratorPhaseType.SIGN_OFF) && r.getOrderTree() < checklistCollaboratorMapping.getOrderTree())
      .map(ChecklistCollaboratorMapping::getState)
      .collect(Collectors.toSet());
    if (previousCollaboratorsStatuses.contains(State.ChecklistCollaborator.NOT_STARTED)) {
      ValidationUtils.invalidate(checklistId, ErrorCode.PROCESS_USER_SIGNOFF_ERROR);
    }

    State.ChecklistCollaborator state = checklistCollaboratorMapping.getState();
    State.ChecklistCollaborator collaboratorsNextState = validateAndGetNextChecklistState(checklistId, state, Action.Collaborator.SIGN_OFF);
    checklistCollaboratorMapping.setState(collaboratorsNextState);

    /*--update the checklist status--*/
    if (checklist.getState().equals(State.Checklist.READY_FOR_SIGNING)) {
      checklist.setState(State.Checklist.SIGNING_IN_PROGRESS);
    }

    checklist.setModifiedBy(principalUserEntity);
    checklistsRepository.save(checklist);

    /*--Check if the user is the last signoff for the checklist--*/
    Set<State.ChecklistCollaborator> collaboratorsStatuses = checklistCollaboratorMappings.stream()
      .filter(r -> r.getPhaseType().equals(State.ChecklistCollaboratorPhaseType.SIGN_OFF))
      .map(ChecklistCollaboratorMapping::getState)
      .collect(Collectors.toSet());
    Set<State.ChecklistCollaborator> collaboratorsSignedStatuses = collaboratorsStatuses.stream()
      .filter(r -> r.equals(State.ChecklistCollaborator.SIGNED))
      .collect(Collectors.toSet());

    /*--Validate if all signOffUsers have submitted ok--*/
    if (collaboratorsSignedStatuses.size() == collaboratorsStatuses.size()) {
      checklist.setState(State.Checklist.READY_FOR_RELEASE);
      checklistsRepository.save(checklist);
    } else {
      /*-- notify next approvers --*/
      notifyNextApprovers(checklist, checklistCollaboratorMapping.getOrderTree(), principalUser.getOrganisationId());
    }

    return checklistMapper.toChecklistReviewDto(checklist, Collections.singletonList(checklistCollaboratorMapping));
  }

  @Override
  @Transactional
  public ChecklistBasicDto publish(Long checklistId) throws ResourceNotFoundException, StreemException {
    log.info("[publish] Request to publish checklist, checklistId: {}", checklistId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Checklist checklist = checklistsRepository.findById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    validateAndGetNextChecklistState(checklistId, checklist.getState(), Action.Collaborator.PUBLISH);
    User principalUserEntity = userRepository.getOne(principalUser.getId());

    List<JobLogColumn> jobLogColumns = jobLogService.getJobLogColumnForChecklist(checklist);
    JsonNode jsonNode = JsonUtils.valueToNode(jobLogColumns);

    checklist.setJobLogColumns(jsonNode);
    checklist.setState(State.Checklist.PUBLISHED);
    checklist.setReleasedAt(DateTimeUtils.now());
    checklist.setReleasedBy(principalUserEntity);
    checklist.setModifiedBy(principalUserEntity);
    checklist = checklistsRepository.save(checklist);

    versionService.publishVersion(checklist.getVersion());

    if (!Utility.isEmpty(checklist.getVersion().getParent())) {
      Long parent = checklist.getVersion().getParent();
      checklistsRepository.updateState(State.Checklist.DEPRECATED, parent);
      String parentChecklistCode = checklistsRepository.getChecklistCodeByChecklistId(parent);
      checklistAuditService.deprecate(parent, parentChecklistCode, checklist.getCode(), principalUser);
      schedulerService.findAndDeprecateSchedulersForChecklist(parent, principalUserEntity);
    }

    checklistAuditService.publish(checklist.getId(), checklist.getCode(), principalUser);

    return checklistMapper.toChecklistBasicDto(checklist);
  }

  @Override
  public List<CollaboratorCommentDto> getComments(Long checklistId, Long reviewerId) throws ResourceNotFoundException, StreemException {
    log.info("[getComments] Request to get checklist comments of a collaborator, checklistId: {}, reviewerId: {}", checklistId, reviewerId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Checklist checklist = checklistsRepository.findById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Integer recentReviewCycle = checklist.getReviewCycle();

    ChecklistCollaboratorMapping activeChecklistCollaboratorMapping = checklistCollaboratorMappingRepository.findByChecklistAndPhaseTypeAndPhaseAndUser(checklist, State.ChecklistCollaboratorPhaseType.REVIEW, recentReviewCycle,
        principalUserEntity)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND,
        ExceptionType.ENTITY_NOT_FOUND));

    List<ChecklistCollaboratorComments> checklistCollaboratorComments = activeChecklistCollaboratorMapping.getComments();

    return checklistCollaboratorComments.stream()
      .map(a -> new CollaboratorCommentDto()
        .setId(a.getIdAsString())
        .setState(a.getReviewState())
        .setComments(a.getComments())
        .setCommentedBy(UserAuditDto.builder()
          .id(a.getCreatedBy().getIdAsString())
          .firstName(a.getCreatedBy().getFirstName())
          .lastName(a.getCreatedBy().getLastName())
          .employeeId(a.getCreatedBy().getEmployeeId())
          .build())
        .setCommentedAt(a.getCreatedAt()).setModifiedAt(a.getModifiedAt())
        .setPhase(a.getChecklistCollaboratorMapping().getPhase()))
      .collect(Collectors.toList());
  }

  /**
   * Delete all existing auto initialized parameter entries for this checklist and recreate them
   *
   * @param checklist
   * @param principalUserEntity
   * @throws JsonProcessingException
   */
  @Override
  public void updateAutoInitializedParametersEntity(Checklist checklist, User principalUserEntity) throws JsonProcessingException {
    autoInitializedParameterRepository.deleteByChecklistId(checklist.getId());

    List<AutoInitializedParameter> autoInitializedParameters = new ArrayList<>();
    for (Stage stage : checklist.getStages()) {
      for (Task task : stage.getTasks()) {
        for (Parameter parameter : task.getParameters()) {
          if (parameter.isAutoInitialized()) {
            AutoInitializeDto autoInitializeDto = JsonUtils.readValue(parameter.getAutoInitialize().toString(), AutoInitializeDto.class);
            Long referencedParameterId = Long.valueOf(autoInitializeDto.getParameterId());
            Parameter referencedParameter = parameterRepository.findById(referencedParameterId).get();

            AutoInitializedParameter autoInitializedParameter = new AutoInitializedParameter();
            autoInitializedParameter.setAutoInitializedParameter(parameter);
            autoInitializedParameter.setReferencedParameter(referencedParameter);
            autoInitializedParameter.setChecklist(checklist);
            autoInitializedParameter.setCreatedAt(DateTimeUtils.now());
            autoInitializedParameter.setModifiedAt(DateTimeUtils.now());
            autoInitializedParameter.setCreatedBy(principalUserEntity);
            autoInitializedParameter.setModifiedBy(principalUserEntity);

            autoInitializedParameters.add(autoInitializedParameter);
          }
          // TODO we are considering CALCULATION without isAutoinitialized flag, this must be changed and all older calculation parameters to be migrated to set the flag true
          else if (parameter.getType().equals(Type.Parameter.CALCULATION)) {
            CalculationParameter calculationParameter = JsonUtils.readValue(parameter.getData().toString(), CalculationParameter.class);
            Map<String, CalculationParameterVariable> variables = calculationParameter.getVariables();

            for (Map.Entry<String, CalculationParameterVariable> entry : variables.entrySet()) {
              Long referencedParameterId = Long.valueOf(entry.getValue().getParameterId());
              Parameter referencedParameter = parameterRepository.findById(referencedParameterId).get();

              AutoInitializedParameter autoInitializedParameter = new AutoInitializedParameter();
              autoInitializedParameter.setAutoInitializedParameter(parameter);
              autoInitializedParameter.setReferencedParameter(referencedParameter);
              autoInitializedParameter.setChecklist(checklist);
              autoInitializedParameter.setCreatedAt(DateTimeUtils.now());
              autoInitializedParameter.setModifiedAt(DateTimeUtils.now());
              autoInitializedParameter.setCreatedBy(principalUserEntity);
              autoInitializedParameter.setModifiedBy(principalUserEntity);

              autoInitializedParameterRepository.save(autoInitializedParameter);
            }

          }
        }
      }

      if (!Utility.isEmpty(autoInitializedParameters)) {
        autoInitializedParameterRepository.saveAll(autoInitializedParameters);
      }
    }
  }

  @Transactional
  void notifyNextApprovers(Checklist checklist, Integer previousOrderTree, Long organisationId) {
    /*- -Validate if all signOff users of same orderTree has completed the checklist signOff --*/
    Set<State.ChecklistCollaborator> previousCollaboratorsStatuses = checklist.getCollaborators().stream()
      .filter(r -> r.getPhaseType().equals(State.ChecklistCollaboratorPhaseType.SIGN_OFF) && r.getOrderTree() <= previousOrderTree)
      .map(ChecklistCollaboratorMapping::getState)
      .collect(Collectors.toSet());
    if (!previousCollaboratorsStatuses.contains(State.ChecklistCollaborator.NOT_STARTED)) {
      Set<Long> nextCollaborators = checklist.getCollaborators().stream()
        .filter(r -> r.getPhaseType().equals(State.ChecklistCollaboratorPhaseType.SIGN_OFF) && r.getOrderTree() == (previousOrderTree + 1))
        .map(u -> u.getUser().getId())
        .collect(Collectors.toSet());
      notificationService.notifyChecklistCollaborators(nextCollaborators, Email.TEMPLATE_PROTOTYPE_SIGNING_REQUEST, Email.SUBJECT_PROTOTYPE_SIGNING_REQUEST, checklist.getId(), organisationId);
    }
  }

  @Transactional
  void validateAndNotifyReviewersForSubmitReview(Set<ChecklistCollaboratorMapping> checklistCollaboratorMappings, Integer reviewCycle, Long checklistId, Long organisationId) {
    // Filter the checklistCollaboratorMapping based on the current review cycle and convert it to a Map of review state
    Set<State.ChecklistCollaborator> collaboratorsStatuses = checklistCollaboratorMappings.stream()
      .filter(r -> r.getPhase().equals(reviewCycle))
      .map(ChecklistCollaboratorMapping::getState)
      .collect(Collectors.toSet());
    Set<Long> activeCollaboratorsId = checklistCollaboratorMappings.stream()
      .filter(r -> (r.getPhase().equals(reviewCycle)) && (!r.getState().equals(State.ChecklistCollaborator.NOT_STARTED)))
      .map(r -> r.getUser().getId())
      .collect(Collectors.toSet());

    /*--Validate if all active reviewers have reviewed --*/
    if (activeCollaboratorsId.size() == collaboratorsStatuses.size()) {
      notificationService.notifyChecklistCollaborators(activeCollaboratorsId, Email.TEMPLATE_REVIEW_SUBMIT_REQUEST, Email.SUBJECT_REVIEW_SUBMIT_REQUEST, checklistId, organisationId);
    }
  }

  private void fixOrderTreeForStagesAndTasksAndParameters(Long checklistId) {
    int orderTree = 1;
    List<Stage> stages = stageRepository.findByChecklistIdOrderByOrderTree(checklistId);
    Set<Long> stageIds = new HashSet<>();
    for (Stage stage : stages) {
      stage.setOrderTree(orderTree);
      orderTree++;
      stageIds.add(stage.getId());
    }

    stageRepository.saveAll(stages);

    orderTree = 1;
    Long currentStageId = null;
    List<Task> tasks = taskRepository.findByStageIdInOrderByOrderTree(stageIds);
    Set<Long> taskIds = new HashSet<>();
    for (Task task : tasks) {
      if (task.getStageId().equals(currentStageId)) {
        orderTree++;
      } else {
        currentStageId = task.getStageId();
        orderTree = 1;
      }
      task.setOrderTree(orderTree);
      taskIds.add(task.getId());
    }
    taskRepository.saveAll(tasks);

    orderTree = 1;
    Long currentTaskId = null;
    List<Parameter> parameters = parameterRepository.findByTaskIdInOrderByOrderTree(taskIds);
    for (Parameter parameter : parameters) {
      if (parameter.getTaskId().equals(currentTaskId)) {
        orderTree++;
      } else {
        currentTaskId = parameter.getTaskId();
        orderTree = 1;
      }
      parameter.setOrderTree(orderTree);
    }
    parameterRepository.saveAll(parameters);
  }

  private State.Checklist validateAndGetNextChecklistState(Long checklistId, State.Checklist currentState, Action.Collaborator action) throws StreemException {
    State.Checklist nextState = State.Checklist.ILLEGAL;

    switch (action) {
      case SUBMIT_FOR_REVIEW:
        switch (currentState) {
          case BEING_BUILT:
          case REQUESTED_CHANGES:
            return State.Checklist.SUBMITTED_FOR_REVIEW;
          default:
            ValidationUtils.invalidate(checklistId, ErrorCode.PROCESS_INVALID_STATE_ACTION);
            break;
        }
        break;
      case REVIEWER_ASSIGNMENT:
        switch (currentState) {
          case SUBMITTED_FOR_REVIEW:
          case BEING_REVIEWED:
          case REQUESTED_CHANGES:
            return State.Checklist.SUBMITTED_FOR_REVIEW;
          default:
            ValidationUtils.invalidate(checklistId, ErrorCode.PROCESS_INVALID_STATE_ACTION);
            break;
        }
        break;
      case START_REVIEW:
        switch (currentState) {
          case REQUESTED_CHANGES:
          case BEING_REVIEWED:
          case SUBMITTED_FOR_REVIEW:
            return State.Checklist.BEING_REVIEWED;
          default:
            ValidationUtils.invalidate(checklistId, ErrorCode.PROCESS_INVALID_STATE_ACTION);
            break;
        }
      case COMMENTED_OK:
        switch (currentState) {
          case BEING_REVIEWED:
          case REQUESTED_CHANGES:
            return State.Checklist.READY_FOR_SIGNING;
          default:
            ValidationUtils.invalidate(checklistId, ErrorCode.PROCESS_INVALID_STATE_ACTION);
            break;
        }
      case COMMENTED_CHANGES:
        switch (currentState) {
          case BEING_REVIEWED:
          case REQUESTED_CHANGES:
            return State.Checklist.REQUESTED_CHANGES;
          default:
            ValidationUtils.invalidate(checklistId, ErrorCode.PROCESS_INVALID_STATE_ACTION);
            break;
        }
      case SUBMIT_REVIEW:
        switch (currentState) {
          case BEING_REVIEWED:
          case REQUESTED_CHANGES:
            return nextState;
          default:
            ValidationUtils.invalidate(checklistId, ErrorCode.PROCESS_INVALID_STATE_ACTION);
            break;
        }
      case SUBMIT_FOR_SIGN_OFF:
        switch (currentState) {
          case READY_FOR_SIGNING:
            return State.Checklist.SIGN_OFF_INITIATED;
          default:
            ValidationUtils.invalidate(checklistId, ErrorCode.PROCESS_INVALID_STATE_ACTION);
            break;
        }
      /*
      case SIGN_OFF_SEQUENCE:
        switch (currentState) {
          case SIGN_OFF_INITIATED:
            break;
          default:
            ValidationUtils.invalidate(checklistId, ErrorCode.CHECKLIST_INVALID_STATE_ACTION);
            break;
        }
       */
      case SIGN_OFF:
        switch (currentState) {
          case SIGN_OFF_INITIATED:
          case SIGNING_IN_PROGRESS:
            return State.Checklist.SIGNING_IN_PROGRESS;
          default:
            ValidationUtils.invalidate(checklistId, ErrorCode.PROCESS_INVALID_STATE_ACTION);
            break;
        }
      case PUBLISH:
        switch (currentState) {
          case READY_FOR_RELEASE:
            return State.Checklist.PUBLISHED;
          default:
            ValidationUtils.invalidate(checklistId, ErrorCode.PROCESS_INVALID_STATE_ACTION);
            break;
        }
      default:
        throw new IllegalStateException("Illegal checklist state.");
    }
    return nextState;
  }

  private State.ChecklistCollaborator validateAndGetNextChecklistState(Long checklistId, State.ChecklistCollaborator collaboratorState, Action.Collaborator action) throws StreemException {
    switch (action) {
      case START_REVIEW:
        switch (collaboratorState) {
          case ILLEGAL:
          case NOT_STARTED:
            return State.ChecklistCollaborator.BEING_REVIEWED;
          default:
            ValidationUtils.invalidate(checklistId, ErrorCode.PROCESS_INVALID_STATE_ACTION);
            break;
        }

      case COMMENTED_OK:
        switch (collaboratorState) {
          case COMMENTED_CHANGES:
          case BEING_REVIEWED:
          case COMMENTED_OK:
            return State.ChecklistCollaborator.COMMENTED_OK;
          default:
            ValidationUtils.invalidate(checklistId, ErrorCode.PROCESS_INVALID_STATE_ACTION);
            break;
        }
      case COMMENTED_CHANGES:
        switch (collaboratorState) {
          case COMMENTED_OK:
          case COMMENTED_CHANGES:
          case BEING_REVIEWED:
            return State.ChecklistCollaborator.COMMENTED_CHANGES;
          default:
            ValidationUtils.invalidate(checklistId, ErrorCode.PROCESS_INVALID_STATE_ACTION);
            break;
        }
      case SUBMIT_REVIEW:
        switch (collaboratorState) {
          case COMMENTED_OK:
            return State.ChecklistCollaborator.REQUESTED_NO_CHANGES;
          case COMMENTED_CHANGES:
            return State.ChecklistCollaborator.REQUESTED_CHANGES;
          default:
            ValidationUtils.invalidate(checklistId, ErrorCode.PROCESS_INVALID_STATE_ACTION);
            break;
        }
      case SIGN_OFF:
        switch (collaboratorState) {
          case NOT_STARTED:
            return State.ChecklistCollaborator.SIGNED;
          default:
            ValidationUtils.invalidate(checklistId, ErrorCode.PROCESS_INVALID_STATE_ACTION);
            break;
        }
    }
    return State.ChecklistCollaborator.ILLEGAL;
  }

  private void validateIfPrincipalUserIsPrimaryAuthor(Checklist checklist, Long principalUsersId) throws StreemException {
    if (!isPrimaryAuthor(checklist, principalUsersId)) {
      ValidationUtils.invalidate(principalUsersId, ErrorCode.ONLY_PRIMARY_AUTHORS_ALLOWED);
    }
  }

  private boolean isPrimaryAuthor(Checklist checklist, Long principalUsersId) {
    return checklist.getCollaborators().stream().anyMatch(a -> a.getUser().getId().equals(principalUsersId) && a.isPrimary());
  }

  private Set<Long> getPrimaryAuthors(Checklist checklist) {
    return checklist.getCollaborators().stream().filter(ChecklistCollaboratorMapping::isPrimary).map(a -> a.getUser().getId()).collect(Collectors.toSet());
  }

  private void updateParameterRules(Checklist checklist) throws JsonProcessingException {
    Map<Long, Set<String>> stageParameters = new HashMap<>();
    Map<Long, Set<String>> taskParameters = new HashMap<>();
    List<Parameter> parametersHavingRules = new ArrayList<>();

    for (Stage stage : checklist.getStages()) {
      Set<String> currentStageParameters = new HashSet<>();
      for (Task task : stage.getTasks()) {
        Set<String> currentTaskParameters = new HashSet<>();
        for (Parameter parameter : task.getParameters()) {
          if (!Utility.isEmpty(parameter.getRules())) {
            parametersHavingRules.add(parameter);
          }
          currentStageParameters.add(String.valueOf(parameter.getId()));
          currentTaskParameters.add(String.valueOf(parameter.getId()));
        }
        taskParameters.put(task.getId(), currentTaskParameters);
      }
      stageParameters.put(stage.getId(), currentStageParameters);
    }

    for (Parameter parameter : parametersHavingRules) {

      List<RuleDto> ruleDtos = JsonUtils.readValue(parameter.getRules().toString(),
        new TypeReference<List<RuleDto>>() {
        });

      if (!Utility.isEmpty(ruleDtos)) {
        for (RuleDto ruleDto : ruleDtos) {
          if (null != ruleDto.getHide()) {
            for (String stageId : ruleDto.getHide().getStages()) {
              ruleDto.getHide().getParameters().addAll(stageParameters.get(Long.valueOf(stageId)));
            }
            for (String taskId : ruleDto.getHide().getTasks()) {
              ruleDto.getHide().getParameters().addAll(taskParameters.get(Long.valueOf(taskId)));
            }
          }
        }

        parameter.setRules(JsonUtils.valueToNode(ruleDtos));
      }
    }

    parameterRepository.saveAll(parametersHavingRules);
  }
}
