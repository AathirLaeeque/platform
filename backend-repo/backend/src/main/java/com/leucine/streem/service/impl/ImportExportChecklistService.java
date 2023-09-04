package com.leucine.streem.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.leucine.commons.id.IdGenerator;
import com.leucine.streem.constant.State;
import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.ChecklistDto;
import com.leucine.streem.dto.ParameterDto;
import com.leucine.streem.dto.mapper.IChecklistMapper;
import com.leucine.streem.dto.mapper.IParameterMapper;
import com.leucine.streem.dto.mapper.importmapper.IImportChecklistMapper;
import com.leucine.streem.dto.request.*;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.dto.response.MediaUploadResponse;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.*;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.repository.*;
import com.leucine.streem.service.*;
import com.leucine.streem.util.CustomMultipartFile;
import com.leucine.streem.util.Utility;
import com.leucine.streem.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class ImportExportChecklistService implements IimportExportChecklistService {
  private final IChecklistRepository checklistRepository;
  private final IStageRepository stageRepository;
  private final ITaskRepository taskRepository;
  private final IChecklistMapper checklistMapper;
  private final IFacilityRepository facilityRepository;
  private final IOrganisationRepository organisationRepository;
  private final IFacilityUseCaseMappingRepository facilityUseCaseMappingRepository;
  private final IUserRepository userRepository;
  private final IParameterService parameterService;
  private final ITaskService taskService;
  private final IMediaService mediaService;
  private final IImportChecklistMapper iImportChecklistMapper;
  private final ObjectMapper objectMapper;
  private final ICodeService codeService;
  private final IChecklistService checklistService;
  private final IParameterRepository parameterRepository;
  private final IParameterMapper parameterMapper;
  private final IVersionService versionService;

  public List<ImportChecklistRequest> exportChecklists(List<Long> checklistIds) throws ResourceNotFoundException {
    log.info("[exportChecklists] Request to export checklists checklistIds: {}", checklistIds);
    List<ChecklistDto> checklists = new ArrayList<>();
    if (Utility.isEmpty(checklistIds)) {
      throw new IllegalArgumentException("checklistIds cannot be null or empty");
    }
      List<Checklist> optionalChecklists = checklistRepository.findAllById(checklistIds);
      for(Checklist checklist : optionalChecklists) {
        if (!Utility.isEmpty(checklist) && checklist.getState().equals(State.Checklist.PUBLISHED)) {
          ChecklistDto checklistDto = checklistMapper.toDto(checklist);
          var parameters = parameterRepository.getParametersByChecklistIdAndTargetEntityType(checklist.getId(), Type.ParameterTargetEntityType.PROCESS);
          var parameterDtos = parameterMapper.toDto(parameters);
          checklistDto.setParameters(parameterDtos);
          checklists.add(checklistDto);
        }
      }
    return iImportChecklistMapper.toDto(checklists);
  }

  public BasicDto importChecklists(MultipartFile file) throws StreemException, ResourceNotFoundException, IOException {
    log.info("[importChecklists] Request to import checklists fileName: {}", file.getName());
    List<ImportChecklistRequest> importChecklistRequests = objectMapper.readValue(file.getInputStream(), objectMapper.getTypeFactory().constructCollectionType(List.class, ImportChecklistRequest.class));
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User principalUserEntity = userRepository.getReferenceById(principalUser.getId());

    for (ImportChecklistRequest importCheckListRequest : importChecklistRequests) {
      Checklist optionalChecklist = checklistRepository.getReferenceById(Long.valueOf(importCheckListRequest.getId()));
      if (!Utility.isEmpty(optionalChecklist)) {
        importCheckListRequest = updateRequestWithNewIds(importCheckListRequest);
      }
      // TODO: we need to check from jaas why currentFacility id is null for global portal, for now fixing this issue by hard coding global portal id as -1"
      Long currentFacilityId;
      if (Utility.isEmpty(principalUser.getCurrentFacilityId())) {
        currentFacilityId = -1L;
      } else {
        currentFacilityId = principalUser.getCurrentFacilityId();
      }

      Facility facility = facilityRepository.getReferenceById(currentFacilityId);
      Organisation organisation = organisationRepository.getReferenceById(principalUser.getOrganisationId());
      FacilityUseCaseMapping facilityUseCaseMapping = facilityUseCaseMappingRepository.findByFacilityIdAndUseCaseId(facility.getId(), importCheckListRequest.getUseCaseId());
      UseCase useCase = !Utility.isEmpty(facilityUseCaseMapping) ? facilityUseCaseMapping.getUseCase() : null;
      if (Utility.isEmpty(useCase)) {
        ValidationUtils.invalidate(importCheckListRequest.getUseCaseId(), ErrorCode.USE_CASE_NOT_FOUND);
      }

      Checklist checklist = createChecklist(principalUser, principalUserEntity, importCheckListRequest, facility, organisation, useCase, currentFacilityId);
      List<Stage> stages = createStages(principalUserEntity, importCheckListRequest.getStageRequests(), checklist);
      createTasks(principalUserEntity, importCheckListRequest.getStageRequests(), stages);

      List<ParameterDto> parameterDtos = new ArrayList<>();
      if (!Utility.isEmpty(importCheckListRequest.getParameterRequests())) {
        for (ImportParameterRequest parameterCreateRequest : importCheckListRequest.getParameterRequests()) {
          ParameterDto parameterDto = parameterService.createParameter(Long.valueOf(importCheckListRequest.getId()), parameterCreateRequest);
          parameterDtos.add(parameterDto);
        }
        MapJobParameterRequest mapJobParameterRequest = new MapJobParameterRequest();
        Map<Long, Integer> mappedParameters = new HashMap<>();
        for (ParameterDto parameterDto : parameterDtos) {
          mappedParameters.put(Long.valueOf(parameterDto.getId()), parameterDto.getOrderTree());
        }
        mapJobParameterRequest.setMappedParameters(mappedParameters);
        checklistService.configureProcessParameters(checklist.getId(), mapJobParameterRequest);
      }


      for (ImportStageRequest importStageRequest : importCheckListRequest.getStageRequests()) {
        for (ImportTaskRequest taskRequest : importStageRequest.getTaskRequests()) {
          for (ImportParameterRequest parameterCreateRequest : taskRequest.getParameterRequests()) {
            if (parameterCreateRequest.getType().equals(Type.Parameter.MATERIAL)) {
              handleParameterMedias(parameterCreateRequest);
            }

            parameterService.addParameterToTask(
              Long.valueOf(importCheckListRequest.getId()),
              Long.valueOf(importStageRequest.getId()),
              Long.valueOf(taskRequest.getId()),
              parameterCreateRequest
            );
          }

          for (ImportMediaRequest mediaRequest : taskRequest.getMediaRequests()) {
            MultipartFile multipartFile = getMediaAsMultipartFile(mediaRequest);
            MediaUploadResponse mediaUploadResponse = mediaService.save(mediaRequest, multipartFile);
            mediaRequest.setLink(mediaUploadResponse.getLink());
            mediaRequest.setMediaId(Long.valueOf(mediaUploadResponse.getMediaId()));
            taskService.addMedia(Long.valueOf(taskRequest.getId()), mediaRequest);
          }
          taskService.addAutomations(Long.valueOf(taskRequest.getId()), taskRequest.getAutomationRequests());
        }
      }
    }
    var basicDto = new BasicDto();
    basicDto.setMessage("success");
    return basicDto;
  }

  private void handleParameterMedias(ParameterCreateRequest parameterCreateRequest) {
    JsonNode data = parameterCreateRequest.getData();
    for (int i = 0; i < data.size(); i++) {
      Map currMedia = objectMapper.convertValue(data.get(i), Map.class);
      String link = (String) currMedia.get("link");
      String filename = (String) currMedia.get("filename");
      String name = (String) currMedia.get("name");

      ImportMediaRequest mediaRequest = new ImportMediaRequest();
      mediaRequest.setName(name);
      mediaRequest.setLink(link);
      mediaRequest.setFileName(filename);

      MultipartFile file = getMediaAsMultipartFile(mediaRequest);
      MediaUploadResponse mediaUploadResponse = mediaService.save(mediaRequest, file);

      currMedia.put("mediaId", mediaUploadResponse.getMediaId());
      currMedia.put("link", mediaUploadResponse.getLink());
      ((ArrayNode) data).set(i, objectMapper.convertValue(currMedia, JsonNode.class));
    }

    parameterCreateRequest.setData(data);
  }

  private static MultipartFile getMediaAsMultipartFile(ImportMediaRequest mediaRequest) {
    log.info("[getMediaAsMultipartFile] downloading media mediaRequest: {}", mediaRequest);
    try (InputStream inputStream = new URL(mediaRequest.getLink()).openStream()) {
      return new CustomMultipartFile(inputStream, mediaRequest.getFileName());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

/*For the usecase-> where process is imported from one facility to other than copy of process is created if checklist is already present in DB
  This method replaces the old Ids to new Ids and create copy of checklist in DB for requested facility.
 */

  private ImportChecklistRequest updateRequestWithNewIds(ImportChecklistRequest importCheckListRequest) throws JsonProcessingException {
    log.info("[updateRequestWithNewIds] creating copy of checklist with new Ids importCheckListRequest: {}", importCheckListRequest);
    Set<String> oldIds = new HashSet<>();

    oldIds.add(importCheckListRequest.getId());

    for (ImportStageRequest importStageRequest : importCheckListRequest.getStageRequests()) {
      oldIds.add(importStageRequest.getId());

      for (ImportTaskRequest importTaskRequest : importStageRequest.getTaskRequests()) {
        oldIds.add(importTaskRequest.getId());

        for (ImportParameterRequest importParameterRequest : importTaskRequest.getParameterRequests()) {
          oldIds.add(importParameterRequest.getId());
        }
        /*we are setting id null here as in add automations we have logic of assigning createdBy user and id if id is null
       and if we are importing in same facility then it will create issues, better to get ids from add automation method
         */
        for (AutomationRequest automationRequest : importTaskRequest.getAutomationRequests()) {
          automationRequest.setId(null);
        }
      }
    }

    if (!Utility.isEmpty(importCheckListRequest.getParameterRequests())) {
      for (ImportParameterRequest importParameterRequest : importCheckListRequest.getParameterRequests()) {
        oldIds.add(importParameterRequest.getId());
      }
    }

    String jsonData = objectMapper.writeValueAsString(importCheckListRequest);
    for (String oldId : oldIds) {
      jsonData = jsonData.replace(oldId, String.valueOf(IdGenerator.getInstance().nextId()));
    }

    return objectMapper.readValue(jsonData, ImportChecklistRequest.class);
  }

  private List<Stage> createStages(User principalUserEntity, List<ImportStageRequest> stageRequests, Checklist checklist) {
    log.info("[createStages] Request to create stages, stageRequests: {}, checklistId: {}", stageRequests, checklist.getId());
    List<Stage> stages = new ArrayList<>();
    for (ImportStageRequest stageRequest : stageRequests) {
      Stage stage = buildStage(principalUserEntity, stageRequest, checklist);
      stages.add(stage);
    }

    return stageRepository.saveAll(stages);
  }

  private void createTasks(User principalUserEntity, List<ImportStageRequest> stageRequests, List<Stage> stages) {
    log.info("[createTasks] Request to create tasks stageRequests: {}, stages: {}", stageRequests, stages);
    List<Task> tasks = new ArrayList<>();
    for (int i = 0; i < stages.size(); i++) {
      for (ImportTaskRequest taskRequest : stageRequests.get(i).getTaskRequests()) {
        Task task = buildTask(principalUserEntity, taskRequest, stages.get(i));
        tasks.add(task);
      }
    }

    taskRepository.saveAll(tasks);
  }

  private Checklist createChecklist(PrincipalUser principalUser, User principalUserEntity, ImportChecklistRequest importCheckListRequest, Facility facility, Organisation organisation, UseCase useCase, Long currentFacilityId) {
    log.info("[createChecklist] Request to create checklist, importCheckListRequest: {}, facility: {}, organisation: {}, useCase: {}", importCheckListRequest, facility, organisation, useCase);
    Checklist checklist = new Checklist();
    checklist.setId(Long.valueOf(importCheckListRequest.getId()));
    checklist.setName(importCheckListRequest.getName());
    checklist.setDescription(importCheckListRequest.getDescription());
    checklist.setOrganisation(organisation);
    checklist.setOrganisationId(organisation.getId());
    checklist.setCode(codeService.getCode(Type.EntityType.CHECKLIST, principalUser.getOrganisationId()));
    checklist.setUseCase(useCase);
    checklist.setUseCaseId(useCase.getId());
    checklist.setState(State.Checklist.BEING_BUILT);
    checklist.setCreatedBy(principalUserEntity);
    checklist.setModifiedBy(principalUserEntity);
    if (currentFacilityId != -1) {
      checklist.addFacility(facility, principalUserEntity);
    } else {
      checklist.setGlobal(true);
    }

    checklist.addPrimaryAuthor(principalUserEntity, checklist.getReviewCycle(), principalUserEntity);
    Version version = versionService.createNewVersion(checklist.getId(), Type.EntityType.CHECKLIST, principalUserEntity);
    checklist.setVersion(version);

    return checklistRepository.save(checklist);
  }

  private Stage buildStage(User principalUserEntity, ImportStageRequest importStageRequest, Checklist checklist) {
    log.info("[buildStage] Request to build stage, ImportStageRequest: {}, checklistId: {}", importStageRequest, checklist.getId());
    Stage stage = new Stage();
    stage.setId(Long.valueOf(importStageRequest.getId()));
    stage.setModifiedBy(principalUserEntity);
    stage.setCreatedBy(principalUserEntity);
    stage.setChecklist(checklist);
    stage.setName(importStageRequest.getName());
    stage.setOrderTree(importStageRequest.getOrderTree());
    return stage;
  }

  private Task buildTask(User principalUserEntity, ImportTaskRequest taskRequest, Stage stage) {
    log.info("[buildTask] Request to build task, taskRequest: {}, stageId: {}", taskRequest, stage.getId());
    Task task = new Task();
    task.setId(Long.valueOf(taskRequest.getId()));
    task.setName(taskRequest.getName());
    task.setHasStop(taskRequest.isHasStop());
    task.setTimed(taskRequest.isTimed());
    task.setMinPeriod(taskRequest.getMinPeriod());
    task.setMaxPeriod(taskRequest.getMaxPeriod());
    task.setTimerOperator(taskRequest.getTimerOperator());
    task.setMandatory(taskRequest.isMandatory());
    task.setModifiedBy(principalUserEntity);
    task.setCreatedBy(principalUserEntity);
    task.setOrderTree(taskRequest.getOrderTree());
    task.setStage(stage);
    return task;
  }
}
