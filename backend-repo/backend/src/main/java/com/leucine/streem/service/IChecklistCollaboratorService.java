package com.leucine.streem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leucine.streem.constant.State;
import com.leucine.streem.dto.ChecklistBasicDto;
import com.leucine.streem.dto.ChecklistCommentDto;
import com.leucine.streem.dto.ChecklistReviewDto;
import com.leucine.streem.dto.CollaboratorCommentDto;
import com.leucine.streem.dto.projection.ChecklistCollaboratorView;
import com.leucine.streem.dto.request.ChecklistCollaboratorAssignmentRequest;
import com.leucine.streem.dto.request.SignOffOrderTreeRequest;
import com.leucine.streem.dto.request.CommentAddRequest;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.Checklist;
import com.leucine.streem.model.User;

import java.util.List;

public interface IChecklistCollaboratorService {

  ChecklistBasicDto submitForReview(Long checklistId) throws ResourceNotFoundException, StreemException, JsonProcessingException;

  ChecklistBasicDto assignments(Long checklistId, ChecklistCollaboratorAssignmentRequest checklistCollaboratorAssignmentRequest) throws ResourceNotFoundException, StreemException;

  List<ChecklistCollaboratorView> getAllAuthors(Long checklistId);

  List<ChecklistCollaboratorView> getAllReviewers(Long checklistId);

  List<ChecklistCollaboratorView> getAllSignOffUsers(Long checklistId);

  List<ChecklistCollaboratorView> getAllCollaborators(Long checklistId, State.ChecklistCollaboratorPhaseType phaseType);

  ChecklistReviewDto startReview(Long checklistId) throws ResourceNotFoundException, StreemException;

  ChecklistCommentDto commentedOk(Long checklistId) throws ResourceNotFoundException, StreemException;

  ChecklistCommentDto commentedChanges(Long checklistId, CommentAddRequest commentAddRequest) throws ResourceNotFoundException, StreemException;

  ChecklistReviewDto submitBack(Long checklistId) throws ResourceNotFoundException, StreemException;

  ChecklistBasicDto initiateSignOff(Long checklistId) throws ResourceNotFoundException, StreemException;

  ChecklistReviewDto signOffOrderTree(Long checklistId, SignOffOrderTreeRequest signOffOrderTreeRequest) throws ResourceNotFoundException, StreemException;

  ChecklistReviewDto signOff(Long checklistId) throws ResourceNotFoundException, StreemException;

  ChecklistBasicDto publish(Long checklistId) throws ResourceNotFoundException, StreemException;

  List<CollaboratorCommentDto> getComments(Long checklistId, Long reviewerId) throws ResourceNotFoundException, StreemException;

  void updateAutoInitializedParametersEntity(Checklist checklist, User principalUserEntity) throws JsonProcessingException;
}
