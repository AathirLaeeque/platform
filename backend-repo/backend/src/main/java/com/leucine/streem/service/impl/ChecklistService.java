package com.leucine.streem.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.leucine.commons.id.IdGenerator;
import com.leucine.streem.constant.*;
import com.leucine.streem.dto.*;
import com.leucine.streem.dto.mapper.*;
import com.leucine.streem.dto.projection.ChecklistCollaboratorView;
import com.leucine.streem.dto.projection.TaskAssigneeView;
import com.leucine.streem.dto.request.*;
import com.leucine.streem.dto.response.Error;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.exception.ExceptionType;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.*;
import com.leucine.streem.model.helper.*;
import com.leucine.streem.model.helper.parameter.*;
import com.leucine.streem.model.helper.search.SearchCriteria;
import com.leucine.streem.repository.*;
import com.leucine.streem.service.*;
import com.leucine.streem.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChecklistService implements IChecklistService {
  private final IChecklistRepository checklistRepository;
  private final IChecklistCollaboratorMappingRepository checklistCollaboratorMappingRepository;
  private final IChecklistMapper checklistMapper;
  private final IUserMapper userMapper;
  private final ICodeService codeService;
  private final IChecklistAuditService checklistAuditService;
  private final IFacilityRepository facilityRepository;
  private final IOrganisationRepository organisationRepository;
  private final IFacilityUseCaseMappingRepository facilityUseCaseMappingRepository;
  private final IPropertyService propertyService;
  private final IUserRepository userRepository;
  private final IVersionRepository versionRepository;
  private final IVersionService versionService;
  private final IJobRepository jobRepository;
  private final IChecklistCollaboratorService checklistCollaboratorService;
  private final IParameterRepository parameterRepository;
  private final IChecklistDefaultUsersRepository checklistDefaultUsersRepository;
  private final IChecklistDefaultUserMapper defaultUserMapper;
  private final ITaskRepository taskRepository;
  private final IParameterMapper parameterMapper;
  private final IFacilityMapper facilityMapper;
  private final IJobLogService jobLogService;
  private final INotificationService notificationService;
  private final IParameterValueRepository parameterValueRepository;

  @Override
  public Page<ChecklistPartialDto> getAllChecklist(String filters, Pageable pageable) {
    log.info("[getAllChecklist] Request to get all checklists, filters: {}, pageable: {}", filters, pageable);

    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    SearchCriteria organisationSearchCriteria = (new SearchCriteria()).setField(Checklist.ORGANISATION_ID).setOp(Operator.Search.EQ.toString()).setValues(Collections.singletonList(principalUser.getOrganisationId()));
    SearchCriteria facilitySearchCriteria = null;
    Long currentFacilityId = principalUser.getCurrentFacilityId();
    if (currentFacilityId != null && !currentFacilityId.equals(Misc.ALL_FACILITY_ID)) {
      facilitySearchCriteria =
        (new SearchCriteria()).setField(Checklist.FACILITY_ID).setOp(Operator.Search.EQ.toString()).setValues(Collections.singletonList(currentFacilityId));
    }

    //TODO make specification extend the original one
    //Specification<Checklist> specification = SpecificationBuilder.createSpecification(filters);
    Specification<Checklist> specification = ChecklistSpecificationBuilder.createSpecification(filters, Arrays.asList(organisationSearchCriteria, facilitySearchCriteria));
    //When checklist is filtered on properties, only the respective
    //property it is filtered on is fetched, thereby forcing us to fetch the property again
    //and fetching is much slower. To avoid this fetch only the top level entity without
    //using the entity graph collect the ids and read the data using entity graph for these ids
    Page<Checklist> checklistPage = checklistRepository.findAll(specification, pageable);
    Set<Long> ids = checklistPage.getContent()
      .stream().map(BaseEntity::getId).collect(Collectors.toSet());
    List<Checklist> checklists = checklistRepository.readAllByIdIn(ids, pageable.getSort());

    return new PageImpl<>(checklistMapper.toPartialDto(checklists), pageable, checklistPage.getTotalElements());
  }

  @Override
  public ChecklistDto getChecklistById(Long checklistId) throws ResourceNotFoundException {
    log.info("[getChecklistById] Request to get checklist, checklistId: {}", checklistId);
    var parameters = parameterRepository.getParametersByChecklistIdAndTargetEntityType(checklistId, Type.ParameterTargetEntityType.PROCESS);
    var checklist = checklistRepository.readById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    var checklistDto = checklistMapper.toDto(checklist);
    var parameterDtos = parameterMapper.toDto(parameters);
    checklistDto.setParameters(parameterDtos);

    return checklistDto;
  }

  @Override
  public ChecklistInfoDto getChecklistInfoById(Long checklistId) throws ResourceNotFoundException {
    log.info("[getChecklistInfoById] Request to get checklist info, checklistId: {}", checklistId);
    // TODO Optimize this.
    try {
      Checklist checklist = checklistRepository.readById(checklistId)
        .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

      List<ChecklistCollaboratorView> signOffCollaborators = checklistCollaboratorMappingRepository.findAllByTypeOrderByOrderTreeAndModifiedAt(checklist.getId(), Type.Collaborator.SIGN_OFF_USER.toString());
      List<ChecklistSignOffDto> signOffUsers = signOffCollaborators.stream().map(u -> (new ChecklistSignOffDto()).setId(u.getId())
        .setEmail(u.getEmail())
        .setEmployeeId(u.getEmployeeId())
        .setFirstName(u.getFirstName())
        .setLastName(u.getLastName())
        .setState(u.getState())
        .setSignedAt(State.ChecklistCollaborator.SIGNED.name().equals(u.getState()) ? u.getModifiedAt() : null)
        .setOrderTree(u.getOrderTree())).collect(Collectors.toList());

      List<VersionDto> versionHistory = null;
      Long ancestor = Utility.isNull(checklist.getVersion()) ? null : checklist.getVersion().getAncestor();
      if (Utility.isNotNull(ancestor)) {
        List<Version> versions = versionRepository.findAllByAncestorOrderByVersionDesc(ancestor);
        if (!Utility.isEmpty(versions)) {
          List<Checklist> previousChecklists = checklistRepository.findAllById(versions.stream().map(Version::getSelf).collect(Collectors.toSet()));
          Map<Long, IdCodeHolder> map = previousChecklists.stream().filter(c -> ((c.getState() == State.Checklist.PUBLISHED) || c.getState() == State.Checklist.DEPRECATED)).collect(Collectors.toMap(Checklist::getId, c -> new IdCodeHolder(c.getId(), c.getCode(), c.getName())));
          versionHistory = versions.stream().filter(v -> map.get(v.getSelf()) != null).map(v -> {
            IdCodeHolder idCodeHolder = map.get(v.getSelf());
            return ((new VersionDto()).setId(String.valueOf(v.getSelf())).setCode(idCodeHolder.getCode()).setName(idCodeHolder.getName()).setVersionNumber(v.getVersion()).setDeprecatedAt(v.getDeprecatedAt()));
          }).collect(Collectors.toList());
        }
      }
      ChecklistInfoDto checklistInfoDto = new ChecklistInfoDto();
      checklistInfoDto.setId(String.valueOf(checklistId))
        .setName(checklist.getName())
        .setDescription(checklist.getDescription())
        .setCode(checklist.getCode())
        .setState(checklist.getState())
        .setAuthors(checklistCollaboratorService.getAllAuthors(checklist.getId()))
        .setPhase(checklist.getReviewCycle())
        .setSignOff(signOffUsers)
        .setVersions(versionHistory)
        .setRelease((new ReleaseDto().setReleaseAt(checklist.getReleasedAt()).setReleaseBy(userMapper.toUserAuditDto(checklist.getReleasedBy()))))
        .setAudit((new AuditDto()).setCreatedAt(checklist.getCreatedAt()).setCreatedBy(userMapper.toUserAuditDto(checklist.getCreatedBy()))
          .setModifiedAt(checklist.getModifiedAt()).setModifiedBy(userMapper.toUserAuditDto(checklist.getModifiedBy())));
      return checklistInfoDto;
    } catch (Exception e) {
      log.error("[getChecklistInfoById] Error fetching checklist", e);
      throw e;
    }
  }

  @Transactional
  @Override
  public ChecklistDto createChecklist(CreateChecklistRequest createChecklistRequest) throws StreemException {
    log.info("[createChecklist] Request to create a checklist, createChecklistRequest: {}", createChecklistRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Facility facility = facilityRepository.getOne(createChecklistRequest.getFacilityId());
    Organisation organisation = organisationRepository.getReferenceById(principalUser.getOrganisationId());
    FacilityUseCaseMapping facilityUseCaseMapping = facilityUseCaseMappingRepository.findByFacilityIdAndUseCaseId(facility.getId(), createChecklistRequest.getUseCaseId());
    UseCase useCase = facilityUseCaseMapping != null ? facilityUseCaseMapping.getUseCase() : null;

    if (useCase == null) {
      ValidationUtils.invalidate(createChecklistRequest.getUseCaseId(), ErrorCode.USE_CASE_NOT_FOUND);
    }

    List<Error> errorList = new ArrayList<>();

    Checklist checklist = new Checklist();
    checklist.setId(IdGenerator.getInstance().nextId());
    checklist.setName(createChecklistRequest.getName());
    checklist.setDescription(createChecklistRequest.getDescription());
    checklist.setCode(codeService.getCode(Type.EntityType.CHECKLIST, principalUser.getOrganisationId()));
    checklist.setState(State.Checklist.BEING_BUILT);
    checklist.setCreatedBy(principalUserEntity);
    checklist.setModifiedBy(principalUserEntity);
    checklist.setOrganisation(organisation);
    if (!Objects.equals(principalUser.getCurrentFacilityId(), null)) {
      checklist.addFacility(facility, principalUserEntity);
    } else {
      checklist.setGlobal(true);
    }
    checklist.setOrganisationId(organisation.getId());
    checklist.setUseCase(useCase);
    checklist.setUseCaseId(useCase.getId());


    //add primary author from user context
    setProperties(checklist, createChecklistRequest.getProperties(), principalUserEntity, errorList);
    addAuthors(checklist, createChecklistRequest.getAuthors(), principalUserEntity);
    addPrimaryAuthor(checklist, principalUserEntity);
    checklist.getStages().add(createStage(principalUserEntity, checklist));

    if (!errorList.isEmpty()) {
      throw new StreemException(ErrorMessage.COULD_NOT_CREATE_CHECKLIST, errorList);
    }

    checklist = checklistRepository.save(checklist);
    checklistAuditService.create(checklist.getId(), checklist.getCode(), principalUser);

    Version version = versionService.createNewVersion(checklist.getId(), Type.EntityType.CHECKLIST, principalUserEntity);
    checklist.setVersion(version);
    checklistRepository.save(checklist);

    return checklistMapper.toDto(checklist);
  }

  @Override
  public BasicDto archiveChecklist(Long checklistId, String reason) throws ResourceNotFoundException, StreemException {
    log.info("[archiveChecklist] Request to archive checklist, checklistId: {}, reason: {}", checklistId, reason);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (Utility.isEmpty(reason)) {
      ValidationUtils.invalidate(checklistId, ErrorCode.ARCHIVE_REASON_CANNOT_BE_EMPTY);
    }
    reason = reason.trim();

    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Checklist checklist = checklistRepository.findById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    validateIfChecklistCanBeArchived(checklistId);

    /*
      Checklist can be archived in any state by unarchived Author, Owner, Facility Admin
    */

    if (!Objects.equals(checklist.getCreatedBy().getId(), principalUser.getId())) {
      validateRolesForChecklistArchival(principalUser, true);
    }

    checklist.setArchived(true);
    checklist.setModifiedBy(principalUserEntity);
    checklist = checklistRepository.save(checklist);

    checklistAuditService.archive(checklistId, checklist.getCode(), reason, principalUser);

    BasicDto basicDto = new BasicDto();
    basicDto.setId(checklist.getIdAsString())
      .setMessage("success");
    return basicDto;
  }

  private void validateRolesForChecklistArchival(PrincipalUser principalUser, boolean archive) throws StreemException {
    boolean archivalRolesMatch = principalUser.getRoleNames()
      .stream().anyMatch(Misc.CHECKLIST_ARCHIVAL_ROLES::contains);
    if (!archivalRolesMatch) {
      if (archive) {
        ValidationUtils.invalidate(principalUser.getId(), ErrorCode.PROCESS_CAN_ONLY_BE_ARCHIVED_BY);
      } else {
        ValidationUtils.invalidate(principalUser.getId(), ErrorCode.PROCESS_CAN_ONLY_BE_UNARCHIVED_BY);
      }
    }
  }

  @Override
  public BasicDto validateChecklistArchival(Long checklistId) throws ResourceNotFoundException, StreemException {
    log.info("[validateChecklistArchival] Request to validate if checklist can be archived, checklistId: {}", checklistId);
    Checklist checklist = checklistRepository.findById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    validateIfChecklistCanBeArchived(checklistId);

    BasicDto basicDto = new BasicDto();
    basicDto.setId(checklist.getIdAsString())
      .setMessage("checklist can be archived");
    return basicDto;
  }

  @Override
  public BasicDto unarchiveChecklist(Long checklistId, String reason) throws ResourceNotFoundException, StreemException {
    log.info("[unarchiveChecklist] Request to unarchive checklist, checklistId: {}, reason: {}", checklistId, reason);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (Utility.isEmpty(reason)) {
      ValidationUtils.invalidate(checklistId, ErrorCode.UNARCHIVE_REASON_CANNOT_BE_EMPTY);
    }
    reason = reason.trim();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Checklist checklist = checklistRepository.findById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    if (!Objects.equals(checklist.getCreatedBy().getId(), principalUser.getId())) {
      validateRolesForChecklistArchival(principalUser, false);
    }

    checklist.setArchived(false);
    checklist.setModifiedBy(principalUserEntity);
    checklist = checklistRepository.save(checklist);

    checklistAuditService.unarchive(checklistId, checklist.getCode(), reason, principalUser);

    BasicDto basicDto = new BasicDto();
    basicDto.setId(checklist.getIdAsString())
      .setMessage("success");
    return basicDto;
  }

  @Override
  public BasicDto updateChecklist(Long checklistId, ChecklistUpdateRequest checklistUpdateRequest) throws ResourceNotFoundException, StreemException {
    log.info("[updateChecklist] Request to update checklist, checklistId: {}, checklistUpdateRequest: {}", checklistId, checklistUpdateRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Checklist checklist = checklistRepository.readById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    validateChecklistModificationState(checklist.getId(), checklist.getState());
    validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());

    List<Error> errorList = new ArrayList<>();
    if (null != checklistUpdateRequest.getName()) {
      checklist.setName(checklistUpdateRequest.getName());
    }
    if (null != checklistUpdateRequest.getDescription()) {
      checklist.setDescription(checklistUpdateRequest.getDescription());
    }
    setProperties(checklist, checklistUpdateRequest.getProperties(), principalUserEntity, errorList);

    Map<Long, Boolean> authors = checklist.getCollaborators().stream()
      .filter(c -> c.getPhaseType().equals(State.ChecklistCollaboratorPhaseType.BUILD))
      .collect(Collectors.toMap(a -> a.getUser().getId(), ChecklistCollaboratorMapping::isPrimary));

    Set<Long> reviewers = checklist.getCollaborators().stream()
      .filter(c -> c.getPhaseType().equals(State.ChecklistCollaboratorPhaseType.REVIEW))
      .map(a -> a.getUser().getId()).collect(Collectors.toSet());
    for (Long id : checklistUpdateRequest.getAddAuthorIds()) {
      if (authors.containsKey(id)) {
        ValidationUtils.addError(id, errorList, ErrorCode.PROCESS_AUTHOR_ALREADY_ASSIGNED);
      }
      if (reviewers.contains(id)) {
        ValidationUtils.addError(id, errorList, ErrorCode.PROCESS_REVIEWER_CANNOT_BE_AUTHOR);
      }
    }

    for (Long id : checklistUpdateRequest.getRemoveAuthorIds()) {
      if (authors.containsKey(id)) {
        boolean isPrimaryAuthor = authors.get(id);
        if (isPrimaryAuthor) {
          ValidationUtils.addError(id, errorList, ErrorCode.CANNOT_UNASSIGN_PRIMARY_AUTHOR_FROM_PROCESS);
        }
      } else {
        ValidationUtils.addError(id, errorList, ErrorCode.PROCESS_AUTHOR_NOT_ASSIGNED);
      }
    }

    if (!errorList.isEmpty()) {
      throw new StreemException(ErrorMessage.COULD_NOT_UPDATE_CHECKLIST, errorList);
    }

    for (Long id : checklistUpdateRequest.getAddAuthorIds()) {
      checklist.addAuthor(userRepository.getOne(id), checklist.getReviewCycle(), principalUserEntity);
    }

    checklist.setModifiedBy(principalUserEntity);
    checklistRepository.save(checklist);
    checklistCollaboratorMappingRepository.deleteAuthors(checklistId, checklistUpdateRequest.getRemoveAuthorIds());

    notificationService.notifyAuthors(checklistUpdateRequest.getAddAuthorIds(), checklistId, principalUser.getOrganisationId());
    BasicDto basicDto = new BasicDto();
    basicDto.setMessage("success");
    return basicDto;
  }

  @Override
  public BasicDto validateChecklist(Long checklistId) throws ResourceNotFoundException, IOException, StreemException {
    log.info("[validateChecklist] Request to validate checklist, checklistId: {}", checklistId);
    Checklist checklist = checklistRepository.findById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    List<Error> errorList = new ArrayList<>();
    Set<Stage> stages = checklist.getStages();

    List<Parameter> unmappedParameters = parameterRepository.getParametersByChecklistIdAndTargetEntityType(checklistId, Type.ParameterTargetEntityType.UNMAPPED);
    if (!Utility.isEmpty(unmappedParameters)) {
      ValidationUtils.invalidate(checklistId, ErrorCode.UNMAPPED_PARAMETERS_EXISTS);
    }

    if (stages.isEmpty()) {
      ValidationUtils.addError(checklist.getId(), errorList, ErrorCode.PROCESS_EMPTY_STAGE_VALIDATION);
    } else {
      for (Stage stage : stages) {
        if (Utility.isEmpty(stage.getName())) {
          ValidationUtils.addError(stage.getId(), errorList, ErrorCode.STAGE_NAME_CANNOT_BE_EMPTY);
        }
        validateTasks(checklistId, stage, errorList);
      }
    }

    List<Parameter> processParameterList = parameterRepository.getParametersByChecklistIdAndTargetEntityType(checklistId, Type.ParameterTargetEntityType.PROCESS);
    validateIfParameterBeingCJFMappedToTask(processParameterList, errorList);

    if (!Utility.isEmpty(errorList)) {
      ValidationUtils.invalidate("Checklist configuration incomplete", errorList);
    }

    BasicDto basicDto = new BasicDto();
    basicDto.setMessage("success");
    return basicDto;
  }

  @Override
  public Checklist findById(Long checklistId) throws ResourceNotFoundException {
    return checklistRepository.findById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
  }

  @Override
  public Checklist findByTaskId(Long taskId) throws ResourceNotFoundException {
    return checklistRepository.findByTaskId(taskId)
      .orElseThrow(() -> new ResourceNotFoundException(taskId, ErrorCode.PROCESS_BY_TASK_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
  }

  /**
   * method validates if for the given checklist id and user id
   * does collaborator mapping entry exists for collaborator types
   * author and primary author
   *
   * @param checklistId
   * @param userId
   * @throws StreemException
   */
  @Override
  public void validateIfUserIsAuthorForPrototype(Long checklistId, Long userId) throws StreemException {
    if (!checklistCollaboratorMappingRepository.isCollaboratorMappingExistsByChecklistAndUserIdAndCollaboratorType(checklistId, userId, Type.AUTHOR_TYPES)) {
      ValidationUtils.invalidate(checklistId, ErrorCode.USER_NOT_ALLOWED_TO_MODIFY_PROCESS);
    }
  }

  @Override
  public void validateChecklistModificationState(Long checklistId, State.Checklist state) throws StreemException {
    if (!State.CHECKLIST_EDIT_STATES.contains(state)) {
      ValidationUtils.invalidate(checklistId, ErrorCode.PROCESS_CANNOT_BE_MODFIFIED);
    }
  }

  @Override
  public List<ChecklistDefaultUserDto> getDefaultUsers(Long checklistId) throws StreemException {
    log.info("[getDefaultUsers] Request to get default user for checklist, checklistId: {}", checklistId);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long currentFacilityId = principalUser.getCurrentFacilityId();
    if (Objects.equals(currentFacilityId, null)) {
      ValidationUtils.invalidate(principalUser.getId(), ErrorCode.CANNOT_ASSIGN_TRAINING_USER_IN_ALL_FACILITY);
    }
    var checklistDefaultUserIds = checklistDefaultUsersRepository.findUserIdsByChecklistIdAndFacilityId(checklistId, currentFacilityId);
    if (!Utility.isEmpty(checklistDefaultUserIds)) {
      var users = userRepository.findAllById(checklistDefaultUserIds);
      return users.stream().map(defaultUserMapper::toDto)
        .map(checklistDefaultUserDto -> {
          var taskIds = checklistDefaultUsersRepository.findTaskIdsByChecklistIdAndUserIdAndFacilityId(checklistId, Long.parseLong(checklistDefaultUserDto.getId()), currentFacilityId);
          checklistDefaultUserDto.setTaskIds(taskIds);
          return checklistDefaultUserDto;
        })
        .sorted(Comparator.comparing(ChecklistDefaultUserDto::getFirstName)
          .thenComparing(ChecklistDefaultUserDto::getLastName))
        .toList();
    }
    return new ArrayList<>();
  }

  @Override
  public List<TaskAssigneeView> getTaskAssignmentDetails(Long checklistId, Set<Long> taskIds) {
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long currentFacilityId = principalUser.getCurrentFacilityId();
    return taskRepository.findByTaskIdIn(checklistId, taskIds, taskIds.size(), currentFacilityId);
  }

  @Override
  public List<FacilityDto> getFacilityChecklistMapping(Long checklistId) throws ResourceNotFoundException {
    log.info("[getFacilityChecklistMapping] Request to get checklist facility mapping, checklistId: {}", checklistId);
    Checklist checklist = checklistRepository.findById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    return facilityMapper.toDto(checklist.getFacilities().stream().map(ChecklistFacilityMapping::getFacility).collect(Collectors.toList()));
  }

  @Override
  public BasicDto bulkAssignmentFacilityIds(Long checklistId, ChecklistFacilityAssignmentRequest checklistFacilityAssignmentRequest) throws ResourceNotFoundException {
    var checklist = findById(checklistId);
    var assignedIds = checklistFacilityAssignmentRequest.getAssignedFacilityIds();
    var unassignedIds = checklistFacilityAssignmentRequest.getUnassignedFacilityIds();

    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getOne(principalUser.getId());
    Set<Facility> assignedFacilities = Set.copyOf(facilityRepository.findAllById(assignedIds));

    checklist.addFacility(assignedFacilities, principalUserEntity);
    checklistRepository.save(checklist);

    checklistRepository.removeChecklistFacilityMapping(checklistId, unassignedIds);
    var basicDto = new BasicDto();
    basicDto.setMessage("success");
    return basicDto;
  }

  @Override
  public BasicDto bulkAssignDefaultUsers(Long checklistId, ChecklistTaskAssignmentRequest checklistTaskAssignmentRequest, boolean notify) throws ResourceNotFoundException, StreemException {
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long currentFacilityId = principalUser.getCurrentFacilityId();
    if (Objects.equals(currentFacilityId, null)) {
      ValidationUtils.invalidate(principalUser.getId(), ErrorCode.CANNOT_ASSIGN_TRAINING_USER_IN_ALL_FACILITY);
    }
    Facility facility = facilityRepository.getOne(currentFacilityId);
    var checklist = findById(checklistId);
    var assignedTasks = checklistTaskAssignmentRequest.getTaskIds();
    var assignedIds = checklistTaskAssignmentRequest.getAssignedUserIds();
    var unassignedIds = checklistTaskAssignmentRequest.getUnassignedUserIds();

    List<ChecklistDefaultUsers> defaultUsersList = checklistDefaultUsersRepository.findByChecklistId(checklistId);
    // taskUserMapping is a hashmap which stores a set of userIds assigned to a task Map<taskId, Set<userId>> where taskId and userId are of type long
    Map<Long, Set<Long>> taskUserMapping = defaultUsersList.stream()
      .collect(Collectors.groupingBy(du -> du.getTask().getId(), Collectors.mapping(du -> du.getUser().getId(), Collectors.toSet())));

    List<User> assignedUsers = userRepository.findAllById(assignedIds);
    assignedTasks.stream()
      .map(taskRepository::findById)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .forEach(task -> {
        var checklistDefaultUsers = assignedUsers.stream()
          .filter(user -> !isUserMappedToTask(taskUserMapping, task, user))
          .map(user -> new ChecklistDefaultUsers(checklist, checklistId, user, user.getId(), task, task.getId(), facility, facility.getId())).collect(Collectors.toSet());
        checklistDefaultUsersRepository.saveAll(checklistDefaultUsers);
      });
    checklistDefaultUsersRepository.unassignUsersByChecklistIdAndTaskIds(unassignedIds, checklistId, assignedTasks);
    var basicDto = new BasicDto();
    basicDto.setMessage("success");
    return basicDto;
  }

  @Transactional
  @Override
  public List<ParameterInfoDto> configureProcessParameters(Long checklistId, MapJobParameterRequest mapJobParameterRequest) throws ResourceNotFoundException, StreemException {
    log.info("[configureProcessParameters] Request to configure Job Parameters, checklistId: {}, configureProcessParameters: {}", checklistId, mapJobParameterRequest);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Checklist checklist = checklistRepository.findById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));

    validateChecklistModificationState(checklist.getId(), checklist.getState());
    validateIfUserIsAuthorForPrototype(checklist.getId(), principalUser.getId());

    parameterRepository.updateParametersTargetEntityType(checklistId, Type.ParameterTargetEntityType.PROCESS, Type.ParameterTargetEntityType.UNMAPPED);

    var parameterIdsToMap = mapJobParameterRequest.getMappedParameters().keySet();
    var unmappedParametersCount = parameterRepository.getParametersCountByChecklistIdAndParameterIdInAndTargetEntityType(checklistId, parameterIdsToMap, Type.ParameterTargetEntityType.UNMAPPED);
    if (unmappedParametersCount != parameterIdsToMap.size()) {
      ValidationUtils.invalidate(checklistId, ErrorCode.ERROR_MAPPING_PARAMETER);
    }
    validateParameterVerification(parameterIdsToMap);

    parameterRepository.updateParametersTargetEntityType(parameterIdsToMap, Type.ParameterTargetEntityType.PROCESS);

    // TODO update batch
    for (Map.Entry<Long, Integer> parameterOrder : mapJobParameterRequest.getMappedParameters().entrySet()) {
      var parameterId = parameterOrder.getKey();
      var order = parameterOrder.getValue();
      parameterRepository.reorderParameter(parameterId, order, principalUser.getId(), DateTimeUtils.now());
    }

    return parameterMapper.toBasicDto(parameterRepository.getParametersByChecklistIdAndTargetEntityType(checklistId, Type.ParameterTargetEntityType.PROCESS));
  }

  @Override
  public BasicDto reconfigureJobLogColumns(Long checklistId) throws ResourceNotFoundException {
    Checklist checklist = checklistRepository.findById(checklistId)
      .orElseThrow(() -> new ResourceNotFoundException(checklistId, ErrorCode.PROCESS_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
    List<JobLogColumn> jobLogColumns = jobLogService.getJobLogColumnForChecklist(checklist);
    JsonNode jsonNode = JsonUtils.valueToNode(jobLogColumns);
    checklist.setJobLogColumns(jsonNode);
    checklistRepository.save(checklist);

    var basicDto = new BasicDto();
    basicDto.setMessage("success");
    return basicDto;
  }

  @Override
  public Page<ChecklistDto> getAllByResource(String objectTypeId, String filters, Pageable pageable) {
    log.info("[getAllByResource] Request to find all checklists by resource, objectTypeId: {}, filters: {}, pageable: {}", objectTypeId, filters, pageable);
    Set<Long> checklistIds = parameterRepository.getChecklistIdsByTargetEntityTypeAndObjectTypeInData(Type.ParameterTargetEntityType.PROCESS.name(), objectTypeId);
    if (!Utility.isEmpty(checklistIds)) {

      PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      SearchCriteria organisationSearchCriteria = (new SearchCriteria()).setField(Checklist.ORGANISATION_ID).setOp(Operator.Search.EQ.toString()).setValues(Collections.singletonList(principalUser.getOrganisationId()));
      SearchCriteria facilitySearchCriteria = null;
      Long currentFacilityId = principalUser.getCurrentFacilityId();
      if (currentFacilityId != null && !currentFacilityId.equals(Misc.ALL_FACILITY_ID)) {
        facilitySearchCriteria =
          (new SearchCriteria()).setField(Checklist.FACILITY_ID).setOp(Operator.Search.EQ.toString()).setValues(Collections.singletonList(currentFacilityId));
      }
      SearchCriteria checklistIdCriteria = (new SearchCriteria()).setField(Checklist.ID).setOp(Operator.Search.ANY.toString()).setValues(new ArrayList<>(checklistIds));

      Specification<Checklist> specification = ChecklistSpecificationBuilder.createSpecification(filters, Arrays.asList(organisationSearchCriteria, facilitySearchCriteria, checklistIdCriteria));
      Page<Checklist> checklistPage = checklistRepository.findAll(specification, pageable);
      Set<Long> ids = checklistPage.getContent()
        .stream().map(BaseEntity::getId).collect(Collectors.toSet());
      List<Checklist> checklists = checklistRepository.readAllByIdIn(ids, pageable.getSort());

      return new PageImpl<>(checklistMapper.toDto(checklists), pageable, checklistPage.getTotalElements());

    } else {
      return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }
  }

  private boolean isUserMappedToTask(Map<Long, Set<Long>> taskUserMapping, Task task, User user) {
    var userIdSet = taskUserMapping.get(task.getId());
    if (Utility.isEmpty(userIdSet))
      return false;
    return userIdSet.contains(user.getId());
  }

  private void setProperties(Checklist checklist, List<PropertyRequest> propertyRequestList, User user, List<Error> errorList) {

    //Get only properties of checklist that are not archived
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long currentFacilityId = principalUser.getCurrentFacilityId() == null ? Misc.ALL_FACILITY_ID : principalUser.getCurrentFacilityId();

    Map<Long, PropertyRequest> propertyValuesMap = propertyRequestList.stream().collect(Collectors.toMap(PropertyRequest::getId, Function.identity()));
    Map<Long, ChecklistPropertyValue> checklistProperties = checklist.getChecklistPropertyValues().stream()
      .collect(Collectors.toMap(p -> p.getFacilityUseCasePropertyMapping().getProperty().getId(), Function.identity()));
    var facilityUseCasePropertyMappings = propertyService.getPropertiesByFacilityIdAndUseCaseIdAndPropertyType(currentFacilityId, checklist.getUseCaseId(), Type.PropertyType.CHECKLIST);
    for (FacilityUseCasePropertyMapping facilityUseCasePropertyMapping : facilityUseCasePropertyMappings) {
      Property property = facilityUseCasePropertyMapping.getProperty();
      if (facilityUseCasePropertyMapping.isMandatory() && (!propertyValuesMap.containsKey(property.getId())
        || null == propertyValuesMap.get(property.getId())
        || Utility.isEmpty(propertyValuesMap.get(property.getId()).getValue()))) {
        ValidationUtils.addError(property.getId(), errorList, ErrorCode.MANDATORY_PROCESS_PROPERTY_NOT_SET);
      } else {
        if (propertyValuesMap.containsKey(property.getId())) {
          PropertyRequest propertyRequest = propertyValuesMap.get(property.getId());
          if (checklistProperties.containsKey(property.getId())) {
            ChecklistPropertyValue checklistPropertyValue = checklistProperties.get(property.getId());
            checklistPropertyValue.setModifiedBy(user);
            checklistPropertyValue.setValue(propertyRequest.getValue());
          } else {
            checklist.addProperty(facilityUseCasePropertyMapping, propertyRequest.getValue(), user);
          }
        } else {
          checklist.addProperty(facilityUseCasePropertyMapping, null, user);
        }
      }
    }
  }

  private Stage createStage(User principalUserEntity, Checklist checklist) {
    Task task = createTask(principalUserEntity);
    Stage stage = new Stage();
    stage.setModifiedBy(principalUserEntity);
    stage.setCreatedBy(principalUserEntity);
    stage.setChecklist(checklist);
    stage.setName("");
    stage.getTasks().add(task);
    stage.setOrderTree(1);
    task.setStage(stage);
    return stage;
  }

  private Task createTask(User principalUserEntity) {
    Task task = new Task();
    task.setName("");
    task.setModifiedBy(principalUserEntity);
    task.setCreatedBy(principalUserEntity);
    task.setOrderTree(1);
    return task;
  }

  private void addPrimaryAuthor(Checklist checklist, User user) {
    checklist.addPrimaryAuthor(user, checklist.getReviewCycle(), user);
  }

  private void addAuthors(Checklist checklist, Set<Long> authors, User user) {
    for (Long authorId : authors) {
      checklist.addAuthor(userRepository.getOne(authorId), checklist.getReviewCycle(), user);
    }
  }

  private void validateTasks(Long checklistId, Stage stage, List<Error> errorList) throws IOException, StreemException {
    Set<Task> tasks = stage.getTasks();
    if (tasks.isEmpty()) {
      ValidationUtils.addError(stage.getId(), errorList, ErrorCode.PROCESS_EMPTY_TASK_VALIDATION);
    } else {
      for (Task task : tasks) {
        if (Utility.isEmpty(task.getName())) {
          ValidationUtils.addError(task.getId(), errorList, ErrorCode.TASK_NAME_CANNOT_BE_EMPTY);
        }
        if (!Utility.isEmpty(task.getAutomations())) {
          validateTaskAutomation(task.getId(), errorList, task.getAutomations());
        }
        validateParameters(checklistId, task, errorList);
      }

    }
  }

  private void validateParameters(Long checklistId, Task task, List<Error> errorList) throws IOException, StreemException {
    boolean hasExecutableParameters = false;
    Map<Long, Parameter> calculationParametersMap = new HashMap<>();
    for (Parameter parameter : task.getParameters()) {
      Type.Parameter parameterType = parameter.getType();
      if (!Type.NON_EXECUTABLE_PARAMETER_TYPES.contains(parameterType)) {
        hasExecutableParameters = true;
      }
      if (Utility.isEmpty(parameter.getLabel())) {
        ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.PARAMETER_LABEL_CANNOT_BE_EMPTY);
      }
      if (parameter.isAutoInitialized() && Utility.isEmpty(parameter.getAutoInitialize())) {
        ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.PARAMETER_AUTO_INITIALIZE_INVALID_DATE);
      }
      switch (parameterType) {
        case CALCULATION -> {
          calculationParametersMap.put(parameter.getId(), parameter);
          validateCalculationParameter(parameter, errorList);
        }
        case CHECKLIST -> validateChecklistParameter(parameter, errorList);
        case NUMBER -> validateNumberParameter(parameter, errorList);
        case INSTRUCTION -> validateInstructionParameter(parameter, errorList);
        case MATERIAL -> validateMaterialParameter(parameter, errorList);
        case MULTISELECT -> validateMultiSelectParameter(parameter, errorList);
        case SHOULD_BE -> validateShouldBeParameter(parameter, errorList);
        case SINGLE_SELECT -> validateSingleSelectParameter(parameter, errorList);
        case YES_NO -> validateYesNoParameter(parameter, errorList);
      }
    }
    if (!hasExecutableParameters) {
      ValidationUtils.addError(task.getId(), errorList, ErrorCode.TASK_SHOULD_HAVE_ATLEAST_ONE_EXECUTABLE_PARAMETER);
    }
    checkForCyclicDependencyOnCalculationParameter(calculationParametersMap, checklistId);
  }

  private void validateTaskAutomation(Long taskId, List<Error> errorList, Set<TaskAutomationMapping> taskAutomationMappings) {
    List<Long> referencedParameterIds = new ArrayList<>();
    for (TaskAutomationMapping taskAutomationMapping : taskAutomationMappings) {
      JsonNode actionDetails = taskAutomationMapping.getAutomation().getActionDetails();
      if (!Utility.isEmpty(actionDetails.get("referencedParameterId"))) {
        referencedParameterIds.add(Long.valueOf(actionDetails.asText()));
      }
      if (!Type.ACTION_TYPES_WITH_NO_REFERENCED_PARAMETER_ID.contains(taskAutomationMapping.getAutomation().getActionType())) {
        referencedParameterIds.add(Long.valueOf(actionDetails.get("parameterId").asText()));
      }
    }

    List<Parameter> archivedParameters = parameterRepository.getArchivedParametersByReferencedParameterIds(referencedParameterIds);
    if (!Utility.isEmpty(archivedParameters)) {
      ValidationUtils.addError(taskId, errorList, ErrorCode.TASK_AUTOMATION_INVALID_MAPPED_PARAMETERS);
    }
  }

  private void validateMaterialParameter(Parameter parameter, List<Error> errorList) throws IOException {
    List<MaterialParameter> materialParameters = JsonUtils.jsonToCollectionType(parameter.getData().toString(), List.class, MaterialParameter.class);

    if (parameter.isMandatory()) {
      ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.MATERIAL_PARAMETER_CANNOT_BE_MANDATORY);
    }

    if (Utility.isEmpty(materialParameters)) {
      ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.MATERIAL_PARAMETER_LIST_CANNOT_BE_EMPTY);
    } else {
      for (MaterialParameter materialParameter : materialParameters) {
        if (Utility.isEmpty(materialParameter.getName())) {
          ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.MATERIAL_PARAMETER_NAME_CANNOT_BE_EMPTY);
        }
      }
    }
  }

  private void validateYesNoParameter(Parameter parameter, List<Error> errorList) throws IOException {
    List<YesNoParameter> yesNoParameters = JsonUtils.jsonToCollectionType(parameter.getData().toString(), List.class, YesNoParameter.class);

    if (Utility.isEmpty(yesNoParameters) || yesNoParameters.size() != 2) {
      ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.YES_NO_PARAMETER_SHOULD_HAVE_EXACTLY_TWO_OPTIONS);
    } else {
      for (YesNoParameter yesNoParameter : yesNoParameters) {
        if (Utility.isEmpty(yesNoParameter.getName())) {
          ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.YES_NO_PARAMETER_OPTIONS_NAME_CANNOT_BE_EMPTY);
        }
      }
      if (Utility.isEmpty(parameter.getLabel())) {
        ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.YES_NO_PARAMETER_TITLE_CANNOT_BE_EMPTY);
      }
    }
  }

  private void validateMultiSelectParameter(Parameter parameter, List<Error> errorList) throws IOException {
    List<MultiSelectParameter> multiSelectParameters = JsonUtils.jsonToCollectionType(parameter.getData().toString(), List.class, MultiSelectParameter.class);
    if (Utility.isEmpty(multiSelectParameters)) {
      ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.MULTISELECT_PARAMETER_OPTIONS_CANNOT_BE_EMPTY);
    } else {
      for (MultiSelectParameter multiSelectParameter : multiSelectParameters) {
        if (Utility.isEmpty(multiSelectParameter.getName())) {
          ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.MULTISELECT_PARAMETER_OPTIONS_NAME_CANNOT_BE_EMPTY);
        }
      }
    }
  }

  private void validateSingleSelectParameter(Parameter parameter, List<Error> errorList) throws IOException {
    List<SingleSelectParameter> singleSelectParameters = JsonUtils.jsonToCollectionType(parameter.getData().toString(), List.class, SingleSelectParameter.class);
    if (Utility.isEmpty(singleSelectParameters)) {
      ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.SINGLE_SELECT_PARAMETER_OPTIONS_CANNOT_BE_EMPTY);
    } else {
      for (SingleSelectParameter singleSelectParameter : singleSelectParameters) {
        if (Utility.isEmpty(singleSelectParameter.getName())) {
          ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.SINGLE_SELECT_PARAMETER_OPTIONS_NAME_CANNOT_BE_EMPTY);
        }
      }
    }
  }

  private void validateChecklistParameter(Parameter parameter, List<Error> errorList) throws IOException {
    List<ChecklistParameter> checklistParameters = JsonUtils.jsonToCollectionType(parameter.getData().toString(), List.class, ChecklistParameter.class);
    if (Utility.isEmpty(checklistParameters)) {
      ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.PROCESS_PARAMETER_OPTIONS_CANNOT_BE_EMPTY);
    } else {
      for (ChecklistParameter checklistParameter : checklistParameters) {
        if (Utility.isEmpty(checklistParameter.getName())) {
          ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.PROCESS_PARAMETER_OPTIONS_NAME_CANNOT_BE_EMPTY);
        }
      }
    }
  }

  private void validateShouldBeParameter(Parameter parameter, List<Error> errorList) throws JsonProcessingException {
    ShouldBeParameter shouldBeParameter = JsonUtils.readValue(parameter.getData().toString(), ShouldBeParameter.class);
    if (Utility.isEmpty(shouldBeParameter.getUom())) {
      ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.SHOULD_BE_PARAMETER_UOM_CANNOT_BE_EMPTY);
    }
    if (Utility.isEmpty(shouldBeParameter.getOperator())) {
      ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.SHOULD_BE_PARAMETER_OPERATOR_CANNOT_BE_EMPTY);
    } else {
      Operator.Parameter operator = Operator.Parameter.valueOf(shouldBeParameter.getOperator());
      switch (operator) {
        case BETWEEN:
          //TODO possibly have different error codes for lower and upper value
          if (Utility.isEmpty(shouldBeParameter.getLowerValue()) || !Utility.isNumeric(shouldBeParameter.getLowerValue())) {
            ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.SHOULD_BE_PARAMETER_VALUE_INVALID);
          } else if (Utility.isEmpty(shouldBeParameter.getUpperValue()) || !Utility.isNumeric(shouldBeParameter.getUpperValue())) {
            ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.SHOULD_BE_PARAMETER_VALUE_INVALID);
          } else {
            double lowerValue = Double.parseDouble(shouldBeParameter.getLowerValue());
            double upperValue = Double.parseDouble(shouldBeParameter.getUpperValue());

            if (lowerValue > upperValue) {
              ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.SHOULD_BE_PARAMETER_LOWER_VALUE_CANNOT_BE_MORE_THAN_UPPER_VALUE);
            }
          }
          break;
        case EQUAL_TO:
        case LESS_THAN:
        case LESS_THAN_EQUAL_TO:
        case MORE_THAN:
        case MORE_THAN_EQUAL_TO:
          if (Utility.isEmpty(shouldBeParameter.getValue()) || !Utility.isNumeric(shouldBeParameter.getValue())) {
            ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.SHOULD_BE_PARAMETER_VALUE_INVALID);
          }
      }
    }
  }

  private void validateInstructionParameter(Parameter parameter, List<Error> errorList) throws JsonProcessingException {
    InstructionParameter instructionParameter = JsonUtils.readValue(parameter.getData().toString(), InstructionParameter.class);
    if (parameter.isMandatory()) {
      ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.INSTRUCTION_PARAMETER_CANNOT_BE_MANDATORY);
    }
    if (Utility.isEmpty(instructionParameter.getText())) {
      ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.INSTRUCTION_PARAMETER_TEXT_CANNOT_BE_EMPTY);
    }
  }

  private void validateNumberParameter(Parameter parameter, List<Error> errorList) throws IOException {
    ParameterRelationValidationDto parameterRelationValidationDto = JsonUtils.readValue(parameter.getValidations().toString(), ParameterRelationValidationDto.class);

    Set<Long> parameterIds = new HashSet<>();
    if (!Utility.isEmpty(parameterRelationValidationDto.getResourceParameterValidations())) {
      for (ResourceParameterPropertyValidationDto resourceParameterPropertyValidationDto : parameterRelationValidationDto.getResourceParameterValidations()) {
        parameterIds.add(Long.valueOf(resourceParameterPropertyValidationDto.getParameterId()));
      }
    }
    if (!parameterIds.isEmpty()) {
      int count = parameterRepository.getEnabledParametersCountByTypeAndIdIn(parameterIds, Type.ALLOWED_PARAMETER_TYPES_NUMBER_PARAMETER_VALIDATION);
      if (count != parameterIds.size()) {
        ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.PARAMETER_VALIDATIONS_INCONSISTENT_DATA);
      }
    }
  }

  private void validateIfChecklistCanBeArchived(Long checklistId) throws StreemException {
    // If the checklist contains active jobs, which are not completed
    // it cannot be archived
    if (jobRepository.findByChecklistIdWhereStateNotIn(checklistId, State.JOB_COMPLETED_STATES)) {
      ValidationUtils.invalidate(checklistId, ErrorCode.CANNOT_ARCHIVE_PROCESS_WITH_ACTIVE_JOBS);
    }
  }

  void checkForCyclicDependencyOnCalculationParameter(Map<Long, Parameter> calculationParametersMap, Long checklistId) throws JsonProcessingException, StreemException {
    // map to create incremental vertex number for calculation parameters
    Map<Long, Integer> parameterIdVertexNumberMap = new HashMap<>();
    List<List<Integer>> directedGraph = new ArrayList<>();

    for (Parameter parameter : calculationParametersMap.values()) {
      CalculationParameter calculationParameter = JsonUtils.readValue(parameter.getData().toString(), CalculationParameter.class);
      Set<Long> parameterIds = calculationParameter.getVariables().values().stream().map(cav -> Long.valueOf(cav.getParameterId()))
        .collect(Collectors.toSet());
      int vertexIndex;
      // same calculation parameter can be in the graph many times
      // so if exists in the map already use existing vertex number
      // or else get the next incremental vertex
      if (parameterIdVertexNumberMap.containsKey(parameter.getId())) {
        vertexIndex = parameterIdVertexNumberMap.get(parameter.getId());
      } else {
        directedGraph.add(new ArrayList<>());
        vertexIndex = directedGraph.size() - 1; // -1 because the vertex needs to starts from 0
        parameterIdVertexNumberMap.put(parameter.getId(), vertexIndex);
      }

      for (Long parameterId : parameterIds) {
        // calculation parameters can have other dependent parameters, eg: number, parameter
        // exclude such parameters since we only need to detect cycle for calculation parameters
        if (calculationParametersMap.containsKey(parameterId)) {
          if (parameterIdVertexNumberMap.containsKey(parameterId)) {
            directedGraph.get(vertexIndex).add(parameterIdVertexNumberMap.get(parameterId));
          } else {
            directedGraph.add(new ArrayList<>());
            int variableParameterVertexIndex = directedGraph.size() - 1;
            parameterIdVertexNumberMap.put(parameterId, variableParameterVertexIndex);
            directedGraph.get(vertexIndex).add(variableParameterVertexIndex);
          }
        }
      }
    }

    boolean hasCycle = CycleDetectionUtil.isCyclic(directedGraph, directedGraph.size());
    if (hasCycle) {
      ValidationUtils.invalidate(checklistId, ErrorCode.DETECTED_A_CYCLE_IN_CALCULATION_PARAMETER);
    }
  }

  // TODO: optimize
  private void validateCalculationParameter(Parameter parameter, List<Error> errorList) throws JsonProcessingException {
    CalculationParameter calculationParameter = JsonUtils.readValue(parameter.getData().toString(), CalculationParameter.class);
    List<Long> calculationParameterVariableIds = calculationParameter.getVariables().values().stream()
      .map(cav -> Long.valueOf(cav.getParameterId())).toList();
    boolean isParameterArchived = parameterRepository.getArchivedParametersByReferencedParameterIds(calculationParameterVariableIds).size() > 0;
    if (isParameterArchived) {
      ValidationUtils.addError(parameter.getId(), errorList, ErrorCode.CALCULATION_PARAMETER_VARIABLE_CONTAINS_ARCHIVED_PARAMETER);
    }
  }

  private void validateParameterVerification(Set<Long> parameterIdsToMap) throws StreemException {
    List<Parameter> parameters = parameterRepository.findAllById(parameterIdsToMap);
    if (!Utility.isEmpty(parameters)) {
      for (Parameter parameter : parameters) {
        if (!parameter.getVerificationType().equals(Type.VerificationType.NONE)) {
          ValidationUtils.invalidate(parameter.getId(), ErrorCode.PARAMETER_CANNOT_BE_ASSIGNED_FOR_CREATE_JOB_FORM);
        }
      }
    }
  }

  private void validateIfParameterBeingCJFMappedToTask(List<Parameter> parameterList, List<Error> errorList) throws JsonProcessingException {
    for (Parameter processParameter : parameterList) {
      if (Objects.requireNonNull(processParameter.getType()) == Type.Parameter.CALCULATION) {
        CalculationParameter calculationParameter = JsonUtils.readValue(processParameter.getData().toString(), CalculationParameter.class);
        Set<Long> parameterIds = calculationParameter.getVariables().values().stream()
          .map(cav -> Long.valueOf(cav.getParameterId())).collect(Collectors.toSet());
        List<Parameter> parameters = parameterRepository.findAllById(parameterIds).stream()
          .filter(parameter -> parameter.getTargetEntityType() == Type.ParameterTargetEntityType.TASK)
          .toList();
        if (!Utility.isEmpty(parameters)) {
          ValidationUtils.addError(processParameter.getId(), errorList, ErrorCode.CJF_PARAMETER_CANNOT_BE_AUTOINITIALIAZED_BY_TASK_PARAMETER);
        }
      } else {
        if (processParameter.isAutoInitialized()) {
          AutoInitializeDto autoInitializeDto = JsonUtils.readValue(processParameter.getAutoInitialize().toString(), AutoInitializeDto.class);
          Parameter autoInitializingParameter = parameterRepository.getReferenceById(Long.valueOf(autoInitializeDto.getParameterId()));
          if (processParameter.getTargetEntityType() == Type.ParameterTargetEntityType.PROCESS && autoInitializingParameter.getTargetEntityType() == Type.ParameterTargetEntityType.TASK) {
            ValidationUtils.addError(processParameter.getId(), errorList, ErrorCode.CJF_PARAMETER_CANNOT_BE_AUTOINITIALIAZED_BY_TASK_PARAMETER);
          }
        }
      }
    }
  }
}

