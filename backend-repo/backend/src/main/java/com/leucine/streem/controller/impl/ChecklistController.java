package com.leucine.streem.controller.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leucine.streem.constant.State;
import com.leucine.streem.controller.IChecklistController;
import com.leucine.streem.dto.*;
import com.leucine.streem.dto.projection.ChecklistCollaboratorView;
import com.leucine.streem.dto.projection.TaskAssigneeView;
import com.leucine.streem.dto.request.*;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.service.*;
import com.leucine.streem.util.DateTimeUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
public class ChecklistController implements IChecklistController {
  private final IChecklistService checklistService;
  private final IChecklistCollaboratorService checklistCollaboratorService;
  private final IChecklistRevisionService checklistRevisionService;
  private final IParameterService parameterService;
  private final ObjectMapper objectMapper;

  private final IimportExportChecklistService importExportChecklistService;

  @Autowired
  public ChecklistController(IChecklistService checklistService, IChecklistCollaboratorService checklistCollaboratorService,
                             IChecklistRevisionService checklistRevisionService, IParameterService parameterService, ObjectMapper objectMapper, IimportExportChecklistService importExportChecklistService) {
    this.checklistService = checklistService;
    this.checklistCollaboratorService = checklistCollaboratorService;
    this.checklistRevisionService = checklistRevisionService;
    this.parameterService = parameterService;
    this.objectMapper = objectMapper;
    this.importExportChecklistService = importExportChecklistService;
  }

  @Override
  public Response<Page<ChecklistPartialDto>> getAll(String filters, Pageable pageable) {
    return Response.builder().data(checklistService.getAllChecklist(filters, pageable)).build();
  }

  @Override
  public Response<ChecklistDto> getChecklist(Long checklistId) throws ResourceNotFoundException {
    return Response.builder().data(checklistService.getChecklistById(checklistId)).build();
  }

  @Override
  public Response<ChecklistInfoDto> getChecklistInfo(Long checklistId) throws ResourceNotFoundException {
    return Response.builder().data(checklistService.getChecklistInfoById(checklistId)).build();
  }

  @Override
  public Response<ChecklistDto> createChecklist(CreateChecklistRequest createChecklistRequest) throws StreemException, ResourceNotFoundException {
    return Response.builder().data(checklistService.createChecklist(createChecklistRequest)).build();
  }

  @Override
  public Response<BasicDto> archiveChecklist(Long checklistId, ArchiveChecklistRequest archiveChecklistRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(checklistService.archiveChecklist(checklistId, archiveChecklistRequest.getReason())).build();
  }

  @Override
  public Response<List<ParameterInfoDto>> configureProcessParameters(Long checklistId, MapJobParameterRequest mapJobParameterRequest) throws StreemException, ResourceNotFoundException {
    return Response.builder().data(checklistService.configureProcessParameters(checklistId, mapJobParameterRequest)).build();
  }

  @Override
  public Response<BasicDto> validateChecklistArchival(Long checklistId) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(checklistService.validateChecklistArchival(checklistId)).build();
  }

  @Override
  public Response<BasicDto> unarchiveChecklist(Long checklistId, UnarchiveChecklistRequest unarchiveChecklistRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(checklistService.unarchiveChecklist(checklistId, unarchiveChecklistRequest.getReason())).build();
  }

  @Override
  public Response<BasicDto> validateChecklist(Long checklistId) throws ResourceNotFoundException, IOException, StreemException {
    return Response.builder().data(checklistService.validateChecklist(checklistId)).build();
  }

  @Override
  public Response<BasicDto> updateChecklist(Long checklistId, ChecklistUpdateRequest checklistUpdateRequest) throws StreemException, ResourceNotFoundException {
    return Response.builder().data(checklistService.updateChecklist(checklistId, checklistUpdateRequest)).build();
  }

  @Override
  public Response<ChecklistBasicDto> reviewerAssignments(Long checklistId, ChecklistCollaboratorAssignmentRequest checklistCollaboratorAssignmentRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(checklistCollaboratorService.assignments(checklistId, checklistCollaboratorAssignmentRequest)).build();
  }

  @Override
  public Response<List<ChecklistCollaboratorView>> getAllAuthors(Long checklistId) {
    return Response.builder().data(checklistCollaboratorService.getAllAuthors(checklistId)).build();
  }

  @Override
  public Response<List<ChecklistCollaboratorView>> getAllReviewers(Long checklistId) {
    return Response.builder().data(checklistCollaboratorService.getAllReviewers(checklistId)).build();
  }

  @Override
  public Response<List<ChecklistCollaboratorView>> getAllSignOffUsers(Long checklistId) {
    return Response.builder().data(checklistCollaboratorService.getAllSignOffUsers(checklistId)).build();
  }

  @Override
  public Response<List<ChecklistCollaboratorView>> getAllCollaborators(Long checklistId, State.ChecklistCollaboratorPhaseType phaseType) {
    return Response.builder().data(checklistCollaboratorService.getAllCollaborators(checklistId, phaseType)).build();
  }

  @Override
  public Response<ChecklistBasicDto> submitForReview(Long checklistId) throws ResourceNotFoundException, StreemException, JsonProcessingException {
    return Response.builder().data(checklistCollaboratorService.submitForReview(checklistId)).build();
  }

  @Override
  public Response<ChecklistReviewDto> startReview(Long checklistId) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(checklistCollaboratorService.startReview(checklistId)).build();
  }

  @Override
  public Response<ChecklistCommentDto> commentedOk(Long checklistId) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(checklistCollaboratorService.commentedOk(checklistId)).build();
  }

  @Override
  public Response<ChecklistCommentDto> commentedChanges(Long checklistId, CommentAddRequest commentAddRequest) throws ResourceNotFoundException,
    StreemException {
    return Response.builder().data(checklistCollaboratorService.commentedChanges(checklistId, commentAddRequest)).build();
  }

  @Override
  public Response<ChecklistReviewDto> submitBack(Long checklistId) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(checklistCollaboratorService.submitBack(checklistId)).build();
  }

  @Override
  public Response<ChecklistBasicDto> initiateSignOff(Long checklistId) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(checklistCollaboratorService.initiateSignOff(checklistId)).build();
  }

  @Override
  public Response<ChecklistReviewDto> signOffOrderTree(Long checklistId, SignOffOrderTreeRequest signOffOrderTreeRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(checklistCollaboratorService.signOffOrderTree(checklistId, signOffOrderTreeRequest)).build();
  }

  @Override
  public Response<ChecklistReviewDto> signOff(Long checklistId) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(checklistCollaboratorService.signOff(checklistId)).build();
  }

  @Override
  public Response<ChecklistBasicDto> publish(Long checklistId) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(checklistCollaboratorService.publish(checklistId)).build();
  }

  @Override
  public Response<List<CollaboratorCommentDto>> getComments(Long checklistId, Long reviewerId, ChecklistCollaboratorAssignmentRequest checklistCollaboratorAssignmentRequest) throws ResourceNotFoundException, StreemException {
    return Response.builder().data(checklistCollaboratorService.getComments(checklistId, reviewerId)).build();
  }

  @Override
  public Response<ChecklistDto> createChecklistRevision(Long checklistId) throws ResourceNotFoundException, StreemException, JsonProcessingException {
    return Response.builder().data(checklistRevisionService.createChecklistRevision(checklistId)).build();
  }

  @Override
  public Response<Page<ParameterInfoDto>> getParametersByTargetEntityType(Long checklistId, String filters, Pageable pageable) {
    return Response.builder().data(parameterService.getAllParameters(checklistId, filters, pageable)).build();
  }

  @Override
  public Response<List<TaskAssigneeView>> getAssignmentList(Long checklistId, Set<Long> taskAssignedIdsDto) throws ResourceNotFoundException {
    return Response.builder().data(checklistService.getTaskAssignmentDetails(checklistId, taskAssignedIdsDto)).build();
  }

  @Override
  public Response<List<ChecklistDefaultUserDto>> getDefaultUsers(Long checklistId) throws StreemException {
    return Response.builder().data(checklistService.getDefaultUsers(checklistId)).build();
  }

  @Override
  public Response<BasicDto> bulkAssignment(Long checklistId, ChecklistTaskAssignmentRequest assignmentRequest, boolean notify) throws StreemException, ResourceNotFoundException {
    return Response.builder().data(checklistService.bulkAssignDefaultUsers(checklistId, assignmentRequest, notify)).build();
  }

  @Override
  public Response<List<FacilityDto>> getAllFacilitiesByChecklistId(Long checklistId) throws ResourceNotFoundException {
    return Response.builder().data((checklistService.getFacilityChecklistMapping(checklistId))).build();
  }

  @Override
  public Response<BasicDto> addFacilitiesToChecklist(Long checklistId, ChecklistFacilityAssignmentRequest checklistFacilityAssignmentRequest) throws ResourceNotFoundException {
    return Response.builder().data(checklistService.bulkAssignmentFacilityIds(checklistId, checklistFacilityAssignmentRequest)).build();
  }

  @Override
  public Response<BasicDto> reconfigureJobLogColumns(Long checklistId) throws ResourceNotFoundException {
    return Response.builder().data(checklistService.reconfigureJobLogColumns(checklistId)).build();
  }

  @Override
  public Response<BasicDto> importChecklists(MultipartFile file) throws StreemException, ResourceNotFoundException, IOException {
    return Response.builder().data(importExportChecklistService.importChecklists(file)).build();
  }

  @Override
  public void exportChecklists(List<Long> ids, HttpServletResponse httpServletResponse) throws IOException, ResourceNotFoundException {
    httpServletResponse.setContentType("application/octet-stream");
    httpServletResponse.setHeader("Content-Disposition", String.format("attachment; filename=%s.json", DateTimeUtils.now()));
    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
    var data = importExportChecklistService.exportChecklists(ids);
    var jsonStream = new ByteArrayInputStream(objectMapper.writeValueAsBytes(data));
    IOUtils.copy(jsonStream, httpServletResponse.getOutputStream());
  }

  @Override
  public Response<Page<ChecklistDto>> getAllByResource(String objectTypeId, String filters, Pageable pageable) {
    return Response.builder().data(checklistService.getAllByResource(objectTypeId, filters, pageable)).build();
  }
}
